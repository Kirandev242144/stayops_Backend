package stayops_api.dto;

public class TelegramStatusResponse {

    private boolean connected;
    private String botUsername;
    private String botName;
    private String botLink;
    private String guestBookingLink;
    private String webhookStatus;
    private boolean ownerLinked;
    private Long ownerChatId;
    private String webAppUrl;
    private String ownerLinkUrl;
    private boolean notifyNewBooking;
    private boolean notifyCancellation;
    private boolean notifyDailyRoster;

    public TelegramStatusResponse() {
    }

    public String getWebAppUrl() {
        return webAppUrl;
    }

    public void setWebAppUrl(String webAppUrl) {
        this.webAppUrl = webAppUrl;
    }

    public Long getOwnerChatId() {
        return ownerChatId;
    }

    public void setOwnerChatId(Long ownerChatId) {
        this.ownerChatId = ownerChatId;
    }

    public static TelegramStatusResponse notConnected() {
        TelegramStatusResponse res = new TelegramStatusResponse();
        res.setConnected(false);
        res.setWebhookStatus("NOT_CONFIGURED");
        res.setNotifyNewBooking(true);
        res.setNotifyCancellation(true);
        res.setNotifyDailyRoster(true);
        return res;
    }

    // Getters and Setters

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public String getBotUsername() {
        return botUsername;
    }

    public void setBotUsername(String botUsername) {
        this.botUsername = botUsername;
    }

    public String getBotName() {
        return botName;
    }

    public void setBotName(String botName) {
        this.botName = botName;
    }

    public String getBotLink() {
        return botLink;
    }

    public void setBotLink(String botLink) {
        this.botLink = botLink;
    }

    public String getGuestBookingLink() {
        return guestBookingLink;
    }

    public void setGuestBookingLink(String guestBookingLink) {
        this.guestBookingLink = guestBookingLink;
    }

    public String getWebhookStatus() {
        return webhookStatus;
    }

    public void setWebhookStatus(String webhookStatus) {
        this.webhookStatus = webhookStatus;
    }

    public boolean isOwnerLinked() {
        return ownerLinked;
    }

    public void setOwnerLinked(boolean ownerLinked) {
        this.ownerLinked = ownerLinked;
    }

    public String getOwnerLinkUrl() {
        return ownerLinkUrl;
    }

    public void setOwnerLinkUrl(String ownerLinkUrl) {
        this.ownerLinkUrl = ownerLinkUrl;
    }

    public boolean isNotifyNewBooking() {
        return notifyNewBooking;
    }

    public void setNotifyNewBooking(boolean notifyNewBooking) {
        this.notifyNewBooking = notifyNewBooking;
    }

    public boolean isNotifyCancellation() {
        return notifyCancellation;
    }

    public void setNotifyCancellation(boolean notifyCancellation) {
        this.notifyCancellation = notifyCancellation;
    }

    public boolean isNotifyDailyRoster() {
        return notifyDailyRoster;
    }

    public void setNotifyDailyRoster(boolean notifyDailyRoster) {
        this.notifyDailyRoster = notifyDailyRoster;
    }
}
