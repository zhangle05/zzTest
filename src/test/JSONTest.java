/**
 * 
 */
package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author zhangle
 *
 */
public class JSONTest {

    public static void main(String[] args) {
        String pid = "fe993ac7-2b5f-473e-80fa-0afba96910fd";
        JSONObject result = new JSONObject();
        Map<String, Object> tmpJb = new HashMap<String, Object>();
        String options = "[\"(-2,2)\", \"(-∞,-2)∪(2,+∞) \", \"[-2,2]\", \"(-∞,-2]∪[2,+∞) \"]";
        JSONArray arr = JSONArray.fromObject(options);
        List<String> optionsArr = new ArrayList<String>();
        for (int i = 0; i < arr.size(); i++) {
            String option = arr.getString(i).trim();
            if (option.startsWith("[") && option.endsWith("]")) {
                option = "\"" + option + "\"";
            }
            optionsArr.add(option);
        }

        tmpJb.put("options", optionsArr);
        result.put("score", 100);
        result.put(pid, tmpJb);
        System.out.println(tmpJb);
        System.out.println(result);
        System.out.println("------------------------------");

        JSONObject result2 = JSONObject.fromObject(result);
        JSONObject tmpJb2 = result2.optJSONObject(pid);
        System.out.println(tmpJb2);
        System.out.println(result2);
        System.out.println("------------------------------");
        handleOptions(result2);
        tmpJb2 = result2.optJSONObject(pid);
        System.out.println(tmpJb2);
        System.out.println(result2);
        System.out.println("------------------------------");
        JSONObject result3 = new JSONObject();
        result3.put("data", result2);
        System.out.println(result3);
        System.out.println("------------------------------");
    }
    
    private static void handleOptions(JSONObject paperJson) {
        for (Iterator it = paperJson.values().iterator(); it.hasNext();) {
            Object obj = it.next();
            if (obj instanceof JSONObject) {
                JSONArray options = ((JSONObject) obj).optJSONArray("options");
                if (options != null) {
                    List<String> optionsArr = new ArrayList<String>();
                    for (int i = 0; i < options.size(); i++) {
                        String option = options.getString(i).trim();
                        if (option.startsWith("[") && option.endsWith("]")) {
                            option = "\"" + option + "\"";
                        }
                        optionsArr.add(option);
                    }
                    ((JSONObject) obj).put("options", optionsArr);
                }
            }
        }
    }
}
