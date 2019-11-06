package org.rkoubsky.jcip.part2.structuringconcurrentapplications.chapter6.taskexecution.findingparalelism.pagerenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * Rendering page elements sequentially
 *
 * SingleThreadRenderer takes still sequential approach that
 * involves rendering the text elements first, leaving
 * rectangular placeholders for the images, and after
 * completing the initial pass on the document, going
 * back and downloading the images and drawing them into
 * associated placeholders.
 *
 * Downloading an image involves waiting for I/O to complete, and
 * during this time the CPU does little work.Se the sequential approach
 * may under-utilize CPU, and also makes the user wait longer than
 * necessary to see the finished page. We can achieve better
 * utilization and responsiveness by breaking the problem into
 * independent tasks that can execute concurrently.
 */
public abstract class SingleThreadRenderer {
    void renderPage(final CharSequence source) {
        this.renderText(source);
        final List<ImageData> imageData = new ArrayList<ImageData>();
        for (final ImageInfo imageInfo : this.scanForImageInfo(source)) {
            imageData.add(imageInfo.downloadImage());
        }
        for (final ImageData data : imageData) {
            this.renderImage(data);
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
