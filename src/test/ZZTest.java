/**
 * 
 */
package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;

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
        try {
            System.out.println(sdf.parse("2020-03-10 15:08:58").getTime());
//            System.out.println(sdf.parse("2017-05-03 19:30:00").getTime());
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
//        testNumber();
//        JSONObject json = JSONObject.fromObject("{\"subject\":20,\"isKnowledge\":false,\"section\":\"xxx\",\"selection\":[]}");
//        JSONArray arr = json.optJSONArray("selection");
//        System.out.println("arr is:" + arr);
//        double fee = 10.0;
//        if (fee >= 30) {
//            System.out.println(3);
//        } else if (fee >= 10) {
//            System.out.println(2);
//        } else if (fee >= 5) {
//            System.out.println(1);
//        }
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

}
