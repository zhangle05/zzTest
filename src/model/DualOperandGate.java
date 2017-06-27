/**
 * 
 */
package model;

/**
 * @author zhangle
 *
 */
public abstract class DualOperandGate extends BaseGate {

    protected Pin in1 = new Pin();
    protected Pin in2 = new Pin();

    /**
     * @return the in1
     */
    public Pin getIn1() {
        return in1;
    }

    /**
     * @param in1
     *            the in1 to set
     */
    public void setIn1(Pin in1) {
        this.in1 = in1;
    }

    /**
     * @return the in2
     */
    public Pin getIn2() {
        return in2;
    }

    /**
     * @param in2
     *            the in2 to set
     */
    public void setIn2(Pin in2) {
        this.in2 = in2;
    }
}
