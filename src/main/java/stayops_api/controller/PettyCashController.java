package stayops_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import stayops_api.entity.PettyCashAccount;
import stayops_api.entity.PettyCashTransaction;
import stayops_api.repository.PettyCashAccountRepository;
import stayops_api.repository.PettyCashTransactionRepository;
import stayops_api.websocket.WebSocketBroadcaster;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Tag(name = "11. Petty Cash Ledger", description = "Property cash float, owner funding, and caretaker expense ledger")
@RestController
@RequestMapping("/api/petty-cash")
public class PettyCashController {

    private final PettyCashAccountRepository accountRepository;
    private final PettyCashTransactionRepository transactionRepository;
    private final WebSocketBroadcaster webSocketBroadcaster;

    public PettyCashController(
            PettyCashAccountRepository accountRepository,
            PettyCashTransactionRepository transactionRepository,
            WebSocketBroadcaster webSocketBroadcaster
    ) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.webSocketBroadcaster = webSocketBroadcaster;
    }

    @Operation(summary = "Get petty cash balance for property")
    @GetMapping("/balance")
    public ResponseEntity<Map<String, Object>> getBalance(
            @RequestParam(required = false) String propertyId
    ) {
        Map<String, Object> response = new HashMap<>();

        if (propertyId == null || propertyId.equalsIgnoreCase("all")) {
            List<PettyCashAccount> accounts = accountRepository.findAll();
            BigDecimal sum = accounts.stream()
                    .map(PettyCashAccount::getCurrentBalance)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            response.put("balance", sum);
        } else {
            try {
                Long pId = Long.parseLong(propertyId);
                Optional<PettyCashAccount> accOpt = accountRepository.findByPropertyId(pId);
                BigDecimal bal = accOpt.map(PettyCashAccount::getCurrentBalance).orElse(BigDecimal.ZERO);
                response.put("balance", bal);
            } catch (Exception e) {
                response.put("balance", BigDecimal.ZERO);
            }
        }
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get petty cash transactions history")
    @GetMapping("/transactions")
    public ResponseEntity<List<PettyCashTransaction>> getTransactions(
            @RequestParam(required = false) String propertyId
    ) {
        if (propertyId == null || propertyId.equalsIgnoreCase("all")) {
            return ResponseEntity.ok(transactionRepository.findAllByOrderByCreatedAtDesc());
        }
        try {
            Long pId = Long.parseLong(propertyId);
            return ResponseEntity.ok(transactionRepository.findByPropertyIdOrderByCreatedAtDesc(pId));
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }

    @Operation(summary = "Inject cash funds (CREDIT)")
    @PostMapping("/add-funds")
    @Transactional
    public ResponseEntity<Map<String, Object>> addFunds(@RequestBody Map<String, Object> payload) {
        String propIdStr = String.valueOf(payload.getOrDefault("propertyId", "1"));
        Long propertyId = 1L;
        try {
            propertyId = Long.parseLong(propIdStr);
        } catch (Exception ignored) {}

        String propertyName = String.valueOf(payload.getOrDefault("propertyName", "Property"));
        BigDecimal amount = new BigDecimal(String.valueOf(payload.getOrDefault("amount", "0")));
        String notes = String.valueOf(payload.getOrDefault("notes", "Cash added by Owner"));
        String addedBy = String.valueOf(payload.getOrDefault("addedBy", "Owner"));

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body(Map.of("message", "Funding amount must be greater than 0"));
        }

        PettyCashAccount account = accountRepository.findByPropertyId(propertyId)
                .orElse(new PettyCashAccount(propertyId, propertyName, BigDecimal.ZERO));

        account.setPropertyName(propertyName);
        account.setCurrentBalance(account.getCurrentBalance().add(amount));
        accountRepository.save(account);

        PettyCashTransaction tx = new PettyCashTransaction(
                propertyId, propertyName, "CREDIT", amount, "Petty Cash Funding", notes, null, addedBy
        );
        transactionRepository.save(tx);

        webSocketBroadcaster.broadcastPettyCash("PETTY_CASH_UPDATED", Map.of(
                "propertyId", propertyId,
                "newBalance", account.getCurrentBalance(),
                "transaction", tx
        ));

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("newBalance", account.getCurrentBalance());
        res.put("transaction", tx);
        return ResponseEntity.ok(res);
    }

    @Operation(summary = "Record operational expense (DEBIT)")
    @PostMapping("/record-expense")
    @Transactional
    public ResponseEntity<Map<String, Object>> recordExpense(@RequestBody Map<String, Object> payload) {
        String propIdStr = String.valueOf(payload.getOrDefault("propertyId", "1"));
        Long propertyId = 1L;
        try {
            propertyId = Long.parseLong(propIdStr);
        } catch (Exception ignored) {}

        String propertyName = String.valueOf(payload.getOrDefault("propertyName", "Property"));
        BigDecimal amount = new BigDecimal(String.valueOf(payload.getOrDefault("amount", "0")));
        String category = String.valueOf(payload.getOrDefault("category", "General Expense"));
        String notes = String.valueOf(payload.getOrDefault("notes", "Expense logged by caretaker"));
        String receiptImageUrl = String.valueOf(payload.getOrDefault("receiptImageUrl", ""));
        String addedBy = String.valueOf(payload.getOrDefault("addedBy", "Caretaker"));

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body(Map.of("message", "Expense amount must be greater than 0"));
        }

        if (receiptImageUrl.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Bill / Receipt photo is compulsory before deducting from petty cash"));
        }

        PettyCashAccount account = accountRepository.findByPropertyId(propertyId)
                .orElse(new PettyCashAccount(propertyId, propertyName, BigDecimal.ZERO));

        if (amount.compareTo(account.getCurrentBalance()) > 0) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Insufficient petty cash balance! Available: ₹" + account.getCurrentBalance() + ", Requested: ₹" + amount
            ));
        }

        account.setPropertyName(propertyName);
        account.setCurrentBalance(account.getCurrentBalance().subtract(amount));
        accountRepository.save(account);

        PettyCashTransaction tx = new PettyCashTransaction(
                propertyId, propertyName, "DEBIT", amount, category, notes, receiptImageUrl, addedBy
        );
        transactionRepository.save(tx);

        webSocketBroadcaster.broadcastPettyCash("PETTY_CASH_UPDATED", Map.of(
                "propertyId", propertyId,
                "newBalance", account.getCurrentBalance(),
                "transaction", tx
        ));

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("newBalance", account.getCurrentBalance());
        res.put("transaction", tx);
        return ResponseEntity.ok(res);
    }
}
