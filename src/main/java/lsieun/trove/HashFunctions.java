package lsieun.trove;

public final class HashFunctions {
    /**
     * Returns a hashcode for the specified value.
     *
     * @return  a hash code value for the specified value. 
     */
    public static int hash(double value) {
        long bits = Double.doubleToLongBits(value);
        return (int)(bits ^ (bits >>> 32));
        //return (int) Double.doubleToLongBits(value*663608941.737);
        //this avoids excessive hashCollisions in the case values are
        //of the form (1.0, 2.0, 3.0, ...)
    }

    /**
     * Returns a hashcode for the specified value.
     *
     * @return  a hash code value for the specified value. 
     */
    public static int hash(float value) {
        return Float.floatToIntBits(value*663608941.737f);
        // this avoids excessive hashCollisions in the case values are
        // of the form (1.0, 2.0, 3.0, ...)
    }

    /**
     * Returns a hashcode for the specified value. The hashcode is computed as
     * <blockquote><pre> 
     * 31^5*(d[0]*31^(n-1) + d[1]*31^(n-2) + ... + d[n-1])
     * </pre></blockquote>
     * using <code>int</code> arithmetic, where <code>d[i]</code> is
     * the <i>i</i>th digit of the value, counting from the right,
     * <code>n</code> is the number of decimal digits of the specified
     * value, and <code>^</code> indicates exponentiation.  (The hash
     * value of the value zero is zero.)
     *
     * @return  a hash code value for the specified value. 
     */
    public static int hash(int value) {
        //return value * 0x278DDE6D; // see cern.jet.random.engine.DRand
	
        return value;
	
        /*
          value &= 0x7FFFFFFF; // make it >=0
          int hashCode = 0;
          do hashCode = 31*hashCode + value%10;
          while ((value /= 10) > 0);

          return 28629151*hashCode; // spread even further; h*31^5
        */
    }

    /**
     * Returns a hashcode for the specified value. 
     *
     * @return  a hash code value for the specified value. 
     */
    public static int hash(long value) {
        return (int)(value ^ (value >> 32));

        /*
         * The hashcode is computed as
         * <blockquote><pre> 
         * 31^5*(d[0]*31^(n-1) + d[1]*31^(n-2) + ... + d[n-1])
         * </pre></blockquote>
         * using <code>int</code> arithmetic, where <code>d[i]</code> is the 
         * <i>i</i>th digit of the value, counting from the right, <code>n</code> is the number of decimal digits of the specified value,
         * and <code>^</code> indicates exponentiation.
         * (The hash value of the value zero is zero.)
 
         value &= 0x7FFFFFFFFFFFFFFFL; // make it >=0 (0x7FFFFFFFFFFFFFFFL==Long.MAX_VALUE)
         int hashCode = 0;
         do hashCode = 31*hashCode + (int) (value%10);
         while ((value /= 10) > 0);

         return 28629151*hashCode; // spread even further; h*31^5
        */
    }

    /**
     * Returns a hashcode for the specified object.
     *
     * @return  a hash code value for the specified object. 
     */
    public static int hash(Object object) {
        return object==null ? 0 : object.hashCode();
    }
}