package com.dut.project.model;

import java.sql.Timestamp;

public class image {
    private int id;
    private int userId;
    private String filePath;
    private String status; 
    // 1. PENDING: Đang chờ xử lý (Mặc định khi vừa upload)
    // 2. SUCCESS: Đã xử lý xong (AI đã trích xuất vector thành công)
    // 3. FAILED:  Gặp lỗi
    private Timestamp uploadTime;

    public image() {}

    // Constructor dùng khi thêm mới (chưa có ID và Time)
    public image(int userId, String filePath, String status) {
        this.userId = userId;
        this.filePath = filePath;
        this.status = status;
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Timestamp getUploadTime() { return uploadTime; }
    public void setUploadTime(Timestamp uploadTime) { this.uploadTime = uploadTime; }
}