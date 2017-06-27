/**
 * 
 */
package model;

/**
 * @author zhangle
 *
 */
public class Xor extends DualOperandGate {

    /* (non-Javadoc)
     * @see model.BaseOp#calculate()
     */
    @Override
    public boolean calculate() {
        return in1.getValue() ^ in2.getValue();
    }

    @Override
    public void reset() {
        this.in1.setReady(false);
        this.in2.setReady(false);
        this.out.setReady(false);
    }

}
