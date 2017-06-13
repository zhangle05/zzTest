/**
 * 
 */
package test;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author zhangle
 *
 */
public class WxTest {

    private String mAppId;
    private String mAppSecret;
    private String mTemplateId;
    private String mAccessToken = "";
    private static int sentCount = 0;
    // private String mAccessToken = "";

    public WxTest(String appId, String appSecret, String templateId) {
        this.mAppId = appId;
        this.mAppSecret = appSecret;
        this.mTemplateId = templateId;
    }

    public boolean sendCourseMsg(String openId, int courseId,
            String lessonTitle, String teacher, long startTime,
            boolean courseAvailable, boolean isTest) {
        JSONObject postData = new JSONObject();
        postData.put("touser", openId);
        postData.put("template_id", mTemplateId);
        if (courseAvailable) {
            if (isTest) {
                if ("oecZwweTbjYZQitQKw8zm6qsC5iA".equals(openId)) {
                    postData.put("url",
                            "http://zhibotest.vxin365.com/index.php/Live/userRoom/lesson_id/"
                                    + courseId + "/open_id/"
                                    + "9ad8d7848f5b2626a07f9c28e61f7350");
                } else {
                    postData.put("url",
                            "http://zhibotest.vxin365.com/index.php/Live/userRoom/lesson_id/"
                                    + courseId + "/open_id/" + openId);
                }
            } else {
                if ("oecZwweTbjYZQitQKw8zm6qsC5iA".equals(openId)) {
                    postData.put("url",
                            "http://abkj.vxin365.com/index.php/Live/userRoom/lesson_id/"
                                    + courseId + "/open_id/"
                                    + "9ad8d7848f5b2626a07f9c28e61f7350");
                } else {
                    postData.put("url",
                            "http://abkj.vxin365.com/index.php/Live/userRoom/lesson_id/"
                                    + courseId + "/open_id/" + openId);
                }
            }
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日  HH:mm");
        java.util.Date date = new java.util.Date(startTime);
        String timeStr = formatter.format(date);
        JSONObject data = new JSONObject();
        data.put("first", getTemplateData("您好! 欢迎来到最爱宝365", "#000"));
        data.put("keyword1", getTemplateData("一键定制父母课堂", "#000"));
        data.put("keyword2", getTemplateData(lessonTitle, "#000"));
        data.put("keyword3", getTemplateData(teacher, "#000"));
        data.put("keyword4", getTemplateData(timeStr, "#000"));
        if (courseAvailable) {
            data.put("remark", getTemplateData("马上开课了,快来听听吧!", "#FF0000"));
        } else {
            data.put("remark", getTemplateData("记得来听课,不要迟到呦!", "#FF0000"));
        }
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

    private static JSONArray getOnlineUserList(int lessonId) {
        return getUserList(lessonId,
                "rdsycfs8y0889r844v7v.mysql.rds.aliyuncs.com", "3306",
                "huiku_admin", "huiku365", "db_jiazhangbang");
    }

    private static JSONArray getUserList(int lessonId, String dbHost,
            String dbPort, String dbUser, String dbPswd, String dbName) {
        MySQLHelper test_helper = new MySQLHelper(dbName, dbUser, dbPswd,
                dbHost, dbPort);
        String sql1 = "SELECT id FROM jzb_strategy WHERE lesson_id LIKE \"%"
                + lessonId + "%\"";
        JSONArray arr = test_helper.query(sql1, null);
        System.out.println(arr);
        if (arr != null && arr.size() > 0) {
            String sql2 = "SELECT stra_id, open_id, nick_name FROM jzb_user WHERE ";
            String condition = "";
            for (int i = 0; i < arr.size(); i++) {
                JSONObject json = arr.getJSONObject(i);
                String straId = json.optString("id");
                condition += " stra_id LIKE \"%" + straId + "%\" OR ";
            }
            condition += " FALSE";
            arr = test_helper.query(sql2 + condition, null);
            System.out.println(arr);
            return arr;
        } else {
            System.err.println("找不到包含课程'" + lessonId + "'的策略!");
            return null;
        }
    }

    private static boolean updateLesson(int lessonId, String teacher,
            String teacherPic, long startTime, boolean isTest) {
        String dbName = "db_jiazhangbang";
        String dbUser = isTest ? "root" : "huiku_admin";
        String dbPswd = isTest ? "ideal2013" : "huiku365";
        String dbHost = isTest ? "123.56.162.14" : "rdsycfs8y0889r844v7v.mysql.rds.aliyuncs.com";
        String dbPort = "3306";
        MySQLHelper test_helper = new MySQLHelper(dbName, dbUser, dbPswd,
                dbHost, dbPort);
        String sql = "UPDATE jzb_course_lesson SET timestamp=" + startTime
                + ", teachername=" + "\"" + teacher + "\""
                + ", teacherpic=" + "\"" + teacherPic + "\""
                + ", status=1 WHERE id=" + lessonId;
        JSONArray arr = test_helper.update(sql, null);
        System.out.println(arr);
        if (arr != null && arr.size() > 0) {
            System.out.println("更新课程信息成功!");
            return true;
        } else {
            System.err.println("更新课程信息失败!");
            return false;
        }
    }

    private static void notifyOnlineUser(int lessonId, String lessonTitle,
            String teacher, String teacherPic, long startTime) {
        WxTest wt_online = new WxTest("wx8d11eeefe31400a8",
                "35099273b48ce317f1b503f37d4f7ade",
                "yVSJlv8G9pHJJ7rsN_T7udYilXNHxp6hLdzuycYimK4");
        JSONArray users = getOnlineUserList(lessonId);
        long now = System.currentTimeMillis();
        long minute5 = 5 * 60 * 1000;
        long minute30 = 30 * 60 * 1000;
        System.out.println("start time is:" + startTime);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日  HH:mm");
        System.out.println("start time text is:" + formatter.format(new java.util.Date(startTime)));
        System.out.println("要发送的用户数量:" + users.size());
        boolean found = false;
        for (int i = 0; i < users.size(); i++) {
            JSONObject json = users.getJSONObject(i);
            String openId = json.optString("open_id");
            String nick = json.optString("nick_name");
            if ("ocunstzaA1YLwHwusULZhWMGEIWQ".equals(openId)) {
                found = true;
                System.out.println("#######已包含用户" + nick);
                break;
            }
        }
        if (!found) {
            System.err.println("#######未找到用户zl");
        }
        if (startTime - now > minute30) {
            System.err.println("课程开始时间距离现在超过30分钟，暂时不能发送课程通知。");
            return;
        }
//        if (true) {
//            System.err.println("发生了一个sb错误");
//            return;
//        }
        boolean isAvailable = (startTime - now) < minute5;
        if (isAvailable) {
            if(!updateLesson(lessonId, teacher, teacherPic, startTime, false)) {
                return;
            }
        }
        System.out.println("#######total count:" + users.size());
        if (users.size() > 0) {
            for (int i = 0; i < users.size(); i++) {
                if (i < sentCount) {
                    continue;
                }
                JSONObject json = users.getJSONObject(i);
                String openId = json.optString("open_id");
                String nick = json.optString("nick_name");
                System.out.println(
                        "sending template msg to'" + nick + "': " + openId);
                if ("9ad8d7848f5b2626a07f9c28e61f7350".equals(openId)) {
                    openId = "oecZwweTbjYZQitQKw8zm6qsC5iA";
                }
                wt_online.sendCourseMsg(openId, lessonId, lessonTitle,
                        teacher, startTime, isAvailable, false);
                System.out.println("#######sent count:" + i);
            }
        } else {
            System.err.println("找不到订阅课程'" + lessonId + "'的用户!");
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        int lessonId = 43;
        String lessonTitle = "怎样培养孩子的同理心";
        String teacher = "苟雪林";
        String teacherPic = "http://ideal.oss-cn-beijing.aliyuncs.com/head/Gouxuelin.png";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日  HH:mm");
        java.util.Date date = null;
        try {
            date = formatter.parse("2017年06月07日  19:30");
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }
        long startTime = date.getTime();
        System.out.println("start time is:" + startTime);
        java.util.Date newDate = new java.util.Date(startTime);
        System.out.println("restore start time is:" + formatter.format(newDate));
//        notifyOnlineUser(lessonId, lessonTitle, teacher, teacherPic, startTime);
//        WxTest wt_online = new WxTest("wx8d11eeefe31400a8",
//                "35099273b48ce317f1b503f37d4f7ade",
//                "yVSJlv8G9pHJJ7rsN_T7udYilXNHxp6hLdzuycYimK4");
//        wt_online.sendCourseMsg("ocunstzaA1YLwHwusULZhWMGEIWQ", 1, "测试", "测试老师", System.currentTimeMillis(), false, false);

//        
        WxTest wt_test = new WxTest("wxa5edbfd15adfa7bb",
                "bea2c9b2d7eb183493d5f5aff0dfc431",
                "uUQZtOH1OCFyepir6B-m0UNSfg1f5qXbZf9UjxAWH1Q");
        String openId = "oecZwweTbjYZQitQKw8zm6qsC5iA";
            wt_test.sendCourseMsg(openId, lessonId, lessonTitle,
                    teacher, startTime, false, true);
    }

}
