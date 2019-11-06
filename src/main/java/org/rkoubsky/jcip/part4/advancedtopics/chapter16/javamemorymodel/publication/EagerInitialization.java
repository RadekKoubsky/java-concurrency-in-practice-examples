package org.rkoubsky.jcip.part4.advancedtopics.chapter16.javamemorymodel.publication;

import net.jcip.annotations.ThreadSafe;

/**
 * Eager initialization
 *
 * Using eager initialization eliminates the synchronization cost incurred
 * on each call to getInstance in {@link SafeLazyInitialization}
 *
 */
@ThreadSafe
public class EagerInitialization {
    static {
        System.out.println("Initializing static resource.");
    }
    private static Resource resource = new Resource();

    public static Resource getResource() {
        System.out.println("In getResource().");
        return resource;
    }

    /**
     * The main method starts after the resource is statically initialized,
     * thus the resource is eagerly loaded.
     */
    public static void main(final String[] args) {
        System.out.println("Start of main.");
        EagerInitialization.getResource();
        System.out.println("End of main.");
    }

    static class Resource {
    }
}
