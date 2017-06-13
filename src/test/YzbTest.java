/**
 * 
 */
package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author zhangle
 *
 */
public class YzbTest {

    private String mAppId;
    private String mAppSecret;
    private String mTemplateId;
    private String mAccessToken = "";
    private static int sentCount = 24105;
    // private String mAccessToken = "";
    private File successF = null;
    private PrintWriter successPw = null;
    private File failF = null;
    private PrintWriter failPw = null;

    public YzbTest(String appId, String appSecret, String templateId) {
        this.mAppId = appId;
        this.mAppSecret = appSecret;
        this.mTemplateId = templateId;
    }

    public boolean sendYzbMsg(String openId) {
        JSONObject postData = new JSONObject();
        postData.put("touser", openId);
        postData.put("template_id", mTemplateId);
        postData.put("url", "http://yzb.vxin365.com/wx_web_index");

        JSONObject data = new JSONObject();
        data.put("first", getTemplateData("中国首家园长一键定制专属课堂火热报名中！", "#000"));
        data.put("keyword1", getTemplateData("开班抢课中", "#000"));
        data.put("keyword2", getTemplateData("史上首个园长私人定制课程", "#000"));
        data.put("remark", getTemplateData(
                "为什么这一生听过很多课，还是成不了最想成为的园长？因为听课就要听自己最适合、最需要和最实用的！中国园长帮2017微课堂，根据园长特质和园所实际情况科学测评，精准定位，一键开启专属自己的微课堂.....", "#000000"));
        postData.put("data", data);
        if (StringUtils.isEmpty(mAccessToken)) {
            mAccessToken = getAccessToken();
        }
        System.out.println("Access token:" + mAccessToken);
        System.out.println("postData is:" + postData);
        if (StringUtils.isEmpty(mAccessToken)) {
            System.out.println("access token is empty!");
            return false;
        }
        String postUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="
                + mAccessToken;
        JSONObject result = WebUtils.postWithJsonInBody(postUrl, postData,
                null);
        System.out.println("result is:" + result);
        int errorCode = result.optInt("errcode");
        if (errorCode == 0) {
            return true;
        } else if (errorCode == 40001) {
            mAccessToken = "";
            return false;
        } else {
            System.err.println("发送失败：" + result.optString("errmsg"));
            return false;
        }
    }

    private Object getTemplateData(String value, String color) {
        JSONObject result = new JSONObject();
        result.put("value", value);
        result.put("color", color);
        return result;
    }

    private String getAccessToken() {
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="
                + mAppId + "&secret=" + mAppSecret;
        String result = WebUtils.getJsonStrFromUrl(url);
        System.out.println(result);
        if (!StringUtils.isEmpty(result)) {
            JSONObject json = JSONObject.fromObject(result);
            return json.getString("access_token");
        }
        return "";
    }

    private static JSONArray getOnlineUserList() {
        return getUserList("idealjxy.mysql.rds.aliyuncs.com", "3306",
                "ideal_root", "ideal2013", "db_yzb");
    }

    private static JSONArray getUserList(String dbHost, String dbPort,
            String dbUser, String dbPswd, String dbName) {
        MySQLHelper test_helper = new MySQLHelper(dbName, dbUser, dbPswd,
                dbHost, dbPort);
        String sql1 = "SELECT open_id,nick_name FROM t_e_wx_user";
        JSONArray arr = test_helper.query(sql1, null);
        return arr;
    }

    private static JSONArray getFileUserList() {
        JSONArray users = new JSONArray();
        String filePathName = "/Users/zhangle/Documents/work/aibao/产品文档/园长帮/template_msg_list.txt";
        InputStream in = null;
        InputStreamReader ir = null;
        BufferedReader br = null;
        try {
            in = new FileInputStream(filePathName);
            ir = new InputStreamReader(in);
            br = new BufferedReader(ir);
            String line = br.readLine();
            while (line != null) {
                String[] arr = line.split(",");
                String openId = arr[0];
                String nick = "";
                if (arr.length > 1) {
                    nick = arr[1];
                }
                JSONObject json = new JSONObject();
                json.put("open_id", openId);
                json.put("nick_name", nick);
                users.add(json);
                line = br.readLine();
            }
            return users;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignore) {
                }
            }
            if (ir != null) {
                try {
                    ir.close();
                } catch (IOException ignore) {
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ignore) {
                }
            }
        }
        return users;
    }

    private void notifyOnlineUser() {
        JSONArray users = getOnlineUserList();
//        JSONArray users = getFileUserList();
        System.out.println("#######total count:" + users.size());
        if (users.size() > 0) {
//            try {
//                initPrintWriter();
//            } catch (IOException e) {
//                return;
//            }
            for (int i = 0; i < users.size(); i++) {
                if (i < sentCount) {
                    continue;
                }
                JSONObject json = users.getJSONObject(i);
                String openId = json.optString("open_id");
                String nick = json.optString("nick_name");
                System.out.println(
                        "sending template msg to'" + nick + "': " + openId);
                boolean result = sendYzbMsg(openId);
//                try {
//                    saveOpenId(openId + "," + nick, result);
//                } catch(Exception ex) {
//                    return;
//                }
//                if ("oojzOvtSPhzSi-Iap_4QHJmfQGYA".equals(openId)
//                        || "oojzOvsu4P9cDr8-wG9B9yC6L4e0".equals(openId)) {
//                    
//                }
                System.out.println("access token is:" + mAccessToken);
                System.out.println("#######sent count:" + i);
            }
        } else {
            System.err.println("找不到用户!");
        }
//        closePrintWriter();
    }

    public void initPrintWriter() throws IOException {
        String successFile = "/Users/zhangle/Documents/work/aibao/产品文档/园长帮/template_msg_success.txt";
        String failFile = "/Users/zhangle/Documents/work/aibao/产品文档/园长帮/template_msg_fail.txt";
        if (successF == null || successPw == null) {
            successF = new File(successFile);
            if (!successF.exists()) {
                successF.createNewFile();
            }
            successPw = new PrintWriter(successF);
        }
        if (failF == null || failPw == null) {
            failF = new File(failFile);
            if (!failF.exists()) {
                failF.createNewFile();
            }
            failPw = new PrintWriter(failF);
        }
    }

    private void saveOpenId(String openId, boolean success) {
        if (StringUtils.isEmpty(openId)) {
            return;
        }
        if (success) {
            successPw.append(openId + "\r\n");
        } else {
            failPw.append(openId + "\r\n");
        }
    }

    private void closePrintWriter() {
        try {
            successPw.flush();
            successPw.close();
            successPw = null;
            failPw.flush();
            failPw.close();
            failPw = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        YzbTest yt_online = new YzbTest("wx5fddb9e91b805950",
                "ff492f03782e2e30c4341066e19c26b2",
                "9Ha9iiRVqXfYCBzUYHtS9hR8Y3e8VFwT9SMCwP-vCk8");
        int sentCount = 0;
        if (args.length > 0) {
            sentCount = Integer.parseInt(args[0]);
            yt_online.sentCount = sentCount;
        }
        System.out.println("sent count:" + sentCount);
        yt_online.notifyOnlineUser();
        System.out.println("sent count:" + sentCount);
    }

}
