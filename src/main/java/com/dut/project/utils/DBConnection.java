package com.dut.project.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3307/semantic_search_db";
    private static final String USER = "root"; 
    private static final String PASS = ""; 

    public static Connection getConnection() {
        try {
           
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Lỗi kết nối Database: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    public static void main(String[] args) {
        System.out.println("Đang thử kết nối đến MySQL...");
        Connection conn = getConnection();
        if(conn != null) {
            System.out.println("KẾT NỐI THÀNH CÔNG!");
        } else {
            System.out.println("KẾT NỐI THẤT BẠI. Kiểm tra lại tên DB hoặc Password.");
        }
    }
}