package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter6.taskexecution.executingtasksinthreads;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Sequential web server
 *
 * SingleThreadedWebServer processes its tasks - HTTP requests arriving on port 80- sequentially.
 */

public class SingleThreadWebServer {
    public static void main(final String[] args) throws IOException {
        final ServerSocket socket = new ServerSocket(80);
        /**
         * The main thread alternates between accepting connections and processing
         * the associated request.
         */
        while (true) {
            final Socket connection = socket.accept();
            handleRequest(connection);
        }
    }

    private static void handleRequest(final Socket connection) {
        // request-handling logic here
    }
}
