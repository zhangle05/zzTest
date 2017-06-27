/**
 * 
 */
package model;

/**
 * @author zhangle
 *
 */
public class Wire {

    private Pin in;
    private Pin out;

    /**
     * default constructor
     */
    public Wire() {
    }

    /**
     * constructor with params
     *
     * @param in
     * @param out
     */
    public Wire(Pin in, Pin out) {
        this.in = in;
        this.out = out;
    }

    /**
     * @return the in
     */
    public Pin getIn() {
        return in;
    }

    /**
     * @param in
     *            the in to set
     */
    public void setIn(Pin in) {
        this.in = in;
    }

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
