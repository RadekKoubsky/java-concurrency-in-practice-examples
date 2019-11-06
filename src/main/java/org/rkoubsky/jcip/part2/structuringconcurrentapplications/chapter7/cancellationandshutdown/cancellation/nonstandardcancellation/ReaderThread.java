package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter7.cancellationandshutdown.cancellation.nonstandardcancellation;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * Encapsulating nonstandard cancellation in a Thread by overriding interrupt
 *
 * Not all blocking methods or blocking mechanisms are responsive
 * to interruption; if a thread is blocked performing synchronous
 * socket I/O or waiting to acquire an intrinsic lock, interruption
 * has no effect other than setting thread's interrupted status.
 *
 * ReaderThread shows a technique for encapsulating nonstandard cancellation.
 * It manages a single socket connection, reading synchronously from the
 * socket and passing any data received to processBuffer.
 */
public class ReaderThread extends Thread {
    private static final int BUFSZ = 512;
    private final Socket socket;
    private final InputStream in;

    public ReaderThread(final Socket socket) throws IOException {
        this.socket = socket;
        this.in = socket.getInputStream();
    }

    /**
     * To facilitate terminating a user connection or shutting down the server,
     * ReaderThread overrides "interrupt" to both deliver a standard interrupt
     * and close the underlying socket; thus interrupting the a ReaderThread
     * makes it stop what it is doing whether it is blocked in "read" or in
     * an interruptible blocking methods.
     */
    @Override
    public void interrupt() {
        try {
            this.socket.close();
        } catch (final IOException ignored) {
        } finally {
            super.interrupt();
        }
    }

    @Override
    public void run() {
        try {
            final byte[] buf = new byte[BUFSZ];
            while (true) {
                final int count = this.in.read(buf);
                if (count < 0) {
                    break;
                } else if (count > 0) {
                    this.processBuffer(buf, count);
                }
            }
        } catch (final IOException e) { /* Allow thread to exit */
        }
    }

    public void processBuffer(final byte[] buf, final int count) {
    }
}
