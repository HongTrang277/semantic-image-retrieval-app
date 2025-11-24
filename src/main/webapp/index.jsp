<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.dut.project.model.user" %>
<%
    // Lấy thông tin người dùng
    user currentUser = (user) session.getAttribute("account");
    boolean isLoggedIn = (currentUser != null);
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= isLoggedIn ? "Dashboard - SmartSearch" : "Trang chủ - SmartSearch" %></title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="css/style.css">
    
    <script>
        tailwind.config = {
            theme: {
                extend: {
                    fontFamily: { sans: ['"Plus Jakarta Sans"', 'sans-serif'] },
                    colors: { brand: { 50: '#f0f9ff', 100: '#e0f2fe', 500: '#0ea5e9', 600: '#0284c7', 700: '#0369a1', 900: '#0c4a6e' } },
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
                }
            }
        }
    </script>
</head>
<body class="bg-slate-50 text-slate-800 antialiased overflow-x-hidden selection:bg-brand-500 selection:text-white min-h-screen flex flex-col">

    <div class="fixed inset-0 pointer-events-none z-0 overflow-hidden">
        <div class="absolute top-0 left-1/4 w-96 h-96 bg-purple-300 rounded-full mix-blend-multiply filter blur-3xl opacity-30 animate-blob"></div>
        <div class="absolute top-0 right-1/4 w-96 h-96 bg-cyan-300 rounded-full mix-blend-multiply filter blur-3xl opacity-30 animate-blob animation-delay-2000"></div>
        <div class="absolute -bottom-32 left-1/3 w-96 h-96 bg-pink-300 rounded-full mix-blend-multiply filter blur-3xl opacity-30 animate-blob animation-delay-4000"></div>
    </div>

    <nav class="fixed w-full z-50 transition-all duration-300 glass shadow-sm" id="navbar">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div class="flex justify-between h-20 items-center">
                <div class="flex items-center gap-3 group cursor-pointer" onclick="window.location.href='index.jsp'">
                    <div class="relative w-10 h-10 bg-gradient-to-br from-brand-500 to-purple-600 rounded-xl flex items-center justify-center text-white font-bold text-xl shadow-lg">
                        <i class="fas fa-search-dollar"></i>
                    </div>
                    <div class="flex flex-col">
                        <span class="font-bold text-xl tracking-tight text-slate-800 leading-none">Smart<span class="text-brand-600">Search</span></span>
                    </div>
                </div>

                <div class="flex items-center gap-6">
                    <% if (isLoggedIn) { %>
                        <a href="uploadStatus.jsp" class="hidden md:flex items-center gap-2 text-slate-600 hover:text-brand-600 font-medium transition">
                            <i class="fas fa-tasks"></i> Trạng thái Upload
                        </a>
                        <div class="h-6 w-px bg-slate-300 hidden md:block"></div>
                        <span class="hidden md:block text-sm font-bold text-slate-700"><%= currentUser.getFullName() %></span>
                        <a href="loginPage.jsp" class="text-red-500 hover:text-red-700 font-semibold text-sm px-3 py-1 rounded-lg hover:bg-red-50 transition">
                            <i class="fas fa-sign-out-alt"></i>
                        </a>
                    <% } else { %>
                        <a href="loginPage.jsp" class="text-slate-600 hover:text-brand-600 font-semibold transition text-sm">Đăng nhập</a>
                        <a href="register.jsp" class="bg-slate-900 hover:bg-slate-800 text-white px-5 py-2.5 rounded-full font-semibold transition-all shadow-lg text-sm">
                            Đăng ký
                        </a>
                    <% } %>
                </div>
            </div>
        </div>
    </nav>

    <% if (!isLoggedIn) { %>
        
        <main class="relative z-10 pt-32 pb-20 lg:pt-40 lg:pb-32">
            <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                <div class="lg:grid lg:grid-cols-12 lg:gap-16 items-center">
                    <div class="lg:col-span-6 text-center lg:text-left mb-16 lg:mb-0">
                        <div class="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-blue-50 border border-blue-100 text-brand-700 font-semibold text-xs mb-8 uppercase tracking-wide shadow-sm">
                            <i class="fas fa-sparkles text-yellow-500"></i> Công nghệ AI 2024
                        </div>
                        <h1 class="text-5xl lg:text-6xl font-extrabold tracking-tight text-slate-900 mb-6 leading-[1.15]">
                            Hệ thống <br> <span class="text-brand-600">Tra cứu Ảnh Thông minh</span>
                        </h1>
                        <p class="text-lg text-slate-600 mb-10">Tìm kiếm hình ảnh bằng ngôn ngữ tự nhiên. Hãy thử mô tả: <i>"con mèo đang ngủ"</i> hoặc <i>"hoàng hôn trên biển"</i>.</p>
                        
                        <form action="search" method="get" class="relative bg-white rounded-2xl shadow-xl flex items-center p-2 border border-slate-100 max-w-xl mx-auto lg:mx-0">
                            <div class="pl-4 text-slate-400"><i class="fas fa-search text-xl"></i></div>
                            <input type="text" name="query" class="w-full bg-transparent border-none focus:ring-0 text-slate-700 text-lg px-4 py-3" placeholder="Nhập từ khóa demo..." required>
                            <button type="submit" class="bg-brand-600 hover:bg-brand-700 text-white px-6 py-3 rounded-xl font-bold">Tìm kiếm</button>
                        </form>
                    </div>

                    <div class="lg:col-span-6 relative">
                        <div class="bg-white/80 backdrop-blur-xl rounded-3xl shadow-2xl border border-white/50 overflow-hidden p-8 text-center">
                            <div class="w-20 h-20 bg-brand-100 text-brand-600 rounded-full flex items-center justify-center text-3xl mx-auto mb-6">
                                <i class="fas fa-flask"></i>
                            </div>
                            <h3 class="text-2xl font-bold text-slate-800 mb-2">Chế độ Demo</h3>
                            <p class="text-slate-500 mb-8">Bạn đang xem ở chế độ khách. Vui lòng đăng nhập để sử dụng tính năng Upload và Lưu trữ.</p>
                            <a href="loginPage.jsp" class="inline-block w-full py-3 bg-slate-900 text-white rounded-xl font-bold hover:bg-slate-800 transition">
                                Đăng nhập ngay
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </main>
        
        <footer class="bg-white border-t border-slate-200 py-8 mt-auto relative z-10">
            <div class="max-w-7xl mx-auto text-center text-slate-500">
                &copy; 2024 SmartSearch Project.
            </div>
        </footer>

    <% } else { %>

        <main class="relative z-10 pt-28 pb-12 flex-grow">
            <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-full">
                
                <div class="grid lg:grid-cols-12 gap-8 h-full">
                    
                    <div class="lg:col-span-7 flex flex-col gap-6">
                        <div class="bg-white rounded-2xl shadow-lg border border-slate-100 p-8">
                            <h2 class="text-2xl font-bold text-slate-800 mb-4 flex items-center gap-2">
                                <i class="fas fa-search text-brand-500"></i> Tra cứu dữ liệu
                            </h2>
                            <form action="search" method="get" class="relative group">
                                <div class="absolute -inset-0.5 bg-gradient-to-r from-brand-500 to-purple-600 rounded-xl blur opacity-30 group-hover:opacity-50 transition duration-200"></div>
                                <div class="relative flex bg-white rounded-xl p-1">
                                    <input type="text" name="query" 
                                           class="w-full bg-transparent border-none focus:ring-0 text-slate-700 text-lg px-4 py-3 outline-none" 
                                           placeholder="Mô tả ảnh cần tìm (VD: Chó cười, biển xanh)..." required>
                                    <button type="submit" class="bg-slate-900 hover:bg-slate-800 text-white px-6 py-2 rounded-lg font-semibold transition">
                                        Tìm
                                    </button>
                                </div>
                            </form>
                            <div class="mt-4 flex flex-wrap gap-2 text-sm text-slate-500">
                                <span>Gợi ý:</span>
                                <span class="cursor-pointer hover:text-brand-600 bg-slate-50 px-2 py-1 rounded border border-slate-200 tag-suggest">Mèo mun</span>
                                <span class="cursor-pointer hover:text-brand-600 bg-slate-50 px-2 py-1 rounded border border-slate-200 tag-suggest">Xe đỏ</span>
                            </div>
                        </div>

                        <div class="bg-blue-50 rounded-2xl border border-blue-100 p-6 flex items-start gap-4">
                            <div class="text-blue-500 text-xl"><i class="fas fa-info-circle"></i></div>
                            <div>
                                <h4 class="font-bold text-blue-900">Mẹo sử dụng</h4>
                                <p class="text-blue-700/80 text-sm mt-1">Hệ thống hỗ trợ tìm kiếm ngữ nghĩa. Bạn không cần nhập đúng tên file, hãy mô tả nội dung bức ảnh.</p>
                            </div>
                        </div>
                    </div>

                    <div class="lg:col-span-5">
                        <div class="bg-white rounded-2xl shadow-xl border border-slate-100 overflow-hidden flex flex-col h-full">
                            <div class="p-4 border-b border-slate-100 bg-slate-50/50 flex justify-between items-center">
                                <h3 class="font-bold text-slate-800"><i class="fas fa-cloud-upload-alt text-brand-500 mr-2"></i> Tải ảnh lên</h3>
                                <span class="text-xs bg-green-100 text-green-700 px-2 py-1 rounded-full font-bold">Live Server</span>
                            </div>

                            <form id="uploadForm" action="upload" method="post" enctype="multipart/form-data" class="p-6 flex flex-col flex-grow">
                                
                                <div id="drop-area" class="flex-grow dashed-border rounded-xl bg-slate-50 hover:bg-blue-50/50 transition-colors cursor-pointer flex flex-col items-center justify-center p-8 min-h-[250px] relative group">
                                    
                                    <div class="w-16 h-16 bg-white shadow-sm rounded-full flex items-center justify-center text-brand-500 text-3xl mb-4 group-hover:scale-110 transition-transform">
                                        <i class="fas fa-images"></i>
                                    </div>
                                    <p class="text-slate-600 font-medium mb-1">Kéo thả ảnh vào đây</p>
                                    <p class="text-slate-400 text-sm mb-6">hoặc bấm để chọn file</p>

                                    <input type="file" id="fileInput" name="files" multiple accept="image/*" class="absolute inset-0 opacity-0 cursor-pointer z-10" onchange="handleFiles(this.files)">
                                    
                                    <button type="button" onclick="document.getElementById('folderInput').click()" class="relative z-20 text-xs text-brand-600 hover:underline font-semibold">
                                        <i class="fas fa-folder-open"></i> Chọn cả thư mục
                                    </button>
                                    <input type="file" id="folderInput" name="folderFiles" webkitdirectory directory multiple class="hidden" onchange="handleFiles(this.files)">
                                </div>

                                <div id="preview-area" class="hidden mt-4 bg-slate-50 rounded-lg p-3 border border-slate-200">
                                    <div class="flex justify-between items-center mb-2">
                                        <span class="text-xs font-bold text-slate-600">Đã chọn: <span id="file-count" class="text-brand-600">0</span> file</span>
                                        <button type="button" onclick="resetUpload()" class="text-xs text-red-500 hover:text-red-700 font-semibold">Hủy</button>
                                    </div>
                                    <div id="preview-grid" class="grid grid-cols-5 gap-2 max-h-24 overflow-y-auto custom-scrollbar"></div>
                                </div>

                                <button type="submit" id="submitBtn" class="mt-4 w-full py-3 bg-brand-600 hover:bg-brand-700 text-white rounded-xl font-bold shadow-lg hover:shadow-brand-500/30 transition-all flex items-center justify-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed" disabled>
                                    <span>Tải lên ngay</span> <i class="fas fa-arrow-right"></i>
                                </button>
                            </form>
                        </div>
                    </div>

                </div>
            </div>
        </main>
    <% } %>

    <div id="loading-overlay" class="fixed inset-0 bg-slate-900/90 backdrop-blur-sm z-[100] hidden flex-col items-center justify-center text-white">
        <div class="relative w-20 h-20 mb-6">
            <div class="absolute inset-0 border-4 border-white/20 rounded-full"></div>
            <div class="absolute inset-0 border-4 border-brand-500 rounded-full border-t-transparent animate-spin"></div>
            <div class="absolute inset-0 flex items-center justify-center text-2xl text-brand-400">
                <i class="fas fa-rocket"></i>
            </div>
        </div>
        <h2 class="text-2xl font-bold mb-1">Đang xử lý...</h2>
        <p class="text-slate-400 text-sm">Đang tải ảnh lên Server và trích xuất Vector.</p>
    </div>

    <script>
        // Script chung cho Dashboard
        const previewArea = document.getElementById('preview-area');
        const previewGrid = document.getElementById('preview-grid');
        const fileCount = document.getElementById('file-count');
        const submitBtn = document.getElementById('submitBtn');
        const uploadForm = document.getElementById('uploadForm');
        const loadingOverlay = document.getElementById('loading-overlay');
        const dropArea = document.getElementById('drop-area');

        // Xử lý File được chọn
        function handleFiles(files) {
            if (files.length > 0) {
                previewArea.classList.remove('hidden');
                // Ẩn bớt vùng drop cho gọn nếu muốn, hoặc giữ nguyên
                dropArea.classList.add('border-brand-500', 'bg-blue-50');
                
                fileCount.innerText = files.length;
                submitBtn.disabled = false;
                previewGrid.innerHTML = '';

                // Preview tối đa 10 ảnh
                const limit = Math.min(files.length, 10);
                Array.from(files).slice(0, limit).forEach(file => {
                    if (file.type.startsWith('image/')) {
                        const reader = new FileReader();
                        reader.onload = e => {
                            const img = document.createElement('img');
                            img.src = e.target.result;
                            img.className = 'w-full h-12 object-cover rounded border border-slate-300';
                            previewGrid.appendChild(img);
                        }
                        reader.readAsDataURL(file);
                    }
                });
                
                if(files.length > 10) {
                    const more = document.createElement('div');
                    more.className = 'w-full h-12 flex items-center justify-center bg-slate-200 rounded text-xs font-bold text-slate-500';
                    more.innerText = '+' + (files.length - 10);
                    previewGrid.appendChild(more);
                }
            }
        }

        // Reset Form
        function resetUpload() {
            if(uploadForm) uploadForm.reset();
            previewArea.classList.add('hidden');
            previewGrid.innerHTML = '';
            submitBtn.disabled = true;
            if(dropArea) dropArea.classList.remove('border-brand-500', 'bg-blue-50');
        }

        // Hiệu ứng Drag & Drop
        if(dropArea) {
            ['dragenter', 'dragover'].forEach(evt => {
                dropArea.addEventListener(evt, (e) => {
                    e.preventDefault();
                    dropArea.classList.add('border-brand-500', 'bg-blue-100');
                });
            });
            ['dragleave', 'drop'].forEach(evt => {
                dropArea.addEventListener(evt, (e) => {
                    e.preventDefault();
                    dropArea.classList.remove('border-brand-500', 'bg-blue-100');
                });
            });
            dropArea.addEventListener('drop', (e) => {
                const dt = e.dataTransfer;
                const files = dt.files;
                document.getElementById('fileInput').files = files;
                handleFiles(files);
            });
        }

        // Submit Form -> Show Loading
        if(uploadForm) {
            uploadForm.addEventListener('submit', () => {
                loadingOverlay.classList.remove('hidden');
                loadingOverlay.classList.add('flex');
                // Sau khi submit, Controller Java sẽ redirect sang uploadStatus.jsp
            });
        }

        // Gợi ý tìm kiếm
        document.querySelectorAll('.tag-suggest').forEach(tag => {
            tag.addEventListener('click', function() {
                const input = document.querySelector('input[name="query"]');
                if(input) input.value = this.innerText;
            });
        });

        // Navbar scroll effect
        window.addEventListener('scroll', () => {
            const nav = document.getElementById('navbar');
            if (window.scrollY > 10) {
                nav.classList.add('bg-white/90', 'backdrop-blur-md', 'shadow-md');
            } else {
                nav.classList.remove('bg-white/90', 'backdrop-blur-md', 'shadow-md');
            }
        });
    </script>
</body>
</html>