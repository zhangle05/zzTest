package test;


/**
 * ͬ�����õ�¼����������
 * 
 * @author liudong
 */
public class KetangLoginEncoder {

    private final static String _KEY = ".keTang.";

    /**
     * �������
     * 
     * @param args
     */
    public static void main(String[] args) {
        String service_id = "QuanTong";
        String role = "2";
        String username = "cszhangchaohong";
        String ip = "127.0.0.1";
        String user_agent = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)";
        // //����
        // String uuid =
        // encodeKetangLoginParam(service_id,role,username,ip,user_agent);
        // System.out.println("###���ܺ�"+uuid);
        // String
        // queryString=CryptUtils.encrypt("QuanTong|2|cszhangqi|127.0.0.1|55|1234167923500",
        // _KEY);
        // System.out.println("-------"+queryString );
        //
        // System.out.println("------------------------");
        //
        // try {
        // String
        // str=CryptUtils.decrypt("4A584FAC9F2929E0B18FAB43821D7E06FABA05E782169DFCCFE4AE81594833921D7A74239187388628743942C90D58BF",_KEY);
        //
        // System.out.println(str);
        // } catch (Exception e) {
        // // TODO �Զ����� catch ��
        // e.printStackTrace();
        // }

        System.out.println(encodeKetangLoginParam(service_id, role, username,
                ip, user_agent));
        try {
            String decrypted = ZhuhaiCryptUtils.decrypt("673FEB2DCB515BA85A53E0F2E65E1AE2B7C7FE9F495F6EEEF7E83DC6EAF9A5F6245FF3BF7798C1ED8C4032EBBFE69A2449B14011D24C158E", _KEY);
            System.out.println("decrypted info is:" + decrypted);
            /*
             * info arr format:
             * service_id|role|username|ip|user_agent.length|currentTimeMillis
             */
            /* example: LongXing|3|ZH606545|10.249.253.146|155|1466587922039 */
            String[] infoArr = decrypted.split("\\|");
            System.out.println("info arr is:" + infoArr + ", length is:" + infoArr.length);
            for (int i = 0; i < infoArr.length; i++) {
                System.out.println(infoArr[i]);
            }
            System.out.println();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * �Բ������м����Ա���URL�д���
     * 
     * @param service_id
     *            �����̱�ʶ����ͬ������ָ��һ���̶�ֵ
     * @param role
     *            �û��Ľ�ɫ(STUDENT:ѧ��,TEACHER:��ʦ,PARENT:�ҳ�)
     * @param username
     *            ��¼�ʺ�(�û��ʺ���)
     * @param ip
     *            �û����ʵ�IP��ַ
     * @param user_agent
     *            ͨ�� request.getHeader("user-agent") ��ȡ����ֵ
     *
     * @return ���ص��ַ���ΪURL��?���������
     */
    public static String encodeKetangLoginParam(String service_id, String role,
            String username, String ip, String user_agent) {
        StringBuffer buf = new StringBuffer();
        buf.append(service_id);
        buf.append('|');
        buf.append(role);
        buf.append('|');
        buf.append(username);
        buf.append('|');
        buf.append(ip);
        buf.append('|');
        buf.append((user_agent == null) ? 0 : user_agent.length());
        buf.append('|');
        buf.append(System.currentTimeMillis());
        System.out.println("����ǰ========" + buf.toString());

        return ZhuhaiCryptUtils.encrypt(buf.toString(), _KEY);
    }

}
