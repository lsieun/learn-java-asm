package lsieun.trove;

public class TIntIntIterator extends TPrimitiveIterator {
    /**
     * the collection being iterated over
     */
    private final TIntIntHashMap _map;

    /**
     * Creates an iterator over the specified map
     */
    public TIntIntIterator(TIntIntHashMap map) {
        super(map);
        _map = map;
    }

    /**
     * Moves the iterator forward to the next entry in the underlying map.
     *
     * @throws java.util.NoSuchElementException
     *          if the iterator is already exhausted
     */
    public void advance() {
        moveToNextIndex();
    }

    /**
     * Provides access to the key of the mapping at the iterator's position.
     * Note that you must <tt>advance()</tt> the iterator at least once
     * before invoking this method.
     *
     * @return the key of the entry at the iterator's current position.
     */
    public int key() {
        return _map._set[_index];
    }

    /**
     * Provides access to the value of the mapping at the iterator's position.
     * Note that you must <tt>advance()</tt> the iterator at least once
     * before invoking this method.
     *
     * @return the value of the entry at the iterator's current position.
     */
    public int value() {
        return _map._values[_index];
    }

    /**
     * Replace the value of the mapping at the iterator's position with the
     * specified value. Note that you must <tt>advance()</tt> the iterator at
     * least once before invoking this method.
     *
     * @param val the value to set in the current entry
     * @return the old value of the entry.
     */
    public int setValue(int val) {
        int old = value();
        _map._values[_index] = val;
        return old;
    }
}// TIntIntIterator