package org.rkoubsky.jcip.part4.advancedtopics.chapter16.javamemorymodel.publication;

import net.jcip.annotations.ThreadSafe;

/**
 * Lazy initialization holder class idiom
 *
 * Eager initialization can be combined with the JVM's lazy class loading
 * to create a lazy initialization technique that does not require
 * synchronization on the common code path.
 *
 * Use the Lazy Initialization Holder Idiom instead of Double Checked Locking
 * as it offers same benefits and is easier to understand
 */
@ThreadSafe
public class LazyInitializationHolderIdiom {
    private static class ResourceHolder {
        static {
            System.out.println("Initializing static resource in ResourceHolder.");
        }
        public static Resource resource = new Resource();
    }

    public static Resource getResource() {
        System.out.println("In getResource().");
        return ResourceHolder.resource;
    }

    /**
     * The main method starts before the resource is statically initialized in the ResourceHolder
     * class, thus the resource is lazily loaded.
     */
    public static void main(final String[] args) {
        System.out.println("Start of main.");
        getResource();
        System.out.println("End of main.");
    }

    static class Resource {
    }
}
