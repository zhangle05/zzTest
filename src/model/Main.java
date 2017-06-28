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

        BaseUnit unit2 = new BaseUnit();
        Pin a = new Pin();
        Pin b = new Pin();
        unit2.addInput(a);
        unit2.addInput(b);
        Not na = new Not();
        Not nb = new Not();
        a1 = new And();
        a2 = new And();
        o = new Or();
        unit2.addGate(na);
        unit2.addGate(nb);
        unit2.addGate(a1);
        unit2.addGate(a2);
        unit2.addGate(o);
        w1 = new Wire(a, na.getPin());
        w2 = new Wire(b, nb.getPin());
        Wire w3 = new Wire(na.getOut(), a1.getIn1());
        Wire w4 = new Wire(b, a1.getIn2());
        Wire w5 = new Wire(nb.getOut(), a2.getIn1());
        Wire w6 = new Wire(a, a2.getIn2());
        Wire w7 = new Wire(a1.getOut(), o.getIn1());
        Wire w8 = new Wire(a2.getOut(), o.getIn2());
        unit2.addWire(w1);
        unit2.addWire(w2);
        unit2.addWire(w3);
        unit2.addWire(w4);
        unit2.addWire(w5);
        unit2.addWire(w6);
        unit2.addWire(w7);
        unit2.addWire(w8);
        unit2.addOutput(o.getOut());

        tt = TrueTableUtil.generateTrueTable(unit2);
        tt.print();
    }

}
