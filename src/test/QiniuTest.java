/**
 * 
 */
package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * @author zhangle
 * 
 */
public class QiniuTest {

    private static final String QINIU_ACCESS_KEY = "FlYtX6d7G__okaQW8J3KY-MCpoKjdFaveHOum2ow";
    private static final String QINIU_SECRET_KEY = "_PxKW1ACqjrVv9a4VwZp6ZNPq56yowrAb_kPdbzV";
    private static final long MILS_OLD_ENOUGH_TO_DELETE = 2*3600*1000;
    private String mMarker = "eyJjIjowLCJrIjoicWlOaXU3MDc0MjY0OTlfamluZ3lvdTY1OTAxNzU2OS5qcGcifQ==";
    private int mDeleteCnt = 0;
    private int mTotalTime = 0;
    private int mOneRoundTime = 0;

    /**
     * @param args
     */
    public static void main(String[] args) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long start = System.currentTimeMillis();
        String startTime = sdf.format(new Date(start));
        QiniuTest test = new QiniuTest();
        int oneRoundTime = 10; // run for 10 minutes
        test.LOG("args length:" + args.length);
        if(args != null && args.length > 0) {
            test.LOG("args are:");
            for(int i=0; i<args.length; i++) {
                test.LOG(args[i]);
            }
            try {
                oneRoundTime = Integer.parseInt(args[0]);
            }
            catch(Exception ex) {
                System.err.println("running time format error!");
            }
        }
        test.LOG();
        test.LOG("Round started at " + startTime);
        test.LOG("**********************Running time is: " + oneRoundTime);

        String fileName = "marker" + oneRoundTime;
        if(args.length > 1) {
            fileName = args[1];
        }
        test.mOneRoundTime = oneRoundTime;
        test.readMarker(oneRoundTime, fileName);
        test.LOG("marker is:" + test.mMarker);
        for(int i=0; i<oneRoundTime; i++) {
            test.LOG();
            test.LOG("***********************delete sub-round " + i);
            test.deleteProcess();
        }
        if(test.getDeleteCount() == 0) {
            // something is wrong with the marker, make a new one for the next run
            test.LOG("0 file deleted, make a new marker.");
            test.makeNewMarker(oneRoundTime);
        }
        test.saveMarker(oneRoundTime, fileName);
        test.LOG("Round finished in " + test.getTotalTime() + " seconds, " + test.getDeleteCount() + " files deleted.");
    }

    private void makeNewMarker(int oneRoundTime) {
        LOG("make new marker, days interval is:" + oneRoundTime);
        long timeInterval = oneRoundTime * 24L * 3600 * 1000;
        Date markerDay = new Date(System.currentTimeMillis() - timeInterval);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
        LOG("make new marker, marker day is:" + markerDay);
        String dateStr = sdf.format(markerDay);
        String prefix = "{\"c\":0,\"k\":\"" + dateStr + "\"}";
        LOG("make new marker, date str is:" + dateStr + ", prefix is:" + prefix);
        String newMarker = urlsafe_base64_encode(prefix);
        if(newMarker.equals(mMarker)) {
            makeNewMarker(oneRoundTime + 1);
        }
        else {
            mMarker = urlsafe_base64_encode(prefix);
        }
        LOG("make new marker, new marker is:" + mMarker);
    }

    public int getDeleteCount() {
        return mDeleteCnt;
    }

    public int getTotalTime() {
        return mTotalTime;
    }

    private void readMarker(int oneRoundTime, String fileName) {
        File f = new File(fileName);
        LOG("Read marker from file: " + f.getPath() + "/" + f.getName());
        if(f.exists()) {
            FileReader fr = null;
            BufferedReader br = null;
            try {
                fr = new FileReader(fileName);
                br = new BufferedReader(fr);
                String line = br.readLine();
                while(line != null) {
                    LOG("Reading line: " + line);
                    mMarker = line;
                    line = br.readLine();
                }
                LOG("Read marker finished, marker is: " + mMarker);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(fr != null) {
                    try {
                        fr.close();
                    } catch (IOException ignore) {
                    }
                }
                if(br != null) {
                    try {
                        br.close();
                    } catch (IOException ignore) {
                    }
                }
            }
        }
    }

    private void saveMarker(int oneRoundTime, String fileName) {
        File f = new File(fileName);
        LOG("save marker to file: " + f.getPath() + "/" + f.getName());
        if(!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            fw = new FileWriter(fileName);
            bw = new BufferedWriter(fw);
            LOG("Saving marker: " + mMarker);
            bw.write("\r\n" + mMarker);
            bw.flush();
            LOG("Save marker finished, marker is: " + mMarker);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(fw != null) {
                try {
                    fw.close();
                } catch (IOException ignore) {
                }
            }
            if(bw != null) {
                try {
                    bw.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

    private void deleteProcess() {
      String[] keepList = new String[] {
              "1970-1-01_19-50-33-5cd59c2a-6485-45ba-b7c0-0df2a53b8add.jpg",
              "2015-1-01_13-04-41-f10e510a-7e32-48c4-a3b2-cb544e603980.jpg",
              "2015-1-04_20-40-54-87e8e523-b30e-4321-ac3f-6d4cea617915.jpg",
              "2015-1-04_20-34-40-112b376d-ef26-4c2d-a8af-f8c41e3a653c.jpg" };
      long start = System.currentTimeMillis();
      long listTime = 0;
      int tryCount = 0;
      List<String> list = null;
      while (listTime < 60000 && (list == null || list.size() == 0)) { // retry
                                                                        // for
                                                                        // 1
                                                                        // minutes
          try {
              list = this.listQiniuKey(1000);
          } catch (Exception ex) {
              ex.printStackTrace();
          }
          tryCount++;
          listTime += System.currentTimeMillis() - start;
      }
      try {
          if (list == null) {
              LOG("get image list failed in " + listTime
                      / 1000 + " seconds (" + tryCount + " tries).");
              mTotalTime += (int)(listTime)/1000;
              return;
          }
          LOG("get image list in " + listTime / 1000
                  + " seconds (" + tryCount + " tries).");
          int deleteCnt = 0;
          for (int i = 0; i < list.size(); i++) {
              LOG("deleting:" + list.get(i) + "...");
              String key = list.get(i);
              boolean keep = false;
              for (int j = 0; j < keepList.length; j++) {
                  if (key.contains(keepList[j])) {
                      keep = true;
                      break;
                  }
              }
              if (keep) {
                  LOG("the file is in keep-list!");
                  continue;
              }
              Long uploadTime = this.getFileUploadTime(list.get(i));
              if(uploadTime < System.currentTimeMillis() - MILS_OLD_ENOUGH_TO_DELETE) {
                  String deleted = this.deleteOnQiniu(list.get(i));
                  LOG(deleted);
                  deleteCnt ++;
              }
              else {
                  LOG("the file is too new to delete!");
              }
              Thread.sleep(200);
          }
          LOG("Delete Round finished, " + deleteCnt + " files deleted.");
          mDeleteCnt += deleteCnt;
      } catch (Exception e) {
          e.printStackTrace();
      }
      int time = (int)((System.currentTimeMillis() - start)/1000);
      LOG("sub round finished in " + time + " seconds.");
      mTotalTime += time;
    }

    private List<String> listQiniuKey(int size) throws Exception {
        String qiniuUrl = "http://rsf.qbox.me";
        CloseableHttpClient client = HttpClients.createDefault();
        List<String> resultList = new ArrayList<String>();
        String path = "/list?bucket=jyocr&limit=100&marker=" + mMarker;
        URI uri = new URI(qiniuUrl + path);
        try {
            byte[] sign = getSignatureBytes(path + "\n", QINIU_SECRET_KEY);
            String encodedSign = urlsafe_base64_encode(sign);
            String token = QINIU_ACCESS_KEY + ":" + encodedSign;

            HttpPost post = new HttpPost(uri);
//            RequestConfig config = RequestConfig.custom()
//                    .setSocketTimeout(20000).setConnectTimeout(20000)
//                    .setConnectionRequestTimeout(20000)
//                    .setStaleConnectionCheckEnabled(true).build();
//            post.setConfig(config);
            post.addHeader("Accept", "application/json");
            post.addHeader("Content-Type", "application/x-www-form-urlencoded");
            post.addHeader("Host", "rsf.qbox.me");
            post.addHeader("Authorization", "QBox " + token);
            String result = "";
            HttpResponse response = client.execute(post);
            int code = response.getStatusLine().getStatusCode();
            LOG("Status code:" + code);
            if (code == 200) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity);
                }
                LOG("Status is ok, json result is:" + result);
                JSONObject json = JSONObject.fromObject(result);
                JSONArray arr = json.getJSONArray("items");
                for (int i = 0; i < arr.size(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    resultList.add(obj.getString("key"));
                }
                mMarker = json.optString("marker");
            } else if (code == 400) {
                throw new Exception("error 400, request format error!");
            } else if (code == 401) {
                throw new Exception("error 401, unauthorized request!");
            } else if (code == 599) {
                throw new Exception("error 599, fail to delete on server side!");
            } else if (code == 612) {
                throw new Exception("error 612, file does not exist!");
            }
            return resultList;
        } finally {
            try {
                if (client != null) {
                    client.close();
                }
            } catch (IOException ignore) {
            }
        }
    }

    private long getFileUploadTime(String key) {
        String qiniuUrl = "http://rs.qiniu.com";
        CloseableHttpClient client = HttpClients.createDefault();

        try {
            String encodedEntryURI = this.urlsafe_base64_encode("jyocr:" + key);
            String path = "/stat/" + encodedEntryURI;
            URI uri = new URI(qiniuUrl + path);

            byte[] sign = getSignatureBytes(path + "\n", QINIU_SECRET_KEY);
            String encodedSign = urlsafe_base64_encode(sign);
            String token = QINIU_ACCESS_KEY + ":" + encodedSign;

            HttpPost post = new HttpPost(uri);
            RequestConfig config = RequestConfig.custom()
                    .setSocketTimeout(4000).setConnectTimeout(4000)
                    .setConnectionRequestTimeout(4000)
                    .setStaleConnectionCheckEnabled(true).build();
            post.setConfig(config);
            post.addHeader("Accept", "application/json");
            post.addHeader("Content-Type", "application/json");
            post.addHeader("Authorization", "QBox " + token);
            String result = "";
            HttpResponse response = client.execute(post);
            JSONObject json = new JSONObject();
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity);
                }
                if (StringUtils.isEmpty(result)) {
                    LOG("result is empty, cannot get the file upload time");
                } else {
                    json = JSONObject.fromObject(result);
                    long time = json.getLong("putTime") / 10000; // 单位是100纳秒
                    Date d = new Date(time);
                    String timeStr = new SimpleDateFormat("yyyy-M-dd_HH:mm:ss", Locale.ENGLISH).format(d);
                    LOG("the file is uploaded at " + timeStr);
                    return time;
                }
            } else if (code == 400) {
                json.accumulate("result", 0);
                json.accumulate("error", "request format error!");
            } else if (code == 401) {
                json.accumulate("result", 0);
                json.accumulate("error", "unauthorized request!");
            } else if (code == 599) {
                json.accumulate("result", 0);
                json.accumulate("error", "fail to delete on server side!");
            } else if (code == 612) {
                json.accumulate("result", 0);
                json.accumulate("error", "file does not exist!");
            }
            return Long.MAX_VALUE;
        } catch (Exception e) {
            e.printStackTrace();
            return Long.MAX_VALUE;
        } finally {
            try {
                if (client != null) {
                    client.close();
                }
            } catch (IOException ignore) {
            }
        }
    }

    private String deleteOnQiniu(String key) {
        String qiniuUrl = "http://rs.qiniu.com";
        CloseableHttpClient client = HttpClients.createDefault();

        try {
            String encodedEntryURI = this.urlsafe_base64_encode("jyocr:" + key);
            String path = "/delete/" + encodedEntryURI;
            URI uri = new URI(qiniuUrl + path);

            byte[] sign = getSignatureBytes(path + "\n", QINIU_SECRET_KEY);
            String encodedSign = urlsafe_base64_encode(sign);
            String token = QINIU_ACCESS_KEY + ":" + encodedSign;

            HttpPost post = new HttpPost(uri);
            RequestConfig config = RequestConfig.custom()
                    .setSocketTimeout(4000).setConnectTimeout(4000)
                    .setConnectionRequestTimeout(4000)
                    .setStaleConnectionCheckEnabled(true).build();
            post.setConfig(config);
            post.addHeader("Accept", "application/json");
            post.addHeader("Content-Type", "application/x-www-form-urlencoded");
            post.addHeader("Accept-Encoding", "identity");
            post.addHeader("Authorization", "QBox " + token);
            String result = "";
            HttpResponse response = client.execute(post);
            JSONObject json = new JSONObject();
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity);
                }
                if (StringUtils.isEmpty(result)) {
                    json.accumulate("result", 1);
                } else {
                    json = JSONObject.fromObject(result);
                    json.accumulate("result", 0);
                }
            } else if (code == 400) {
                json.accumulate("result", 0);
                json.accumulate("error", "request format error!");
            } else if (code == 401) {
                json.accumulate("result", 0);
                json.accumulate("error", "unauthorized request!");
            } else if (code == 599) {
                json.accumulate("result", 0);
                json.accumulate("error", "fail to delete on server side!");
            } else if (code == 612) {
                json.accumulate("result", 0);
                json.accumulate("error", "file does not exist!");
            }
            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            try {
                if (client != null) {
                    client.close();
                }
            } catch (IOException ignore) {
            }
        }
    }

    private String urlsafe_base64_encode(String src) {
        String result = Base64.encodeBase64String(src.getBytes());
        result = result.replace('+', '-');
        result = result.replace('/', '_');
        return result;
    }

    private String urlsafe_base64_encode(byte[] code) {
        String result = Base64.encodeBase64String(code);
        result = result.replace('+', '-');
        result = result.replace('/', '_');
        return result;
    }

    private byte[] getSignatureBytes(String data, String key)
            throws InvalidKeyException, NoSuchAlgorithmException {
        byte[] keyBytes = key.getBytes();
        Key signingKey = new SecretKeySpec(keyBytes, "HMACSHA1");
        Mac mac = Mac.getInstance("HMACSHA1");
        mac.init(signingKey);
        byte[] rawHmac = mac.doFinal(data.getBytes());
        return rawHmac;
    }

    private void LOG() {
        LOG("");
    }

    private void LOG(String log) {
        if(log == null || "".equals(log)) {
            System.out.println();
            return;
        }
        System.out.println("Roundtime (" + mOneRoundTime + "): " + log);
    }

}
