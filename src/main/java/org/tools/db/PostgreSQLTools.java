package org.tools.db;

import java.sql.*;

/**
 * The type My sql tools.
 */
public class PostgreSQLTools {
    static final String JDBC_DRIVER = "org.postgresql.Driver";
    static final String DB_URL = "jdbc:postgresql://localhost:5432/demo";
    static final String USERNAME = "postgres";
    static final String PASSWORD = "123456";

    public static void main(String[] args) {
        connectPGSQL();
    }

    public static void connectPGSQL(){
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
            String sql = "SELECT * FROM public.demo";
            rs = stmt.executeQuery(sql);

            System.out.println("展开结果集...");
            while (rs.next()) {
                // 通过字段名检索
                System.out.print("id: " + rs.getInt("id"));
                System.out.println(",data: " + rs.getString("data"));
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
