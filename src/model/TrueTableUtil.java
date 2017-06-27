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
public class TrueTableUtil {

    public static TrueTable generateTrueTable(BaseUnit unit) {
        TrueTable tt = new TrueTable(unit.getInputs().size(), unit.getOutputs().size());
        boolean[][] inputsArr = tt.getInTable();
        for (int i = 0; i < inputsArr.length; i++) {
            unit.reset();
            List<Boolean> inputs = new ArrayList<Boolean>();
            for (int j = 0; j < inputsArr[i].length; j++) {
                inputs.add(inputsArr[i][j]);
            }
            unit.setInputValues(inputs);
            unit.calculate();
            List<Pin> outputs = unit.getOutputs();
            boolean[][] outArr = tt.getOutTable();
            for (int j = 0; j < outArr[i].length; j++) {
                //System.out.println("out[" + i + "][" + j + "] is:" + outputs.get(j).getValue());
                outArr[i][j] = outputs.get(j).getValue();
            }
        }
        return tt;
    }
}
