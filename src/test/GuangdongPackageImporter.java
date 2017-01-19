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
public class GuangdongPackageImporter {

    private static final String driver = "com.mysql.jdbc.Driver";
    private static final String dbName = "eclass";
    private static final String passwrod = "xyt@talkweb$2015";
    private static final String userName = "xyt";
    private static final String url = "jdbc:mysql://localhost:3306/";
    private static final String partnerCode = "op_guangdong";

    private Connection mConn = null;

    /**
     * @param args
     */
    public static void main(String[] args) {
        String fileName = "";
        fileName = "/Users/zhangle/tmp/packages.csv";
        GuangdongPackageImporter importer = new GuangdongPackageImporter();
        importer.initConnection();
        importer.importPackages(fileName);
        importer.releaseConnection();
    }

    /**
     * import package information from csv file to the database;
     * 
     * @param filePathName
     *            : path name of the source csv file
     */
    public void importPackages(String filePathName) {
        System.out.println("importing data from file:" + filePathName);
        File f = new File(filePathName);
        FileInputStream fs = null;
        InputStreamReader in = null;
        BufferedReader br = null;
        int doneCount = 0, failCount = 0, lineCount = 0;
        try {
            fs = new FileInputStream(f);
            in = new InputStreamReader(fs);
            br = new BufferedReader(in);
            String line = br.readLine();
            while (line != null) {
                lineCount++;
                if (insertOneLine(line)) {
                    doneCount++;
                } else {
                    failCount++;
                    System.out.println("Line " + lineCount
                            + " failed to import:" + line);
                }
                line = br.readLine();
            }
            System.out.println("import file done." + doneCount
                    + " items imported, " + failCount + " items failed.");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception ignore) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (Exception ignore) {
                }
            }
            if (fs != null) {
                try {
                    fs.close();
                } catch (Exception ignore) {
                }
            }
        }
    }

    private boolean insertOneLine(String line) {
        System.out.println("inserting line '" + line + "'");
        if(null == line || "".equals(line)) {
            System.out.println("line is empty");
            return false;
        }
        String[] data = line.split(",");
        if(data.length != 5) {
            System.out.println("data format error, length is:" + data.length);
            return false;
        }
        try {
            int packageId = Integer.parseInt(data[0]);
            int areaId = Integer.parseInt(data[1]);
            int siId = Integer.parseInt(data[2]);
            double fee = Double.parseDouble(data[4]);
            return insertPackage(packageId, areaId, siId, data[3], fee);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private boolean insertPackage(int packageId, int areaId,
            int siId, String name, double fee) {
        String sql = "INSERT INTO eclass_operatorpackage("
                + "ECLASS_OPERATORPACKAGE_PACKAGEID,"
                + "ECLASS_OPERATORPACKAGE_AREAID,"
                + "ECLASS_OPERATORPACKAGE_SIID,"
                + "ECLASS_OPERATORPACKAGE_NAME,"
                + "ECLASS_OPERATORPACKAGE_FEE,"
                + "ECLASS_OPERATORPACKAGE_PARTNERCODE"
                + ") VALUES(?,?,?,?,?,?)";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = mConn.prepareStatement(sql);
            ps.setInt(1, packageId);
            ps.setInt(2, areaId);
            ps.setInt(3, siId);
            ps.setString(4, name);
            ps.setDouble(5, fee);
            ps.setString(6, partnerCode);
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
            mConn = DriverManager.getConnection(url + dbName, userName,
                    passwrod);
            System.out.println("init connection done, connection is:" + mConn);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void releaseConnection() {
        if (mConn != null) {
            try {
                mConn.close();
                mConn = null;
                System.out.println("release connection done, connection is:"
                        + mConn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
