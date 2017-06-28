/**
 * 
 */
package test;

/**
 * @author zhangle
 *
 */
public class AliRdsTest {

    private MySQLHelper helper;

    public AliRdsTest() {
        helper = new MySQLHelper("db_lecture_new", "lecture",
                "lecture@IDEAL$rds2017", "idealinner.mysql.rds.aliyuncs.com",
                "3306");
    }

    public void test() {
        helper.update("update live_discuss set content=? where id=633", new String[] {"😆"});
    }
    public static void main(String[] args) {
        AliRdsTest t = new AliRdsTest();
        t.test();
    }
}
