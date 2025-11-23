package com.dut.project.bo;

import com.dut.project.dao.imageDAO;
import com.dut.project.model.image;
import java.util.List;

public class imageBO {
    
    private imageDAO imageDAO = new imageDAO();

    // 1. Nghiệp vụ: Thêm ảnh mới
    public int addImage(image img) {
        
        if (img.getStatus() == null || img.getStatus().isEmpty()) {
            img.setStatus("PENDING");
        }
        
        // Logic 2: Kiểm tra đường dẫn file có rỗng không?
        if (img.getFilePath() == null || img.getFilePath().trim().isEmpty()) {
            System.out.println("Lỗi: Đường dẫn ảnh không được để trống!");
            return -1;
        }

        // Sau khi kiểm tra ok hết mới gọi DAO
        return imageDAO.addImage(img);
    }
    
    public String getImageStatus(int imageId) {
    	return imageDAO.getImageStatus(imageId);
    }

    // 2. Nghiệp vụ: Lấy danh sách ảnh theo trạng thái (Cho Worker dùng)
    public List<image> getImagesByStatus(String status, int limit) {
        // Logic: Giới hạn limit không được < 0
        if (limit <= 0) {
            limit = 10; // Mặc định lấy 10 ảnh nếu truyền sai
        }
        return imageDAO.getImagesByStatus(status, limit);
    }

    // 3. Nghiệp vụ: Cập nhật trạng thái (Cho Worker dùng sau khi AI xử lý xong)
    public void updateStatusAndFilePath(int imageId, String newStatus, String newPath) {
        // Logic: Chỉ cho phép cập nhật các trạng thái hợp lệ
        if (newStatus.equals("PENDING") || newStatus.equals("SUCCESS") || newStatus.equals("FAILED")) {
            imageDAO.updateStatusAndFilePath(imageId, newStatus, newPath);
        } else {
            System.out.println("Lỗi: Trạng thái không hợp lệ! (" + newStatus + ")");
        }
    }
    public List<image> getPendingAndMarkAsRunning(int limit) {
        if (limit <= 0 || limit > 100) {
            limit = 10; 
        }
        return imageDAO.getPendingAndMarkAsRunning(limit);
    }
}