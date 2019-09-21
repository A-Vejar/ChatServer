package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.Getter;

public final class ChatMessage {

    /**
     * Time message receiver by the server
     */
    @Getter
    private final LocalDateTime timestamp;

    /**
     * Username
     */
    @Getter
    private final String username;

    /**
     * Username message
     */
    @Getter
    private final String message;

    // Constructor
    public ChatMessage(LocalDateTime timestamp, String username, String message) {
        this.timestamp = timestamp;
        this.username = username;
        this.message = message;
    }

    public static String getDate(LocalDateTime timestamp) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return formatter.format(timestamp);
    }
}
