/**
 * 
 */
package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author zhangle
 *
 */
public class ZZTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // String inputFile =
        // "/Users/zhangle/dev/jingyou/normal_chinese_code.txt";
        // String outputFile = "/Users/zhangle/dev/jingyou/code.txt";
        //
        // trim(inputFile, outputFile);
        // Long a = null;
        // long b = 12345l;
        // System.out.println("equals? " + (a == b));
//        System.out.println(System.currentTimeMillis());
//        System.out.println(UUID.randomUUID());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(sdf.format(new java.util.Date(1477929600000L)));
        System.out.println(sdf.format(new java.util.Date(1479281867854L)));
        testNumber();
        JSONObject json = JSONObject.fromObject("{\"subject\":20,\"isKnowledge\":false,\"section\":\"xxx\",\"selection\":[]}");
        JSONArray arr = json.optJSONArray("selection");
        System.out.println("arr is:" + arr);
        double fee = 10.0;
        if (fee >= 30) {
            System.out.println(3);
        } else if (fee >= 10) {
            System.out.println(2);
        } else if (fee >= 5) {
            System.out.println(1);
        }
    }

    private static void trim(String inFile, String outFile) {
        try {
            FileReader fr = new FileReader(new File(inFile));
            BufferedReader br = new BufferedReader(fr);

            String line = br.readLine();
            StringBuffer sb = new StringBuffer();
            while (null != line) {
                if (line.trim().length() > 0) {
                    String[] codes = line.split(",");
                    for (int i = 0; i < codes.length; i++) {
                        if (codes[i].trim().length() > 0) {
                            String code = codes[i].trim();
                            int unicode = Integer.parseInt(code.substring(2),
                                    16);
                            sb.append(String.valueOf(unicode));
                            sb.append(',');
                        }
                    }
                }
                line = br.readLine();
            }
            br.close();
            fr.close();

            FileWriter fw = new FileWriter(new File(outFile));
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(sb.toString());
            bw.close();
            fw.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void convertChar(String inFile, String outFile) {
        try {
            FileReader fr = new FileReader(new File(inFile));
            BufferedReader br = new BufferedReader(fr);

            String line = br.readLine();
            StringBuffer sb = new StringBuffer();
            while (null != line) {
                if (line.trim().length() > 0) {
                    String[] codes = line.split(",");
                    for (int i = 0; i < codes.length; i++) {
                        if (codes[i].trim().length() > 0) {
                            String code = codes[i].trim();
                            int unicode = Integer.parseInt(code.substring(2),
                                    16);
                            char c = (char) unicode;
                            sb.append(c);
                        }
                    }
                }
                line = br.readLine();
            }
            FileWriter fw = new FileWriter(new File(outFile));
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(sb.toString());
            bw.close();
            fw.close();
            br.close();
            fr.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void testNumber() {
        int a = 12345;
        a = getNext(a);
        System.out.println(a);
        while (a > 0) {
            a = getNext(a);
            System.out.println(a);
        }
    }

    private static int getNext(int num) {
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
