package com.dut.project.dao;

import com.dut.project.model.image;
import com.dut.project.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class imageDAO {

    // 1. Hàm thêm ảnh (Module 4 sẽ gọi hàm này khi User upload)
    public int addImage(image img) {
        String sql = "INSERT INTO images (user_id, file_path, status) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, img.getUserId());
            ps.setString(2, img.getFilePath());
            ps.setString(3, img.getStatus());
            
            int result = ps.executeUpdate();
            if (result > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1); 
                    }
                }
            }
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    // 2. Hàm lấy danh sách ảnh theo trạng thái (Module 4 - Worker sẽ gọi hàm này để quét DB)
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
    public void updateStatusAndFilePath(int imageId, String newStatus, String newPath) {
    	String sql = "UPDATE images SET status = ?, file_path = ? WHERE image_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, newStatus);
            ps.setString(2, newPath);
            ps.setInt(3, imageId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    } 
    
    public String getImageStatus(int imageId) {
    	String sql = "SELECT status FROM images WHERE image_id = ?";
    	try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
    		ps.setInt(1, imageId);
    		ResultSet rs = ps.executeQuery();
    		if(rs.next()) {
    			return rs.getString("status");
    		}
       } catch (Exception e) {
           e.printStackTrace();
       }
    	return null;
    }
    
    public List<image> getPendingAndMarkAsRunning(int limit){
    	Connection conn = null;
    	List<image> batch = new ArrayList<>();
    	
    	try {
    		conn = DBConnection.getConnection();
    		if (conn == null) {
    	        // Nếu không có kết nối, throw lỗi để xử lý hoặc trả về ngay lập tức
    	        throw new SQLException("Không thể thiết lập kết nối DB."); 
    	    }
    		conn.setAutoCommit(false);
    		
    		String sql = "SELECT image_id, user_id, file_path, upload_time FROM images " +
                    "WHERE status = 'PENDING' ORDER BY upload_time ASC LIMIT ? FOR UPDATE";
    		try (PreparedStatement psSelect = conn.prepareStatement(sql)) {
                psSelect.setInt(1, limit);
                ResultSet rs = psSelect.executeQuery();

                List<Integer> idsToUpdate = new ArrayList<>();
                while (rs.next()) {
                    image img = new image();
                    int imageId = rs.getInt("image_id");
                    
                    img.setId(imageId); 
                    img.setUserId(rs.getInt("user_id"));
                    img.setFilePath(rs.getString("file_path"));
                    img.setUploadTime(rs.getTimestamp("upload_time"));
                    
                    batch.add(img);
                    idsToUpdate.add(imageId);
                }
                
                if (!batch.isEmpty()) {
                    String updateSql = "UPDATE images SET status = 'RUNNING' WHERE image_id IN (" + 
                                       String.join(",", java.util.Collections.nCopies(idsToUpdate.size(), "?")) + ")";
                    
                    try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                        for (int i = 0; i < idsToUpdate.size(); i++) {
                            psUpdate.setInt(i + 1, idsToUpdate.get(i));
                        }
                        psUpdate.executeUpdate();
                    }
                    
                    conn.commit(); // Hoàn tất Transaction và NHẢ KHÓA
                } else {
                    conn.rollback(); // Không có Job nào, rollback
                }
            }

        } catch (SQLException e) {
            System.err.println("Lỗi Transaction khi lấy Batch Job: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
        return batch;
    }
    public List<image> getImagesByIds(List<Integer> imageIds) {
        List<image> sortedImages = new ArrayList<>();
        if (imageIds == null || imageIds.isEmpty()) {
            return sortedImages;
        }

        StringJoiner joiner = new StringJoiner(",");
        for (Integer id : imageIds) {
            joiner.add("?");
        }
        
        String sql = "SELECT * FROM images WHERE image_id IN (" + joiner.toString() + ")";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            int index = 1;
            for (Integer id : imageIds) {
                statement.setInt(index++, id);
            }

            ResultSet rs = statement.executeQuery();
            
            // Dùng Map để lưu tạm kết quả từ DB: Key = ID, Value = Image Object
            java.util.Map<Integer, image> tempMap = new java.util.HashMap<>();
            
            while (rs.next()) {
                image img = new image();
                img.setId(rs.getInt("image_id")); 
                img.setUserId(rs.getInt("user_id"));
                img.setFilePath(rs.getString("file_path"));
                img.setStatus(rs.getString("status"));
                img.setUploadTime(rs.getTimestamp("upload_time"));
                
                tempMap.put(img.getId(), img);
            }
            
            // QUAN TRỌNG: Duyệt lại theo danh sách imageIds ban đầu (đã sort theo score)
            // để lấy object từ Map ra -> Đảm bảo thứ tự hiển thị
            for (Integer id : imageIds) {
                if (tempMap.containsKey(id)) {
                    sortedImages.add(tempMap.get(id));
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sortedImages;
    }
    public List<image> getRecentImagesByUserId(int userId, int limit) {
        List<image> list = new ArrayList<>();
        // Lấy ảnh mới nhất lên đầu (ORDER BY upload_time DESC)
        String sql = "SELECT * FROM images WHERE user_id = ? ORDER BY upload_time DESC LIMIT ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setInt(2, limit);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                image img = new image();
                img.setId(rs.getInt("image_id"));
                img.setUserId(rs.getInt("user_id"));
                img.setFilePath(rs.getString("file_path"));
                img.setStatus(rs.getString("status"));
                img.setUploadTime(rs.getTimestamp("upload_time"));
                list.add(img);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public List<image> getIncompleteImagesByUserId(int userId) {
        List<image> list = new ArrayList<>();
        // Lấy tất cả ảnh của user mà trạng thái KHÔNG PHẢI LÀ SUCCESS
        String sql = "SELECT * FROM images WHERE user_id = ? AND status != 'SUCCESS' ORDER BY upload_time DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                image img = new image();
                img.setId(rs.getInt("image_id"));
                img.setUserId(rs.getInt("user_id"));
                img.setFilePath(rs.getString("file_path"));
                img.setStatus(rs.getString("status"));
                img.setUploadTime(rs.getTimestamp("upload_time"));
                list.add(img);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
}
    public List<image> getLatestBatchImagesByUserId(int userId) {
        List<image> list = new ArrayList<>();
        // 1. Tìm thời gian upload mới nhất của người dùng
        String sqlSelectMaxTime = "SELECT MAX(upload_time) as max_time FROM images WHERE user_id = ?";
        java.sql.Timestamp latestTime = null;

        try (java.sql.Connection conn = com.dut.project.utils.DBConnection.getConnection();
             java.sql.PreparedStatement psMax = conn.prepareStatement(sqlSelectMaxTime)) {
            
            psMax.setInt(1, userId);
            java.sql.ResultSet rsMax = psMax.executeQuery();
            if (rsMax.next()) {
                latestTime = rsMax.getTimestamp("max_time");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return list; 
        }

        if (latestTime == null) {
            return list; // Không tìm thấy ảnh nào
        }

        // 2. Chọn tất cả ảnh có cùng thời gian upload mới nhất đó
        String sqlSelectBatch = "SELECT * FROM images WHERE user_id = ? AND upload_time = ? ORDER BY image_id ASC";
        
        try (java.sql.Connection conn = com.dut.project.utils.DBConnection.getConnection();
             java.sql.PreparedStatement psBatch = conn.prepareStatement(sqlSelectBatch)) {
            
            psBatch.setInt(1, userId);
            psBatch.setTimestamp(2, latestTime);
            
            java.sql.ResultSet rs = psBatch.executeQuery();
            while (rs.next()) {
                image img = new image();
                img.setId(rs.getInt("image_id"));
                img.setUserId(rs.getInt("user_id"));
                img.setFilePath(rs.getString("file_path"));
                img.setStatus(rs.getString("status"));
                img.setUploadTime(rs.getTimestamp("upload_time"));
                list.add(img);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}