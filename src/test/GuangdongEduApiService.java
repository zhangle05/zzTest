/**
 * 
 */
package test;

import java.text.SimpleDateFormat;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import net.sf.json.JSONObject;

import org.apache.commons.codec.digest.DigestUtils;
import org.w3c.dom.NodeList;

/**
 * @author zhangle
 *
 */
public class GuangdongEduApiService {

    private static int sMsgSequence = 0;

    private static final int USER_TYPE_TEACHER = 1;
    private static final int USER_TYPE_STUDENT = 2;
    private static final int USER_TYPE_PARENT = 3;

    public static final int MSG_TYPE_NORMAL     = 1; // 普通消息
    public static final int MSG_TYPE_SMS        = 2; // 短信消息
    public static final int MSG_TYPE_CHAT       = 3; // 群组聊天
    public static final int MSG_TYPE_BATCH      = 4; // 群发消息
    public static final int MSG_TYPE_HOMEWORK   = 5; // 作业消息

    public static final int MSG_RECEIVER_TYPE_TEACHER   = 1; // 老师
    public static final int MSG_RECEIVER_TYPE_PARENT    = 2; // 家长
    public static final int MSG_RECEIVER_TYPE_STUDENT   = 3; // 学生
    public static final int MSG_RECEIVER_TYPE_CLASS     = 20; // 班级群组
    public static final int MSG_RECEIVER_TYPE_GROUP     = 21; // 自定义群组

    private static final String RESULT_OK = "<Result>ok</Result>";
    private static final String DESC_OK = "<Desc>ok</Desc>";

    private static final SimpleDateFormat DATE_FORMATOR = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSSS");

    private static final int IM_MSG_SEND_TYPE = 26;
    private static final int IM_MSG_SUB_SEND_TYPE = 301;
    private static final String YCL_LOGO_URL = "http://7rfjyq.com2.z0.glb.qiniucdn.com/image/yuncelian_logo.png";
    private static final String YCL_IM_TITLE = "云测练";

//    private String APP_KEY = "8ff90c50-2059-4f43-bca7-e8bbc905358d";                //预发布平台
    private String APP_KEY = "726e3eed-0749-4015-aae8-44fbb8be4f17";              // 正式平台
//    private String APP_KEY = "4876739e-0d98-4a10-88a1-443dd9bff521";                // 测试平台

    private String APP_CODE = "ycl";

//    private String WSDL_ENTRY = "http://121.201.6.195:13088/services/eduSOAP?wsdl";  // 预发布平台
    private String WSDL_ENTRY = "http://api.ydxxt.com/services/eduSOAP?wsdl";     // 正式平台
//    private String WSDL_ENTRY = "http://121.201.6.195:3088/services/eduSOAP?wsdl";  // 测试平台

    private String SERVER_URI = "http://www.cmcc.com/edu/";

    public String getSSOResponse(String token) {
        System.out.println("getting operator SSO by token:" + token );
        String CHK_OAUTH = "<MSG_BODY><Token>" + token + "</Token></MSG_BODY>";
        return getSOAPResponse("CHK_OAUTH", CHK_OAUTH);
    }
    
    public String getUserInfo(String role, String cityId, String operatorUid) {
        System.out.println("getting operator user info, role:" + role + "cityId:" + cityId + ", operatorUid:" + operatorUid);
        if ("teacher".equals(role)) {
            String QRY_TEACHER_INFO="<MSG_BODY><CityId>" + cityId + "</CityId><UserId>" + operatorUid + "</UserId></MSG_BODY>";
            return getSOAPResponse("QRY_TEACHER_INFO", QRY_TEACHER_INFO);
        } else if ("parent".equals(role)) {
            String QRY_PARENT_INFO="<MSG_BODY><CityId>" + cityId + "</CityId><UserId>" + operatorUid + "</UserId></MSG_BODY>";
            return getSOAPResponse("QRY_PARENT_INFO", QRY_PARENT_INFO);
        } else if ("student".equals(role)) {
            String QRY_STUDENT_INFO="<MSG_BODY><CityId>" + cityId + "</CityId><UserId>" + operatorUid + "</UserId></MSG_BODY>";
            return getSOAPResponse("QRY_STUDENT_INFO", QRY_STUDENT_INFO);
        }
        return "";
    }

    public String getStudentsByClass(String cityId, String classId) {
        System.out.println("getting operator students by class, cityId:" + cityId + ", classId:" + classId);
        String QRY_CLASS_STUDENT = "<MSG_BODY><CityId>" + cityId + "</CityId><ClassId>" + classId + "</ClassId></MSG_BODY>";
        return getSOAPResponse("QRY_CLASS_STUDENT", QRY_CLASS_STUDENT);
    }

    public String getClassInfo(String cityId, String classId) {
        System.out.println("getting operator class info, cityId:" + cityId + ", classId:" + classId);
        String QRY_CLASS = "<MSG_BODY><CityId>" + cityId + "</CityId><ClassId>" + classId + "</ClassId></MSG_BODY>";
        return getSOAPResponse("QRY_CLASS", QRY_CLASS);
    }

    public String getTeacherByClass(String classId) {
        System.out.println("getting operator teacher by class, classId:" + classId);
        String QRY_CLASS_TEACHER = "<MSG_BODY><ClassId>" + classId + "</ClassId><TeacherId></TeacherId></MSG_BODY>";
        return getSOAPResponse("QRY_CLASS_TEACHER", QRY_CLASS_TEACHER);
    }

    public String getClassByTeacher(String teacherId) {
        System.out.println("getting operator class by teacher, teacherId:" + teacherId);
        String QRY_CLASS_TEACHER = "<MSG_BODY><ClassId></ClassId><TeacherId>" + teacherId + "</TeacherId></MSG_BODY>";
        return getSOAPResponse("QRY_CLASS_TEACHER", QRY_CLASS_TEACHER);
    }

    public String getSubjectInfo(String schoolId, String subjectId) {
        System.out.println("getting operator subject info, schoolId:" + schoolId + ", subjectId:" + subjectId);
        String QRY_SUBJECT = "<MSG_BODY><SchoolId>" + schoolId + "</SchoolId><SubjectId>" + subjectId + "</SubjectId></MSG_BODY>";
        return getSOAPResponse("QRY_SUBJECT", QRY_SUBJECT);
    }
    public String getSchoolInfo(String schoolId) {
        System.out.println("getting operator school info, schoolId:" + schoolId);
        String QRY_SCHOOL = "<MSG_BODY><SchoolId>" + schoolId + "</SchoolId></MSG_BODY>";
        return getSOAPResponse("QRY_SCHOOL", QRY_SCHOOL);
    }

    public String getUserOrderInfo(String cityId, String parentId, String childId) {
        System.out.println("get order info in guagndong edu service, cityId:" + cityId + ", parentId:" + parentId + ", childId:" + childId);
        String QRY_ORDER = "<MSG_BODY><CityId>" + cityId + "</CityId><ParentId>"
                + parentId+ "</ParentId><StudentId>" + childId + "</StudentId></MSG_BODY>";
        return getSOAPResponse("QRY_ORDER", QRY_ORDER);
    }

    public String getTownInfo(String cityId) {
        System.out.println("get town info in guangdong edu service, cityId:" + cityId);
        String QRY_TOWN = "<MSG_BODY><CityId>" + cityId + "</CityId></MSG_BODY>";
        return getSOAPResponse("QRY_TOWN", QRY_TOWN);
    }
    
    public String sendSingleMessage(String cityId, String schoolId, String role, String msg, String userId, String targetUrl, String appMsgId) {
        System.out.println("send message to operator user, cityId:" + cityId + ", schoolId:" + schoolId
                + ", role:" + role + ", userId:" + userId + ", appMsgId:" + appMsgId);
        int userType = USER_TYPE_STUDENT;
        if ("teacher".equals(role)) {
            userType = USER_TYPE_TEACHER;
        } else if ("parent".equals(role)) {
            userType = USER_TYPE_PARENT;
        }
        java.util.Date now = new java.util.Date();
        java.util.Date validDate = new java.util.Date(now.getTime() + 1000L * 3600 * 24);
        String SEND_SYS_PUBLIC_MESSAGE =
            "<MSG_BODY>"
                + "<CityId>" + cityId + "</CityId>"
                + "<SchoolId>" + schoolId + "</SchoolId>"
                + "<MessageType>1</MessageType>"
                + "<UserType>" + userType + "</UserType>"
                + "<MessageContent>" + msg + "</MessageContent>"
                + "<UserId>" + userId + "</UserId>"
                + "<MessageURL>" + targetUrl + "</MessageURL>"
                + "<IsOauth>0</IsOauth>"
                + "<OthMsgId>" + appMsgId + "</OthMsgId>"
                + "<ValidDate>" + DATE_FORMATOR.format(validDate) + "</ValidDate>"
            + "</MSG_BODY>";
        return getSOAPResponse("SEND_SYS_PUBLIC_MESSAGE", SEND_SYS_PUBLIC_MESSAGE);
    }

    public String sendBatchMessage(String cityId, String classId, String role, String templateCode, String targetUrl, String appMsgId) {
        System.out.println("send message to operator class, cityId:" + cityId + ", classId:" + classId
                + ", role:" + role + ", appMsgId:" + appMsgId);
        int userType = USER_TYPE_STUDENT;
        if ("teacher".equals(role)) {
            userType = USER_TYPE_TEACHER;
        } else if ("parent".equals(role)) {
            userType = USER_TYPE_PARENT;
        }
        java.util.Date now = new java.util.Date();
        java.util.Date validDate = new java.util.Date(now.getTime() + 1000L * 3600 * 24);
        String SEND_SYS_CLS_MESSAGE = 
                "<MSG_BODY>"
                        + "<CityId>" + cityId + "</CityId>"
                        + "<ClassId>" + classId + "</ClassId>"
                        + "<MessageType>1</MessageType>"
                        + "<UserType>" + userType + "</UserType>"
                        + "<TemplateCode>" + templateCode + "</TemplateCode>"
                        + "<MessageURL>" + targetUrl + "</MessageURL>"
                        + "<IsOauth>0</IsOauth>"
                        + "<OthMsgId>" + appMsgId + "</OthMsgId>"
                        + "<ValidDate>" + DATE_FORMATOR.format(validDate) + "</ValidDate>"
               + "</MSG_BODY>";
        return getSOAPResponse("SEND_SYS_CLS_MESSAGE", SEND_SYS_CLS_MESSAGE);
    }

    public String sendIMClassMessage(String cityId, String schoolId, String senderId, int senderType, String senderName,
            int smsType, String msgUrl, String title, String content,
            String receiverId, int receiverType, int pushRecordId) {
        JSONObject extJson = new JSONObject();
        extJson.put("cpMsgIcon", YCL_LOGO_URL);
        extJson.put("cpMsgSecondTitle", title);
        extJson.put("cpMsgTitle", YCL_IM_TITLE);
        extJson.put("cpMsgType", 1);
        String extJsonStr = extJson.toString();
        extJsonStr = extJsonStr.replace("&", "&amp;");
        extJsonStr = extJsonStr.replace("\"", "&quot;");
        extJsonStr = extJsonStr.replace("<", "&lt;");
        extJsonStr = extJsonStr.replace(">", "&gt;");
        extJsonStr = extJsonStr.replace("'", "&apos;");
        String SEND_IM_CLS_MESSAGE = "<MSG_BODY>"
                + "<AreaAbb>" + cityId + "</AreaAbb>"
                + "<SchoolId>" + schoolId + "</SchoolId>"
                + "<SenderId>" + senderId + "</SenderId>"
                + "<SenderType>" + senderType + "</SenderType>"
                + "<SenderName>" + senderName + "</SenderName>"
                + "<SmsType>" + smsType + "</SmsType>"
                + "<MessageURL>" + msgUrl + "</MessageURL>"
                + "<Title>" + title + "</Title>"
                + "<SmsContent>" + content + "</SmsContent>"
                + "<SendType>" + IM_MSG_SEND_TYPE + "</SendType>"
                + "<SubSendType>" + IM_MSG_SUB_SEND_TYPE + "</SubSendType>"
                + "<ReceiverId>" + receiverId + "</ReceiverId>"
                + "<ReceiverType>" + receiverType + "</ReceiverType>"
                + "<PushRecordId>" + pushRecordId + "</PushRecordId>"
                + "<Ext>"
                + extJsonStr
                + "</Ext>"
            + "</MSG_BODY>";
        System.out.println("Guangdong edu debug: SEND_IM_CLS_MESSAGE=" + SEND_IM_CLS_MESSAGE);
        return getSOAPResponse("SEND_IM_CLS_MESSAGE", SEND_IM_CLS_MESSAGE);
    }

    public String addGrowthValue(String opUserId, String role, String userName, String opCityId, int growthVal) {
        System.out.println("add growth value, city id:" + opCityId + ", user id:" + opUserId
                + ", role:" + role + ", userName:" + userName + ", growth value:" + growthVal);
        int userType = USER_TYPE_STUDENT;
        if ("teacher".equals(role)) {
            userType = USER_TYPE_TEACHER;
        } else if ("parent".equals(role)) {
            userType = USER_TYPE_PARENT;
        }
        String GROWTH_VALUE_ADD = 
                "<MSG_BODY>"
                        + "<AccountId>" + opUserId + "</AccountId>"
                        + "<UserType>" + userType + "</UserType>"
                        + "<UserId>" + opUserId + "</UserId>"
                        + "<UserName>" + userName + "</UserName>"
                        + "<Area>" + opCityId + "</Area>"
                        + "<GrowthVal>" + growthVal + "</GrowthVal>"
                        + "<AppCode>" + APP_CODE + "</AppCode>"
               + "</MSG_BODY>";
        return getSOAPResponse("GROWTH_VALUE_ADD", GROWTH_VALUE_ADD);
    }

    private String sendIMClassMessageWithErrorLog(String opCityId, String opSchoolId, String opSenderId, int opSenderType, String opSenderName,
            int opSmsType, String opMsgUrl, String opTitle, String content,
            String receiverId, int opReceiverType, int opPushRecordId) {
        String result = sendIMClassMessage(opCityId, opSchoolId, opSenderId, opSenderType, opSenderName,
                opSmsType, opMsgUrl, opTitle, content,
                receiverId, opReceiverType, opPushRecordId);
        if (!result.contains(RESULT_OK) && !result.contains(DESC_OK)) {
            System.err.println("Failed to send exercise '" + opPushRecordId + "' to '" + receiverId + "', error:" + result);
        }
        return result;
    }

    public String sendOrderSms(String cityId, String parentId, String packageId, int chargeType, String reason,
            java.util.Date dueTime, int isCharges) {
        System.out.println("sending order SMS, cityId:" + cityId + ",parentId:" + parentId
                + ",packageId:" + packageId + ",chargeType:" + chargeType + ",reason:" + reason
                + ",dueTime:" + dueTime + ",isChartes:" + isCharges);
        String PRT_SEND_COMFIRM_SMS = "<MSG_BODY>"
            + "<CityId>" + cityId + "</CityId>"
            + "<ParentId>" + parentId + "</ParentId>"
            + "<PackageId>" + packageId + "</PackageId>"
            + "<ChargeType>" + chargeType + "</ChargeType>"
            + "<OperatorReason>" + reason + "</OperatorReason>"
            + "<Validate>" + new SimpleDateFormat("yyyy-MM-ddd").format(dueTime) + "</Validate>"
            + "<IsCharges>" + isCharges + "</IsCharges>"
            + "</MSG_BODY>";

        return getSOAPResponse("PRT_SEND_COMFIRM_SMS", PRT_SEND_COMFIRM_SMS);
    }

    private String getSOAPResponse(String msgType, String reqBody) {
        SOAPConnection soapConnection = null;
        try {
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            soapConnection = soapConnectionFactory.createConnection();

            // Send SOAP Message to SOAP Server
            SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(msgType, reqBody), WSDL_ENTRY);

            // Process the SOAP Response
            return parseSOAPResponse(soapResponse);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (soapConnection != null) {
                try {
                    soapConnection.close();
                } catch (SOAPException ignore) { }
            }
        }
        return "";
    }

    private String parseSOAPResponse(SOAPMessage soapResponse) throws SOAPException {
        SOAPBody body = soapResponse.getSOAPBody();
        NodeList results = body.getElementsByTagName("Result");
        if(results.getLength() == 0) {
            throw new SOAPException("Result is empty");
        }
        String result = results.item(0).getTextContent();
        if(!"200".equals(result)) {
            NodeList descs = body.getElementsByTagName("Desc");
            String desc = descs.item(0).getTextContent();
            throw new SOAPException(result + desc);
        }
        NodeList bodyList = body.getElementsByTagName("Body");
        if(bodyList.getLength() == 0) {
            throw new SOAPException("Body is empty");
        }
        return bodyList.item(0).getTextContent();
    }

    private synchronized SOAPMessage createSOAPRequest(String msgType, String reqBody) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();

        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("nsl", SERVER_URI);

        // SOAP Body
        SOAPBody soapBody = envelope.getBody();
        SOAPElement soapBodyElem = soapBody.addChildElement("Request", "nsl");
        SOAPElement version = soapBodyElem.addChildElement("Version");
        version.addTextNode("1.0");
        SOAPElement msgSeqNode = soapBodyElem.addChildElement("MsgSeq");
        String msgSequence = String.valueOf(sMsgSequence++);
        msgSeqNode.addTextNode(msgSequence);
        SOAPElement msgTypeNode = soapBodyElem.addChildElement("MsgType");
        msgTypeNode.addTextNode(msgType);
        SOAPElement timeStamp = soapBodyElem.addChildElement("TimeStamp");
        String timeStr = DATE_FORMATOR.format(new java.util.Date());
        timeStamp.addTextNode(timeStr);
        SOAPElement performCode = soapBodyElem.addChildElement("PerformCode");
        performCode.addTextNode(APP_CODE);
        SOAPElement skey = soapBodyElem.addChildElement("Skey");
        String text = APP_CODE + timeStr + msgSequence + msgType + APP_KEY;
        skey.addTextNode(DigestUtils.md5Hex(text).toUpperCase());
        SOAPElement body = soapBodyElem.addChildElement("Body");
        body.addTextNode(reqBody);

        soapMessage.saveChanges();

        return soapMessage;
    }

    public static void main(String[] args) {
        GuangdongEduApiService svc = new GuangdongEduApiService();
//        System.out.println("=============teacher info===============");
//        System.out.println(svc.getUserInfo("teacher", "gz", "104495141"));
//        System.out.println("=============parent info===============");
//        System.out.println(svc.getUserInfo("parent", "gz", "107008439"));
//        System.out.println("=============student info===============");
//        System.out.println(svc.getUserInfo("student", "zs", "2365638"));
//        System.out.println("=============student in class===============");
//        System.out.println(svc.getStudentsByClass("gz", "102203314"));
//        System.out.println("=============class info===============");
//        System.out.println(svc.getClassInfo("zs", "1090555"));
//        System.out.println("=============teacher in class===============");
//        System.out.println(svc.getTeacherByClass("605668"));
//        System.out.println("=============class by teacher===============");
//        System.out.println(svc.getClassByTeacher("104495141"));
//        System.out.println("=============subject info===============");
//        System.out.println(svc.getSubjectInfo("54522", ""));
//        System.out.println("=============school info===============");
//        System.out.println(svc.getSchoolInfo("49526"));
//        System.out.println("=============single message===============");
//        System.out.println(svc.sendSingleMessage("zs", "49568", "teacher", "云测练测试发送消息", "1278168", "www.yuncelian.com.cn", "12345"));
//        System.out.println("=============batch message===============");
//        System.out.println(svc.sendBatchMessage("zs", "1015835", "teacher", "309APP_Test", "www.yuncelian.com.cn", "12345"));
//        System.out.println(svc.sendIMClassMessageWithErrorLog("gz", "123", "123", 123, "123", 123, "123", "123", "123",
//                "123", MSG_RECEIVER_TYPE_STUDENT, 123));
//        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.DATE, 7);
//        java.util.Date dueDate = cal.getTime();
//        System.out.println(svc.sendOrderSms("zs", "4964852", "2521", 0, "测试", dueDate, 0));
        System.out.println(svc.getTownInfo("st"));
    }
}
