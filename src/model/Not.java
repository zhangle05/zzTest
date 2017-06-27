/**
 * 
 */
package model;

/**
 * @author zhangle
 *
 */
public class Not extends SingleOperandGate {

    /* (non-Javadoc)
     * @see model.BaseOp#calculate()
     */
    @Override
    public boolean calculate() {
        if (in.isReady()) {
            out.setValue(!in.getValue());
            out.setReady(true);
            return true;
        }
        return false;
    }

    @Override
    public void reset() {
        this.in.setReady(false);
        this.out.setReady(false);
    }

}
