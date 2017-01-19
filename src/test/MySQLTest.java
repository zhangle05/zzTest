/**
 * 
 */
package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * @author zhangle
 *
 */
public class MySQLTest {

    private static final String driver = "com.mysql.jdbc.Driver";
    private static final String dbName = "jingyoushuxue";
    private static final String passwrod = "jyweb$web*2014";
    private static final String userName = "jyweb";
    private static final String url = "jdbc:mysql://localhost:3306/";
    private static long codeBase = 10000600l;

    private Connection mConn = null;

    /**
     * @param args
     */
    public static void main(String[] args) {
        String fileName = "";
        int stage = 3;
        if(args != null && args.length > 0) {
            System.out.println("args are:");
            for(int i=0; i<args.length; i++) {
                System.out.println(args[i]);
            }
            fileName = args[0];
            if(null == fileName || "".equals(fileName)) {
                System.err.println("school file is empty!");
//                System.exit(0);
            }
            try {
                stage = Integer.parseInt(args[1]);
            }
            catch(Exception ex) {
                System.err.println("stage format error!");
//                System.exit(0);
            }
        }
        fileName = "/Users/zhangle/tmp/senior.txt";
        MySQLTest test = new MySQLTest();
        test.initConnection();
        test.importSchools(fileName, stage);
        test.releaseConnection();
    }

    /**
     * import school information from txt file to the database;
     * @param filePathName: path name of the source txt file
     * @param stage: stage of the school, 1 for primary school, 2 for junior middle school, 3 for high school
     */
    public void importSchools(String filePathName, int stage) {
        System.out.println("importing data from file:" + filePathName + ", stage is:" + stage);
        File f = new File(filePathName);
        FileInputStream fs = null;
        InputStreamReader in = null;
        BufferedReader br = null;
        int doneCount = 0, failCount = 0, lineCount=0;
        try {
            fs = new FileInputStream(f);
            in = new InputStreamReader(fs);
            br = new BufferedReader(in);
            String line = br.readLine();
            while(line != null) {
                lineCount ++;
                System.out.println("################################## codeBase is:" + codeBase);
                if(insertOneLine(line, stage)) {
                    System.out.println("################################## codeBase is:" + codeBase);
                    doneCount++;
                }
                else {
                    failCount++;
                    System.out.println("Line " + lineCount + " failed to import:" + line);
                }
                line = br.readLine();
            }
            System.out.println("import file done." + doneCount + " items imported, "
                    + failCount + " items failed.");
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        finally {
            if(br != null) {
                try {
                    br.close();
                } catch(Exception ignore) {}
            }
            if(in != null) {
                try {
                    in.close();
                } catch(Exception ignore) {}
            }
            if(fs != null) {
                try {
                    fs.close();
                } catch(Exception ignore) {}
            }
        }
    }

    private boolean insertOneLine(String line, int stage) {
        System.out.println("inserting line '" + line + "'");
        if(null == line || "".equals(line)) {
            System.out.println("line is empty");
            return false;
        }
        String[] data = line.split("\t");
        if(data.length != 4) {
            data = line.split(" ");
        }
        if(data.length != 4) {
            data = line.split(",");
        }
        if(data.length != 4) {
            System.out.println("data format error, length is:" + data.length);
            return false;
        }
        return insertSchool(data[0], data[1], data[2], data[3], stage);
    }

    private boolean insertSchool(String province, String city, String district,
            String school, int stage) {
        province = province.trim();
        if(province.contains("北京")) {
            city = "北京市";
        }
        else if(province.contains("天津")) {
            city = "天津市";
        }
        else if(province.contains("上海")) {
            city = "上海市";
        }
        else if(province.contains("重庆")) {
            city = "重庆市";
        }
        city = city.trim();
        if("北京市".equals(province)) {
            city = "北京";
        }
        else if("天津市".equals(province)) {
            city = "天津";
        }
        else if("上海市".equals(province)) {
            city = "上海";
        }
        else if("重庆市".equals(province)) {
            city = "重庆";
        }
        district = district.trim();
        school = school.trim();
        try {
            String pCode = getProvinceCode(province);
            if("".equals(pCode)) {
                pCode = insertProvince(province);
                System.out.println("insert province '" + province + "', code is:" + pCode);
            }
            else {
                System.out.println("found province '" + province + "', code is:" + pCode);
            }
            String cCode = getCityCode(pCode, city);
            if("".equals(cCode)) {
                cCode = insertCity(pCode, city);
                System.out.println("insert city '" + city + "', code is:" + cCode);
            }
            else {
                System.out.println("found city '" + city + "', code is:" + cCode);
            }
            String dCode = getDistrictCode(cCode, district);
            if("".equals(dCode)) {
                dCode = insertDistrict(cCode, district);
                System.out.println("insert district '" + district + "', code is:" + dCode);
            }
            else {
                System.out.println("found district '" + district + "', code is:" + dCode);
            }
            String schoolArea = getSchool(school);
            if(schoolArea != null) {
                System.out.println("School " + school + " exist, no need to insert.");
                if(schoolArea.equals(dCode)) {
                    System.out.println("Area code equal, nothing to do with '" + school + "', stage is:" + stage);
                    return true;
                }
                else {
                    int result = updateSchool(dCode, school);
                    if(result < 0) {
                        System.out.println("failed to update area, insert school '" + school + "', stage is:" + stage);
                        return insertSchool(dCode, school, stage);
                    }
                    else {
                        System.out.println("update area code done for school '" + school + "', stage is:" + stage);
                        return true;
                    }
                }
            }
            else {
                System.out.println("insert school '" + school + "', stage is:" + stage);
                return insertSchool(dCode, school, stage);
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private String getProvinceCode(String province) {
        String sql = "SELECT province_code FROM jingyou_province WHERE province_name LIKE ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = mConn.prepareStatement(sql);
            ps.setString(1, province + "%");
            System.out.println("SQL is:" + ps.toString());
            rs = ps.executeQuery();
            if(rs.next()) {
                return rs.getString(1);
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
        return "";
    }

    private String getCityCode(String provinceCode, String city) {
        String sql = "SELECT city_code FROM jingyou_city WHERE city_father_code=? AND city_name LIKE ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = mConn.prepareStatement(sql);
            ps.setString(1, provinceCode);
            ps.setString(2, city + "%");
            System.out.println("SQL is:" + ps.toString());
            rs = ps.executeQuery();
            if(rs.next()) {
                return rs.getString(1);
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
        return "";
    }
    private String getDistrictCode(String cityCode, String district) {
        String sql = "SELECT area_code FROM jingyou_area WHERE area_father_code=? AND area_name LIKE ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = mConn.prepareStatement(sql);
            ps.setString(1, cityCode);
            ps.setString(2, district + "%");
            System.out.println("SQL is:" + ps.toString());
            rs = ps.executeQuery();
            if(rs.next()) {
                return rs.getString(1);
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
        return "";
    }
    private String getSchool(String school) {
        String sql = "SELECT school_name, school_area_code FROM jingyou_school WHERE school_name= ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = mConn.prepareStatement(sql);
            ps.setString(1, school);
            System.out.println("SQL is:" + ps.toString());
            rs = ps.executeQuery();
            if(rs.next()) {
                System.out.println("find school result is:" + rs.getString(1));
                String schoolArea = rs.getString(2);
                return schoolArea;
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
        return null;
    }
    private int updateSchool(String areaCode, String school) {
        String sql = "UPDATE jingyou_school SET school_area_code=? WHERE school_name=?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = mConn.prepareStatement(sql);
            ps.setString(1, areaCode);
            ps.setString(2, school);
            System.out.println("SQL is:" + ps.toString());
            int result = ps.executeUpdate();
            return result;
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
        return -1;
    }
    private String insertProvince(String province) {
        String sql = "INSERT INTO jingyou_province(province_code,province_name) VALUES(?,?)";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = mConn.prepareStatement(sql);
            String code = String.valueOf(codeBase);
            codeBase++;
            ps.setString(1, code);
            ps.setString(2, province);
            System.out.println("SQL is:" + ps.toString());
            ps.executeUpdate();
            return code;
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
        return "";
    }
    private String insertCity(String provinceCode, String city) {
        String sql = "INSERT INTO jingyou_city(city_code,city_name,city_father_code) VALUES(?,?,?)";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = mConn.prepareStatement(sql);
            String code = String.valueOf(codeBase);
            codeBase++;
            ps.setString(1, code);
            ps.setString(2, city);
            ps.setString(3, provinceCode);
            System.out.println("SQL is:" + ps.toString());
            ps.executeUpdate();
            return code;
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
        return "";
    }
    private String insertDistrict(String cityCode, String district) {
        String sql = "INSERT INTO jingyou_area(area_code,area_name,area_father_code) VALUES(?,?,?)";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = mConn.prepareStatement(sql);
            String code = String.valueOf(codeBase);
            codeBase++;
            ps.setString(1, code);
            ps.setString(2, district);
            ps.setString(3, cityCode);
            System.out.println("SQL is:" + ps.toString());
            ps.executeUpdate();
            return code;
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
        return "";
    }
    private boolean insertSchool(String areaCode, String school, int stage) {
        String sql = "INSERT INTO jingyou_school(school_area_code,school_name,school_category) VALUES(?,?,?)";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = mConn.prepareStatement(sql);
            ps.setString(1, areaCode);
            ps.setString(2, school);
            ps.setInt(3, stage);
            System.out.println("SQL is:" + ps.toString());
            ps.executeUpdate();
            return true;
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
        return false;
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
