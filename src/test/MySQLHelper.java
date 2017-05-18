/**
 * 
 */
package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author zhangle
 *
 */
public class MySQLHelper {

    private String driver = "com.mysql.jdbc.Driver";
    private String dbName;
    private String passwrod;
    private String userName;
    private String url;

    private Connection mConn = null;

    public MySQLHelper(String dbName, String user, String pswd, String host,
            String port) {
        this.dbName = dbName;
        this.userName = user;
        this.passwrod = pswd;
        this.url = String.format("jdbc:mysql://%s:%s/", host, port);
    }

    public JSONArray query(String sql, String[] args) {
        this.initConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        JSONArray result = new JSONArray();
        try {
            ps = mConn.prepareStatement(sql);
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    ps.setString(i + 1, args[i]);
                }
            }
            System.out.println("final SQL is:" + ps.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                JSONObject json = new JSONObject();
                ResultSetMetaData meta = rs.getMetaData();
                for (int i = 0; i < meta.getColumnCount(); i++) {
                    String column = meta.getColumnName(i + 1);
                    String value = rs.getString(column);
                    json.put(column, value);
                }
                result.add(json);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                    ps = null;
                } catch (Exception ignore) {
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                    rs = null;
                } catch (Exception ignore) {
                }
            }
            this.releaseConnection();
        }
        return result;
    }

    public JSONArray update(String sql, String[] args) {
        this.initConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        JSONArray result = new JSONArray();
        try {
            ps = mConn.prepareStatement(sql);
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    ps.setString(i + 1, args[i]);
                }
            }
            System.out.println("final SQL is:" + ps.toString());
            int count = ps.executeUpdate();
            if (count > 0) {
                JSONObject json = new JSONObject();
                json.put("row_count", count);
                result.add(json);
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                    ps = null;
                } catch (Exception ignore) {
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                    rs = null;
                } catch (Exception ignore) {
                }
            }
            this.releaseConnection();
        }
        return result;
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
                System.out.println(
                        "release connection done, connection is:" + mConn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
