import org.apache.commons.cli.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class PortScanner {
    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("h", "host", true, "Target host");
        options.addOption("p", "port", true, "Target port");

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);

            String host = cmd.getOptionValue("host");
            int port = Integer.parseInt(cmd.getOptionValue("port"));

            if (isPortOpen(host, port)) {
                System.out.println("Port: " + port + " is open");
            }
        } catch (ParseException e) {
            System.err.println(e.getMessage());
        }
    }

    private static boolean isPortOpen(String host, int port) {
        try (Socket socket = new Socket()) {
            System.out.println("Scanning host: " + host + " port: " + port);
            socket.connect(new InetSocketAddress(host, port), 1000);
            System.out.flush();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
