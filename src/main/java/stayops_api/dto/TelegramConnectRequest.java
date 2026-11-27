package stayops_api.dto;

import jakarta.validation.constraints.NotBlank;

public class TelegramConnectRequest {

    @NotBlank(message = "Telegram Bot API Token is required")
    private String botToken;

    public TelegramConnectRequest() {
    }

    public TelegramConnectRequest(String botToken) {
        this.botToken = botToken;
    }

    public String getBotToken() {
        return botToken;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }
}
