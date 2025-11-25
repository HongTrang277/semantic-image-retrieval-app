<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
    <script>
        tailwind.config = {
            theme: {
                extend: {
                    fontFamily: { sans: ['"Plus Jakarta Sans"', 'sans-serif'] },
                    colors: { brand: { 500: '#0ea5e9', 600: '#0284c7' } },
                    // BỔ SUNG KEYFRAMES VÀ ANIMATION CHO HIỆU ỨNG BLOB
                    animation: {
                        'blob': 'blob 7s infinite',
                        'pulse-slow': 'pulse 4s cubic-bezier(0.4, 0, 0.6, 1) infinite',
                    },
                    keyframes: {
                        blob: {
                            '0%': { transform: 'translate(0px, 0px) scale(1)' },
                            '33%': { transform: 'translate(30px, -50px) scale(1.1)' },
                            '66%': { transform: 'translate(-20px, 20px) scale(0.9)' },
                            '100%': { transform: 'translate(0px, 0px) scale(1)' },
                        }
                    }
                    // KẾT THÚC BỔ SUNG BLOB
                }
            }
        }
    </script>
    <style>
        /* Đảm bảo màu lỗi/thông báo thống nhất */
        .error { color: #ef4444; } /* Đỏ */
        .success-message { color: #22c55e; } /* Xanh lá cây */
        
        /* BACKGROUND MỚI: Màu nền sáng (như index) */
        body {
            /* Loại bỏ gradient xanh lá cây cũ */
            background-color: #f4f4f9; 
            color: #334155; 
            min-height: 100vh;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            padding: 24px;
            position: relative; /* Quan trọng cho z-index */
            z-index: 10;
        }

        /* Đảm bảo các thành phần overlay không bị ảnh hưởng */
        .w-full.max-w-md.bg-white {
            background-color: white; 
            color: #334155;
            z-index: 20; /* Đặt card lên trên các blob */
            position: relative;
        }
        
        /* Đặt lại màu chữ cho tiêu đề lớn (vốn bị đổi thành màu sáng) */
        .login-header-title {
            color: #0f172a; /* Slate-900 */
            z-index: 20;
        }
        .login-header-subtitle {
            color: #475569; /* Slate-600 */
        }
        
    </style>
</head>
<body class="bg-slate-50 text-slate-800 antialiased overflow-x-hidden min-h-screen flex flex-col">
    
    <div class="fixed inset-0 pointer-events-none z-0 overflow-hidden">
        <div class="absolute top-0 left-1/4 w-96 h-96 bg-purple-300 rounded-full mix-blend-multiply filter blur-3xl opacity-30 animate-blob"></div>
        <div class="absolute top-0 right-1/4 w-96 h-96 bg-cyan-300 rounded-full mix-blend-multiply filter blur-3xl opacity-30 animate-blob animation-delay-2000"></div>
        <div class="absolute -bottom-32 left-1/3 w-96 h-96 bg-pink-300 rounded-full mix-blend-multiply filter blur-3xl opacity-30 animate-blob animation-delay-4000"></div>
    </div>
    <div class="mb-8 text-center login-header-title">
        <h1 class="text-4xl font-extrabold leading-tight">Hệ thống lưu trữ và tìm kiếm hình ảnh thông qua ngữ nghĩa</h1>
        <p class="text-xl login-header-subtitle mt-2">Dự án cuối kỳ môn Phát triển ứng dụng Web</p>
    </div>

    <div class="w-full max-w-md bg-white rounded-3xl shadow-xl overflow-hidden">
        <div class="p-8 md:p-10">
            <div class="text-center mb-8">
                <h2 class="text-3xl font-extrabold text-slate-900 mb-2">Đăng nhập</h2>
                <p class="text-slate-500">Tiếp tục hành trình tìm kiếm ảnh của bạn</p>
            </div>

            <form action="login" method="post" class="space-y-6">
                
                <div>
                    <label for="username" class="block text-sm font-medium text-slate-700 mb-1">Username:</label>
                    <input type="text" id="username" name="username" placeholder="Nhập tên đăng nhập..." required
                           class="w-full px-4 py-2.5 border border-slate-300 rounded-lg focus:ring-brand-500 focus:border-brand-500 transition duration-150">
                </div>
                
                <div>
                    <label for="password" class="block text-sm font-medium text-slate-700 mb-1">Password:</label>
                    <input type="password" id="password" name="password" placeholder="Nhập mật khẩu..." required
                           class="w-full px-4 py-2.5 border border-slate-300 rounded-lg focus:ring-brand-500 focus:border-brand-500 transition duration-150">
                </div>
                
                <button type="submit" class="w-full py-3 bg-brand-500 hover:bg-brand-600 text-white rounded-xl font-bold text-lg shadow-lg hover:shadow-brand-500/30 transition-all">
                    <i class="fas fa-sign-in-alt mr-2"></i> Đăng Nhập
                </button>
            </form>
            
            <div class="mt-6 text-center">
                <a href="register.jsp" class="text-sm font-medium text-brand-600 hover:text-brand-500 hover:underline">
                    Chưa có tài khoản? Đăng ký ngay
                </a>
                
                <c:if test="${not empty message}">
                    <p class="success-message mt-4 text-sm font-semibold">${message}</p>
                </c:if>
                <c:if test="${not empty error}">
                    <p class="error mt-4 text-sm font-semibold">${error}</p>
                </c:if>
            </div>
        </div>
    </div>
</body>
</html>