/**
 * 
 */
package model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangle
 *
 */
public class BaseUnit {

    List<Pin> inputs = new ArrayList<Pin>();
    List<Pin> outputs = new ArrayList<Pin>();
    List<BaseGate> gates = new ArrayList<BaseGate>();
    List<Wire> wires = new ArrayList<Wire>();

    /**
     * @return the inputs
     */
    public List<Pin> getInputs() {
        return inputs;
    }

    /**
     * @param input
     *            the input to add
     */
    public void addInput(Pin input) {
        this.inputs.add(input);
    }

    /**
     * @return the outputs
     */
    public List<Pin> getOutputs() {
        return outputs;
    }

    /**
     * @param output
     *            the output to add
     */
    public void addOutput(Pin output) {
        this.outputs.add(output);
    }

    /**
     * @return the gates
     */
    public List<BaseGate> getGates() {
        return gates;
    }

    /**
     * @param gate
     *            the gate to add
     */
    public void addGate(BaseGate gate) {
        this.gates.add(gate);
    }

    /**
     * @return the wires
     */
    public List<Wire> getWires() {
        return wires;
    }

    /**
     * @param wire
     *            the wire to add
     */
    public void addWire(Wire wire) {
        this.wires.add(wire);
    }

    public void setInputValues(List<Boolean> values) {
        if (values.size() != inputs.size()) {
            throw new IllegalArgumentException(
                    "value size does not match input size!");
        }
        for (int i = 0; i < inputs.size(); i++) {
            Pin p = inputs.get(i);
            p.setValue(values.get(i));
            p.setReady(true);
        }
    }

    public boolean calculate() {
        for (int i = 0; i < inputs.size(); i++) {
            Pin p = inputs.get(i);
            if (!p.isReady()) {
                System.err.println("input[" + i + "] is not ready!");
                return false;
            }
        }
        while (!done()) {
            for (Wire w : wires) {
                if (w.getIn().isReady()) {
                    w.getOut().setValue(w.getIn().getValue());
                    w.getOut().setReady(true);
                }
            }
            for (BaseGate g : gates) {
                g.calculate();
            }
        }
        return true;
    }

    public boolean done() {
        for (int i = 0; i < outputs.size(); i++) {
            Pin p = outputs.get(i);
            if (!p.isReady()) {
                return false;
            }
        }
        return true;
    }
    
    public void reset() {
        for (BaseGate g : gates) {
            g.reset();
        }
    }
}
