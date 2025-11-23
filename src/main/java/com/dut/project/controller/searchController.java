package com.dut.project.controller;



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

@WebServlet("/search")
public class searchController extends HttpServlet {

    private imageDAO imageDAO = new imageDAO();
    
    // URL API Python (dựa trên hình ảnh bạn cung cấp)
    private static final String PYTHON_API_URL = "http://160.30.129.168:8386/search";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        // 1. Lấy từ khóa tìm kiếm từ JSP
        String query = request.getParameter("query");
        
        // Lấy user_id từ Session (Giả sử user đã login). 
        HttpSession session = request.getSession();
        // Integer userId = (Integer) session.getAttribute("userId");
        // if (userId == null) userId = 1; 
        int userId = 2; // Hardcode tạm để test

        List<image> resultImages = new ArrayList<>();

        if (query != null && !query.trim().isEmpty()) {
            try {
                // 2. Gọi API Python để lấy danh sách ID ảnh
                List<Integer> imageIds = callPythonSearchApi(userId, query, 2); // Top 10

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

    // Hàm gọi API Python
    private List<Integer> callPythonSearchApi(int userId, String query, int topK) throws IOException {
        URL url = new URL(PYTHON_API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Accept", "application/json"); // Thêm header Accept
        conn.setDoOutput(true);

        // Tạo body request
        // Lưu ý: Cần encode query để tránh lỗi với ký tự đặc biệt hoặc tiếng Việt
        String encodedQuery = java.net.URLEncoder.encode(query, "UTF-8");
        String urlParameters = "user_id=" + userId + "&query=" + encodedQuery + "&top_k=" + topK;

        // Gửi request
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = urlParameters.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // Kiểm tra mã phản hồi
        int responseCode = conn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Server trả về lỗi: " + responseCode);
        }

        // Đọc response
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        // Parse kết quả
        return extractIdsFromJson(response.toString());
    }

    // Hàm tách số ID từ JSON trả về
    // Cấu trúc JSON: { "data": { "results": [ { "image_id": "1", ... } ] } }
    private List<Integer> extractIdsFromJson(String jsonResponse) {
        List<Integer> ids = new ArrayList<>();
        
        // Regex tìm chuỗi: "image_id": "1" hoặc "image_id": 1
        // Giải thích Regex:
        // \"image_id\"  : Tìm chính xác key "image_id"
        // \s*:\s* : Dấu hai chấm và khoảng trắng tùy ý
        // \"?           : Dấu ngoặc kép mở (có thể có hoặc không)
        // (\d+)         : Nhóm cần lấy (các chữ số)
        // \"?           : Dấu ngoặc kép đóng (có thể có hoặc không)
        Pattern p = Pattern.compile("\"image_id\"\\s*:\\s*\"?(\\d+)\"?");
        Matcher m = p.matcher(jsonResponse);
        
        while(m.find()) {
            try {
                // m.group(1) là phần số nằm trong dấu ngoặc đơn (\d+)
                ids.add(Integer.parseInt(m.group(1)));
            } catch (NumberFormatException e) {
                // Bỏ qua nếu không phải số hợp lệ
            }
        }
        return ids;
    }
}