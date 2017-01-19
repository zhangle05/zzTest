/**
 * 
 */
package test;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author zhangle
 *
 */
public class SOAPTest {
    /**
     * Starting point for the SAAJ - SOAP Client Testing
     */
    public static void main(String args[]) {
        try {
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Send SOAP Message to SOAP Server
            String url = "http://121.201.6.195:3088/services/eduSOAP?wsdl";
            SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(), url);

            // Process the SOAP Response
            printSOAPResponse(soapResponse);

            soapConnection.close();
        } catch (Exception e) {
            System.err.println("Error occurred while sending SOAP Request to Server");
            e.printStackTrace();
        }
    }

    private static SOAPMessage createSOAPRequest() throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();

        String serverURI = "http://www.cmcc.com/edu/";

        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("nsl", serverURI);

        /*
        Constructed SOAP Request Message:
        <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:example="http://ws.cdyne.com/">
            <SOAP-ENV:Header/>
            <SOAP-ENV:Body>
                <example:VerifyEmail>
                    <example:email>mutantninja@gmail.com</example:email>
                    <example:LicenseKey>123</example:LicenseKey>
                </example:VerifyEmail>
            </SOAP-ENV:Body>
        </SOAP-ENV:Envelope>
         */

        // SOAP Body
        SOAPBody soapBody = envelope.getBody();
        SOAPElement soapBodyElem = soapBody.addChildElement("Request", "nsl");
        SOAPElement version = soapBodyElem.addChildElement("Version");
        version.addTextNode("1.0");
        SOAPElement msgSeq = soapBodyElem.addChildElement("MsgSeq");
        msgSeq.addTextNode("1");
        SOAPElement msgType = soapBodyElem.addChildElement("MsgType");
        msgType.addTextNode("QRY_TEACHER_INFO");
//        msgType.addTextNode("QRY_SCHOOL_TEACHER");
        SOAPElement timeStamp = soapBodyElem.addChildElement("TimeStamp");
        String timeStr = "2016-03-17 14:41:06:0610";
//        String timeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSSS").format(new java.util.Date());
        timeStamp.addTextNode(timeStr);
        SOAPElement performCode = soapBodyElem.addChildElement("PerformCode");
        performCode.addTextNode("ycl");
        SOAPElement skey = soapBodyElem.addChildElement("Skey");
        String key = "4876739e-0d98-4a10-88a1-443dd9bff521";
        String text = "ycl" + timeStr + "1" + "QRY_TEACHER_INFO" + key;
        System.out.println(text);
//        skey.addTextNode(e.authcode(text, "encode"));
        skey.addTextNode(DigestUtils.md5Hex(text).toUpperCase());
        SOAPElement body = soapBodyElem.addChildElement("Body");
//        body.addChildElement("CityId").addTextNode("gz");
//        body.addChildElement("UserId").addTextNode("3810304");
        String QRY_TEACHER_INFO="<MSG_BODY><CityId>gz</CityId><UserId>2251418</UserId></MSG_BODY>";
//        String QRY_SCHOOL_TEACHER="<MSG_BODY><SchoolId>3205</SchoolId></MSG_BODY>";
        body.addTextNode(QRY_TEACHER_INFO);


        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", serverURI  + "VerifyEmail");

        soapMessage.saveChanges();

        /* Print the request message */
        System.out.print("Request SOAP Message = ");
        soapMessage.writeTo(System.out);
        System.out.println();

        return soapMessage;
    }

    /**
     * Method used to print the SOAP Response
     */
    private static void printSOAPResponse(SOAPMessage soapResponse) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        Source sourceContent = soapResponse.getSOAPPart().getContent();
        System.out.print("\nResponse SOAP Message = ");
        StreamResult result = new StreamResult(System.out);
        transformer.transform(sourceContent, result);
    }
}
