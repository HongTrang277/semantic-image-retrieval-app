package com.dut.project.controller.worker;

import com.dut.project.bo.imageBO;
import com.dut.project.model.image;
import com.dut.project.model.user; 
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;

@WebServlet("/upload")
public class UploadController extends HttpServlet {
	private static final String UPLOAD_DIRECTORY = "uploads";
	private final imageBO imageBO = new imageBO();
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		user currentUser = (user) request.getSession().getAttribute("account");
		if(currentUser == null) {
			response.sendRedirect("loginPage.jsp");
			return;
		}
		int userId = currentUser.getId();
		
		if(!ServletFileUpload.isMultipartContent(request)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Lỗi khi tải ảnh");
			return;
		}
		
		String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIRECTORY;
		File uploadDir = new File(uploadPath);
		if(!uploadDir.exists()) 
			uploadDir.mkdirs();
		ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
		StringBuilder successfulIds = new StringBuilder(); 
	    int successCount = 0;
		try {
			upload.setHeaderEncoding("UTF-8");
			List<FileItem> formItems = upload.parseRequest(request);
			for(FileItem item : formItems) {
				if(!item.isFormField()) {
					
					if (item.getName() == null || item.getName().trim().isEmpty() || item.getSize() == 0) {
                        continue; 
                    }
					
					String fullFileName = item.getName(); 
				    String fileName = java.nio.file.Paths.get(fullFileName).getFileName().toString();
					String filePath = uploadPath + File.separator + fileName;
					System.out.println("File ảnh lưu: " + filePath);
					File storeFile = new File(filePath);
					item.write(storeFile); //Ghi nội dung vào file vật lý
					
					image newImage = new image(userId, filePath, "PENDING");
					
					int newImageId = imageBO.addImage(newImage);
					
					if(newImageId > 0) {
						if(successfulIds.length() > 0) {
							successfulIds.append(",");
						}
						successfulIds.append(newImageId);
						successCount++;
					} else {
						System.err.println("Lỗi lưu DB cho file: " + fileName);
					}
				}
			}
			if(successCount > 0) {
	            response.sendRedirect("uploadStatus.jsp?ids=" + successfulIds.toString()); 
	            //Phần ni nếu hồng trang làm thì sửa lại nhe, nó tùy vào cái view mà hồng trang sẽ hiển thị trạng thái ảnh á
	        } else {
	        	request.setAttribute("message", "Bạn chưa chọn file nào để tải lên!");
                request.getRequestDispatcher("index.jsp").forward(request, response);
	        }
		}catch (Exception ex) {
			ex.printStackTrace(); // In lỗi ra console để debug
            request.setAttribute("message", "Lỗi Upload: " + ex.getMessage());
            // 4. Sửa error.jsp -> index.jsp để tránh lỗi 404 nếu chưa có trang error
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
	}
}
