package org.tools.db;

import java.sql.*;

/**
 * The type My sql tools.
 */
public class MySQLTools {
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/test?serverTimezone=Asia/Shanghai&useSSL=false&useUnicode=true&characterEncoding=UTF-8";
    static final String USERNAME = "root";
    static final String PASSWORD = "123456";

    public static void main(String[] args) {
        connectMySQL();
    }

    public static void connectMySQL(){
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            Class.forName(JDBC_DRIVER);

            System.out.println("数据库连接中...");
            conn = DriverManager.getConnection(DB_URL,USERNAME, PASSWORD);
            System.out.println("数据库连接成功!");

            System.out.println("实例化Statement对象");
            stmt = conn.createStatement();

            System.out.println("执行SQL操作");
            String sql = "SELECT * FROM endntest";
            rs = stmt.executeQuery(sql);

            System.out.println("展开结果集...");
            while (rs.next()) {
                // 通过字段名检索
                System.out.print("id: " + rs.getInt("id"));
                System.out.print(",name: " + rs.getString("name"));
                System.out.print(",age: " + rs.getInt("age"));
                System.out.println(",remark: " + rs.getString("remark"));
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            if (null != rs) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (null != stmt) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (null != conn) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
