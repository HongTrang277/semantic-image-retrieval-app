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
        String baseName = originalName;
        if (originalName.contains(".")) {
            baseName = originalName.substring(0, originalName.lastIndexOf('.'));
        }
        String newFileName = baseName + "." + WEBP_FORMAT;
        
        File webpFile = new File(parentDir, newFileName);
        
        // 3. Đọc ảnh gốc
        BufferedImage image = ImageIO.read(rawFile);
        if (image == null) {
            throw new IOException("ImageIO không thể đọc file (định dạng không hỗ trợ hoặc file lỗi): " + rawFile.getName());
        }
        
        // 4. Ghi file WebP
        // Lưu ý: Nếu lỗi "Can't create ImageOutputStream" vẫn còn, nguyên nhân là do thư viện trong pom.xml
        boolean result = ImageIO.write(image, WEBP_FORMAT, webpFile);
        
        if (!result) {
            throw new IOException("Lỗi ghi file: Không tìm thấy Writer cho định dạng WebP. Hãy kiểm tra lại pom.xml.");
        }
        
        return webpFile;
    }
}