package org.rkoubsky.jcip.part1.fundamentals.chapter3.sharingobjects.threadconfinement.threadlocal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Using ThreadLocal to ensure thread confinement.
 *
 * ThreadLocal allows you to associate a per-thread value with value-holding object.
 *
 * <NOTE_use_thread_locals_with_care>
 *
 * It is easy to abuse ThreadLocal by treating its thread confinement property
 * as a license to use global variables or as a means of creating “hidden” method arguments.
 *
 * Like global variables, thread-local variables can detract from reusability and introduce
 * hidden couplings among classes, and should therefore be used with care.
 *
 * </NOTE_use_thread_locals_with_care>
 *
 * Note from stack overflow:
 *
 * In Java, if you have a datum that can vary per-thread, your choices are to pass that datum around to every method
 * that needs (or may need) it, or to associate the datum with the thread. Passing the datum around everywhere may
 * be workable if all your methods already need to pass around a common "context" variable.
 *
 * If that's not the case, you may not want to clutter up your method signatures with an additional parameter.
 * In a non-threaded world, you could solve the problem with the Java equivalent of a global variable.
 * In a threaded word, the equivalent of a global variable is a thread-local variable.
 *
 * FINAL_NOTE:
 *
 * Avoid using ThreadLocals as you avoid using globals because they introduce couplings in your system.
 *
 */
public class ConnectionDispenser {
    static String DB_URL = "jdbc:mysql://localhost/mydatabase";

    private ThreadLocal<Connection> connectionHolder
            = new ThreadLocal<Connection>() {
        /**
         *
         * A new connection is created for each thread.
         */
        @Override
        public Connection initialValue() {
            try {
                return DriverManager.getConnection(DB_URL);
            } catch (final SQLException e) {
                throw new RuntimeException("Unable to acquire Connection, e");
            }
        };
    };

    /**
     * As stated in ThreadLocal.get() method:
     *
     * When a thread calls ThreadLocal.get() using this.connectionHolder.get(),
     * it also invoke ThreadLocal.initialValue() which creates a new connection (per each thread). The new connection
     * is associated only with the thread which means it is confined only to the executing thread.
     *
     * This means that every thread opens up a new database connection which can lead to
     * unrestricted amount of connections. That's why we should rather use connection pool from which we can
     * get connections in a controlled way.
     *
     */
    public Connection getConnection() {
        return this.connectionHolder.get();
    }
}
