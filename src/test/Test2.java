/**
 * 
 */
package test;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

import net.sf.json.JSONObject;

/**
 * @author zhangle
 *
 */

public class Test2 {
 
    private static final String HMAC_SHA1 = "HMACSHA1";
    private static final String ACCESS_KEY = "FlYtX6d7G__okaQW8J3KY-MCpoKjdFaveHOum2ow";
    private static final String SECRET_KEY = "_PxKW1ACqjrVv9a4VwZp6ZNPq56yowrAb_kPdbzV";
//
//    public static void main(String[] args) {
//        try {
//            System.out.println(URLEncoder.encode("vid=198E34212094A8E89C33DC5901307461&siteid=0C728A4805962911&autoStart=true&width=100%&height=700&playerid=A8E5C78ED3BA6213&playertype=1", "utf-8"));
//            System.out.println();
//            System.out.println();
//        } catch (UnsupportedEncodingException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        String code = "1522000011442015360106";
//        System.out.println("Long.max is:" + Long.MAX_VALUE);
//        long value = Long.parseLong(code);
//        System.out.println("Long.max is:" + Long.MAX_VALUE + ", value is:" + value);
//        java.util.Date now = new java.util.Date();
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(now);
//        System.out.println(cal.getTime());
//        cal.add(Calendar.DAY_OF_MONTH, 30);
//        System.out.println(cal.getTime());
//
//    	UUID tmp = UUID.randomUUID();
//    	System.out.println(tmp);
//    	System.out.println("珠海市斗门区斗门镇初级中学".hashCode());
////    	String result = upload();
////    	System.out.println(result);
//    	String version = "2.7.0";
//		String[] versionArr = version.split("\\.");
//		for(int i=0; i<versionArr.length; i++) {
//			System.out.println(versionArr[i]);
//		}
//    }

    public static byte[] getSignatureBytes(String data, String key)
            throws InvalidKeyException, NoSuchAlgorithmException {
        byte[] keyBytes = key.getBytes();
        Key signingKey = new SecretKeySpec(keyBytes, HMAC_SHA1);
        Mac mac = Mac.getInstance(HMAC_SHA1);
        mac.init(signingKey);
        byte[] rawHmac = mac.doFinal(data.getBytes());
        return rawHmac;
    }

    public static byte[] getHmacSHA1(String data, String key) 
    	throws NoSuchAlgorithmException, InvalidKeyException {
    	byte[] keybytes = key.getBytes();  
        //根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称  
        SecretKey secretKey = new SecretKeySpec(keybytes, HMAC_SHA1);   
        //生成一个指定 Mac 算法 的 Mac 对象  
        Mac mac = Mac.getInstance(HMAC_SHA1);   
        //用给定密钥初始化 Mac 对象  
        mac.init(secretKey);

        byte[] text = data.getBytes();
        //完成 Mac 操作
        return mac.doFinal(text);
    }

    private static String urlsafe_base64_encode(String src) {
    	String result = Base64.encodeBase64String(src.getBytes());
		result = result.replace('+', '-');
		result = result.replace('/', '_');
		return result;
    }

    private static String urlsafe_base64_encode(byte[] code) {
    	String result = Base64.encodeBase64String(code);
		result = result.replace('+', '-');
		result = result.replace('/', '_');
		return result;
    }

    private static String getKey(String fileName) {
    	java.util.Date now = new java.util.Date();
    	String nowStr = new SimpleDateFormat("yyyy-M-dd_HH-mm-ss-").format(now);
    	int rand = new Random().nextInt(10000);
    	String ext = fileName.substring(fileName.lastIndexOf('.'));
    	fileName = nowStr + rand + ext;
    	return fileName;
    }

    private static String getToken(String key)
    		throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
    	java.util.Date now = new java.util.Date();
    	long deadline = now.getTime() + 1000 * 3600 * 24; // one day after
    	JSONObject json = new JSONObject();
    	json.put("scope", "jyocr:" + key);
    	json.put("deadline", deadline/1000);

    	String jsonStr = json.toString();//"{\"scope\":\"jyocr\",\"deadline\":1420550700}";
    			
    	String encodedPutPolicy = urlsafe_base64_encode(jsonStr);

    	String encodedSign = getEncodedSign(encodedPutPolicy);
    	
    	String token = ACCESS_KEY + ":" + encodedSign + ":" + encodedPutPolicy;
    	return token;
    }

	private static String getEncodedSign(String encodedPutPolicy)
    		throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
		byte[] sign = getSignatureBytes(encodedPutPolicy, SECRET_KEY);
		String encodedSign = urlsafe_base64_encode(sign);
		return encodedSign;
	}

	private static String upload() {
    	String uploadUrl = "http://upload.qiniu.com";
    	String imgPath = "/Users/zhangle/dev/jingyou/2014-12-05.jpg";
    	HttpClient httpclient = new DefaultHttpClient();
		httpclient.getParams().setParameter(
				CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		HttpPost post = new HttpPost(uploadUrl);
		String result = "";
		// imgPath.substring(imgPath.lastIndexOf(".") + 1)
		try {
			File file = new File(imgPath);
			MultipartEntity entity = new MultipartEntity();
			FileBody fb = new FileBody(file);

			entity.addPart("file", fb);
			String key = getKey(imgPath);
			StringBody keyPart = new StringBody(key);
			entity.addPart("key", keyPart);
			StringBody tokenPart = new StringBody(getToken(key));
			entity.addPart("token", tokenPart);
			post.setEntity(entity);

			HttpResponse response = httpclient.execute(post);
			HttpEntity resEntity = response.getEntity();
			if (resEntity != null) {
				result = EntityUtils.toString(resEntity);
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return result;
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
    }

	public static void main(String[] args) {
	    List<LectureScheduleDetailAdapter> result = new ArrayList<LectureScheduleDetailAdapter>();
	    LectureScheduleDetailAdapter l1 = new LectureScheduleDetailAdapter();
	    l1.setId(1);
	    l1.setStartTime(new java.util.Date(1495440000000L));
	    result.add(l1);
	    LectureScheduleDetailAdapter l2 = new LectureScheduleDetailAdapter();
        l2.setId(2);
        l2.setStartTime(new java.util.Date(1495447200000L));
        result.add(l2);
        LectureScheduleDetailAdapter l3 = new LectureScheduleDetailAdapter();
        l3.setId(3);
        l3.setStartTime(new java.util.Date(1495443600000L));
        result.add(l3);
        LectureScheduleDetailAdapter l4 = new LectureScheduleDetailAdapter();
        l4.setId(4);
        l4.setStartTime(new java.util.Date(1495438800000L));
        result.add(l4);
        Collections.sort(result,
                new Comparator<LectureScheduleDetailAdapter>() {

                    @Override
                    public int compare(LectureScheduleDetailAdapter o1,
                            LectureScheduleDetailAdapter o2) {
                        if (o1 == null || o2 == null
                                || o1.getStartTime() == null
                                || o2.getStartTime() == null) {
                            return 0;
                        }
                        if (o1.getStartTime().getTime() > o2.getStartTime()
                                .getTime()) {
                            return 1;
                        }
                        if (o1.getStartTime().getTime() < o2.getStartTime()
                                .getTime()) {
                            return -1;
                        }
                        return 0;
                    }
                });
        for (LectureScheduleDetailAdapter l : result) {
            System.out.println(l.getId());
            System.out.println(l.getStartTime());
        }
	}
	
	static class LectureScheduleDetailAdapter {
	    int id;
	    java.util.Date startTime;
	    
        /**
         * @return the startTime
         */
        public java.util.Date getStartTime() {
            return startTime;
        }
        /**
         * @param startTime the startTime to set
         */
        public void setStartTime(java.util.Date startTime) {
            this.startTime = startTime;
        }
        /**
         * @return the id
         */
        public int getId() {
            return id;
        }
        /**
         * @param id the id to set
         */
        public void setId(int id) {
            this.id = id;
        }
	    
	}
}