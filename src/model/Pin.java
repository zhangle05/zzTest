/**
 * 
 */
package model;

/**
 * @author zhangle
 *
 */
public class Pin {
    private boolean value;
    private boolean ready;

    /**
     * @return the input
     */
    public boolean getValue() {
        return value;
    }

    /**
     * @param input the input to set
     */
    public void setValue(boolean value) {
        this.value = value;
    }

    /**
     * @return the ready
     */
    public boolean isReady() {
        return ready;
    }

    /**
     * @param ready the ready to set
     */
    public void setReady(boolean ready) {
        this.ready = ready;
    }

}
