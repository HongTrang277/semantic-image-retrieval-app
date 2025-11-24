package com.dut.project.controller.worker;
import com.dut.project.bo.imageBO;
import com.dut.project.model.image;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/checkStatus")
public class CheckStatusController extends HttpServlet{
	private final imageBO imageBO = new imageBO();
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException{
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String imageIdStr = request.getParameter("id");
		
		// Tìm đoạn try-catch và sửa lại như sau:
		try {
		    int imageId = Integer.parseInt(imageIdStr);
		    
		    // SỬA ĐOẠN NÀY: Gọi hàm lấy full thông tin ảnh thay vì chỉ lấy status
		    image img = imageBO.getImageById(imageId); // Hàm vừa thêm ở Bước 1
		    
		    if (img == null) {
		        out.print("{\"status\":\"NOT_FOUND\", \"id\":" + imageId + "}");
		    } else {
		        // Trả về cả đường dẫn mới (filePath)
		        // Lưu ý: Cần xử lý dấu gạch chéo cho JSON hợp lệ
		        String cleanPath = "";
		        if(img.getFilePath() != null) {
		             cleanPath = img.getFilePath().replace("\\", "/");
		             // Nếu DB lưu full path ổ cứng, cắt lấy phần uploads/ trở đi
		             int idx = cleanPath.indexOf("uploads/");
		             if (idx != -1) cleanPath = cleanPath.substring(idx);
		        }
		        
		        out.print("{");
		        out.print("\"status\":\"" + img.getStatus() + "\",");
		        out.print("\"id\":" + imageId + ",");
		        out.print("\"filePath\":\"" + cleanPath + "\""); // Thêm dòng này
		        out.print("}");
		    }
		} catch (NumberFormatException e) {
		    out.print("{\"status\":\"ERROR\", \"message\":\"Invalid Image ID\"}");
		}
	}

}
