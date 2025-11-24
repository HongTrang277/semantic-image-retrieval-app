package com.dut.project.controller;

import com.dut.project.controller.worker.PythonApiClient;

import com.dut.project.dao.*;
import com.dut.project.model.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.dut.project.controller.worker.PythonApiClient;

@WebServlet("/search")
public class searchController extends HttpServlet {

    private imageDAO imageDAO = new imageDAO();
    private final PythonApiClient apiClient = new PythonApiClient();
    

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        // 1. Lấy từ khóa tìm kiếm từ JSP
        String query = request.getParameter("query");
        
        // Lấy user_id từ Session (Giả sử user đã login). 
        HttpSession session = request.getSession();
        // Integer userId = (Integer) session.getAttribute("userId");
        // if (userId == null) userId = 1; 
        int userId = 1; // Hardcode tạm để test

        List<image> resultImages = new ArrayList<>();

        if (query != null && !query.trim().isEmpty()) {
            try {
                // 2. Gọi API Python để lấy danh sách ID ảnh
                List<Integer> imageIds = apiClient.callSearch(userId, query, 2); // Top 10

                // 3. Từ danh sách ID, gọi DAO để lấy thông tin ảnh (đường dẫn file)
                if (!imageIds.isEmpty()) {
                    resultImages = imageDAO.getImagesByIds(imageIds);
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("error", "Lỗi kết nối đến AI Server: " + e.getMessage());
            }
        }

        // 4. Đẩy dữ liệu sang JSP để hiển thị
        request.setAttribute("searchResults", resultImages);
        request.setAttribute("currentQuery", query); 
        request.getRequestDispatcher("search.jsp").forward(request, response);
    }

}