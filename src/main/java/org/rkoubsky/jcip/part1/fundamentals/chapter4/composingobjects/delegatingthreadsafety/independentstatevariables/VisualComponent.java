package org.rkoubsky.jcip.part1.fundamentals.chapter4.composingobjects.delegatingthreadsafety.independentstatevariables;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Delegating thread safety to multiple underlying state variables
 *
 * <NOTE_multiple_state_variables>
 *
 *      We can delegate thread safety to more than one underlying state variable
 *      as long as those underlying state variables are independent, meaning that
 *      composite class does not impose any invariants involving multiple state variables.
 *
 * </NOTE_multiple_state_variables>
 *
 */
public class VisualComponent {
    /**
     * There is no relationship between the set of mouse listeners and key listeners;
     * the two are independent, and therefore the VisualComponent can delegate
     * its thread safety obligations to the two underlying thread-safe lists.
     *
     * <NOTE_copy_on_write_array_list>
     *     This is a thread safe list implementation particularly suited for managing listeners lists.
     * </NOTE_copy_on_write_array_list>
     */
    private final List<KeyListener> keyListeners
            = new CopyOnWriteArrayList<KeyListener>();
    private final List<MouseListener> mouseListeners
            = new CopyOnWriteArrayList<MouseListener>();

    public void addKeyListener(final KeyListener listener) {
        this.keyListeners.add(listener);
    }

    public void addMouseListener(final MouseListener listener) {
        this.mouseListeners.add(listener);
    }

    public void removeKeyListener(final KeyListener listener) {
        this.keyListeners.remove(listener);
    }

    public void removeMouseListener(final MouseListener listener) {
        this.mouseListeners.remove(listener);
    }
}
