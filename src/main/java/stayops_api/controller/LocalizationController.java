package stayops_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "12. Localization & i18n", description = "Dynamic multilingual localization bundles for platform modules")
@RestController
@RequestMapping("/api/localization")
public class LocalizationController {

    private static final List<Map<String, String>> SUPPORTED_LANGUAGES = List.of(
            Map.of("code", "en", "label", "English", "native", "English"),
            Map.of("code", "hi", "label", "Hindi", "native", "हिंदी"),
            Map.of("code", "ta", "label", "Tamil", "native", "தமிழ்"),
            Map.of("code", "kn", "label", "Kannada", "native", "ಕನ್ನಡ")
    );

    @Operation(summary = "Get list of supported platform languages")
    @GetMapping("/languages")
    public ResponseEntity<List<Map<String, String>>> getSupportedLanguages() {
        return ResponseEntity.ok(SUPPORTED_LANGUAGES);
    }

    @Operation(summary = "Get localization translation bundle for Petty Cash Ledger")
    @GetMapping("/petty-cash")
    public ResponseEntity<Map<String, Object>> getPettyCashTranslations(
            @RequestParam(defaultValue = "en") String lang
    ) {
        String normalizedLang = (lang == null || lang.isBlank()) ? "en" : lang.toLowerCase().trim();
        Map<String, String> bundle = getPettyCashBundle(normalizedLang);

        Map<String, Object> response = new HashMap<>();
        response.put("lang", normalizedLang);
        response.put("module", "petty-cash");
        response.put("translations", bundle);

        return ResponseEntity.ok(response);
    }

    private Map<String, String> getPettyCashBundle(String lang) {
        switch (lang) {
            case "hi":
                return getHindiPettyCashBundle();
            case "ta":
                return getTamilPettyCashBundle();
            case "kn":
                return getKannadaPettyCashBundle();
            case "en":
            default:
                return getEnglishPettyCashBundle();
        }
    }

    private Map<String, String> getEnglishPettyCashBundle() {
        Map<String, String> m = new HashMap<>();
        m.put("badge", "STAYOPS LEDGER");
        m.put("mainHeading", "Petty Cash Ledger");
        m.put("subHeading", "Track internal cash float, owner funding, and caretaker verified expenses");
        m.put("addCashFunds", "Add Cash Funds");
        m.put("recordExpense", "Record Expense");
        m.put("refresh", "Refresh ledger");

        m.put("currentBalance", "Current Petty Cash Balance");
        m.put("lowFloat", "Low Float");
        m.put("balanceSubAll", "Real live balance across all property units");
        m.put("balanceSubSingle", "Real live balance for selected property");
        m.put("cashAdded", "Cash Added");
        m.put("expensesPaid", "Expenses Paid");

        m.put("allProperties", "All Properties");
        m.put("allTime", "All Time");
        m.put("thisMonth", "This Month");
        m.put("lastMonth", "Last Month");
        m.put("thisYear", "This Year");
        m.put("searchPlaceholder", "Search category, notes, or user...");
        m.put("showingTransactions", "Showing {count} transaction(s)");

        m.put("thDateTime", "DATE & TIME");
        m.put("thProperty", "PROPERTY");
        m.put("thType", "TYPE");
        m.put("thCategory", "CATEGORY");
        m.put("thLoggedBy", "LOGGED BY");
        m.put("thNotes", "NOTES / DETAILS");
        m.put("thAmount", "AMOUNT");
        m.put("thReceipt", "RECEIPT PHOTO");

        m.put("creditType", "CREDIT");
        m.put("debitType", "DEBIT");
        m.put("viewBill", "View Bill");
        m.put("noProof", "No photo attached");
        m.put("noTransactionsTitle", "No transactions found");
        m.put("noTransactionsSubtitle", "No petty cash deposits or expense entries recorded for this selection.");

        m.put("catCleaning", "Cleaning Supplies");
        m.put("catGrocery", "Grocery & Tea");
        m.put("catRepair", "Maintenance & Repair");
        m.put("catUtility", "Electricity & Utility");
        m.put("catAmenities", "Room Amenities");
        m.put("catOther", "Other Expense");
        m.put("catFunding", "Petty Cash Funding");

        m.put("addFundsTitle", "Add Petty Cash Funds");
        m.put("addFundsSub", "Owner / Manager internal float deposit into property cash drawer");
        m.put("currentLiveBalance", "Current Live Balance");
        m.put("selectProperty", "Select Property Unit");
        m.put("fundingAmount", "Cash Amount to Add");
        m.put("fundingAmountPlaceholder", "e.g. 5000");
        m.put("fundingNotes", "Funding Notes / Purpose");
        m.put("fundingNotesPlaceholder", "e.g. Monthly operating float, weekend advance");
        m.put("cancel", "Cancel");
        m.put("confirmAddFunds", "Confirm & Add Cash Float");
        m.put("addingFunds", "Adding Cash Float...");

        m.put("recordExpenseTitle", "Record Petty Cash Expense");
        m.put("availableCash", "Available Cash");
        m.put("propertyUnit", "Property Unit");
        m.put("expenseAmount", "Expense Amount");
        m.put("category", "Category");
        m.put("receiptPhoto", "Receipt Photo");
        m.put("required", "* Required");
        m.put("uploadReceipt", "Upload Bill / Receipt Photo");
        m.put("billAttached", "Bill Photo Attached & Ready");
        m.put("replacePhoto", "Replace Photo");
        m.put("notesOptional", "Notes / Item Description (Optional)");
        m.put("notesPlaceholder", "e.g. 2 packs tea bags, cleaning detergent");
        m.put("submitExpense", "Record Expense & Deduct Cash");
        m.put("savingExpense", "Saving Expense...");

        m.put("receiptViewerTitle", "Verified Bill / Expense Proof");
        m.put("downloadReceipt", "Download Receipt");
        m.put("close", "Close");
        return m;
    }

    private Map<String, String> getHindiPettyCashBundle() {
        Map<String, String> m = new HashMap<>();
        m.put("badge", "स्टेऑप्स लेज़र");
        m.put("mainHeading", "पेट्टी कैश लेज़र (दैनिक खर्च खाता)");
        m.put("subHeading", "आंतरिक नकद राशि, मालिक द्वारा फंडिंग और केयरटेकर के सत्यापित खर्चों को ट्रैक करें");
        m.put("addCashFunds", "नकद फंड जोड़ें");
        m.put("recordExpense", "खर्च दर्ज करें");
        m.put("refresh", "लेज़र रिफ्रेश करें");

        m.put("currentBalance", "वर्तमान पेट्टी कैश बैलेंस");
        m.put("lowFloat", "कम बैलेंस");
        m.put("balanceSubAll", "सभी प्रॉपर्टी का वास्तविक लाइव बैलेंस");
        m.put("balanceSubSingle", "चयनित प्रॉपर्टी का लाइव बैलेंस");
        m.put("cashAdded", "कुल जमा नकद");
        m.put("expensesPaid", "कुल किए गए खर्च");

        m.put("allProperties", "सभी संपत्तियां");
        m.put("allTime", "सभी समय");
        m.put("thisMonth", "इस महीने");
        m.put("lastMonth", "पिछले महीने");
        m.put("thisYear", "इस वर्ष");
        m.put("searchPlaceholder", "श्रेणी, विवरण या उपयोगकर्ता खोजें...");
        m.put("showingTransactions", "{count} लेनदेन दिखाए जा रहे हैं");

        m.put("thDateTime", "दिनांक और समय");
        m.put("thProperty", "प्रॉपर्टी");
        m.put("thType", "प्रकार");
        m.put("thCategory", "श्रेणी");
        m.put("thLoggedBy", "दर्जकर्ता");
        m.put("thNotes", "विवरण / नोट्स");
        m.put("thAmount", "राशि");
        m.put("thReceipt", "रसीद फोटो");

        m.put("creditType", "क्रेडिट (जमा)");
        m.put("debitType", "डेबिट (खर्च)");
        m.put("viewBill", "बिल देखें");
        m.put("noProof", "कोई फोटो संलग्न नहीं");
        m.put("noTransactionsTitle", "कोई लेनदेन नहीं मिला");
        m.put("noTransactionsSubtitle", "इस चयन के लिए कोई पेट्टी कैश जमा या खर्च प्रविष्टि दर्ज नहीं है।");

        m.put("catCleaning", "सफाई सामग्री");
        m.put("catGrocery", "किराना और चाय");
        m.put("catRepair", "रखरखाव और मरम्मत");
        m.put("catUtility", "बिजली और बिल");
        m.put("catAmenities", "कमरे की सुविधाएं");
        m.put("catOther", "अन्य खर्च");
        m.put("catFunding", "पेट्टी कैश फंडिंग");

        m.put("addFundsTitle", "पेट्टी कैश फंड जोड़ें");
        m.put("addFundsSub", "मालिक / प्रबंधक द्वारा प्रॉपर्टी कैश ड्रावर में आंतरिक नकद जमा");
        m.put("currentLiveBalance", "वर्तमान लाइव बैलेंस");
        m.put("selectProperty", "प्रॉपर्टी यूनिट चुनें");
        m.put("fundingAmount", "जोड़ने के लिए नकद राशि");
        m.put("fundingAmountPlaceholder", "उदा. 5000");
        m.put("fundingNotes", "फंडिंग विवरण / उद्देश्य");
        m.put("fundingNotesPlaceholder", "उदा. मासिक ऑपरेटिंग फ्लोट, वीकेंड एडवांस");
        m.put("cancel", "रद्द करें");
        m.put("confirmAddFunds", "पुष्टि करें और नकद जोड़ें");
        m.put("addingFunds", "फंड जोड़ा जा रहा है...");

        m.put("recordExpenseTitle", "पेट्टी कैश खर्च दर्ज करें");
        m.put("availableCash", "उपलब्ध नकद");
        m.put("propertyUnit", "प्रॉपर्टी यूनिट");
        m.put("expenseAmount", "खर्च की राशि");
        m.put("category", "श्रेणी");
        m.put("receiptPhoto", "रसीद फोटो");
        m.put("required", "* अनिवार्य");
        m.put("uploadReceipt", "बिल / रसीद फोटो अपलोड करें");
        m.put("billAttached", "बिल फोटो संलग्न है");
        m.put("replacePhoto", "फोटो बदलें");
        m.put("notesOptional", "विवरण / सामग्री (वैकल्पिक)");
        m.put("notesPlaceholder", "उदा. 2 पैकेट चायपत्ती, फिनाइल");
        m.put("submitExpense", "खर्च दर्ज करें और नकद काटें");
        m.put("savingExpense", "दर्ज किया जा रहा है...");

        m.put("receiptViewerTitle", "सत्यापित बिल / खर्च का प्रमाण");
        m.put("downloadReceipt", "रसीद डाउनलोड करें");
        m.put("close", "बंद करें");
        return m;
    }

    private Map<String, String> getTamilPettyCashBundle() {
        Map<String, String> m = new HashMap<>();
        m.put("badge", "ஸ்டேஆப்ஸ் லெட்ஜர்");
        m.put("mainHeading", "சில்லறை பண லெட்ஜர் (பெட்டி கேஷ்)");
        m.put("subHeading", "உள்நாட்டு பண இருப்பு, உரிமையாளர் நிதி மற்றும் பராமரிப்பாளர் சரிபார்த்த செலவுகளை கண்காணிக்கவும்");
        m.put("addCashFunds", "பணம் சேர்க்க");
        m.put("recordExpense", "செலவை பதிவு செய்க");
        m.put("refresh", "புதுப்பிக்கவும்");

        m.put("currentBalance", "தற்போதைய பெட்டி கேஷ் இருப்பு");
        m.put("lowFloat", "குறைந்த இருப்பு");
        m.put("balanceSubAll", "அனைத்து சொத்துகளின் நேரடி இருப்பு");
        m.put("balanceSubSingle", "தேர்ந்தெடுக்கப்பட்ட சொத்தின் நேரடி இருப்பு");
        m.put("cashAdded", "சேர்க்கப்பட்ட மொத்த பணம்");
        m.put("expensesPaid", "செலுத்தப்பட்ட மொத்த செலவுகள்");

        m.put("allProperties", "அனைத்து சொத்துக்கள்");
        m.put("allTime", "எல்லா நேரமும்");
        m.put("thisMonth", "இந்த மாதம்");
        m.put("lastMonth", "கடந்த மாதம்");
        m.put("thisYear", "இந்த வருடம்");
        m.put("searchPlaceholder", "வகை, குறிப்புகள் அல்லது பயனரைத் தேடவும்...");
        m.put("showingTransactions", "{count} பரிவர்த்தனைகள் காட்டப்படுகின்றன");

        m.put("thDateTime", "தேதி & நேரம்");
        m.put("thProperty", "சொத்து");
        m.put("thType", "வகை");
        m.put("thCategory", "பிரிவு");
        m.put("thLoggedBy", "பதிவு செய்தவர்");
        m.put("thNotes", "குறிப்புகள் / விவரங்கள்");
        m.put("thAmount", "தொகை");
        m.put("thReceipt", "ரசீது புகைப்படம்");

        m.put("creditType", "வரவு (கிரெடிட்)");
        m.put("debitType", "பற்று (டெபிட்)");
        m.put("viewBill", "பில் பார்க்கவும்");
        m.put("noProof", "புகைப்படம் இணைக்கப்படவில்லை");
        m.put("noTransactionsTitle", "பரிவர்த்தனைகள் எதுவும் இல்லை");
        m.put("noTransactionsSubtitle", "இந்த தேர்விற்கு எந்த பெட்டி கேஷ் வரவு அல்லது செலவு உள்ளீடுகளும் இல்லை.");

        m.put("catCleaning", "துப்புரவு பொருட்கள்");
        m.put("catGrocery", "மளிகை மற்றும் தேநீர்");
        m.put("catRepair", "பராமரிப்பு மற்றும் பழுது");
        m.put("catUtility", "மின்சாரம் & கட்டணங்கள்");
        m.put("catAmenities", "அறை வசதிகள்");
        m.put("catOther", "இதர செலவுகள்");
        m.put("catFunding", "பெட்டி கேஷ் நிதி");

        m.put("addFundsTitle", "பெட்டி கேஷ் நிதி சேர்க்க");
        m.put("addFundsSub", "உரிமையாளர் / மேலாளரால் பணப் பெட்டியில் உள்நாட்டு ரொக்க வைப்பு");
        m.put("currentLiveBalance", "தற்போதைய இருப்பு");
        m.put("selectProperty", "சொத்து பிரிவை தேர்ந்தெடுக்கவும்");
        m.put("fundingAmount", "சேர்க்க வேண்டிய தொகை");
        m.put("fundingAmountPlaceholder", "எ.கா. 5000");
        m.put("fundingNotes", "நிதி குறிப்புகள் / நோக்கம்");
        m.put("fundingNotesPlaceholder", "எ.கா. மாதாந்திர இயக்க நிதி, வார இறுதி முன்பணம்");
        m.put("cancel", "ரத்துசெய்");
        m.put("confirmAddFunds", "உறுதிசெய்து பணத்தை சேர்க்கவும்");
        m.put("addingFunds", "பணம் சேர்க்கப்படுகிறது...");

        m.put("recordExpenseTitle", "பெட்டி கேஷ் செலவை பதிவு செய்க");
        m.put("availableCash", "கிடைக்கக்கூடிய பணம்");
        m.put("propertyUnit", "சொத்து பிரிவு");
        m.put("expenseAmount", "செலவுத் தொகை");
        m.put("category", "பிரிவு");
        m.put("receiptPhoto", "ரசீது புகைப்படம்");
        m.put("required", "* கட்டாயம்");
        m.put("uploadReceipt", "பில் / ரசீதை பதிவேற்றவும்");
        m.put("billAttached", "பில் புகைப்படம் இணைக்கப்பட்டுள்ளது");
        m.put("replacePhoto", "புகைப்படத்தை மாற்றவும்");
        m.put("notesOptional", "குறிப்புகள் (விருப்பத்தேர்வு)");
        m.put("notesPlaceholder", "எ.கா. 2 பாக்கெட் டீத்தூள், துப்புரவு திரவம்");
        m.put("submitExpense", "செலவை பதிவு செய்து பணத்தை கழிக்கவும்");
        m.put("savingExpense", "பதிவு செய்யப்படுகிறது...");

        m.put("receiptViewerTitle", "சரிபார்க்கப்பட்ட பில் / செலவு சான்று");
        m.put("downloadReceipt", "ரசீதை பதிவிறக்கவும்");
        m.put("close", "மூடு");
        return m;
    }

    private Map<String, String> getKannadaPettyCashBundle() {
        Map<String, String> m = new HashMap<>();
        m.put("badge", "ಸ್ಟೇಆಪ್ಸ್ ಲೆಡ್ಜರ್");
        m.put("mainHeading", "ಪೆಟ್ಟಿ ಕ್ಯಾಶ್ (ದಿನನಿತ್ಯದ ಖರ್ಚು)");
        m.put("subHeading", "ದಿನನಿತ್ಯದ ಸಣ್ಣ-ಪುಟ್ಟ ಖರ್ಚುಗಳು ಮತ್ತು ಜಮೆಯಾದ ಕ್ಯಾಶ್ ಲೆಕ್ಕ");
        m.put("addCashFunds", "ಕ್ಯಾಶ್ ಜಮೆ ಮಾಡಿ");
        m.put("recordExpense", "ಖರ್ಚು ಎಂಟ್ರಿ ಮಾಡಿ");
        m.put("refresh", "ರಿಫ್ರೆಶ್ ಮಾಡಿ");

        m.put("currentBalance", "ಈಗಿರುವ ಕ್ಯಾಶ್ ಬ್ಯಾಲೆನ್ಸ್");
        m.put("lowFloat", "ಕ್ಯಾಶ್ ಕಮ್ಮಿ ಇದೆ");
        m.put("balanceSubAll", "ಎಲ್ಲಾ ಪ್ರಾಪರ್ಟಿಗಳಲ್ಲಿ ಸದ್ಯಕ್ಕಿರುವ ಕ್ಯಾಶ್");
        m.put("balanceSubSingle", "ಈ ಪ್ರಾಪರ್ಟಿಯಲ್ಲಿ ಸದ್ಯಕ್ಕಿರುವ ಕ್ಯಾಶ್");
        m.put("cashAdded", "ಜಮೆಯಾದ ಕ್ಯಾಶ್");
        m.put("expensesPaid", "ಆದ ಒಟ್ಟು ಖರ್ಚು");

        m.put("allProperties", "ಎಲ್ಲಾ ಪ್ರಾಪರ್ಟಿಗಳು");
        m.put("allTime", "ಎಲ್ಲವೂ");
        m.put("thisMonth", "ಈ ತಿಂಗಳು");
        m.put("lastMonth", "ಕಳೆದ ತಿಂಗಳು");
        m.put("thisYear", "ಈ ವರ್ಷ");
        m.put("searchPlaceholder", "ಖರ್ಚು, ವಿವರ ಅಥವಾ ಹೆಸರು ಹುಡುಕಿ...");
        m.put("showingTransactions", "{count} ಎಂಟ್ರಿಗಳು");

        m.put("thDateTime", "ದಿನಾಂಕ & ಸಮಯ");
        m.put("thProperty", "ಪ್ರಾಪರ್ಟಿ");
        m.put("thType", "ವಿಧ");
        m.put("thCategory", "ಖರ್ಚಿನ ವಿವರ");
        m.put("thLoggedBy", "ಎಂಟ್ರಿ ಮಾಡಿದವರು");
        m.put("thNotes", "ವಿವರಗಳು");
        m.put("thAmount", "ಮೊತ್ತ");
        m.put("thReceipt", "ಬಿಲ್ ಫೋಟೋ");

        m.put("creditType", "ಜಮೆ");
        m.put("debitType", "ಖರ್ಚು");
        m.put("viewBill", "ಬಿಲ್ ನೋಡಿ");
        m.put("noProof", "ಬಿಲ್ ಫೋಟೋ ಇಲ್ಲ");
        m.put("noTransactionsTitle", "ಯಾವ ಖರ್ಚು-ವೆಚ್ಚಗಳೂ ಇಲ್ಲ");
        m.put("noTransactionsSubtitle", "ಈ ಆಯ್ಕೆಯಲ್ಲಿ ಯಾವುದೇ ಖರ್ಚು ಅಥವಾ ಜಮೆಯ ಎಂಟ್ರಿ ಇಲ್ಲ.");

        m.put("catCleaning", "ಕ್ಲೀನಿಂಗ್ ಸಾಮಾನು");
        m.put("catGrocery", "ದಿನಸಿ ಮತ್ತು ಟೀ-ಕಾಫಿ");
        m.put("catRepair", "ರಿಪೇರಿ & ಮೇಂಟೆನೆನ್ಸ್");
        m.put("catUtility", "ಕರೆಂಟ್ & ನೀರು ಬಿಲ್");
        m.put("catAmenities", "ರೂಮ್ ಸಾಮಾನುಗಳು");
        m.put("catOther", "ಬೇರೆ ಖರ್ಚು");
        m.put("catFunding", "ಪೆಟ್ಟಿ ಕ್ಯಾಶ್ ಜಮೆ");

        m.put("addFundsTitle", "ಪೆಟ್ಟಿ ಕ್ಯಾಶ್ ಜಮೆ ಮಾಡಿ");
        m.put("addFundsSub", "ರೆಸಾರ್ಟ್ ಖರ್ಚಿಗಾಗಿ ಮಾಲೀಕರು ಅಥವಾ ಮ್ಯಾನೇಜರ್ ನೀಡಿದ ಕ್ಯಾಶ್ ಜಮೆ");
        m.put("currentLiveBalance", "ಈಗಿರುವ ಕ್ಯಾಶ್ ಬ್ಯಾಲೆನ್ಸ್");
        m.put("selectProperty", "ಪ್ರಾಪರ್ಟಿ ಆಯ್ಕೆ ಮಾಡಿ");
        m.put("fundingAmount", "ಜಮೆ ಮಾಡುವ ದುಡ್ಡು");
        m.put("fundingAmountPlaceholder", "ಉದಾ. 5000");
        m.put("fundingNotes", "ಯಾತಕ್ಕಾಗಿ / ವಿವರ");
        m.put("fundingNotesPlaceholder", "ಉದಾ. ವೀಕೆಂಡ್ ಖರ್ಚಿಗೆ, ತಿಂಗಳ ಮುಂಗಡ");
        m.put("cancel", "ಕ್ಯಾನ್ಸಲ್");
        m.put("confirmAddFunds", "ಕ್ಯಾಶ್ ಜಮೆ ಮಾಡಿ");
        m.put("addingFunds", "ಜಮೆ ಆಗುತ್ತಿದೆ...");

        m.put("recordExpenseTitle", "ಖರ್ಚು ಎಂಟ್ರಿ ಮಾಡಿ");
        m.put("availableCash", "ಕೈಯಲ್ಲಿರುವ ಕ್ಯಾಶ್");
        m.put("propertyUnit", "ಪ್ರಾಪರ್ಟಿ");
        m.put("expenseAmount", "ಖರ್ಚಾದ ಮೊತ್ತ");
        m.put("category", "ಖರ್ಚಿನ ವಿಭಾಗ");
        m.put("receiptPhoto", "ಬಿಲ್ ಫೋಟೋ");
        m.put("required", "* ಮುಖ್ಯ");
        m.put("uploadReceipt", "ಬಿಲ್ ಫೋಟೋ ಅಪ್ಲೋಡ್ ಮಾಡಿ");
        m.put("billAttached", "ಬಿಲ್ ಫೋಟೋ ಹಾಕಲಾಗಿದೆ");
        m.put("replacePhoto", "ಬೇರೆ ಫೋಟೋ ಹಾಕಿ");
        m.put("notesOptional", "ಖರ್ಚಿನ ವಿವರ (ಬೇಕಿದ್ದರೆ ಬರೆಯಿರಿ)");
        m.put("notesPlaceholder", "ಉದಾ. 2 ಪ್ಯಾಕೆಟ್ ಟೀ ಪುಡಿ, ಫಿನಾಯಿಲ್");
        m.put("submitExpense", "ಖರ್ಚು ಸೇರಿಸಿ (ಕ್ಯಾಶ್ ಕಡಿತ)");
        m.put("savingExpense", "ಸೇವ್ ಆಗುತ್ತಿದೆ...");

        m.put("receiptViewerTitle", "ಖರ್ಚಿನ ಬಿಲ್ ವಿವರ");
        m.put("downloadReceipt", "ಬಿಲ್ ಡೌನ್‌ಲೋಡ್ ಮಾಡಿ");
        m.put("close", "ಮುಚ್ಚಿ");
        return m;
    }
}
