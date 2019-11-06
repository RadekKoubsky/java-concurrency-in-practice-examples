package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter6.taskexecution.findingparalelism.pagerenderer;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static org.rkoubsky.jcip.part1.fundamentals.chapter5.buildingblocks.synchronizers.futuretask.LaunderThrowable.launderThrowable;

/**
 * Using CompletionService to render page elements as they become available
 *
 * <NOTE_completion_service>
 *
 *     CompletionService combines the functionality of an Executor and a
 *     BlockingQueue. You can submit Callable tasks to it for execution, and
 *     use the queue-like methods "take" and "poll" to retrieve completed result,
 *     packaged as Futures, as they become available.
 *
 *     When a task is submitted using CompletionService, it is wrapped with a
 *     java.util.concurrent.ExecutorCompletionService.QueueingFuture, a subclass
 *     of FutureTask that overrides the "done" method to place the result onto
 *     the BlockingQueue. The "take" and "poll" methods delegate to the BlockingQueue,
 *     blocking if results are not yet available.
 *
 * </NOTE_completion_service>
 *
 * We can use a CompletionService to improve the performance of the page renderer
 * in two ways: shorter total runtime and responsiveness.
 *
 * 1) Shorter total runtie
 * We can create a separate task for downloading "each" image and execute them in
 * a thread pool, turning the sequential download into parallel one: this reduces the
 * time to download all the images
 *
 * 2) Better responsiveness
 * By fetching results from CompletionService and rendering each image as soon as it
 * is available, we can give the user a more dynamic and responsive UI.
 *
 * <NOTE_completion_service_vs_future>
 *
 *     When used i this way, a CompletionService acts as a handle for a batch of computations
 *     in much the same way that a Future acts as a handle for a single computation.
 *     
 *
 * </NOTE_completion_service_vs_future>
 */
public abstract class FinalRenderer {
    private final ExecutorService executor;

    FinalRenderer(final ExecutorService executor) {
        this.executor = executor;
    }

    void renderPage(final CharSequence source) {
        final List<ImageInfo> info = this.scanForImageInfo(source);
        final CompletionService<ImageData> completionService =
                new ExecutorCompletionService<ImageData>(this.executor);
        for (final ImageInfo imageInfo : info) {
            completionService.submit(new Callable<ImageData>() {
                @Override
                public ImageData call() {
                    return imageInfo.downloadImage();
                }
            });
        }

        this.renderText(source);

        try {
            for (int t = 0, n = info.size(); t < n; t++) {
                final Future<ImageData> f = completionService.take();
                final ImageData imageData = f.get();
                this.renderImage(imageData);
            }
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (final ExecutionException e) {
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
