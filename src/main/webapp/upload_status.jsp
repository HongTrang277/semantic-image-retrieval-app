<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head><title>Job Status</title></head>
<body>
    <h2>✅ Job Khởi tạo Thành công!</h2>
    <% 
        String imageId = request.getParameter("id");
    %>
    
    <p>Metadata ảnh đã được lưu vào DB.</p>
    <p>ID Ảnh vừa tạo: <strong><%= imageId %></strong></p>
    <p>Trạng thái hiện tại: PENDING</p>
    
    <hr>
    <h3>Kiểm tra:</h3>
    <ul>
        <li>File ảnh có nằm trong thư mục uploads/raw không?</li>
        <li>Trong DB, có record mới với status 'PENDING' và ID = <%= imageId %> không?</li>
    </ul>

    </body>
</html>