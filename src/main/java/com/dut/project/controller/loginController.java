package com.dut.project.controller;

import com.dut.project.bo.userBO;
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
    private userBO userBO = new userBO();

    // Xử lý khi User bấm nút Login (Method POST)
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Xử lý tiếng Việt
        request.setCharacterEncoding("UTF-8");
        
        String u = request.getParameter("username");
        String p = request.getParameter("password");
        
        user account = userBO.checkLogin(u, p);
        
        if (account != null) {
            // Đăng nhập thành công -> Lưu vào Session
            HttpSession session = request.getSession();
            session.setAttribute("account", account);
            
            // Tạm thời in ra màn hình trình duyệt
            response.sendRedirect("index.jsp");
        } else {
            // Đăng nhập thất bại -> Quay lại trang login và báo lỗi
            request.setAttribute("message", "Sai tên đăng nhập hoặc mật khẩu!");
            request.getRequestDispatcher("loginPage.jsp").forward(request, response);
        }
    }
    
    // Xử lý khi User gõ link /login trực tiếp (Method GET)
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("loginPage.jsp").forward(request, response);
    }
}