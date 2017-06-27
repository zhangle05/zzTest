/**
 * 
 */
package model;

/**
 * @author zhangle
 *
 */
public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) {
        And a1 = new And();
        Or o = new Or();
        And a2 = new And();

        Wire w1 = new Wire(a1.getOut(), a2.getIn1());
        Wire w2 = new Wire(o.getOut(), a2.getIn2());

        BaseUnit unit = new BaseUnit();
        unit.addGate(a1);
        unit.addGate(a2);
        unit.addGate(o);
        unit.addWire(w1);
        unit.addWire(w2);
        unit.addInput(a1.getIn1());
        unit.addInput(a1.getIn2());
        unit.addInput(o.getIn1());
        unit.addInput(o.getIn2());
        unit.addOutput(a2.getOut());

        TrueTable tt = TrueTableUtil.generateTrueTable(unit);
        tt.print();

    }

}
