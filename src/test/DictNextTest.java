/**
 * 
 */
package test;

/**
 * @author zhangle
 *
 */
public class DictNextTest {

    public static void main(String[] args) {
        DictNextTest t = new DictNextTest();
        t.test();
    }

    public void test() {
        int a = 12345;
        testNumber(a);
    }
    private void testNumber(int a) {
        a = getNext(a);
        System.out.println(a);
        while (a > 0) {
            a = getNext(a);
            System.out.println(a);
        }
    }

    private int getNext(int num) {
        String str = String.valueOf(num);
        if (str.length() <= 1) {
            return num;
        }
        char[] arr = str.toCharArray();
        int len = arr.length;
        int i = len - 1;
        while (i > 0 && arr[i] < arr[i - 1]) {
            i--;
        }
        if (i == 0) {
            return -1;
        }
        int j = len - 1;
        while (arr[j] < arr[i - 1]) {
            j--;
        }
        char tmp = arr[i - 1];
        arr[i - 1] = arr[j];
        arr[j] = tmp;
        j = len - 1;
        while (i < j) {
            tmp = arr[i];
            arr[i] = arr[j];
            arr[j] = tmp;
            i++;
            j--;
        }
        StringBuffer sb = new StringBuffer();
        for (i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
        }
        int result = Integer.parseInt(sb.toString());
        return result;
    }
}
