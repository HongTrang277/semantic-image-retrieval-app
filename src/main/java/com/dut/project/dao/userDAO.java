package com.dut.project.dao;

import com.dut.project.model.user;
import com.dut.project.utils.DBConnection; 
import java.sql.Connection;        
import java.sql.PreparedStatement; 
import java.sql.ResultSet;         
import java.sql.SQLException;

public class userDAO {
    
    // Hàm kiểm tra đăng nhập: Trả về User nếu đúng, trả về null nếu sai
    public user checkLogin(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        
        // Dùng try-with-resources để tự động đóng kết nối
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            ps.setString(2, password); 
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                user user = new user();
                user.setId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setFullName(rs.getString("full_name"));
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean registerUser(user user) {
        String sql = "INSERT INTO users (username, password, full_name) VALUES (?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword()); 
            ps.setString(3, user.getFullName());
            
            // executeUpdate trả về số dòng bị thay đổi trong DB
            // Nếu > 0 nghĩa là insert thành công
            int result = ps.executeUpdate();
            return result > 0; 
            
        } catch (SQLException e) {
            // Lỗi này thường xảy ra nếu trùng Username
            System.err.println("Lỗi khi đăng ký user: " + e.getMessage());
            return false;
        }
    }
}