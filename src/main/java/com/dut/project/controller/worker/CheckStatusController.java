package com.dut.project.controller.worker;
import com.dut.project.bo.imageBO;

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
			String status = imageBO.getImageStatus(imageId);
			if (status == null) {
                out.print("{\"status\":\"NOT_FOUND\", \"id\":" + imageId + "}");
            } else {
                out.print("{\"status\":\"" + status + "\", \"id\":" + imageId + "}");
            }
		}catch (NumberFormatException e) {
            out.print("{\"status\":\"ERROR\", \"message\":\"Invalid Image ID\"}");
        }
	}

}
