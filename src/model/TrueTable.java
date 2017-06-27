/**
 * 
 */
package model;

/**
 * @author zhangle
 *
 */
public class TrueTable {

    private int inSize;
    private int outSize;

    private boolean[][] inTable;
    private boolean[][] outTable;

    public TrueTable(int inSize, int outSize) {
        this.inSize = inSize;
        this.outSize = outSize;
        int valueSize = 1 << inSize;
        inTable = new boolean[valueSize][inSize];
        outTable = new boolean[valueSize][outSize];
        for (int i = 0; i < valueSize; i++) {
            inTable[i] = new boolean[inSize];
            for (int j = 0; j < inSize; j++) {
                if ((i >> j) % 2 > 0) {
                    inTable[i][inSize - j - 1] = true;
                } else {
                    inTable[i][inSize - j - 1] = false;
                }
            }
        }
    }

    /**
     * @return the inSize
     */
    public int getInSize() {
        return inSize;
    }

    /**
     * @return the outSize
     */
    public int getOutSize() {
        return outSize;
    }

    /**
     * @return the inTable
     */
    public boolean[][] getInTable() {
        return inTable;
    }

    /**
     * @param inTable
     *            the inTable to set
     */
    public void setInTable(boolean[][] inTable) {
        this.inTable = inTable;
    }

    /**
     * @return the outTable
     */
    public boolean[][] getOutTable() {
        return outTable;
    }

    /**
     * @param outTable
     *            the outTable to set
     */
    public void setOutTable(boolean[][] outTable) {
        this.outTable = outTable;
    }

    public void print() {
        for (int i = 0; i < 1 << inSize; i++) {
            for (int j = 0; j < inSize; j++) {
                System.out.print((inTable[i][j] ? 1 : 0) + " ");
            }
            System.out.print("=>");
            for (int j = 0; j < outSize; j++) {
                System.out.print(" " + (outTable[i][j] ? 1 : 0));
            }
            System.out.println();
        }
    }
}
