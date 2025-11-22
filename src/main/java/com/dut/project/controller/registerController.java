package com.dut.project.controller;

import com.dut.project.bo.userBO;
import com.dut.project.model.user;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/register")
public class registerController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private userBO userBO = new userBO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        String u = request.getParameter("username");
        String p = request.getParameter("password");
        String f = request.getParameter("fullname");
        
        user newUser = new user(u, p, f);
        
        boolean success = userBO.registerUser(newUser);
        
        if (success) {
            request.setAttribute("message", "Đăng ký thành công! Vui lòng đăng nhập.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        } else {
            // Nếu BO trả về false (do trùng tên hoặc pass ngắn...)
            request.setAttribute("error", "Đăng ký thất bại (Trùng tên hoặc mật khẩu < 6 ký tự)");
            request.getRequestDispatcher("register.jsp").forward(request, response);
        }
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("register.jsp").forward(request, response);
    }
}