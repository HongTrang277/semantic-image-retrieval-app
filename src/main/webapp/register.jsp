<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Đăng ký tài khoản</title>
    <style>
        body { font-family: Arial, sans-serif; margin-top: 50px; text-align: center; }
        form { display: inline-block; border: 1px solid #ccc; padding: 20px; border-radius: 10px; }
        input { display: block; margin: 10px 0; padding: 8px; width: 250px; }
        .error { color: red; }
    </style>
    
</head>
<body>
    <h2>Đăng ký thành viên mới</h2>
    
    <form action="register" method="post">
        <label>Username:</label>
        <input type="text" name="username" required>
        
        <label>Password:</label>
        <input type="password" name="password" required>
        
        <label>Họ và tên:</label>
        <input type="text" name="fullname" required>
        
        <button type="submit" style="background: #28a745; color: white; padding: 10px 20px; border: none; cursor: pointer;">Đăng Ký</button>
        <br><br>
        <a href="loginPage.jsp">Đã có tài khoản? Đăng nhập ngay</a>
        
        <p class="error">${error}</p>
    </form>
</body>
</html>