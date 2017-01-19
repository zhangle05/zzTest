/**
 * 
 */
package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

/**
 * @author zhangle
 *
 */
public class TeachingResourceService {

    private static Log LOG = LogFactory.getLog(TeachingResourceService.class);

    private String NUWA_HOST = "nuwa-cloud-test.zuoyetong.com.cn";

    private String USERNAME = "pangu";

    private String PASSWORD = "123456";

    /**
     * get books by given subject code
     *
     * @param subjectCode
     * @return
     */
    public JSONObject getBooks(String subjectCode) {
        String url = "http://" + NUWA_HOST + "/api/metadata/books";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("stagesubject", subjectCode);
        return getFromNuwaUrl(url, params);
    }

    /**
     * get books with grades by given subject code
     *
     * @param subjectCode
     * @return
     */
    public JSONObject getBooksWithGrades(String subjectCode) {
        String url = "http://" + NUWA_HOST + "/api/metadata/books";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("stagesubject", subjectCode);
        JSONObject booksJson = getFromNuwaUrl(url, params);
        JSONArray booksArr = booksJson.optJSONArray("data");
        for (int i = 0; i < booksArr.size(); i++) {
            JSONObject bookJson = booksArr.getJSONObject(i);
            String code = bookJson.optString("code");
            JSONObject gradesJson = getGrades(code);
            JSONArray gradesArr = gradesJson.optJSONArray("data");
            JSONObject allGrade = new JSONObject();
            allGrade.put("code", "-1");
            allGrade.put("name", "全部");
            gradesArr.add(allGrade);
            bookJson.put("grades", gradesArr);
        }
        return booksJson;
    }

    /**
     * get knowledges by given subject code
     * 
     * @param subjectCode
     * @return
     */
    public JSONObject getKnowledges(String subjectCode) {
        String url = "http://" + NUWA_HOST + "/api/metadata/knowledges";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("stagesubject", subjectCode);
        return getFromNuwaUrl(url, params);
    }

    /**
     * get top-level category list
     *
     * @return
     */
    public JSONObject getCateList() {
        String url = "http://" + NUWA_HOST + "/api/metadata/cate_list";
        HashMap<String, String> params = new HashMap<String, String>();
        return getFromNuwaUrl(url, params);
    }

    /**
     * get grades by given book code book code is get from @see
     * com.techyou.eclass
     * .service.TeachingResourceService#getBooks(java.lang.String)
     * 
     * @param bookCode
     * @return
     */
    public JSONObject getGrades(String bookCode) {
        String url = "http://" + NUWA_HOST + "/api/metadata/grades";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("bookcode", bookCode);
        return getFromNuwaUrl(url, params);
    }

    /**
     * get chapters by given subject code and grade code grade code is get from @see
     * com.techyou.eclass.service.TeachingResourceService#getGrades(java.lang.
     * String)
     * 
     * @param subjectCode
     * @param gradeCode
     * @return
     */
    public JSONObject getChapters(String subjectCode, String gradeCode) {
        String url = "http://" + NUWA_HOST + "/api/metadata/chapters";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("stagesubject", subjectCode);
        params.put("gradecode", gradeCode);
        return getFromNuwaUrl(url, params);
    }

    /**
     * search resources
     * 
     * subject_id:学科id，source_type:(1-知识点，2-章节)，chapter:
     * 指定的章节code，后端会自动带上子节点，keyword: 关键字，order: 0-顺序、1-倒序， cate:
     * 选择的标签（学案，试卷），book：教材版本（”全部“时为空），gradecode：年级（”全部“时为空），page: 页码(从1开始)
     * 
     * @param subjectCode
     *            : 学科code
     * @param sourceType
     *            : 1-知识点，2-章节
     * @param portfolioId
     *            : 知识点id或章节id
     * @param keyword
     *            : 搜索关键字
     * @param order
     *            : 排序(根据下载数)0-顺序、1-倒序
     * @param cate
     *            : 选择的标签（学案，试卷）
     * @param bookId
     *            : 教材版本id, 只在用章节筛选时有效
     * @param gradeCode
     *            : 年级编码, 只在用章节筛选时有效
     * @param page
     *            : 页码(从1开始)
     * @param orderField
     *            : 排序字段
     * @return
     */
    public JSONObject searchResources(String subjectCode, int sourceType,
            String portfolioId, String keyword, int order, String cate,
            String bookId, String gradeCode, int page, String orderField) {
        String url = "http://" + NUWA_HOST + "/api/resource/cloud/default";
        HashMap<String, String> params = new HashMap<String, String>();
        if (!StringUtils.isEmpty(keyword)) {
            // keyword is not empty, search with keyword
            url = "http://" + NUWA_HOST + "/api/resource/cloud/search";
            params.put("keyword", keyword);
        }
        params.put("stagesubject", subjectCode);
        params.put("source_type", String.valueOf(sourceType));
        params.put("order", String.valueOf(order));
        if (!StringUtils.isEmpty(cate)) {
            // empty for all
            params.put("cate", cate);
        }
        if (!StringUtils.isEmpty(portfolioId)) {
            // empty for all
            params.put("code", portfolioId);
        }
        params.put("book", bookId);
        params.put("gradecode", gradeCode);
        params.put("page", String.valueOf(page));
        params.put("order_field", orderField);
        return getFromNuwaUrl(url, params);
    }

    /**
     * get resource preview contents (may be a URL)
     * 
     * @param resourceId
     * @return
     */
    public JSONObject getResourcePreview(String resourceId) {
        String url = "http://" + NUWA_HOST + "/api/resource/preview";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("code", resourceId);
        JSONObject json = getFromNuwaUrl(url, params);
        String previewUrl = json.optString("data");
        // add domain to the preview url
        previewUrl = "http://" + NUWA_HOST + previewUrl;
        json.put("data", previewUrl);
        return json;
    }

    /**
     * get download resource url for the controller to handle download process
     * by itself
     * 
     * @param resourceId
     * @param responseOutStream
     * @return
     */
    public String getDownloadUrl(String resourceId) {
        String url = "http://" + NUWA_HOST + "/api/resource/download";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("code", resourceId);
        url = generateGetUrl(url, params);
        return url;
    }

    /**
     * add collection count to resource with the given id
     * 
     * @return
     */
    public JSONObject collectResource(String subjectCode, String resourceId,
            long userId) {
        // first add to favorite in YCL
        // then add collection count to Nuwa platform
        String url = "http://" + NUWA_HOST + "/api/resource/collect";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("code", resourceId);
        return getFromNuwaUrl(url, params);
    }

    public Header[] generateNuwaHeaders() {
        String token = getNuwaToken();
        Header[] headers = new Header[] {
                new BasicHeader("Cookie", "nuwa_token=" + token),
                new BasicHeader("Content-Type", "application/json") };
        return headers;
    }

    /**
     * get resource detail by resource id array
     * @param idList
     * @return
     */
    public JSONObject getResourcesByIdList(List<String> idList) {
        String url = "http://" + NUWA_HOST + "/api/resource/batch";
        String[] idArr = idList.toArray(new String[0]);
        JSONObject params = new JSONObject();
        params.put("list", idArr);
        return postToNuwaUrl(url, params);
    }

    /**
     * send get request to a given Nuwa url with given params, the params goes
     * to request url
     * 
     * @param url
     *            : target url
     * @param params
     *            : params in map fromat
     * @return: json format results, empty json if fails
     */
    private JSONObject getFromNuwaUrl(String url, HashMap<String, String> params) {
        Header[] headers = generateNuwaHeaders();
        url = generateGetUrl(url, params);
        LOG.debug("get from Nuwa url:" + url);
        JSONObject json = WebUtils.getMethod(url, headers);
        if (json != null) {
            String jsonStr = json.toString();
            LOG.debug("Nuwa result json:"
                    + (jsonStr.length() > 100 ? jsonStr.substring(0, 100)
                            : jsonStr));
        }
        return json;
    }


    /**
     * post request to a given Nuwa url with given Json params, the params goes
     * to request body
     * 
     * @param url
     *            : the target url
     * @param params
     *            : params in json format
     * @return
     */
    private JSONObject postToNuwaUrl(String url, JSONObject params) {
        String token = getNuwaToken();
        Header[] headers = new Header[] {
                new BasicHeader("Cookie", "nuwa_token=" + token),
                new BasicHeader("Content-Type", "application/json") };
        try {
            LOG.debug("post to Nuwa url:" + url + ", params:" + params);
            JSONObject json = WebUtils.postWithJsonInBody(url, params, headers);
            LOG.debug("login result json:" + json);
            return json;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new JSONObject();
        }
    }

    private String generateGetUrl(String baseUrl, HashMap<String, String> params) {
        baseUrl += params.size() > 0 ? "?" : "";
        List<String> paramList = new ArrayList<String>();
        for (Iterator<String> it = params.keySet().iterator(); it.hasNext();) {
            String key = it.next();
            String value = params.get(key);
            paramList.add(key + "=" + value);
        }
        return baseUrl += StringUtils.join(paramList, "&");
    }

    /**
     * get nuwa token of the default user
     *
     * @return
     */
    private String getNuwaToken() {
        String token = "";
        long start = System.currentTimeMillis();
        token = login(USERNAME, PASSWORD);
        long duration = System.currentTimeMillis() - start;
        System.out.println("get Nuwa token time:" + duration);
        return token;
    }

    /**
     * login with given user name and password
     * 
     * @return: token of the login user if succeed, otherwise ""
     */
    private String login(String username, String password) {
        String url = "http://" + NUWA_HOST + "/api/account/login";
        JSONObject params = new JSONObject();
        params.put("username", username);
        params.put("password", password);
        Header[] header = new Header[] { new BasicHeader("Content-Type",
                "application/json") };
        try {
            JSONObject json = WebUtils.postWithJsonInBody(url, params, header);
            LOG.debug("login result json:" + json);
            if (json.optInt("statusCode") == 200) {
                return json.optString("token");
            }
            return "";
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public static void main(String[] args) {
        TeachingResourceService trs = new TeachingResourceService();
//        JSONObject json = trs.getChapters("21",
//                "3472a37a-4255-3f0c-a6ca-e921370b5f7c");
//        System.out.println(json);
        long start = System.currentTimeMillis();
//        json = trs.collectResource("22","937cc7b2-015a-11e6-a216-0242ac117592",0);
//        teachingResourceSvc.searchResources(subjectCode,
//                sourceType, portfolioId, keyword, order, cate, bookCode,
//                gradeCode, page, orderField);
        JSONObject json = trs.searchResources("22", 2, "449f4dc5-a3d6-3f39-8f7a-a4bdd2a4d31a",
                "", 1, null, "9d754883-a2d8-343a-88e7-9f32a4e34d05", 
                "e01a65da-bc9f-36aa-822b-cd2360eec999", 1, "upload_time");
        long duration = System.currentTimeMillis() - start;
        System.out.println("Total duration:" + duration + ", \r\n" + json);
//        List<String> idList = new ArrayList<String>();
//        idList.add("937cc7b2-015a-11e6-a216-0242ac117592");
//        json = trs.getResourcesByIdList(idList);
//        System.out.println(json);
    }
}
