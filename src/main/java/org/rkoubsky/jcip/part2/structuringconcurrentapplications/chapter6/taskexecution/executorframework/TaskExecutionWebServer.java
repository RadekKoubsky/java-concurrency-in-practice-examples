package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter6.taskexecution.executorframework;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Web server using a thread pool
 *
 * <NOTE_executor_interface>
 *
 *     Executor framework forms the basis for a flexible and powerful framework
 *     for asynchronous task execution that supports a wide variety of task
 *     execution policies.
 *
 *     It provides a standard means of decoupling task submission from task execution.
 *
 *     Executor is based on producer-consumer pattern, where activities that submit
 *     tasks are producers (producing units of work to be done) and the threads
 *     that executes tasks are consumers (consuming those units of work)
 *
 *     TIP: Using an Executor is usually the easies path to implementing
 *     a producer-consumer design in your application.
 *
 * </NOTE_executor_interface>
 *
 * <NOTE_task_execution_policy>
 *
 *     Whenever you see code of the form:
 *        new Thread(runnable).start
 *     and you think you might at some point want a more flexible
 *     execution policy, seriously consider replacing it with the
 *     use of an Executor.
 *
 * </NOTE_task_execution_policy>
 *
 * In TaskExecutionWebServer, submission of the request-handling task is decoupled
 * from its execution using an Executor implementation.
 */
public class TaskExecutionWebServer {
    private static final int NTHREADS = 100;
    private static final Executor exec = Executors.newFixedThreadPool(NTHREADS);

    public static void main(final String[] args) throws IOException {
        final ServerSocket socket = new ServerSocket(80);
        while (true) {
            final Socket connection = socket.accept();
            final Runnable task = new Runnable() {
                @Override
                public void run() {
                    handleRequest(connection);
                }
            };
            exec.execute(task);
        }
    }

    private static void handleRequest(final Socket connection) {
        // request-handling logic here
    }
}
