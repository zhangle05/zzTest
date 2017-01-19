/**
 * 
 */
package test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * @author zhangle
 *
 */
public class WebUtils {

    private static Log LOG = LogFactory.getLog(WebUtils.class);

    public static String getJsonStrFromPostUrl(String url,
            Map<String, String> params) {
        List<NameValuePair> paramList = new ArrayList<NameValuePair>();
        if(params!=null){
            NameValuePair p = null;
            for (String key : params.keySet()) {
                p = new BasicNameValuePair(key, params.get(key));
                paramList.add(p);
            }
        }
        return getJsonStrFromPostUrl(url, paramList);
    }

    public static String getJsonFromPostUrl(String url,
            Map<String, Object> params) {
        List<NameValuePair> paramList = new ArrayList<NameValuePair>();
        if(params!=null){
            NameValuePair p = null;
            Object item = null;
            for (String key : params.keySet()) {
                item = params.get(key);
                if(item !=null){
                    if(item instanceof List){
                        List<?> list = (List<?>)item;
                        if(list.size() == 0) {
                            paramList.add(new BasicNameValuePair(key, ""));
                        } else {
                            for(Object d:list){
                                p = new BasicNameValuePair(key, String.valueOf(d));
                                paramList.add(p);
                            }
                        }
                    } else {
                        p = new BasicNameValuePair(key, String.valueOf(item));
                        paramList.add(p);
                    }
                } else {
                    paramList.add(new BasicNameValuePair(key, ""));
                }
            }
        }
        return getJsonStrFromPostUrl(url, paramList);
    }
    
    public static String getJsonStrFromPostUrl(String url,
            List<NameValuePair> params) {
        return getJsonStrFromPostUrlWithContentType(url, params, ContentType.APPLICATION_JSON);
    }

    public static String getJsonStrFromPostUrlWithContentType(String url,
            List<NameValuePair> params, ContentType contentType) {
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost post = new HttpPost(url);
            post.addHeader("Content-Type", contentType.toString());
            LOG.info("try get json with post method, uri is:" + post.getURI() 
            + ", ContentType is:" + post.getHeaders("Content-Type")[0].getValue());
            post.setConfig(createRequestConfig());
            HttpEntity entity = new UrlEncodedFormEntity(params, StandardCharsets.UTF_8);
            post.setEntity(entity);
            String result = "";
            try {
                HttpResponse response = client.execute(post);
                int  statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    /* 读返回数据 */
                    HttpEntity resEntity = response.getEntity();
                    if (resEntity != null) {
                        result = EntityUtils.toString(resEntity);
                    }
                    if (!StringUtils.isEmpty(result)) {
                        LOG.info("result is:" + (result.length() > 20 ?
                                (result.substring(0, 20) + "...") : result));
                    } else {
                        LOG.info("result is empty.");
                    }
                    return result;
                } else {
                    LOG.info("status error:"
                            + statusCode
                            + EntityUtils.toString(response.getEntity(), "UTF-8"));
                    return "";
                }
            } catch (ClientProtocolException ex) {
                LOG.info("getJsonStrFromUrl done with exception" + ex);
                ex.printStackTrace();
            } catch (IOException ex) {
                LOG.info("getJsonStrFromUrl done with exception" + ex);
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            LOG.info("getJsonStrFromUrl done with exception" + ex);
            ex.printStackTrace();
        }
        LOG.info("getJsonStrFromUrl, error happened.");
        return "";
    }

    public static String getJsonFromPostUrlAsForm(String url,
            Map<String, Object> params) {
        List<NameValuePair> paramList = new ArrayList<NameValuePair>();
        if(params!=null){
            NameValuePair p = null;
            Object item = null;
            for (String key : params.keySet()) {
                item = params.get(key);
                if(item !=null){
                    if(item instanceof List){
                        List<?> list = (List<?>)item;
                        if(list.size() == 0) {
                            paramList.add(new BasicNameValuePair(key, ""));
                        } else {
                            for(Object d:list){
                                p = new BasicNameValuePair(key, String.valueOf(d));
                                paramList.add(p);
                            }
                        }
                    } else {
                        p = new BasicNameValuePair(key, String.valueOf(item));
                        paramList.add(p);
                    }
                } else {
                    paramList.add(new BasicNameValuePair(key, ""));
                }
            }
        }
        return getJsonStrFromPostUrlWithContentType(url, paramList, ContentType.APPLICATION_FORM_URLENCODED);
    }

    public static String getJsonStrFromPostUrl(String url,String inputJson) {
    	JSONObject result = new JSONObject();
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost post = new HttpPost(url);
            post.addHeader("Content-Type", "application/json");
            LOG.info("try get json, uri is:" + post.getURI()+"\tbody:"+inputJson);
            post.setConfig(createRequestConfig());
            post.setEntity(new StringEntity(inputJson,StandardCharsets.UTF_8));            
            String res = "";
            try {
                HttpResponse response = client.execute(post);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200 || statusCode == 201) {
                    /* 读返回数据 */
                    HttpEntity resEntity = response.getEntity();
                    if (resEntity != null) {
                    	res = EntityUtils.toString(resEntity);
                    }
                    if (!StringUtils.isEmpty(res)) {
                        LOG.info("result is:" + (res.length() > 20 ?
                                (res.substring(0, 20) + "...") : res));
                    } else {
                        LOG.info("result is empty.");
                    }
                    result = JSONObject.fromObject(res);
                } else {
                	String entity = EntityUtils.toString(response.getEntity(), "UTF-8");
//                    LOG.info("status error:"+ statusCode + entity);
                    result.put("msg", entity);
                }
                result.put("statusCode", statusCode);
                return result.toString();
            } catch (ClientProtocolException ex) {
                LOG.info("getJsonStrFromUrl done with exception" + ex);
                result.put("msg", ex.getMessage());
                ex.printStackTrace();
            } catch (IOException ex) {
                LOG.info("getJsonStrFromUrl done with exception" + ex);
                result.put("msg", ex.getMessage());
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            LOG.info("getJsonStrFromUrl done with exception" + ex);
            result.put("msg", ex.getMessage());
            ex.printStackTrace();
        }
        LOG.info("getJsonStrFromUrl, error happened.");
        return result.toString();
    }

    public static String getJsonStrFromGetUrl(String url) {
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpGet get = new HttpGet(url);
            get.addHeader("Content-Type", "application/json");
            LOG.info("try get json, uri is:" + get.getURI());
            String result = "";
            try {
                HttpResponse response = client.execute(get);
                if (response.getStatusLine().getStatusCode() == 200) {
                    /* 读返回数据 */
                    HttpEntity resEntity = response.getEntity();
                    if (resEntity != null) {
                        result = EntityUtils.toString(resEntity);
                    }
                    if (!StringUtils.isEmpty(result)) {
                        LOG.info("result is:" + (result.length() > 20 ?
                                (result.substring(0, 20) + "...") : result));
                    } else {
                        LOG.info("result is empty.");
                    }
                    return result;
                } else {
                    LOG.info("status error:"
                            + response.getStatusLine().getStatusCode()
                            + EntityUtils.toString(response.getEntity(), "UTF-8"));
                    return "";
                }
            } catch (ClientProtocolException ex) {
                LOG.info("getJsonStrFromGetUrl done with exception" + ex);
                ex.printStackTrace();
            } catch (IOException ex) {
                LOG.info("getJsonStrFromGetUrl done with exception" + ex);
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            LOG.info("getJsonStrFromGetUrl done with exception" + ex);
            ex.printStackTrace();
        }
        LOG.info("getJsonStrFromGetUrl, error happened.");
        return "";
    }

    public static String getJsonStrFromUrl(String url) {
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpGet get = new HttpGet(url);
            LOG.info("try get json, uri is:" + get.getURI());
            get.setConfig(createRequestConfig());
            String result = "";
            try {
                HttpResponse response = client.execute(get);
                if (response.getStatusLine().getStatusCode() == 200) {
                    /* 读返回数据 */
                    HttpEntity resEntity = response.getEntity();
                    if (resEntity != null) {
                        result = EntityUtils.toString(resEntity, "UTF-8");
                    }
                    if (!StringUtils.isEmpty(result)) {
                        LOG.info("result is:" +  result);
                    } else {
                        LOG.info("result is empty.");
                    }
                    return result;
                } else {
                    LOG.info("status error:"
                            + response.getStatusLine().getStatusCode()
                            + EntityUtils.toString(response.getEntity(), "UTF-8"));
                    return "";
                }
            } catch (ClientProtocolException ex) {
                LOG.info("getJsonStrFromUrl done with exception" + ex);
                ex.printStackTrace();
            } catch (IOException ex) {
                LOG.info("getJsonStrFromUrl done with exception" + ex);
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            LOG.info("getJsonStrFromUrl done with exception" + ex);
            ex.printStackTrace();
        }
        LOG.info("getJsonStrFromUrl, error happened.");
        return "";
    }

    private static RequestConfig createRequestConfig() {
        RequestConfig config = RequestConfig.custom().setSocketTimeout(4000)
                .setConnectTimeout(2000).setConnectionRequestTimeout(2000)
                .setStaleConnectionCheckEnabled(true).build();
        return config;
    }

    public static String getJsonStrFromPostUrl(String url,
            Map<String, String> params, Map<String, File> files) {
        if (files == null || files.isEmpty()) {
            return getJsonStrFromPostUrl(url, params);
        }
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setCharset(Charset.forName("utf-8"));// 设置请求的编码格式
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);// 设置浏览器兼容模式
        if (params != null) {
            for (String key : params.keySet()) {
                builder.addTextBody(key, params.get(key));// 设置请求参数
            }
        }
        FileBody body = null;
        for (String key : files.keySet()) {
            body = new FileBody(files.get(key));
            builder.addPart(key, body);// 设置请求参数
        }
        HttpEntity entity = builder.build();// 生成 HTTP POST 实体
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost post = new HttpPost(url);
            LOG.info("try get json, uri is:" + post.getURI());
            post.setConfig(createRequestConfig());
            post.setEntity(entity);
            String result = "";
            try {
                HttpResponse response = client.execute(post);
                if (response.getStatusLine().getStatusCode() == 200) {
                    /* 读返回数据 */
                    HttpEntity resEntity = response.getEntity();
                    if (resEntity != null) {
                        result = EntityUtils.toString(resEntity);
                    }
                    if (!StringUtils.isEmpty(result)) {
                        LOG.info("result is:" + (result.length() > 20 ?
                                (result.substring(0, 20) + "...") : result));
                    } else {
                        LOG.info("result is empty.");
                    }
                    return result;
                } else {
                    LOG.info("status error:"
                            + response.getStatusLine().getStatusCode());
                    return "";
                }
            } catch (ClientProtocolException ex) {
                LOG.info("getJsonStrFromPostUrl done with exception" + ex);
                ex.printStackTrace();
            } catch (IOException ex) {
                LOG.info("getJsonStrFromPostUrl done with exception" + ex);
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            LOG.info("getJsonStrFromPostUrl done with exception" + ex);
            ex.printStackTrace();
        }
        LOG.info("getJsonStrFromPostUrl, error happened.");
        return "";
    }

    public static JSONObject postMethod(String url, List<NameValuePair> list, Header[] headers) {
    	CloseableHttpClient client = HttpClients.createDefault();	
    	
    	HttpPost post = new HttpPost(url);
        if (headers != null && headers.length > 0) {
            post.setHeaders(headers);
        }
        HttpEntity entity = null;
        CloseableHttpResponse response = null;
        JSONObject result = new JSONObject();
        LOG.info("try post json, uri is:" + url);
        try {
            post.setEntity(new UrlEncodedFormEntity(list, StandardCharsets.UTF_8));
            response = client.execute(post);
            int  statusCode = response.getStatusLine().getStatusCode();
            
            if (statusCode == 200 || statusCode == 201) {
                entity = response.getEntity(); 
                String body = null;
                if (entity != null) {
                    body = EntityUtils.toString(entity, "UTF-8");  
                    result = JSONObject.fromObject(body);
                    EntityUtils.consume(entity);
                }
                
            } else {
            	String responseEntity = EntityUtils.toString(response.getEntity(), "UTF-8");
                LOG.info("status error:"+ statusCode);
                if (responseEntity != null && responseEntity.length() > 100) {
                    responseEntity = responseEntity.substring(0, 100);
                }
                LOG.info("response:" + responseEntity);
                String description = null;
            	if(! StringUtils.isEmpty(responseEntity)){
            		JSONObject errorMsg = JSONObject.fromObject(responseEntity);
            		String errorDescription = errorMsg.optString("error_description");
            		String error =  errorMsg.optString("error");
            		description = !StringUtils.isEmpty(errorDescription)? errorDescription : error;
            		if (StringUtils.isEmpty(description)) {
                		description = errorMsg.optString("detail");
                	}
            	}
                
                result.put("msg", description);
            }
            result.put("statusCode", statusCode);
            
        } catch (ClientProtocolException ex) {
            LOG.error("ClientProtocolException", ex);
        } catch (IOException ex) {
            LOG.error("IOException", ex);
        } finally {
            if(null != response){
                try {
                    response.close();
                } catch (IOException e) {
                    LOG.info("postMethod close exception" + e);
                }
            }
        }
        return result;
    }

    public static JSONObject postWithJsonInBody(String url, JSONObject json, Header[] headers) {
        CloseableHttpClient client = HttpClients.createDefault();   
        
        HttpPost post = new HttpPost(url);
        if (headers != null && headers.length > 0) {
            post.setHeaders(headers);
        }
        HttpEntity entity = null;
        CloseableHttpResponse response = null;
        JSONObject result = new JSONObject();
        LOG.info("try post json, uri is:" + url);
        try {
            post.setEntity(new StringEntity(json.toString(),StandardCharsets.UTF_8));
            response = client.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();
            LOG.info("status code:" + statusCode);
            if (statusCode == 200 || statusCode == 201) {
                entity = response.getEntity(); 
                String body = null;
                if (entity != null) {
                    body = EntityUtils.toString(entity, "UTF-8");
                    if(StringUtils.isNotEmpty(body)){
                        if (body.startsWith("[")) {
                            JSONArray arr = JSONArray.fromObject(body);
                            result.put("data", arr);
                        } else {
                            result = JSONObject.fromObject(body);
                        }
                    }
                    EntityUtils.consume(entity);
                    LOG.info("result:" + result + ", isEmpty:" + result.isEmpty()
                            + ", containsKey:" + result.containsKey("code"));
                }
            } else {
                String responseEntity = EntityUtils.toString(response.getEntity(), "UTF-8");
                LOG.info("status error:"+ statusCode);
                String description = null;
            	if(! StringUtils.isEmpty(responseEntity)){
            		JSONObject errorMsg = JSONObject.fromObject(responseEntity);
            		String errorDescription = errorMsg.optString("error_description");
            		String error =  errorMsg.optString("error");
            		description = !StringUtils.isEmpty(errorDescription)? errorDescription : error;
            		if (StringUtils.isEmpty(description)) {
                		description = errorMsg.optString("detail");
                	}
            	}
                
                result.put("msg", description);
            }
            result.put("statusCode", statusCode);
        } catch (ClientProtocolException ex) {
            LOG.error("ClientProtocolException", ex);
        } catch (IOException ex) {
            LOG.error("IOException", ex);
        } finally {
            if(null != response){
                try {
                    response.close();
                } catch (IOException e) {
                    LOG.info("postMethod close exception" + e);
                }
            }
        }
        return result;
    }

    public static JSONObject getMethod(String url, Header[] headers) {
    	CloseableHttpClient client = HttpClients.createDefault();
    	
    	HttpGet get = new HttpGet(url);
        if (headers != null && headers.length > 0) {
            get.setHeaders(headers);
        }
        HttpEntity entity = null;
        CloseableHttpResponse response = null;
        JSONObject result = new JSONObject();
        LOG.info("try get json, uri is:" + url);
        try {
            response = client.execute(get);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                entity = response.getEntity(); 
                String body = null;
                if (entity != null) {
                    body = EntityUtils.toString(entity, "UTF-8"); 
                    EntityUtils.consume(entity);
                }
                if (!StringUtils.isEmpty(body)) {
                    LOG.info("result is:" + body.substring(0, body.length() > 20 ? 20 : body.length()) + "...");
                } else {
                    LOG.info("result is empty.");
                }
                if (body.startsWith("[")) {
                    JSONArray arr = JSONArray.fromObject(body);
                    result.put("data", arr);
                } else {
                    result = JSONObject.fromObject(body);
                }
            } else {
            	String responseEntity = EntityUtils.toString(response.getEntity(), "UTF-8");
                LOG.info("status error:"+ statusCode);
                String description = null;
            	if(! StringUtils.isEmpty(responseEntity)){
            		JSONObject errorMsg = JSONObject.fromObject(responseEntity);
            		String errorDescription = errorMsg.optString("error_description");
            		String error =  errorMsg.optString("error");
            		description = !StringUtils.isEmpty(errorDescription)? errorDescription : error;
            		if (StringUtils.isEmpty(description)) {
                		description = errorMsg.optString("detail");
                	}
            	}
                
                result.put("msg", description);
            }
            
            result.put("statusCode", statusCode);
        } catch (ClientProtocolException ex) {
            LOG.error("", ex);
        } catch (IOException ex) {
            LOG.error("", ex);
        } finally {
            if(null != response){
                try {
                    response.close();
                } catch (IOException e) {
                    LOG.info("getMethod" + e);
                }
            }
        }
        return result;  
    }

    public static JSONObject patchMethod(String url, String inputJson, Header[] headers) {
    	CloseableHttpClient client = HttpClients.createDefault();	
    	
    	HttpPatch put = new HttpPatch(url);
        if (headers != null && headers.length > 0) {
            put.setHeaders(headers);
        }
        HttpEntity entity = null;
        CloseableHttpResponse response = null;
        JSONObject result = new JSONObject();
        LOG.info("try patch json, uri is:" + url);
        try {
            put.setEntity(new StringEntity(inputJson,StandardCharsets.UTF_8));
            response = client.execute(put);
            int  statusCode = response.getStatusLine().getStatusCode();
            
            if (statusCode == 200 || statusCode == 201) {
                entity = response.getEntity(); 
                String body = null;
                if (entity != null) {
                    body = EntityUtils.toString(entity, "UTF-8");  
                    result = JSONObject.fromObject(body);
                    EntityUtils.consume(entity);
                }
                
            } else {
            	String responseEntity = EntityUtils.toString(response.getEntity(), "UTF-8");
                LOG.info("status error:"+ statusCode);
                String description = null;
            	if(! StringUtils.isEmpty(responseEntity)){
            		JSONObject errorMsg = JSONObject.fromObject(responseEntity);
            		String errorDescription = errorMsg.optString("error_description");
            		String error =  errorMsg.optString("error");
            		description = !StringUtils.isEmpty(errorDescription)? errorDescription : error;
            		if (StringUtils.isEmpty(description)) {
                		description = errorMsg.optString("detail");
                	}
            	}
            
                result.put("msg", description);
            }
            result.put("statusCode", statusCode);
            
        } catch (ClientProtocolException ex) {
            LOG.error("ClientProtocolException", ex);
        } catch (IOException ex) {
            LOG.error("IOException", ex);
        } finally {
            if(null != response){
                try {
                    response.close();
                } catch (IOException e) {
                    LOG.info("postMethod close exception" + e);
                }
            }
        }
        return result;
    }
    
}
