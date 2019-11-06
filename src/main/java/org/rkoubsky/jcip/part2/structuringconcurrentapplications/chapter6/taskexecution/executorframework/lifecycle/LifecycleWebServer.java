package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter6.taskexecution.executorframework.lifecycle;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Web server with shutdown support
 *
 * LifecycleWebServer extends the our web server with lifecycle support.
 * It can shut down in two ways: programmatically by calling "stop", and
 * through a client request by sending the web server a specially formatted
 * HTTP request.
 *
 * <NOTE_shutdown>
 *
 *     It is common to follow the "shutdown" method () immediately by
 *     the "awaitTermination" method, creating the effect of synchronously
 *     shutting down the ExecutorService.
 *
 * </NOTE_shutdown>
 */
public class LifecycleWebServer {
    private final ExecutorService exec = Executors.newCachedThreadPool();

    public void start() throws IOException {
        final ServerSocket socket = new ServerSocket(80);
        while (!this.exec.isShutdown()) {
            try {
                final Socket conn = socket.accept();
                this.exec.execute(new Runnable() {
                    @Override
                    public void run() {
                        LifecycleWebServer.this.handleRequest(conn);
                    }
                });
            } catch (final RejectedExecutionException e) {
                if (!this.exec.isShutdown()) {
                    this.log("task submission rejected", e);
                }
            }
        }
    }


    public void stop() {
        /**
         * The "shutdown" method initiates a graceful shutdown: no new tasks
         * are accepted but previously submitted tasks are allowed to complete -
         * including those that have not begun execution.
         *
         * The "shutdownNow" initiates the abrupt shutdown: it attempts
         * to cancel outstanding tasks and does not start any tasks that are
         * queued but not begun.
         */
        this.exec.shutdown();
    }

    private void log(final String msg, final Exception e) {
        Logger.getAnonymousLogger().log(Level.WARNING, msg, e);
    }

    void handleRequest(final Socket connection) {
        final Request req = this.readRequest(connection);
        if (this.isShutdownRequest(req)) {
            this.stop();
        } else {
            this.dispatchRequest(req);
        }
    }

    interface Request {
    }

    private Request readRequest(final Socket s) {
        return null;
    }

    private void dispatchRequest(final Request r) {
    }

    private boolean isShutdownRequest(final Request r) {
        return false;
    }
}
