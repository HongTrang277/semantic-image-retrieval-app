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
	private static final String BASE_STORAGE_PATH = "D:\\kiki\\DUT\\SEM5\\sem5_hchang\\LTM\\workspace\\SemanticSearchApp\\src\\main\\webapp";
    private static final String UPLOAD_DIRECTORY = "uploads";
	
	public ImageProcessingWorker(image image) {
		this.imageToProcess = image;
		this.imageBO = new imageBO();
		this.apiClient = new PythonApiClient();
	}
	public void run() {
		String finalStatus = "SUCCESS";
		File rawImageFile = null;
		File webpImage = null;
		
		String pathForDB = imageToProcess.getFilePath();
		
		try {
			int imageId = imageToProcess.getId();
			int userId = imageToProcess.getUserId();
			String filePath = imageToProcess.getFilePath();
			
			String absoluteRawPath = BASE_STORAGE_PATH + File.separator + imageToProcess.getFilePath();
			
			rawImageFile = new File(absoluteRawPath);
			if (!rawImageFile.exists()) {
                throw new Exception("Lỗi File I/O: File ảnh không tồn tại: " + filePath);
            }
			
			webpImage = ImageConvert.convertToWebp(rawImageFile);
			String webpFileName = webpImage.getName();
			pathForDB = UPLOAD_DIRECTORY + "/" + webpFileName;
            System.out.println("Worker ID " + imageId + " - Đang xử lý: " + webpImage.getName());
            
            apiClient.callExtract(String.valueOf(userId), String.valueOf(imageId), webpImage);
            
            System.out.println("Worker ID " + imageId + " - Hoàn thành xử lý AI.");
		}catch (Exception e) {
            System.err.println("LỖI (Job " + imageToProcess.getId() + "): " + e.getMessage());
            finalStatus = "FAILED";
		}finally {
			if(rawImageFile != null && rawImageFile.exists()) {
				rawImageFile.delete();
			}
            imageBO.updateStatusAndFilePath(imageToProcess.getId(), finalStatus, pathForDB);
        }
	}
}
