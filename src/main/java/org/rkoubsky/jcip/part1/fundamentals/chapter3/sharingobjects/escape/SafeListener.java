package org.rkoubsky.jcip.part1.fundamentals.chapter3.sharingobjects.escape;

/**
 * Using a factory method to prevent the this reference from escaping during construction.
 *
 * <NOTE_escape_during_construction>
 *     When an object creates a thread from its constructor,
 *      it almost always shares its "this" reference with the new thread:
 *
 *      1. Explicitly - passing it to the constructor
 *
 *      2. Implicitly - the Thread or Runnable is an inner class of the owning object
 *
 *     !!! Do not allow the "this" reference to escape during construction !!!
 *
 * </NOTE_escape_during_construction>
 *
 * Solution for escaping this reference during construction:
 *
 * If you are tempted to register an event listener or start a thread from a constructor,
 * you can avoid the improper construction by using a private constructor and a public
 * factory method.
 */
public class SafeListener {
    private final EventListener listener;

    private SafeListener() {
        this.listener = new EventListener() {
            @Override
            public void onEvent(final Event e) {
                SafeListener.this.doSomething(e);
            }
        };
    }

    public static SafeListener newInstance(final EventSource source) {
        final SafeListener safe = new SafeListener();
        source.registerListener(safe.listener);
        return safe;
    }

    void doSomething(final Event e) {
    }


    interface EventSource {
        void registerListener(EventListener e);
    }

    interface EventListener {
        void onEvent(Event e);
    }

    interface Event {
    }
}
