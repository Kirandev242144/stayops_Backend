package stayops_api.dto;

public class TelegramPreferencesRequest {

    private Boolean notifyNewBooking;
    private Boolean notifyCancellation;
    private Boolean notifyDailyRoster;
    private Long ownerChatId;
    private String webAppUrl;

    public TelegramPreferencesRequest() {
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

    public Boolean getNotifyNewBooking() {
        return notifyNewBooking;
    }

    public void setNotifyNewBooking(Boolean notifyNewBooking) {
        this.notifyNewBooking = notifyNewBooking;
    }

    public Boolean getNotifyCancellation() {
        return notifyCancellation;
    }

    public void setNotifyCancellation(Boolean notifyCancellation) {
        this.notifyCancellation = notifyCancellation;
    }

    public Boolean getNotifyDailyRoster() {
        return notifyDailyRoster;
    }

    public void setNotifyDailyRoster(Boolean notifyDailyRoster) {
        this.notifyDailyRoster = notifyDailyRoster;
    }
}
