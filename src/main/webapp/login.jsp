<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.dut.project.model.user" %>
<%
    // KHỐI CODE GIẢ LẬP: Nếu chưa có user trong session, tạo một user giả
    if (session.getAttribute("user") == null) {
        user dummyUser = new user();
        dummyUser.setId(4); // ID giả lập
        dummyUser.setFullName("Administrator");
        session.setAttribute("user", dummyUser);
        System.out.println("DEBUG: User giả lập (ID 1) đã được đặt vào Session.");
    }
%>
<!DOCTYPE html>
<html>
<head><title>Login Mock</title></head>
<body>
    <h1>Login Successful!</h1>
    <p>Session initialized with User ID: <%= ((user)session.getAttribute("user")).getId() %>.</p>
    <p>Tiếp tục đến trang upload:</p>
    <a href="upload.jsp">Bắt đầu Upload</a>
</body>
</html>