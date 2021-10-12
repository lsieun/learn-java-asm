package lsieun.trove;

import java.io.Serializable;

/**
 * Interface to support pluggable hashing strategies in maps and sets.
 * Implementors can use this interface to make the trove hashing
 * algorithms use an optimal strategy when computing hashcodes.
 *
 * Created: Sun Nov  4 08:56:06 2001
 *
 * @author Eric D. Friedman
 */
public interface TIntHashingStrategy extends Serializable {
    /**
     * Computes a hash code for the specified int.  Implementors
     * can use the int's own value or a custom scheme designed to
     * minimize collisions for a known set of input.
     *
     * @param val int for which the hashcode is to be computed
     * @return the hashCode
     */
    int computeHashCode(int val);
} // TIntHashingStrategy