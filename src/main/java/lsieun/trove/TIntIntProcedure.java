package lsieun.trove;

public interface TIntIntProcedure {

    /**
     * Executes this procedure. A false return value indicates that
     * the application executing this procedure should not invoke this
     * procedure again.
     *
     * @param a an <code>int</code> value
     * @param b an <code>int</code> value
     * @return true if additional invocations of the procedure are
     * allowed.
     */
    boolean execute(int a, int b);
}// TIntIntProcedure