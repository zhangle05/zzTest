/**
 * 
 */
package test;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.commons.lang.StringUtils;

/**
 * @author zhangle
 *
 */
public class Calculate24New {

    /**
     * @param args
     */
    public static void main(String[] args) {
        Calculate24New c = new Calculate24New();
        String[] param = new String[] { "11", "5", "5", "7" };
        System.out.println(c.calculate(param));
    }

    public String calculate(String[] values) {
        String[] allOps = new String[] { "+", "-", "*", "/" };
        String[][] numberArr = getFullPermutations(values);
        String[][] opArr = getRepeatablePermutations(allOps, values.length - 1);
        List<BinaryTree<String>> isomers = getBTreeIsomers(values.length - 1);
        System.out.println("number array size:" + numberArr.length);
        System.out.println("op array size:" + opArr.length);
        System.out.println("binary tree size:" + isomers.size());
        for (int i = 0; i < numberArr.length; i++) {
            for (int j = 0; j < opArr.length; j++) {
                for (BinaryTree<String> tree : isomers) {
                    float value = evaluate(numberArr[i], opArr[j], tree);
                    if (value == 24.0) {
                        String resultStr = treeToHumanStr(tree);
                        return "Solved! " + resultStr;
                    }
                }
            }
        }
        return "Cannot solve!";
    }

    private float evaluate(String[] numbers, String[] ops,
            BinaryTree<String> tree) {
        Stack<String> numberStack = new Stack<String>();
        Stack<String> opStack = new Stack<String>();
        for (int i = 0; i < numbers.length; i++) {
            numberStack.push(numbers[i]);
        }
        for (int j = 0; j < ops.length; j++) {
            opStack.push(ops[j]);
        }
        cleanTree(tree);
        buildCalculationTree(numberStack, opStack, tree);
        return evaluateTree(tree);
    }

    private void cleanTree(BinaryTree<String> tree) {
        if (tree != null) {
            tree.setValue(null);
            cleanTree(tree.getLeft());
            cleanTree(tree.getRight());
        }
    }

    private float evaluateTree(BinaryTree<String> tree) {
        if (tree.isLeaf()) {
            return Float.valueOf(tree.getValue());
        }
        float lp = evaluateTree(tree.getLeft());
        float rp = evaluateTree(tree.getRight());
        String op = tree.getValue();
        if ("+".equals(op)) {
            return lp + rp;
        } else if ("-".equals(op)) {
            return lp - rp;
        } else if ("*".equals(op)) {
            return lp * rp;
        } else if ("/".equals(op)) {
            return (float) lp / rp;
        }
        return 0;
    }

    private void buildCalculationTree(Stack<String> numberStack,
            Stack<String> opStack, BinaryTree<String> tree) {
        if (tree.isLeaf()) {
            if (StringUtils.isEmpty(tree.getValue())) {
                tree.setValue(numberStack.pop());
            }
            return;
        }
        if (StringUtils.isEmpty(tree.getValue())) {
            tree.setValue(opStack.pop());
        }
        buildCalculationTree(numberStack, opStack, tree.getLeft());
        buildCalculationTree(numberStack, opStack, tree.getRight());
    }

    private String treeToHumanStr(BinaryTree<String> tree) {
        if (tree.isLeaf()) {
            return tree.getValue();
        }
        String left = treeToHumanStr(tree.getLeft());
        String right = treeToHumanStr(tree.getRight());
        String op = tree.getValue();
        if (!tree.getLeft().isLeaf() && isPrior(op, tree.getLeft().getValue())) {
            left = "(" + left + ")";
        }
        if (!tree.getRight().isLeaf() && isPrior(op, tree.getRight().getValue())) {
            right = "(" + right + ")";
        }
        return left + op + right;
    }

    private boolean isPrior(String op1, String op2) {
        int level1 = 0;
        int level2 = 0;
        if ("+".equals(op1) || "-".equals(op1)) {
            level1 = 1;
        } else {
            level1 = 2;
        }
        if ("+".equals(op2) || "-".equals(op2)) {
            level2 = 1;
        } else {
            level2 = 2;
        }
        return level1 > level2;
    }

    private List<BinaryTree<String>> getBTreeIsomers(int n) {
        List<BinaryTree<String>> result = new ArrayList<BinaryTree<String>>();
        if (n == 0) {
            BinaryTree<String> tree = new BinaryTree<String>();
            tree.setLeaf(true);
            result.add(tree);
            return result;
        }
        for (int i = 0; i < n; i++) {
            List<BinaryTree<String>> leftList = getBTreeIsomers(i);
            List<BinaryTree<String>> rightList = getBTreeIsomers(n - 1 - i);
            for (BinaryTree<String> left : leftList) {
                for (BinaryTree<String> right : rightList) {
                    BinaryTree<String> tree = new BinaryTree<String>();
                    tree.setLeft(left);
                    tree.setRight(right);
                    result.add(tree);
                }
            }
        }
        return result;
    }

    public String[][] getFullPermutations(String[] values) {
        if (values == null) {
            System.out.println("value is null.");
            return null;
        }
        if (values.length < 1) {
            System.out.println("value size smaller than 1.");
            return null;
        }
        if (values.length == 1) {
            String[][] result = new String[values.length][];
            for (int i = 0; i < values.length; i++) {
                String[] permutation = new String[1];
                permutation[0] = values[i];
                result[i] = permutation;
            }
            return result;
        }
        String[] subValue = new String[values.length - 1];
        String separator = values[0];
        for (int i = 1; i < values.length; i++) {
            subValue[i - 1] = values[i];
        }
        String[][] subResult = getFullPermutations(subValue);
        String[][] result = new String[subResult.length * values.length][];
        for (int i = 0; i < subResult.length; i++) {
            String[] subPermutation = subResult[i];
            for (int j = 0; j < values.length; j++) {
                String[] permutation = new String[values.length];
                for (int k = 0; k < j; k++) {
                    permutation[k] = subPermutation[k];
                }
                permutation[j] = separator;
                for (int k = j + 1; k < permutation.length; k++) {
                    permutation[k] = subPermutation[k - 1];
                }
                result[i * values.length + j] = permutation;
            }
        }
        return result;
    }

    public String[][] getRepeatablePermutations(String[] values,
            int resultSize) {
        if (resultSize < 1) {
            System.out.println("result size error: resultSize=" + resultSize);
            return null;
        }
        if (values == null) {
            System.out.println("value is null.");
            return null;
        }
        List<String[]> resultList = new ArrayList<String[]>();
        if (resultSize == 1) {
            for (int i = 0; i < values.length; i++) {
                String[] tmp = new String[] { values[i] };
                resultList.add(tmp);
            }
            return listToArray(resultList);
        }
        String[][] subArr = getRepeatablePermutations(values, resultSize - 1);
        for (int i = 0; i < values.length; i++) {
            for (String[] sub : subArr) {
                String[] tmp = new String[sub.length + 1];
                tmp[0] = values[i];
                for (int j = 0; j < sub.length; j++) {
                    tmp[j + 1] = sub[j];
                }
                resultList.add(tmp);
            }
        }
        return listToArray(resultList);
    }

    private String[][] listToArray(List<String[]> resultList) {
        String[][] result = new String[resultList.size()][];
        for (int i = 0; i < resultList.size(); i++) {
            result[i] = resultList.get(i);
        }
        return result;
    }

    public static class BinaryTree<T> {
        private boolean isLeaf = false;
        private T value;
        private BinaryTree<T> left;
        private BinaryTree<T> right;

        /**
         * @return the value
         */
        public T getValue() {
            return value;
        }

        /**
         * @param value
         *            the value to set
         */
        public void setValue(T value) {
            this.value = value;
        }

        /**
         * @return the left
         */
        public BinaryTree<T> getLeft() {
            return left;
        }

        /**
         * @param left
         *            the left to set
         */
        public void setLeft(BinaryTree<T> left) {
            this.left = left;
        }

        /**
         * @return the right
         */
        public BinaryTree<T> getRight() {
            return right;
        }

        /**
         * @param right
         *            the right to set
         */
        public void setRight(BinaryTree<T> right) {
            this.right = right;
        }

        /**
         * @return the isLeaf
         */
        public boolean isLeaf() {
            return isLeaf;
        }

        /**
         * @param isLeaf the isLeaf to set
         */
        public void setLeaf(boolean isLeaf) {
            this.isLeaf = isLeaf;
        }

    }
}
