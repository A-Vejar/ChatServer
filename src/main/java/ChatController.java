import model.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ChatController implements Runnable {

    /**
     * Logger
     */
    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    private Socket socket;

    public ChatController(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            ChatController.processConnection(socket);

        }catch(IOException e) {
            log.error("Error: {}", e);
        }
    }

    /**
     * Process the connection.
     * @param socket to use as source of data.
     */
    private static void processConnection(Socket socket) throws IOException {

        // Reading the InputStream
        final List<String> lines = readInputStreamByLines(socket);

        String request = lines.get(0);
        log.debug("Request: {}", request);

        final PrintWriter pw = new PrintWriter(socket.getOutputStream());

        // Get request
        if(request.contains("GET")) {

            pw.println("HTTP/1.1 200 OK");
            pw.println("Server: DSM-CHAT v0.0.1");
            pw.println("Content-Type: text/html; charset=UTF-8");
            pw.println();
            pw.println(ChatServer.htmlFile());
            pw.flush();

        // POST request
        }else if(request.contains("POST")) {

            bringMessage(lines);

            pw.println("HTTP/1.1 200 OK");
            pw.println("Server: DSM-CHAT v0.0.1");
            pw.println("Content-Type: text/html; charset=UTF-8");
            pw.println();
            pw.println(ChatServer.htmlFile());
            pw.flush();
        }

        log.debug("Ended process.");
    }

    /**
     * Read all the input stream.
     * @param socket to use to read.
     * @return all the string read.
     */
    private static List<String> readInputStreamByLines(Socket socket) throws IOException {

        // InputStream
        InputStream inS = socket.getInputStream();

        // The list of strings read from "InputStream"
        List<String> lines = new ArrayList<>();

        // BufferedReader
        BufferedReader bfR = new BufferedReader(new InputStreamReader(inS));

        // Scanner
        //Scanner scanner = new Scanner(inS).useDelimiter("\\A");

        log.debug("Reading the InputStream ...");

        while(true) {

            final String line = bfR.readLine();
            //final String line = scanner.nextLine();

            if(line.length() != 0) {

                lines.add(line);

            }else {

                // Stores the 'Content-Length' size coming from the InputStream' socket (Message sent)
                int contentSize = 0;

                for(int i = 0; i < lines.size(); i++){
                    //lines.get(i);
                    if (lines.get(i).contains("Content-Length:")) {
                        // Stores both sizes data: 'username' and 'message' as int
                        contentSize = Integer.parseInt(lines.get(i).substring(16));
                    }
                }

                // An user-message is found, a message is sent
                if(contentSize != 0) {

                    // StringBuffer
                    StringBuffer strBf = new StringBuffer(contentSize);

                    // Reads the 'message-data' char by char and then stores it inside the StringBuffer
                    for(int i = 0; i < contentSize; i++) {
                        //strBf.appendCodePoint(scanner.next().charAt(i));
                        strBf.appendCodePoint(bfR.read());
                    }

                    // HTML 'message-data' decoding
                    String msgData = URLDecoder.decode(String.valueOf(strBf), String.valueOf(StandardCharsets.UTF_8));
                    log.debug("Data content: {}", msgData);

                    lines.add(msgData);
                    //log.debug("Lines to add: {}", lines);
                    break;

                }else {
                    break;
                }
            }
        }
        return lines;
    }

    /**
     * Take the data-message content from the InputStream list
     * @param data - InputStream list
     * @return - ChatMessage instance
     */
    private static ChatMessage bringMessage(List<String> data) {

        String dataContent = data.get(data.size() - 1);
        String username = dataContent.substring(dataContent.indexOf('=') + 1, dataContent.indexOf('&'));
        String message = dataContent.substring(dataContent.indexOf('&') + 9);

        if (username.isEmpty() || message.isEmpty())
            return null;

        ChatMessage chatMessage = new ChatMessage(username, message);
        return ChatServer.add(chatMessage);
    }
}