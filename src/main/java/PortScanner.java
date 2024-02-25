import org.apache.commons.cli.*;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PortScanner {
    private static final int MAX_THREADS = 100;
    private static final int TIMEOUT = 100;

    public static void main(String[] args) {
        try (ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS)) {

            Options options = new Options();
            options.addOption("h", "host", true, "Target host");
            options.addOption("p", "port", true, "Target port");

            CommandLineParser parser = new DefaultParser();
            try {
                CommandLine cmd = parser.parse(options, args);

                String host = cmd.getOptionValue("host");

                if (cmd.getOptionValue("port") == null) {
                    Instant startInstance = Instant.now();
                    for (int port = 1; port <= 65535; port++) {
                        executor.submit(new PortScanTask(host, port));
                    }
                    executor.shutdown();

                    try {
                        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Duration.between(startInstance, Instant.now()));
                } else {
                    int port = Integer.parseInt(cmd.getOptionValue("port"));

                    if (isPortOpen(host, port)) {
                        printOpenedPort(port);
                    }
                }


            } catch (ParseException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private record PortScanTask(String host, int port) implements Runnable {

        @Override
        public void run() {
            if (isPortOpen(host, port)) {
                printOpenedPort(port);
            } else {
                System.out.println("Port " + port + " is closed");
            }
        }
    }

    private static boolean isPortOpen(String host, int port) {
        try (Socket socket = new Socket()) {
            System.out.println("Scanning host: " + host + " port: " + port);
            socket.connect(new InetSocketAddress(host, port), TIMEOUT);
            System.out.flush();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static void printOpenedPort(int port) {
        System.out.println("Port: " + port + " is open");
    }
}
