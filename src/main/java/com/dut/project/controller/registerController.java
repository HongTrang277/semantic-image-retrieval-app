package com.dut.project.controller;

import com.dut.project.dao.userDAO;
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
    private userDAO userDAO = new userDAO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        String u = request.getParameter("username");
        String p = request.getParameter("password");
        String f = request.getParameter("fullname");
        
        user newUser = new user(u, p, f);
        
        boolean success = userDAO.registerUser(newUser);
        
        if (success) {
            // Đăng ký thành công -> Chuyển sang trang login để đăng nhập
            request.setAttribute("message", "Đăng ký thành công! Vui lòng đăng nhập.");
            request.getRequestDispatcher("loginPage.jsp").forward(request, response);
        } else {
            // Đăng ký thất bại (Trùng tên) -> Báo lỗi
            request.setAttribute("error", "Tên đăng nhập đã tồn tại hoặc lỗi hệ thống!");
            request.getRequestDispatcher("register.jsp").forward(request, response);
        }
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("register.jsp").forward(request, response);
    }
}