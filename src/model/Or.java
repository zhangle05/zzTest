/**
 * 
 */
package model;

/**
 * @author zhangle
 *
 */
public class Or extends DualOperandGate {

    /* (non-Javadoc)
     * @see model.BaseOp#calculate()
     */
    @Override
    public boolean calculate() {
        if (in1.isReady() && in2.isReady()) {
            out.setValue(in1.getValue() || in2.getValue());
            out.setReady(true);
            return true;
        }
        return false;
    }

    @Override
    public void reset() {
        this.in1.setReady(false);
        this.in2.setReady(false);
        this.out.setReady(false);
    }

}
