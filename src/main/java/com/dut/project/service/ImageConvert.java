package com.dut.project.service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageConvert {
    // Định dạng ảnh đích
    private static final String WEBP_FORMAT = "webp"; 
    
    public static File convertToWebp(File rawFile) throws IOException {
        // 1. Xác định thư mục chứa ảnh (chính là thư mục uploads)
        File parentDir = rawFile.getParentFile();
        
        if (parentDir == null || !parentDir.exists()) {
            throw new IOException("Thư mục cha không tồn tại: " + rawFile.getAbsolutePath());
        }

        // 2. Tạo tên file mới: abc.jpg -> abc.webp
        String originalName = rawFile.getName();
        String newFileName = originalName.substring(0, originalName.lastIndexOf('.'))+ "." + WEBP_FORMAT;
        
        File webpFile = new File(parentDir, newFileName);
        
        BufferedImage image = ImageIO.read(rawFile);
        if (image == null) {
            throw new IOException("ImageIO không thể đọc file (định dạng không hỗ trợ hoặc file lỗi): " + rawFile.getName());
        }
        
        boolean result = ImageIO.write(image, WEBP_FORMAT, webpFile);
        
        if (!result) {
            throw new IOException("Lỗi ghi file: Không tìm thấy Writer cho định dạng WebP. Hãy kiểm tra lại pom.xml.");
        }
        
        return webpFile;
    }
}