package stayops_api.websocket;

import org.springframework.stereotype.Service;

@Service
public class WebSocketBroadcaster {

    private final StayOpsWebSocketHandler webSocketHandler;

    public WebSocketBroadcaster(StayOpsWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    public void broadcastReservations(String event, Object data) {
        webSocketHandler.broadcast("RESERVATIONS", event, data);
        webSocketHandler.broadcast("BOOKING_UPDATED", event, data);
        webSocketHandler.broadcast("FRONTDESK", event, data);
    }

    public void broadcastFrontdesk(String event, Object data) {
        webSocketHandler.broadcast("FRONTDESK", event, data);
    }

    public void broadcastPettyCash(String event, Object data) {
        webSocketHandler.broadcast("PETTY_CASH", event, data);
        webSocketHandler.broadcast("FRONTDESK", event, data);
    }

    public void broadcastHousekeeping(String event, Object data) {
        webSocketHandler.broadcast("FRONTDESK", event, data);
        webSocketHandler.broadcast("HOUSEKEEPING", event, data);
    }

    public void broadcastGuestVerification(String event, Object data) {
        webSocketHandler.broadcast("GUEST_VERIFICATION", event, data);
        webSocketHandler.broadcast("FRONTDESK", event, data);
        webSocketHandler.broadcast("RESERVATIONS", event, data);
    }
}
