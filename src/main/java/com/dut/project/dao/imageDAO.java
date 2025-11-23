package com.dut.project.dao;

import com.dut.project.model.image;
import com.dut.project.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class imageDAO {

    // 1. Hàm thêm ảnh (Module 4 sẽ gọi hàm này khi User upload)
    public boolean addImage(image img) {
        String sql = "INSERT INTO images (user_id, file_path, status) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, img.getUserId());
            ps.setString(2, img.getFilePath());
            ps.setString(3, img.getStatus());
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2. Hàm lấy danh sách ảnh PENDING (Module 4 - Worker sẽ gọi hàm này để quét DB)
    public List<image> getImagesByStatus(String status, int limit) {
        List<image> list = new ArrayList<>();
        String sql = "SELECT * FROM images WHERE status = ? LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, status);
            ps.setInt(2, limit);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                image img = new image();
                img.setId(rs.getInt("image_id"));
                img.setUserId(rs.getInt("user_id"));
                img.setFilePath(rs.getString("file_path"));
                img.setStatus(rs.getString("status"));
                list.add(img);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 3. Hàm cập nhật trạng thái (Module 4 - Worker sẽ gọi hàm này sau khi gửi sang AI xong)
    public void updateStatus(int imageId, String newStatus) {
        String sql = "UPDATE images SET status = ? WHERE image_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, newStatus);
            ps.setInt(2, imageId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public List<image> getImagesByIds(List<Integer> imageIds) {
        List<image> images = new ArrayList<>();
        if (imageIds == null || imageIds.isEmpty()) {
            return images;
        }

        StringJoiner joiner = new StringJoiner(",");
        for (Integer id : imageIds) {
            joiner.add("?");
        }
        
        // Lưu ý: Cột trong DB vẫn là 'image_id' (theo SQL bạn gửi lúc đầu)
        String sql = "SELECT * FROM images WHERE image_id IN (" + joiner.toString() + ")";

        try (Connection connection =  DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            int index = 1;
            for (Integer id : imageIds) {
                statement.setInt(index++, id);
            }

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                image img = new image(); // Sử dụng class 'image' của bạn
                
                // MAP DỮ LIỆU: Cột DB 'image_id' -> Model 'id'
                img.setId(rs.getInt("image_id")); 
                
                img.setUserId(rs.getInt("user_id"));
                img.setFilePath(rs.getString("file_path"));
                img.setStatus(rs.getString("status"));
                img.setUploadTime(rs.getTimestamp("upload_time"));
                
                images.add(img);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return images;
    }
}