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
            String command, response;

            while (true) {
                System.out.print("server> ");
                command = scanner.nextLine();

                if (command.equalsIgnoreCase("quit")) {
                    out.write("quit\n");
                    out.flush();
                    break;
                }

                out.write(command + "\n");
                out.flush();

                response = in.readLine();
                System.out.println(response);
            }

            scanner.close();
            socket.close();
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
