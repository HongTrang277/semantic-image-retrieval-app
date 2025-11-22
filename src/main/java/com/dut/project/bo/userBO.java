package com.dut.project.bo;

import com.dut.project.dao.userDAO;
import com.dut.project.model.user;

public class userBO {
    
    private userDAO userDAO = new userDAO();

    // Nghiệp vụ Đăng ký
    public boolean registerUser(user newUser) {
        // --- ĐÂY LÀ CHỖ ĐỂ VIẾT LOGIC NGHIỆP VỤ (BUSINESS LOGIC) ---
        
        // Ví dụ 1: Kiểm tra password có đủ dài không?
        if (newUser.getPassword().length() < 6) {
            System.out.println("Password quá ngắn!");
            return false; 
        }
        
        // Ví dụ 2: Mã hóa password trước khi lưu (VD dùng MD5/BCrypt)
        // String encryptedPass = someLibrary.encrypt(newUser.getPassword());
        // newUser.setPassword(encryptedPass);
        
        // Sau khi xử lý xong nghiệp vụ, mới gọi DAO để lưu xuống DB
        return userDAO.registerUser(newUser);
    }
    
    // Nghiệp vụ Đăng nhập
    public user checkLogin(String username, String password) {
        // Có thể thêm logic kiểm tra user bị khóa hay không ở đây
        return userDAO.checkLogin(username, password);
    }
}