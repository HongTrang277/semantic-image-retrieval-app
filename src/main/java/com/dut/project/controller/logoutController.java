package com.dut.project.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/logout")
public class logoutController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false); // Lấy session hiện tại (nếu có)
        
        if (session != null) {
            // Hủy session (xóa thuộc tính "account")
            session.invalidate();
        }
        
        // Thêm thông báo đăng xuất thành công (tùy chọn)
        request.setAttribute("message", "Bạn đã đăng xuất thành công.");
        
        // Chuyển hướng người dùng về trang đăng nhập
        response.sendRedirect("loginPage.jsp");
    }
    
    // Xử lý POST (nếu cần, nhưng GET thường được dùng cho Logout)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}