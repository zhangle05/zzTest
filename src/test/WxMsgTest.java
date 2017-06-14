/**
 * 
 */
package test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author zhangle
 *
 */
public class WxMsgTest {
    private String mAppId;
    private String mAppSecret;
    private String mAccessToken = "";

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

    /**
     * 上传图文消息正文content中的图片（不上传无法在正文中显示） 不占用公众号5000上限的素材量
     *
     * @param imgFilePathName
     * @return
     */
    public String uploadImg(String imgFilePathName) {
        if (StringUtils.isEmpty(mAccessToken)) {
            mAccessToken = getAccessToken();
        }
        System.out.println("Accesstoken is: " + mAccessToken);
        String targetUrl = "https://api.weixin.qq.com/cgi-bin/media/uploadimg?access_token="
                + mAccessToken;
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

    /**
     * 上传图文消息封面所用的图片素材，占用公众号5000上限的素材量
     *
     * @return
     */
    public String uploadMedia(String mediaFilePathName, String mediaType) {
        if (StringUtils.isEmpty(mAccessToken)) {
            mAccessToken = getAccessToken();
        }
        System.out.println("Accesstoken is: " + mAccessToken);
        String targetUrl = "https://api.weixin.qq.com/cgi-bin/media/upload?access_token="
                + mAccessToken + "&type=" + mediaType;
        try {
            String jsonStr = WebUtils.wechatUploadFile(targetUrl,
                    mediaFilePathName);
            System.out.println("result is: " + jsonStr);
            JSONObject json = JSONObject.fromObject(jsonStr);
            return json.optString(getMediaIdKey(mediaType));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String uploadArticles(JSONArray articles) {
        if (StringUtils.isEmpty(mAccessToken)) {
            mAccessToken = getAccessToken();
        }
        System.out.println("Accesstoken is: " + mAccessToken);
        String targetUrl = "https://api.weixin.qq.com/cgi-bin/media/uploadnews?access_token="
                + mAccessToken;
        JSONObject inputJson = new JSONObject();
        inputJson.put("articles", articles);
        String jsonStr = WebUtils.getJsonStrFromPostUrl(targetUrl,
                inputJson.toString());
        System.out.println("result is: " + jsonStr);
        JSONObject json = JSONObject.fromObject(jsonStr);
        return json.optString("media_id");
    }

    public String sendToUser(List<String> openIds, String mediaId, String type,
            boolean ignoreReprint) {
        if (StringUtils.isEmpty(mAccessToken)) {
            mAccessToken = getAccessToken();
        }
        System.out.println("Accesstoken is: " + mAccessToken);
        String targetUrl = "https://api.weixin.qq.com/cgi-bin/message/mass/send?access_token="
                + mAccessToken;
        JSONArray toUser = new JSONArray();
        for (String openId : openIds) {
            toUser.add(openId);
        }
        JSONObject inputJson = new JSONObject();
        inputJson.put("touser", toUser);
        inputJson.put("msgtype", type);
        JSONObject media = new JSONObject();
        media.put("media_id", mediaId);
        inputJson.put(type, media);
        inputJson.put("send_ignore_reprint", ignoreReprint ? 1 : 0);
        String jsonStr = WebUtils.getJsonStrFromPostUrl(targetUrl,
                inputJson.toString());
        System.out.println("result is: " + jsonStr);
        JSONObject json = JSONObject.fromObject(jsonStr);
        return json.optString("msg_id");
    }

    protected JSONObject generateArticle(String thumbMediaId, String author,
            String title, String contentSrcUrl, String content, String digest,
            boolean showCoverPic) {
        JSONObject json = new JSONObject();
        json.put("thumb_media_id", thumbMediaId);
        json.put("author", author);
        json.put("title", title);
        json.put("content_source_url", contentSrcUrl);
        json.put("content", content);
        json.put("digest", digest);
        json.put("show_cover_pic", showCoverPic ? 1 : 0);
        return json;
    }

    private String getMediaIdKey(String mediaType) {
        if ("image".equals(mediaType)) {
            return "media_id";
        } else if ("thumb".equals(mediaType)) {
            return "thumb_media_id";
        }
        return "media_id";
    }

    public static void main(String[] args) {
        WxMsgTest test = new WxMsgTest("wxa5edbfd15adfa7bb",
                "bea2c9b2d7eb183493d5f5aff0dfc431");
        // String url =
        // test.uploadMedia("/Users/zhangle/Desktop/WechatIMG21.jpeg",
        // "image");
        // JSONObject article = test.generateArticle(
        // "1AVl55oJM1VNXZlJDNj_TT0NaPvdgVcxdLPpddKm-2LXYHz8TjrFCa1amGeOKpNR",
        // "zl", "测试文章", "http://www.baidu.com",
        // "哈哈哈<img
        // src='http://mmbiz.qpic.cn/mmbiz_jpg/mxC6wedeqSdfIZVmjn8SkvU0ZXJhy1p1ia1aXno0ibHdDED0tQMLgrIRBfrpEzQpnwHgh4qVeseWgSiaKOibCjjEvw/0'
        // />",
        // "哈哈", true);
        // JSONArray arr = new JSONArray();
        // arr.add(article);
        // String url = test.uploadArticles(arr);
        List<String> openIds = new ArrayList<String>();
        openIds.add("oecZwweTbjYZQitQKw8zm6qsC5iA");
        openIds.add("oecZwweTbjYZQitQKw8zm6qsC5iA");
        String url = test.sendToUser(openIds,
                "Cfbje3VTNzhYH1XWl-SP55ioQJVVmbH6WZNVP18cD7MS1V4pPl7tZI0nKCbJBrst",
                "mpnews", true);
        System.out.println("url is:" + url);
    }
}
