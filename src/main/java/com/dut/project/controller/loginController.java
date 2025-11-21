package com.dut.project.controller;

import com.dut.project.dao.userDAO;
import com.dut.project.model.user;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

// URL này trùng với action="login" trong form JSP
@WebServlet("/login")
public class loginController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private userDAO userDAO = new userDAO();

    // Xử lý khi User bấm nút Login (Method POST)
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Xử lý tiếng Việt
        request.setCharacterEncoding("UTF-8");
        
        String u = request.getParameter("username");
        String p = request.getParameter("password");
        
        user user = userDAO.checkLogin(u, p);
        
        if (user != null) {
            // Đăng nhập thành công -> Lưu vào Session
            HttpSession session = request.getSession();
            session.setAttribute("account", user);
            
            // Tạm thời in ra màn hình trình duyệt
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("<h1>Đăng nhập thành công!</h1>");
            response.getWriter().println("<h2>Xin chào: " + user.getFullName() + "</h2>");
        } else {
            // Đăng nhập thất bại -> Quay lại trang login và báo lỗi
            request.setAttribute("message", "Sai tên đăng nhập hoặc mật khẩu!");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
    
    // Xử lý khi User gõ link /login trực tiếp (Method GET)
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }
}