package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter6.taskexecution.findingparalelism.pagerenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.rkoubsky.jcip.part1.fundamentals.chapter5.buildingblocks.synchronizers.futuretask.LaunderThrowable.launderThrowable;

/**
 * Waiting for image download with \Future
 *
 * <NOTE_callable>
 *
 *     Many tasks are effectively deferred computations - executing a
 *     database query, fetching a resource over the network, or computing
 *     a complicated function.
 *
 *     For these type of tasks, Callable is a better abstraction:
 *     It expects that the main entry point, "call", will return
 *     a value and anticipates that it might throw an exception.
 *
 * </NOTE_callable>
 *
 * <NOTE_tasks_phases>
 *
 *     The lifecycle of a task executed by an Executor has four phases:
 *     created, submitted, started, and completed.
 *
 *     In Executor framework, Tasks that have been submitted, but not
 *     yet started can always be cancelled, and tasks that have been
 *     started can sometimes be cancelled if they are responsive to cancellation.
 *
 * </NOTE_tasks_phases>
 *
 * FutureRenderer allows the text to be rendered concurrently with downloading
 * the image data. This is an improvement in that user sees a result quickly and
 * it exploits some parallelism, but we can do considerable better.
 * There is no need for users to wait for all images to be downloaded; they
 * would probably prefer to see individual images drawn as become available.
 *
 * <NOTE_limitations_of_parallelizing_heterogenous_tasks>
 *
 *     In the example above, we tried to execute two different types of tasks in parallel -
 *     downloading the images and rendering the page. But obtaining significant performance
 *     improvements by trying to parallelize sequential heterogeneous tasks can be tricky.
 *
 *     If task 'A' takes 10 times as long as 'B', you've speeded up the total process by 9 %.
 *
 *     Dividing a task always involves coordination overhead; it must be more than compensated
 *     by productivity improvements due to parallelism.
 *
 *     FutureRenderer uses two tasks: rendering text and downloading images.
 *     If rendering text is much faster than downloading images, as is entirely
 *     possible, the resulting performance is not much different from the sequential
 *     version, but the code is a lot more complicated.
 *
 *     <NOTE_homogeneous_tasks>
 *
 *         The real performance payoff of dividing a program's workload into tasks
 *         comes when there are a large number of independent, homogeneous tasks
 *         that can be processed concurrently.
 *     </NOTE_homogeneous_tasks>
 *
 * </NOTE_limitations_of_parallelizing_heterogenous_tasks>
 *
 *
 */
public abstract class FutureRenderer {
    private final ExecutorService executor = Executors.newCachedThreadPool();

    void renderPage(final CharSequence source) {
        final List<ImageInfo> imageInfos = this.scanForImageInfo(source);

        // We create a Callable to download the images, and submit it to the ExecutorService.
        final Callable<List<ImageData>> task =
                new Callable<List<ImageData>>() {
                    @Override
                    public List<ImageData> call() {
                        final List<ImageData> result = new ArrayList<ImageData>();
                        for (final ImageInfo imageInfo : imageInfos) {
                            result.add(imageInfo.downloadImage());
                        }
                        return result;
                    }
                };

        final Future<List<ImageData>> future = this.executor.submit(task);
        this.renderText(source);

        try {
            final List<ImageData> imageData = future.get();
            for (final ImageData data : imageData) {
                this.renderImage(data);
            }
        }

        /**
         * The exception handling code surrounding the Future.get
         * deals with two possible problems:
         *
         *  First catch - the thread calling "get" was interrupted before
         *    the results were available
         */
        catch (final InterruptedException e) {
            // Re-assert the thread's interrupted status
            Thread.currentThread().interrupt();
            // We don't need the result, so cancel the task too
            future.cancel(true);
        }
        // Second catch - the task encountered an Exception
        catch (final ExecutionException e) {
            throw launderThrowable(e.getCause());
        }
    }

    interface ImageData {
    }

    interface ImageInfo {
        ImageData downloadImage();
    }

    abstract void renderText(CharSequence s);

    abstract List<ImageInfo> scanForImageInfo(CharSequence s);

    abstract void renderImage(ImageData i);
}
