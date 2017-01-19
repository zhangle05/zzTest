/**
 * 
 */
package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


/**
 * @author zhangle
 *
 */
public class ExamPaperTest {

    private static final String driver = "com.mysql.jdbc.Driver";
    private static final String dbName = "jingyoushuxue";
    private static final String passwrod = "jyweb$web*2014";
    private static final String userName = "jyweb";
    private static final String url = "jdbc:mysql://localhost:3306/";

    private Connection mConn = null;

    private int mProcessCount = 0;

    /**
     * @param args
     */
    public static void main(String[] args) {
        ExamPaperTest test = new ExamPaperTest();
        test.initConnection();
        test.updateExamPapers();
        test.releaseConnection();
    }

    public void updateExamPapers() {
        int p = 0;
        int ps = 100;
        int count = getExamCount();
        while(p * ps < count) {
            System.out.println("Getting exam paper " + (p*ps) + " to " + (ps + p * ps));
            JSONArray arr = getExamPapers(p, ps);
            System.out.println("Getting exam paper done, result size: " + arr.size());
            for(int i = 0; i < arr.size(); i++) {
                JSONObject json = arr.getJSONObject(i);
                processSinglePaper(json);
            }
            p++;
        }
        System.out.println("Updated exam paper count: " + mProcessCount);
    }

    private int getExamCount() {
        String sql = "SELECT COUNT(paper_id) FROM jingyou_exam_paper";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = mConn.prepareStatement(sql);
            rs = ps.executeQuery();
            if(rs.next()) {
                int count = rs.getInt(1);
                return count;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(ps != null) {
                try {
                    ps.close();
                    ps = null;
                } catch(Exception ignore) {}
            }
            if(rs != null) {
                try {
                    rs.close();
                    rs = null;
                } catch(Exception ignore) {}
            }
        }
        return 0;
    }

    private JSONArray getExamPapers(int p, int size) {
        if(p < 0) {
            p = 0;
        }
        if(size < 1) {
            size = 100;
        }
        int offset = p * size;
        String sql = "SELECT paper_id, json_data FROM jingyou_exam_paper WHERE subject_code=0 LIMIT ? OFFSET ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = mConn.prepareStatement(sql);
            ps.setInt(1, size);
            ps.setInt(2, offset);
            rs = ps.executeQuery();
            JSONArray arr = new JSONArray();
            while(rs.next()) {
                long id = rs.getLong(1);
                String jsonStr = rs.getString(2);
                JSONObject json = JSONObject.fromObject(jsonStr);
                json.put("id", id);
                arr.add(json);
            }
            return arr;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(ps != null) {
                try {
                    ps.close();
                    ps = null;
                } catch(Exception ignore) {}
            }
            if(rs != null) {
                try {
                    rs.close();
                    rs = null;
                } catch(Exception ignore) {}
            }
        }
        return null;
    }

    private void doUpdatePaper(long id, String title, int subjectCode,
            String areaCode, int yearFrom, int yearTo, int gradeNo, int examType) {
        String sql = "UPDATE jingyou_exam_paper SET title=?, subject_code=?, area_code=?, "
                + " year_from=?, year_to=?, grade_no=?, exam_type=? "
                + " WHERE paper_id=?";
        PreparedStatement ps = null;
        try {
            ps = mConn.prepareStatement(sql);
            ps.setString(1, title);
            ps.setInt(2, subjectCode);
            ps.setString(3, areaCode);
            ps.setInt(4, yearFrom);
            ps.setInt(5, yearTo);
            ps.setInt(6, gradeNo);
            ps.setInt(7, examType);
            ps.setLong(8, id);
            int result = ps.executeUpdate();
            if(result == 0) {
                System.out.println("Failed to update paper '" + id +"'.");
                System.exit(0);
            } else {
                mProcessCount++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(ps != null) {
                try {
                    ps.close();
                    ps = null;
                } catch(Exception ignore) {}
            }
        }
    }

    private void processSinglePaper(JSONObject json) {
        long id = json.getLong("id");
        if(id == 514648) {
            System.out.println("break");
        }
        String title = json.getString("Title");
        String subject = json.getString("Subject");
        int subjectCode = getSubjectCode(subject);
        String areaCode = getAreaCode(title);
        int yearFrom = getYearFrom(title);
        int yearTo = getYearTo(title);
        int gradeNo = getGradeNo(title);
        int examType = getExamType(title);
//        System.out.println("Process done. area:" + areaCode + ", year from:" + yearFrom
//                + ", year to:" + yearTo + ", grade NO:" + gradeNo + ", subject code:" + subjectCode
//                + ", exam type:" + examType);
        doUpdatePaper(id, title, subjectCode, areaCode, yearFrom, yearTo, gradeNo, examType);
    }

    private int getSubjectCode(String subject) {
        if("math".equals(subject)) {
            return 20;
        } else if("physics".equals(subject)) {
            return 21;
        } else if("chemistry".equals(subject)) {
            return 22;
        } else if("bio".equals(subject)) {
            return 23;
        } else if("geography".equals(subject)) {
            return 25;
        } else if("math2".equals(subject)) {
            return 30;
        } else if("physics2".equals(subject)) {
            return 31;
        } else if("chemistry2".equals(subject)) {
            return 32;
        } else if("bio2".equals(subject)) {
            return 33;
        } else if("geography2".equals(subject)) {
            return 35;
        } else if("math3".equals(subject)) {
            return 10;
        } else if("physics3".equals(subject)) {
            return 11;
        } else if("chemistry3".equals(subject)) {
            return 12;
        } else if("bio3".equals(subject)) {
            return 13;
        } else if("geography3".equals(subject)) {
            return 15;
        }
        return 0;
    }

    private String getAreaCode(String title) {
        if(title.contains("北京"))
            return "110000";
        else if(title.contains("天津"))
            return "120000";
        else if(title.contains("河北"))
            return "130000";
        else if(title.contains("山西"))
            return "140000";
        else if(title.contains("内蒙"))
            return "150000";
        else if(title.contains("辽宁"))
            return "210000";
        else if(title.contains("吉林"))
            return "220000";
        else if(title.contains("黑龙江"))
            return "230000";
        else if(title.contains("上海"))
            return "310000";
        else if(title.contains("江苏"))
            return "320000";
        else if(title.contains("浙江"))
            return "330000";
        else if(title.contains("安徽"))
            return "340000";
        else if(title.contains("福建"))
            return "350000";
        else if(title.contains("江西"))
            return "360000";
        else if(title.contains("山东"))
            return "370000";
        else if(title.contains("河南"))
            return "410000";
        else if(title.contains("湖北"))
            return "420000";
        else if(title.contains("湖南"))
            return "430000";
        else if(title.contains("广东"))
            return "440000";
        else if(title.contains("广西"))
            return "450000";
        else if(title.contains("海南"))
            return "460000";
        else if(title.contains("重庆"))
            return "500000";
        else if(title.contains("四川"))
            return "510000";
        else if(title.contains("贵州"))
            return "520000";
        else if(title.contains("云南"))
            return "530000";
        else if(title.contains("西藏"))
            return "540000";
        else if(title.contains("陕西"))
            return "610000";
        else if(title.contains("甘肃"))
            return "620000";
        else if(title.contains("青海"))
            return "630000";
        else if(title.contains("宁夏"))
            return "640000";
        else if(title.contains("新疆"))
            return "650000";
        else if(title.contains("台湾"))
            return "710000";
        else if(title.contains("香港"))
            return "810000";
        else if(title.contains("澳门"))
            return "820000";
        else if(title.contains("海外"))
            return "10000020";
        else
            return "";
    }

    private int getYearTo(String title) {
        for(int year = 2000; year <= 2015; year++) {
            String yearStr = String.valueOf(year);
            if(title.contains("-" + yearStr)) {
                return year;
            }
            if(title.contains(yearStr) && !title.contains("-" + (year + 1))) {
                return year;
            }
        }
        return 0;
    }

    private int getYearFrom(String title) {
        for(int year = 2000; year <= 2015; year++) {
            String yearStr = String.valueOf(year);
            if(title.contains(yearStr + "-")) {
                return year;
            }
            if(title.contains(yearStr) && !title.contains((year - 1) + "-")) {
                return year;
            }
        }
        return 0;
    }

    private int getGradeNo(String title) {
        if(title.contains("一年级")) {
            return 1;
        } else if(title.contains("二年级")) {
            return 2;
        } else if(title.contains("三年级")) {
            return 3;
        } else if(title.contains("四年级")) {
            return 4;
        } else if(title.contains("五年级")) {
            return 5;
        } else if(title.contains("六年级")) {
            return 6;
        } else if(title.contains("七年级") || title.contains("初一")) {
            return 7;
        } else if(title.contains("八年级") || title.contains("初二")) {
            return 8;
        } else if(title.contains("九年级") || title.contains("初三") || title.contains("中考")) {
            return 9;
        } else if(title.contains("高一") || title.contains("高中一")) {
            return 10;
        } else if(title.contains("高二") || title.contains("高中二")) {
            return 11;
        } else if(title.contains("高三") || title.contains("高中三") || title.contains("高考")) {
            return 12;
        }
        return 0;
    }

    private int getExamType(String title) {
        if(title.contains("期末")) {
            return 6;
        } else if(title.contains("期中")) {
            return 7;
        } else if(title.contains("月考")) {
            return 8;
        } else if(title.contains("单元测试")) {
            return 9;
        } else if(title.contains("同步")) {
            return 10;
        } else if(title.contains("竞赛")) {
            return 11;
        } else if(title.contains("寒假") || title.contains("暑假")) {
            return 12;
        }
        return 0;
    }

    public void initConnection() {
        try {
            Class.forName(driver);
            mConn = DriverManager.getConnection(url+dbName, userName,
                    passwrod);
            System.out.println("init connection done, connection is:" + mConn);
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    public void releaseConnection() {
        if (mConn != null) {
            try {
                mConn.close();
                mConn = null;
                System.out.println("release connection done, connection is:" + mConn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
