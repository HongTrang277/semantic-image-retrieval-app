<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Đăng nhập hệ thống</title>
    <style>
        body { font-family: Arial, sans-serif; margin-top: 50px; text-align: center; }
        form { display: inline-block; border: 1px solid #ccc; padding: 20px; border-radius: 10px; }
        input { display: block; margin: 10px 0; padding: 8px; width: 200px; }
        .error { color: red; }
    </style>
</head>
<body>
    <h2>Hệ thống lưu trữ và tìm kím hình ảnh thong qua ngữ nghĩa</h2>
    
    <form action="login" method="post">
        <h3>Đăng nhập</h3>
        
        <label>Username:</label>
        <input type="text" name="username" placeholder="Nhập username..." required>
        
        <label>Password:</label>
        <input type="password" name="password" placeholder="Nhập password..." required>
        
        <button type="submit" style="cursor:pointer; background: #007bff; color: white; border:none; padding: 10px 20px;">Login</button>
        
        <!-- Hiển thị thông báo lỗi nếu có -->
        <p class="error">${message}</p>
    </form>
</body>
</html>