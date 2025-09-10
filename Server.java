import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Server {

    private static ServerSocket serverSocket;
    private static Socket socket;
    private static BufferedReader in;
    private static BufferedWriter out;

    public static void main(String[] args) {
        try {
            int port = 9999;
            serverSocket = new ServerSocket(port);
            System.out.println("[LOG] Server started, waiting for connection on port " + port);

            socket = serverSocket.accept();
            System.out.println("[LOG] Connection established with " + socket.getInetAddress().getHostAddress());

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            Scanner scanner = new Scanner(System.in);
            String command;

            while (true) {
                System.out.print("server> ");
                command = scanner.nextLine();

                if (command.equalsIgnoreCase("quit")) {
                    System.out.println("[LOG] Sending quit command to client");
                    out.write("quit\n");
                    out.flush();
                    break;
                }

                System.out.println("[LOG] Sending command: " + command);
                out.write(command + "\n");
                out.flush();

                // Collect response until client sends [END_OF_OUTPUT]
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                System.out.println("[LOG] Waiting for client response...");
                while ((line = in.readLine()) != null) {
                    if (line.equals("[END_OF_OUTPUT]")) {
                        System.out.println("[LOG] Received end of output delimiter");
                        break;
                    }
                    responseBuilder.append(line).append("\n");
                }

                System.out.println("[LOG] Command output:\n" + responseBuilder.toString());
            }

            System.out.println("[LOG] Closing connections...");
            scanner.close();
            socket.close();
            serverSocket.close();
            System.out.println("[LOG] Server stopped.");
        } catch (Exception e) {
            System.err.println("[ERROR] Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
