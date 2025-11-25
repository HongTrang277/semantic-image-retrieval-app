package com.dut.project.controller;

import com.dut.project.bo.imageBO;
import com.dut.project.model.image;
import com.dut.project.model.user;
import com.dut.project.model.ImageBatch; // Đảm bảo đã tạo ImageBatch.java

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/uploadHistory")
public class UploadHistoryController extends HttpServlet {
    private final imageBO imageBO = new imageBO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        user currentUser = (user) session.getAttribute("account");
        
        if (currentUser == null) {
            response.sendRedirect("loginPage.jsp");
            return;
        }
        
        int userId = currentUser.getId();
        
        // 1. Lấy tất cả ảnh của user
        List<image> allImages = imageBO.getAllImagesByUserId(userId);
        
        // 2. Nhóm ảnh theo thời gian upload (Batch)
        Map<String, List<image>> groupedByTime = new LinkedHashMap<>();
        
        for (image img : allImages) {
            // Chuẩn hóa thời gian thành chuỗi YYYY-MM-DD HH:MM:SS để nhóm chính xác
            Timestamp uploadTime = img.getUploadTime();
            if(uploadTime == null) continue; 
            
            // Cắt bỏ phần nanoseconds để đảm bảo các ảnh upload cùng lúc được nhóm
            String timeKey = uploadTime.toString().substring(0, 19); 
            
            groupedByTime.computeIfAbsent(timeKey, k -> new ArrayList<>()).add(img);
        }
        
        // 3. Chuyển đổi Map sang List<ImageBatch>
        List<ImageBatch> batches = groupedByTime.entrySet().stream()
            .map(entry -> new ImageBatch(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
        
        request.setAttribute("imageBatches", batches);
        
        // 4. Forward đến trang JSP
        request.getRequestDispatcher("uploadHistory.jsp").forward(request, response);
    }
}