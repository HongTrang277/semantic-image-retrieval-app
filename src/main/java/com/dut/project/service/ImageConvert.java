package com.dut.project.service;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageConvert {
	private static final String WEBP_FORMAT = "webp";
	private static final String BASE_STORAGE_PATH = "C:\\Users\\ADMIN\\semantic-image-retrieval-app\\src\\main\\webapp";
    private static final String UPLOAD_DIRECTORY = "uploads";
	
	public static File convertToWebp(File rawFile) throws IOException{
		File uploadDir = new File(BASE_STORAGE_PATH, UPLOAD_DIRECTORY);
		if(!uploadDir.exists()) {
			uploadDir.mkdirs();
		}
		
		String newFileName = rawFile.getName().substring(0, rawFile.getName().lastIndexOf('.')) + "." + WEBP_FORMAT;
		File webpFile = new File(uploadDir, newFileName);
		
		BufferedImage image = ImageIO.read(rawFile);
		
		if(image == null) {
			throw new IOException("Không thể đọc file ảnh: Định dạng không được hỗ trợ hoặc file bị hỏng.");
		}
		
		boolean result = ImageIO.write(image, WEBP_FORMAT, webpFile);
		if(!result) {
			throw new IOException("Lỗi ghi file: Không tìm thấy ImageWriter cho định dạng WebP.");
        }
		return webpFile;
	}
	
}
