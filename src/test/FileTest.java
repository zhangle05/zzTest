/**
 * 
 */
package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangle
 *
 */
public class FileTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        FileTest ft = new FileTest();
        List<String> list1 = ft.read("/Users/zhangle/dev/jingyou/JingyouWeb/jingyou-web/src/main/resources/censor2.txt");
        List<String> list2 = ft.read("/Users/zhangle/dev/jingyou/JingyouWeb/jingyou-web/src/main/resources/censor-new.txt");
        System.out.println("list 1 size:" + list1.size());
        System.out.println("list 2 size:" + list2.size());
        for(int i = 0; i < list2.size(); i++) {
            String str = list2.get(i);
            if(!list1.contains(str)) {
                list1.add(str);
            }
        }
        System.out.println("final list size:" + list1.size());
        ft.write("/Users/zhangle/dev/jingyou/JingyouWeb/jingyou-web/src/main/resources/censor3.txt", list1);
        System.out.println("done!");
    }

    public void write(String filePathName, List<String> list) {
        FileOutputStream fos = null;
        OutputStreamWriter wr = null;
        BufferedWriter bw = null;
        try {
            File f = new File(filePathName);
            fos = new FileOutputStream(f);
            wr = new OutputStreamWriter(fos);
            bw = new BufferedWriter(wr);
            for(int i = 0; i < list.size(); i++) {
                bw.write(list.get(i) + "\n");
            }
            bw.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if(fos != null) {
                try {
                    fos.close();
                    fos = null;
                } catch (IOException ignore) { }
            }
            if(wr != null) {
                try {
                    wr.close();
                    wr = null;
                } catch (IOException ignore) { }
            }
            if(bw != null) {
                try {
                    bw.close();
                    bw = null;
                } catch (IOException ignore) { }
            }
        }
    }
    public List<String> read(String filePathName) {
        List<String> result = new ArrayList<String>();
        FileInputStream fis = null;
        InputStreamReader ir = null;
        BufferedReader br = null;
        try {
            File f = new File(filePathName);
            fis = new FileInputStream(f);
            ir = new InputStreamReader(fis);
            br = new BufferedReader(ir);
            String line = br.readLine();
            while (line != null) {
                try {
                    String[] words = line.split("=");
                    String word = "";
                    if(words.length > 0) {
                        word = words[0];
                    }
                    if(null == word || "".equals(word)) {
                        line = br.readLine();
                        continue;
                    }
                    result.add(word);
                } catch (Exception ignore) {}
                line = br.readLine();
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        finally {
            if(fis != null) {
                try {
                    fis.close();
                    fis = null;
                } catch (IOException ignore) { }
            }
            if(ir != null) {
                try {
                    ir.close();
                    ir = null;
                } catch (IOException ignore) { }
            }
            if(br != null) {
                try {
                    br.close();
                    br = null;
                } catch (IOException ignore) { }
            }
        }
        return result;
    }
}
