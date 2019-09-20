package model;

import java.time.LocalDateTime;

public final class ChatMessage {

    /**
     * Time message receiver by the server
     */
    private final LocalDateTime timestamp;

    /**
     * Username
     */
    private final String username;

    /**
     * Username message
     */
    private final String message;

    // Constructor
    public ChatMessage(LocalDateTime timestamp, String username, String message) {
        this.timestamp = timestamp;
        this.username = username;
        this.message = message;
    }

    /**
     * @return - Date
     */
    public LocalDateTime getDate() {
        return timestamp;
    }

    /**
     * @return - Username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return - Message
     */
    public String getMessage() {
        return message;
    }

}
