import model.ChatMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/*
 * > String vs StringBuilder vs StringBuffer <
 * https://www.journaldev.com/538/string-vs-stringbuffer-vs-stringbuilder
 */

public final class ChatServer {

    /**
     * Logger
     */
    private static final Logger log = LoggerFactory.getLogger(ChatServer.class);

    /**
     * DB's Representation
     */
    private static final List<ChatMessage> messages = new ArrayList<>();

    /**
     * HTML File path
     */
    private static String PATH = "./src/index.html";

    /**
     * Listen port
     */
    private final int PORT;

    // Constructor
    public ChatServer(int PORT) {

        if(PORT < 1024 || PORT > 65535) {
            throw new IllegalArgumentException("Error in port number");
        }
        this.PORT = PORT;
    }

    /**
     * Main
     */
    public static void main(String[] args) throws IOException {

        log.debug("Main start ...");

        final ChatServer chatServer = new ChatServer(8080);
        chatServer.begin();
    }

    /**
     * Server start
     */
    private void begin() throws IOException {

        log.debug("Starting the server in port: [{}]", PORT);

        final ServerSocket server = new ServerSocket(PORT);

        while(true) {

            try(Socket socket = server.accept()) {
                log.debug("_____________________________________________________________________________________");
                log.debug("Connected from: {}", socket);

                ChatController cnx = new ChatController(socket);
                cnx.run();

            }catch(IOException e) {
                log.error("Error: {}", e);
                throw e;
            }
        }
    }

    /**
     * Add the message to the list - Checks if its not null
     * @param chatMessage - Message to add
     * @return
     */
    public static ChatMessage add(final ChatMessage chatMessage){

        if(chatMessage == null)
            throw new IllegalArgumentException("Null data cant be insert");

        synchronized (messages){
            messages.add(chatMessage);
        }

        return chatMessage;
    }

    /**
     * HTML Web page
     * @return - HTML inside a String variable
     */
    public static String htmlFile() {

        File file = new File(PATH);
        StringBuffer str = new StringBuffer();

        try(Scanner scanner = new Scanner(file).useDelimiter("\\A")) {

            // Reads line by line until there's no another input lines in the file
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                str.append(line);

                if(line.contains("chat_interface")) {

                    str.append("<textarea rows='20' cols='75' readonly>");
                    for(ChatMessage cht : messages) {
                        str.append(cht.getUsername()).append(": ").append(cht.getMessage()).append("\n");
                    }
                    str.append("</textarea>");
                }
            }

        }catch(FileNotFoundException e) {
            log.error("Error: {}", e);
        }

        return str.toString();
    }
}