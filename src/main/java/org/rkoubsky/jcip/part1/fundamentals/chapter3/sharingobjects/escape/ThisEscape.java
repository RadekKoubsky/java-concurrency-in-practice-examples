package org.rkoubsky.jcip.part1.fundamentals.chapter3.sharingobjects.escape;

/**
 * Implicitly allowing the this reference to escape. Don't do this.
 */
public class ThisEscape {
    /**
     * Another mechanism an object can be published: publishing an inner class instance
     *
     * When ThisEscape publishes the EventListener, it implicitly publishes the enclosing
     * ThisEscape instance as well, because inner class instances contain a hidden reference to
     * the enclosing instance - this is allowed by "ThisEscape.this" which is "qualified this" expression
     * defined in Java Spec, it allows to refer to any lexically enclosing instance.
     *
     */
    public ThisEscape(final EventSource source) {
        source.registerListener(new EventListener() {
            @Override
            public void onEvent(final Event e) {
                // WARN The "qualified this" expression - "ThisEscape.this" publishes the ThisEscape object.
                ThisEscape.this.doSomething(e);
            }
        });
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
