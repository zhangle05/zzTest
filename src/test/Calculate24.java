/**
 * 
 */
package test;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author zhangle
 *
 */
public class Calculate24 {

    /**
     * @param args
     */
    public static void main(String[] args) {
        int correctCount = 3, sectionCount = 5;
        int score = (int)(correctCount / (float)sectionCount * 100);
        System.out.println("score is:" + score);
//        System.out.println(new java.util.Date().getTime());
//        Calculate24 c = new Calculate24();
//        String [] param = new String[] {"11", "3", "8", "9"};
//        int size = 29;
//        String [] test = new String[size];
//        for(int i=0; i<size; i++) {
//            test[i] = String.valueOf(i);
//        }
//        long start = System.currentTimeMillis();
//        String[][] result = c.getCombination2(test, 3);
//        long end = System.currentTimeMillis();
//        System.out.println("result size:" + result.length + ", time is:" + (end - start));
//        for(String[] r : result) {
//            for(int i = 0; i < r.length; i++) {
//                System.out.print(r[i]);
//                System.out.print((i == (r.length - 1) ? "\r\n" : ","));
//            }
//        }
//        System.out.println(c.calculate(param));
    }

    public String calculate(String[] values) {
        String[][] numberArr = getFullPermutations(values);
        String[] allOps = new String[] {"+", "+", "+", "-", "-", "-", "*", "*", "*", "/", "/", "/"};
        String[][] opArr = getPermutations(allOps, values.length - 1);
        for(int i = 0; i < numberArr.length; i++) {
            for(int j = 0; j < opArr.length; j++) {
                String[] numbers = numberArr[i];
                String[] ops = opArr[j];
                String[] result = new String[numbers.length + ops.length];
                int p = 0, q = 0;
                for(int k = 0; k < result.length; k++) {
                    if(k % 2 == 0) {
                        result[k] = numbers[p];
                        p++;
                    }
                    else {
                        result[k] = ops[q];
                        q++;
                    }
                }
                float value = evaluate(result);
                String resultStr = arrayToHumanString(result);
                System.out.println(resultStr + "=" + value);
                if(value == 24.0) {
                    return "Solved! " + resultStr;
                }
            }
        }
        return "Cannot solve!";
    }

    public String[][] getPermutations(String[] values, int resultSize) {
        if(resultSize < 1) {
            System.out.println("result size error: resultSize=" + resultSize);
            return null;
        }
        if(values == null) {
            System.out.println("value is null.");
            return null;
        }
        if(values.length < resultSize) {
            System.out.println("value size small than result size: valueSize=" + values.length + ", resultSize=" + resultSize);
            return null;
        }
        if(values.length == resultSize) {
            return getFullPermutations(values);
        }
        List<String[]> resultList = new ArrayList<String[]>();
        String[][] combinations = getCombination(values, resultSize);
        for(int i = 0; i < combinations.length; i++) {
            String[] combination = combinations[i];
            String[][] permutations = getFullPermutations(combination);
            for(String[] permutation : permutations) {
                resultList.add(permutation);
            }
        }
        return listToArray(resultList);
    }

    public String[][] getCombination(String[] values, int resultSize) {
        if(resultSize < 1) {
            System.out.println("result size error: resultSize=" + resultSize);
            return null;
        }
        if(values == null) {
            System.out.println("value is null.");
            return null;
        }
        if(values.length < resultSize) {
            System.out.println("value size small than result size: valueSize=" + values.length + ", resultSize=" + resultSize);
            return null;
        }
        if(values.length == resultSize) {
            String[][] result = new String[1][];
            result[0] = values;
            return result;
        }
        if(resultSize == 1) {
            String[][] result = new String[values.length][];
            for(int i = 0; i < values.length; i++) {
                String[] combination = new String[1];
                combination[0] = values[i];
                result[i] = combination;
            }
            return result;
        }
        List<String[]>resultList = new ArrayList<String[]>();
        for(int j = 0; j < values.length; j++) {
            String separator = values[j];
            String[] subValue = new String[values.length - 1];
            for(int k = 0; k < j; k++) {
                subValue[k] = values[k];
            }
            for(int k = j + 1; k < values.length; k++) {
                subValue[k - 1] = values[k];
            }
            String[][] subResult = getCombination(subValue, resultSize - 1);
            for(int i = 0; i < subResult.length; i++) {
                String[] subCombination = subResult[i];
                String[] combination = new String[subCombination.length + 1];
                combination[0] = separator;
                for(int k = 1; k < combination.length; k++) {
                    combination[k] = subCombination[k - 1];
                }
                // filter out duplication
                boolean contains = false;
                for(int k = 0; k < resultList.size(); k++) {
                    String[] preCombination = resultList.get(k);
                    if(combinationEquals(preCombination, combination)) {
                        contains = true;
                    }
                }
                if(!contains) {
                    resultList.add(combination);
                }
            }
        }
        return listToArray(resultList);
    }

    public String[][] getCombination2(String[] values, int resultSize) {
        if(resultSize < 1) {
            System.out.println("result size error: resultSize=" + resultSize);
            return null;
        }
        if(values == null) {
            System.out.println("value is null.");
            return null;
        }
        if(values.length < resultSize) {
            System.out.println("value size smaller than result size: valueSize=" + values.length + ", resultSize=" + resultSize);
            return null;
        }
        if(values.length == resultSize) {
            String[][] result = new String[1][];
            result[0] = values;
            return result;
        }
        long upper = 1L << values.length;
        long lower = 1L << resultSize - 1;
        List<String[]> resultList = new ArrayList<String[]>();
        String[] tmp = new String[values.length];
        for(long i=lower; i<upper; i++) {
            long left = i;
            int q = 0, p = 0;
            long tmpLeft = left;
            while (tmpLeft > 0) {
                tmpLeft = tmpLeft & (tmpLeft - 1);
                q ++;
                if(q > resultSize) {
                    break;
                }
            }
//            while(left > 0) {
//                if((left & 1) == 1) {
//                    tmp[q] = values[p];
//                    q++;
//                }
//                if(q > resultSize) {
//                    break;
//                }
//                p++;
//                left = left >> 1;
//            }
            if(q == resultSize) {
                q = 0;
                while(left > 0) {
                  if((left & 1) == 1) {
                      tmp[q] = values[p];
                      q++;
                  }
                  p++;
                  left = left >> 1;
              }
                String[] combination = new String[resultSize];
                for(q = 0; q < combination.length; q++) {
                    combination[q] = tmp[q];
                }
                // filter out duplication
                boolean contains = false;
                for(int k = 0; k < resultList.size(); k++) {
                    String[] preCombination = resultList.get(k);
                    if(combinationEquals(preCombination, combination)) {
                        contains = true;
                    }
                }
                if(!contains) {
                    resultList.add(combination);
                }
            }
        }
        return listToArray(resultList);
    }

    public String[][] getFullPermutations(String[] values) {
        if(values == null) {
            System.out.println("value is null.");
            return null;
        }
        if(values.length < 1) {
            System.out.println("value size smaller than 1.");
            return null;
        }
        if(values.length == 1) {
            String[][] result = new String[values.length][];
            for(int i = 0; i < values.length; i++) {
                String[] permutation = new String[1];
                permutation[0] = values[i];
                result[i] = permutation;
            }
            return result;
        }
        String[] subValue = new String[values.length - 1];
        String separator = values[0];
        for(int i = 1; i < values.length; i++) {
            subValue[i - 1] = values[i];
        }
        String[][] subResult = getFullPermutations(subValue);
        String[][] result = new String[subResult.length * values.length][];
        for(int i = 0; i < subResult.length; i++) {
            String[] subPermutation = subResult[i];
            for(int j = 0; j < values.length; j++) {
                String[] permutation = new String[values.length];
                for(int k = 0; k < j; k++) {
                    permutation[k] = subPermutation[k];
                }
                permutation[j] = separator;
                for(int k = j + 1; k < permutation.length; k++) {
                    permutation[k] = subPermutation[k - 1];
                }
                result[i * values.length + j] = permutation;
            }
        }
        return result;
    }

    private boolean combinationEquals(final String[] combination1, final String[] combination2) {
        String[] comb1 = copyArray(combination1);
        String[] comb2 = copyArray(combination2);
        for(int i = 0; i < comb1.length; i++) {
            boolean contain = false;
            for(int j = 0; j < comb2.length; j++) {
                if(comb1[i].equals(comb2[j])) {
                    contain = true;
                    comb1[i] = "";
                    comb2[j] = "";
                    break;
                }
            }
            if(!contain) {
                return false;
            }
        }
        return true;
    }

    private String[] copyArray (final String[] arr) {
        String[] result = new String[arr.length];
        for(int i = 0; i < arr.length; i++) {
            result[i] = arr[i];
        }
        return result;
    }

    private String[][] listToArray(List<String[]> resultList) {
        String[][] result = new String[resultList.size()][];
        for(int i = 0; i < resultList.size(); i++) {
            result[i] = resultList.get(i);
        }
        return result;
    }


    private String arrayToHumanString(String[] result) {
        Stack<String> s = new Stack<String>();
        for(int i = result.length - 1; i >= 0; i--) {
            s.push(result[i]);
        }
        StringBuilder sb = new StringBuilder();
        try {
            while(s.size() > 1) {
                String lp = s.pop();
                String op = s.pop();
                String rp = s.pop();
                if(s.isEmpty()) {
                    sb.append(lp);
                    sb.append(op);
                    sb.append(rp);
                } else {
                    String nextOp = s.peek();
                    if(isPrior(nextOp, op)) {
                        sb.append("(");
                        sb.append(lp);
                        sb.append(op);
                        sb.append(rp);
                        sb.append(")");
                    } else {
                        sb.append(lp);
                        sb.append(op);
                        sb.append(rp);
                    }
                }
                s.push(sb.toString());
                sb.delete(0, sb.length());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s.pop();
    }

    private boolean isPrior(String op1, String op2) {
        int level1 = 0;
        int level2 = 0;
        if("+".equals(op1) || "-".equals(op1)) {
            level1 = 1;
        } else {
            level1 = 2;
        }
        if("+".equals(op2) || "-".equals(op2)) {
            level2 = 1;
        } else {
            level2 = 2;
        }
        return level1 > level2;
    }

    private float evaluate(String[] result) {
        Stack<String> s = new Stack<String>();
        for(int i = result.length - 1; i >= 0; i--) {
            s.push(result[i]);
        }
        float value = 0;
        try {
            while(s.size() > 1) {
                Float lp = Float.parseFloat(s.pop());
                String op = s.pop();
                Float rp = Float.parseFloat(s.pop());
                if("+".equals(op)) {
                    value = lp + rp;
                } else if("-".equals(op)) {
                    value = lp - rp;
                } else if("*".equals(op)) {
                    value = lp * rp;
                } else if("/".equals(op)) {
                    value = (float)lp / rp;
                }
                s.push(String.valueOf(value));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }
}
