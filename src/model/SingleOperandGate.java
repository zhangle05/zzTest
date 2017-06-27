/**
 * 
 */
package model;

/**
 * @author zhangle
 *
 */
public abstract class SingleOperandGate extends BaseGate {

    protected Pin in = new Pin();

    /**
     * @return the pin
     */
    public Pin getPin() {
        return in;
    }

    /**
     * @param pin
     *            the pin to set
     */
    public void setPin(Pin pin) {
        this.in = pin;
    }

}
