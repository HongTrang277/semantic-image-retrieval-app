package com.dut.project.service;

import com.dut.project.dao.imageDAO; 
import com.dut.project.controller.worker.PythonApiClient;
import com.dut.project.model.image;
import java.io.File;

public class ImageProcessingWorker implements Runnable {
    private final image imageToProcess;
    private final imageDAO imgDAO; 
    private final PythonApiClient apiClient;
    
    // [QUAN TRỌNG] Đã cập nhật về đường dẫn máy của BẠN
    private static final String BASE_STORAGE_PATH = "D:\\kiki\\DUT\\SEM5\\sem5_hchang\\LTM\\workspace\\SemanticSearchApp\\src\\main\\webapp";
    private static final String UPLOAD_DIRECTORY = "uploads";

    public ImageProcessingWorker(image image) {
        this.imageToProcess = image;
        this.imgDAO = new imageDAO();
        this.apiClient = new PythonApiClient();
    }

    @Override
    public void run() {
        String finalStatus = "SUCCESS";
        File rawImageFile = null;
        File webpImage = null;
        String pathForDB = imageToProcess.getFilePath(); 

        try {
            int imageId = imageToProcess.getId();
            int userId = imageToProcess.getUserId();
            
            // Xử lý đường dẫn đầu vào
            String currentDbPath = imageToProcess.getFilePath();
            if(currentDbPath.startsWith("uploads/") || currentDbPath.startsWith("uploads\\")) {
                currentDbPath = currentDbPath.substring(8); 
            }
            
            // Tìm file ảnh gốc theo đường dẫn máy bạn
            String absoluteRawPath = BASE_STORAGE_PATH + File.separator + UPLOAD_DIRECTORY + File.separator + currentDbPath;
            
            System.out.println("Worker đang tìm file tại: " + absoluteRawPath);
            
            rawImageFile = new File(absoluteRawPath);
            if (!rawImageFile.exists()) {
                // Fallback tìm ở thư mục gốc nếu logic trên sai
                rawImageFile = new File(BASE_STORAGE_PATH + File.separator + imageToProcess.getFilePath());
                if (!rawImageFile.exists()) {
                     throw new Exception("Không tìm thấy file ảnh gốc trên ổ cứng: " + absoluteRawPath);
                }
            }

            // 1. Convert sang WebP
            webpImage = ImageConvert.convertToWebp(rawImageFile);
            String webpFileName = webpImage.getName();

            // 2. Cập nhật đường dẫn DB
            pathForDB = UPLOAD_DIRECTORY + "/" + webpFileName;
            
            System.out.println("Worker ID " + imageId + " - Đã convert sang: " + webpFileName);

            // 3. Gọi AI
            apiClient.callExtract(String.valueOf(userId), String.valueOf(imageId), webpImage);
            
            System.out.println("Worker ID " + imageId + " - Hoàn thành xử lý AI.");

        } catch (Exception e) {
            System.err.println("LỖI (Job " + imageToProcess.getId() + "): " + e.getMessage());
            e.printStackTrace();
            finalStatus = "FAILED";
        } finally {
            // Tạm thời KHÔNG xóa file gốc để tránh lỗi hiển thị khi chưa sync
            
            // 4. Update vào DB
            System.out.println("Worker Update DB -> ID: " + imageToProcess.getId() + " | Status: " + finalStatus + " | Path: " + pathForDB);
            imgDAO.updateStatusAndFilePath(imageToProcess.getId(), finalStatus, pathForDB);
        }
    }
}
