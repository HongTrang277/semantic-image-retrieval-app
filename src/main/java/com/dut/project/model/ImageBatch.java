package com.dut.project.model;


import java.sql.Timestamp;
import java.util.List;

public class ImageBatch {
    private String uploadTime;
    private List<image> images;
    private int totalImages;
    private String overallStatus; // SUCCESS, FAILED, PARTIAL, PENDING

    // Constructor
    public ImageBatch(String uploadTime, List<image> images) {
        this.uploadTime = uploadTime;
        this.images = images;
        this.totalImages = images.size();
        this.calculateOverallStatus();
    }

    // Logic tính trạng thái tổng quát (tương tự JS mockup)
    private void calculateOverallStatus() {
        long success = images.stream().filter(i -> "SUCCESS".equals(i.getStatus())).count();
        long failed = images.stream().filter(i -> "FAILED".equals(i.getStatus())).count();
        long pendingOrRunning = images.stream().filter(i -> "PENDING".equals(i.getStatus()) || "RUNNING".equals(i.getStatus())).count();

        if (failed > 0 && success == 0 && pendingOrRunning == 0) {
            this.overallStatus = "FAILED"; 
        } else if (pendingOrRunning > 0 || (failed > 0 && success > 0)) {
            this.overallStatus = "PARTIAL"; // Bao gồm cả đang chạy (RUNNING/PENDING) và có lỗi/chưa xong
        } else if (success > 0 && failed == 0 && pendingOrRunning == 0) {
            this.overallStatus = "SUCCESS"; 
        } else {
            this.overallStatus = "PENDING"; 
        }
    }
    
    // Getters for JSP/EL
    public String getUploadTime() { return uploadTime; }
    public List<image> getImages() { return images; }
    public int getTotalImages() { return totalImages; }
    public String getOverallStatus() { return overallStatus; }
    
    // Thêm một ID đơn giản cho JSP dễ dùng (dùng timestamp)
    public long getId() { 
        try {
            // Parse ngược lại Timestamp để dùng làm ID/Key
            return Timestamp.valueOf(uploadTime).getTime();
        } catch (Exception e) {
            return 0; 
        }
    }
}