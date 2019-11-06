package org.rkoubsky.jcip.part1.fundamentals.chapter3.sharingobjects.threadconfinement.stackconfinement;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Thread confinement of local primitive and reference variables.
 *
 * Stack confinement is a special case of thread confinement in which
 * an object can only be reached trough local variables.
 */
public class Animals {
    private Ark ark;
    private Species species;
    private Gender gender;

    public int loadTheArk(final Collection<Animal> candidates) {
        final SortedSet<Animal> animals;
        int numPairs = 0;
        Animal candidate = null;

        /** animals confined to method, don't let them escape!
         *
         * Using a non-thread-safe object in a within-thread context is still thread-safe.
         * However, the design requirement that object be confined to the executing thread/not-thread-safe,
         * often exists only in the head of the developer when the code is written.
         *
         * If the assumption of within-thread usage is not clearly documented, future maintainers
         * might mistakenly allow the object to escape.
         */
        animals = new TreeSet<Animal>(new SpeciesGenderComparator());
        animals.addAll(candidates);
        for (final Animal a : animals) {
            if (candidate == null || !candidate.isPotentialMate(a)) {
                candidate = a;
            } else {
                this.ark.load(new AnimalPair(candidate, a));
                ++numPairs;
                candidate = null;
            }
        }
        /**
         * There is no way to obtain a reference to a primitive variable, so the language semantics
         * ensure that primitive local variables are always stack confined.
         */
        return numPairs;
    }

}
