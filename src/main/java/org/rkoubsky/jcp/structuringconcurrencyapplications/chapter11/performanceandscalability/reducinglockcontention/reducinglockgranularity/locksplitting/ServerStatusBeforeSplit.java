package org.rkoubsky.jcp.structuringconcurrencyapplications.chapter11.performanceandscalability.reducinglockcontention.reducinglockgranularity.locksplitting;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.HashSet;
import java.util.Set;

/**
 * Candidate for lock splitting
 *
 * The two state variables are completely independent; ServerStatus
 * could even be split into two separate classes with no loss of
 * functionality.
 */
@ThreadSafe
public class ServerStatusBeforeSplit {
    @GuardedBy("this") public final Set<String> users;
    @GuardedBy("this") public final Set<String> queries;

    public ServerStatusBeforeSplit() {
        this.users = new HashSet<String>();
        this.queries = new HashSet<String>();
    }

    public synchronized void addUser(final String u) {
        this.users.add(u);
    }

    public synchronized void addQuery(final String q) {
        this.queries.add(q);
    }

    public synchronized void removeUser(final String u) {
        this.users.remove(u);
    }

    public synchronized void removeQuery(final String q) {
        this.queries.remove(q);
    }
}
