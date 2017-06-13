/**
 * 
 */
package test;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONObject;

/**
 * @author zhangle
 *
 */
public class WxMsgTest {
    private String mAppId;
    private String mAppSecret;
    private String mAccessToken = "fB3HSgI1iWgLvk-JHmbSaXNik6blV8mlccQwDY29DfPsKgoPsx9JZ74VgIQwu_3DCSh0-uITFyIBRt2uZkF9m4Y5HQG703LHZ9E9rD5ArfNByuA0S7qm11PInmp3uQ-jDBJjAHAZXF";

    public WxMsgTest(String appId, String appSecret) {
        this.mAppId = appId;
        this.mAppSecret = appSecret;
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

    public String uploadImg(String imgFilePathName) {
        if (StringUtils.isEmpty(mAccessToken)) {
            mAccessToken = getAccessToken();
        }
        System.out.println("Accesstoken is: " + mAccessToken);
        String targetUrl = "https://api.weixin.qq.com/cgi-bin/media/uploadimg?access_token="
                + mAccessToken;
        Map<String, String> params = new HashMap<String, String>();
        params.put("access_token", mAccessToken);
        params.put("type", "image");
        Map<String, File> files = new HashMap<String, File>();
        File f1 = new File(imgFilePathName);
        if (!f1.exists()) {
            System.err.println("File '" + imgFilePathName + "' not exist!");
        }
        files.put("media", f1);
        files.put("file2", f1);
        try {
            String jsonStr = WebUtils.wechatUploadFile(targetUrl,
                    imgFilePathName);
            System.out.println("result is: " + jsonStr);
            JSONObject json = JSONObject.fromObject(jsonStr);
            return json.optString("url");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void main(String[] args) {
        WxMsgTest test = new WxMsgTest("wxa5edbfd15adfa7bb",
                "bea2c9b2d7eb183493d5f5aff0dfc431");
        String url = test.uploadImg("/Users/zhangle/Desktop/WechatIMG21.jpeg");
        System.out.println("url is:" + url);
    }
}
