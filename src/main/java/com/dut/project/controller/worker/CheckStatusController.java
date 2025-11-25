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
		
		try {
		    int imageId = Integer.parseInt(imageIdStr);
		    
		    image img = imageBO.getImageById(imageId); 
		    
		    if (img == null) {
		        out.print("{\"status\":\"NOT_FOUND\", \"id\":" + imageId + "}");
		    } else {
		        String cleanPath = "";
		        if(img.getFilePath() != null) {
		             cleanPath = img.getFilePath().replace("\\", "/");
		             int idx = cleanPath.indexOf("uploads/");
		             if (idx != -1) cleanPath = cleanPath.substring(idx);
		        }
		        
		        out.print("{");
		        out.print("\"status\":\"" + img.getStatus() + "\",");
		        out.print("\"id\":" + imageId + ",");
		        out.print("\"filePath\":\"" + cleanPath + "\""); // Thêm dòng này
		        out.print("}");
		        
		        //Trả về kiểu dữ liệu JSON, tức là kiểu đối tượng có dạng: 
		        // {
		        //  "status": "DONE"
		        //  "id": 101
		        // "filePath": ...
		        //}
		        //Tức là nó đang gửi đối tượng Image
		    }
		} catch (NumberFormatException e) {
		    out.print("{\"status\":\"ERROR\", \"message\":\"Invalid Image ID\"}");
		}
	}

}
