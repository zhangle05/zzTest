/**
 * 
 */
package model;

/**
 * @author zhangle
 *
 */
public abstract class BaseGate {

    protected Pin out = new Pin();

    public abstract boolean calculate();

    public abstract void reset();

    /**
     * @return the out
     */
    public Pin getOut() {
        return out;
    }

    /**
     * @param out
     *            the out to set
     */
    public void setOut(Pin out) {
        this.out = out;
    }
}
