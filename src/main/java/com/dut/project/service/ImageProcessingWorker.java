package com.dut.project.service;

import com.dut.project.bo.imageBO;
import com.dut.project.controller.worker.PythonApiClient;
import com.dut.project.model.image;
import com.dut.project.model.user; 
import java.io.File;

public class ImageProcessingWorker implements Runnable{
	private final image imageToProcess;
	private final imageBO imageBO;
	private final PythonApiClient apiClient;
	
	public ImageProcessingWorker(image image) {
		this.imageToProcess = image;
		this.imageBO = new imageBO();
		this.apiClient = new PythonApiClient();
	}
	public void run() {
		String finalStatus = "SUCCESS";
		try {
			int imageId = imageToProcess.getId();
			int userId = imageToProcess.getUserId();
			String filePath = imageToProcess.getFilePath();
			
			// --- Giai đoạn 1: Kiểm tra File ---
			File imageFile = new File(filePath);
			if (!imageFile.exists()) {
                throw new Exception("Lỗi File I/O: File ảnh không tồn tại: " + filePath);
            }
			
			// --- Giai đoạn 2: Gọi AI (Xử lý nặng) ---
            System.out.println("Worker ID " + imageId + " - Đang xử lý: " + imageFile.getName());
            
            // GOI API CHÍNH XÁC: Worker bị BLOCK tại đây
            apiClient.callExtract(userId, imageId, imageFile);
            
            System.out.println("Worker ID " + imageId + " - Hoàn thành xử lý AI.");
		}catch (Exception e) {
            // --- Xử lý Lỗi ---
            System.err.println("LỖI (Job " + imageToProcess.getId() + "): " + e.getMessage());
            finalStatus = "FAILED";
		}finally {
            // --- Giai đoạn 3: Cập nhật Trạng thái ---
            // Đảm bảo Job không bị kẹt ở trạng thái RUNNING
            imageBO.updateStatus(imageToProcess.getId(), finalStatus);
        }
	}
}
