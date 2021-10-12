package lsieun.trove;

/**
 * Interface for functions that accept and return one int primitive.
 *
 * Created: Mon Nov  5 22:19:36 2001
 *
 * @author Eric D. Friedman
 */

public interface TIntFunction {
    /**
     * Execute this function with <tt>value</tt>
     *
     * @param value an <code>int</code> input
     * @return an <code>int</code> result
     */
    int execute(int value);
}// TIntFunction