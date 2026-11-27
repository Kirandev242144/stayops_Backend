package stayops_api.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class StayOpsWebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(StayOpsWebSocketHandler.class);
    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        logger.info("[StayOps WebSocket] Live session connected: {} (Total active: {})", session.getId(), sessions.size());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            String payload = message.getPayload();
            if (payload.contains("\"PING\"")) {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(Map.of(
                        "type", "PONG",
                        "timestamp", System.currentTimeMillis()
                ))));
            }
        } catch (Exception e) {
            logger.warn("[StayOps WebSocket] Error parsing incoming message: {}", e.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        logger.info("[StayOps WebSocket] Live session disconnected: {} (Total active: {})", session.getId(), sessions.size());
    }

    public void broadcast(String topic, String event, Object data) {
        if (sessions.isEmpty()) return;

        try {
            Map<String, Object> messageMap = Map.of(
                    "topic", topic != null ? topic : "SYSTEM",
                    "event", event != null ? event : "UPDATE",
                    "data", data != null ? data : Map.of(),
                    "timestamp", System.currentTimeMillis()
            );

            String jsonPayload = objectMapper.writeValueAsString(messageMap);
            TextMessage textMessage = new TextMessage(jsonPayload);

            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    try {
                        session.sendMessage(textMessage);
                    } catch (IOException e) {
                        logger.error("[StayOps WebSocket] Failed to send message to session {}: {}", session.getId(), e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("[StayOps WebSocket] Error building broadcast JSON payload: {}", e.getMessage());
        }
    }
}
