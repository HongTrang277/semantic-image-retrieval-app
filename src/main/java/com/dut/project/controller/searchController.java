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
    
 // Ngưỡng mặc định nếu người dùng không chọn
    private static final double DEFAULT_MIN_SCORE = 0.5;
    

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        // 1. Lấy từ khóa tìm kiếm và Ngưỡng điểm tương đồng từ JSP
        String query = request.getParameter("query");
        String minScoreParam = request.getParameter("minScore"); // Đọc tham số mới
        
        double minScore = DEFAULT_MIN_SCORE;
        if (minScoreParam != null && !minScoreParam.trim().isEmpty()) {
            try {
                minScore = Double.parseDouble(minScoreParam);
                // Đảm bảo minScore nằm trong khoảng hợp lệ (0.0 đến 1.0)
                if (minScore < 0.0) minScore = 0.0;
                if (minScore > 1.0) minScore = 1.0;
            } catch (NumberFormatException e) {
                System.err.println("Lỗi parse minScore, sử dụng giá trị mặc định.");
                minScore = DEFAULT_MIN_SCORE;
            }
        }
        
        // Lấy user_id từ Session (Giả sử user đã login). 
        HttpSession session = request.getSession();
        user currentUser = (user) session.getAttribute("account");
        
        int userId = 1; // ID mặc định (nếu chưa đăng nhập hoặc demo)
        
        if (currentUser != null) {
            userId = currentUser.getId();
        }

        List<image> resultImages = new ArrayList<>();
        
        System.out.println("DEBUG: Đang tìm kiếm với Query: " + query + " | UserID: " + userId + " | MinScore: " + minScore);

        if (query != null && !query.trim().isEmpty()) {
            try {
                // SỬA LỖI TẠI ĐÂY: Thêm minScore vào làm tham số thứ tư (Dòng 82)
                List<Integer> imageIds = apiClient.callSearch(userId, query, 1000, minScore); 

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