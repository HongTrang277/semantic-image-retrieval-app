<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.dut.project.model.user" %>
<%
    // Kiểm tra đăng nhập (Bảo vệ trang)
    user currentUser = (user) session.getAttribute("account");
    if (currentUser == null) {
        response.sendRedirect("loginPage.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Upload Ảnh - SmartSearch AI</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="css/style.css">
    
    <script>
        tailwind.config = {
            theme: {
                extend: {
                    fontFamily: { sans: ['"Plus Jakarta Sans"', 'sans-serif'] },
                    colors: { brand: { 500: '#0ea5e9', 600: '#0284c7' } }
                }
            }
        }
    </script>
</head>
<body class="bg-slate-50 text-slate-800 min-h-screen flex flex-col">

    <nav class="bg-white border-b border-gray-200 px-6 py-4 flex justify-between items-center sticky top-0 z-50">
        <div class="flex items-center gap-2 cursor-pointer" onclick="window.location.href='index.jsp'">
            <div class="w-8 h-8 bg-gradient-to-br from-brand-500 to-blue-600 rounded-lg text-white flex items-center justify-center font-bold">S</div>
            <span class="font-bold text-lg tracking-tight">SmartSearch</span>
        </div>
        <div class="flex items-center gap-4">
            <span class="text-sm text-slate-500">Xin chào, <b><%= currentUser.getFullName() %></b></span>
            <a href="logout" class="text-sm text-red-500 hover:text-red-700 font-medium">Đăng xuất</a>
        </div>
    </nav>

    <main class="flex-grow flex flex-col items-center justify-center p-6">
        
        <div class="w-full max-w-3xl bg-white rounded-3xl shadow-xl overflow-hidden">
            <div class="p-8 md:p-12">
                <div class="text-center mb-10">
                    <h1 class="text-3xl font-extrabold text-slate-900 mb-2">Tải ảnh lên hệ thống</h1>
                    <p class="text-slate-500">Hỗ trợ định dạng JPG, PNG, WebP. AI sẽ tự động phân tích sau khi tải lên.</p>
                </div>

                <form id="uploadForm" action="upload" method="post" enctype="multipart/form-data" class="space-y-6">
                    
                    <div id="drop-zone" class="dashed-border rounded-2xl p-10 flex flex-col items-center justify-center text-center cursor-pointer transition-all duration-300 min-h-[250px]">
                        <div class="w-20 h-20 bg-blue-50 text-brand-500 rounded-full flex items-center justify-center text-4xl mb-4 pointer-events-none">
                            <i class="fas fa-cloud-upload-alt"></i>
                        </div>
                        <h3 class="text-lg font-semibold text-slate-700 pointer-events-none">Kéo thả ảnh vào đây</h3>
                        <p class="text-sm text-slate-400 mt-2 mb-6 pointer-events-none">Hoặc chọn phương thức tải lên bên dưới</p>

                        <div class="flex gap-4 z-10">
                            <button type="button" onclick="document.getElementById('fileInput').click()" 
                                    class="px-5 py-2.5 bg-white border border-slate-200 text-slate-700 rounded-xl font-semibold hover:border-brand-500 hover:text-brand-600 hover:shadow-md transition flex items-center gap-2">
                                <i class="fas fa-images"></i> Chọn Ảnh Lẻ
                            </button>
                            
                            <button type="button" onclick="document.getElementById('folderInput').click()" 
                                    class="px-5 py-2.5 bg-white border border-slate-200 text-slate-700 rounded-xl font-semibold hover:border-brand-500 hover:text-brand-600 hover:shadow-md transition flex items-center gap-2">
                                <i class="fas fa-folder-open"></i> Chọn Thư Mục
                            </button>
                        </div>

                        <input type="file" id="fileInput" name="files" multiple accept="image/*" class="hidden" onchange="handleFiles(this.files)">
                        <input type="file" id="folderInput" name="folderFiles" webkitdirectory directory multiple class="hidden" onchange="handleFiles(this.files)">
                    </div>

                    <div id="preview-container" class="hidden">
                        <div class="flex justify-between items-center mb-4">
                            <h4 class="font-bold text-slate-700">Ảnh đã chọn (<span id="count">0</span>)</h4>
                            <button type="button" onclick="resetForm()" class="text-xs text-red-500 hover:underline">Xóa tất cả</button>
                        </div>
                        <div class="grid grid-cols-4 sm:grid-cols-5 md:grid-cols-6 gap-3 max-h-60 overflow-y-auto p-2 border border-slate-100 rounded-xl bg-slate-50" id="preview-grid">
                            </div>
                    </div>

                    <button type="submit" id="submitBtn" class="w-full py-4 bg-brand-600 hover:bg-brand-500 text-white rounded-xl font-bold text-lg shadow-lg hover:shadow-xl transition-all disabled:opacity-50 disabled:cursor-not-allowed" disabled>
                        Bắt đầu Tải lên & Xử lý AI
                    </button>
                </form>
            </div>
            
            <div class="bg-slate-50 px-8 py-4 border-t border-slate-100 text-center text-xs text-slate-400">
                Hệ thống sẽ chuyển hướng sang trang trạng thái sau khi tải xong. Vui lòng không tắt trình duyệt.
            </div>
        </div>
    </main>

    <div id="loading-overlay" class="fixed inset-0 bg-slate-900/80 backdrop-blur-sm z-[100] hidden flex-col items-center justify-center text-white">
        <div class="relative w-24 h-24 mb-6">
            <div class="absolute inset-0 border-4 border-white/20 rounded-full"></div>
            <div class="absolute inset-0 border-4 border-brand-500 rounded-full border-t-transparent animate-spin"></div>
            <div class="absolute inset-0 flex items-center justify-center text-2xl">
                <i class="fas fa-rocket animate-pulse"></i>
            </div>
        </div>
        <h2 class="text-2xl font-bold mb-2">Đang tải ảnh lên...</h2>
        <p class="text-slate-300">Đang khởi tạo tiến trình xử lý AI. Vui lòng đợi.</p>
    </div>

    <script>
        const dropZone = document.getElementById('drop-zone');
        const fileInput = document.getElementById('fileInput');
        const folderInput = document.getElementById('folderInput');
        const previewContainer = document.getElementById('preview-container');
        const previewGrid = document.getElementById('preview-grid');
        const countSpan = document.getElementById('count');
        const submitBtn = document.getElementById('submitBtn');
        const uploadForm = document.getElementById('uploadForm');
        const loadingOverlay = document.getElementById('loading-overlay');

        // Drag & Drop Events
        ['dragenter', 'dragover', 'dragleave', 'drop'].forEach(eventName => {
            dropZone.addEventListener(eventName, preventDefaults, false);
        });

        function preventDefaults(e) {
            e.preventDefault();
            e.stopPropagation();
        }

        ['dragenter', 'dragover'].forEach(eventName => {
            dropZone.addEventListener(eventName, () => dropZone.classList.add('drag-active'), false);
        });

        ['dragleave', 'drop'].forEach(eventName => {
            dropZone.addEventListener(eventName, () => dropZone.classList.remove('drag-active'), false);
        });

        dropZone.addEventListener('drop', handleDrop, false);

        function handleDrop(e) {
            const dt = e.dataTransfer;
            const files = dt.files;
            
            // Lưu ý: Input file chuẩn của HTML không hỗ trợ gán files từ Drop một cách trực tiếp cho cả 2 input
            // Chúng ta sẽ gán vào fileInput (input lẻ)
            fileInput.files = files; 
            handleFiles(files);
        }

        function handleFiles(files) {
            if (files.length > 0) {
                previewContainer.classList.remove('hidden');
                submitBtn.disabled = false;
                countSpan.innerText = files.length;
                
                // Clear old preview
                previewGrid.innerHTML = '';

                // Chỉ hiện tối đa 12 ảnh đầu tiên để demo (tránh lag nếu folder nặng)
                const limit = Math.min(files.length, 12);
                
                Array.from(files).slice(0, limit).forEach(file => {
                    if (file.type.startsWith('image/')) {
                        const reader = new FileReader();
                        reader.onload = function(e) {
                            const div = document.createElement('div');
                            div.className = 'relative aspect-square rounded-lg overflow-hidden border border-slate-200';
                            div.innerHTML = `<img src="\${e.target.result}" class="w-full h-full object-cover">`;
                            previewGrid.appendChild(div);
                        }
                        reader.readAsDataURL(file);
                    }
                });

                if (files.length > 12) {
                    const moreDiv = document.createElement('div');
                    moreDiv.className = 'aspect-square rounded-lg bg-slate-100 flex items-center justify-center text-slate-500 font-bold border border-slate-200';
                    moreDiv.innerText = '+' + (files.length - 12);
                    previewGrid.appendChild(moreDiv);
                }
            }
        }

        function resetForm() {
            uploadForm.reset();
            previewContainer.classList.add('hidden');
            previewGrid.innerHTML = '';
            submitBtn.disabled = true;
        }

        // Xử lý khi Submit form
        uploadForm.addEventListener('submit', function(e) {
            // Hiển thị Overlay Loading
            loadingOverlay.classList.remove('hidden');
            loadingOverlay.classList.add('flex');
            
            // Form sẽ tự submit sau đó và trình duyệt sẽ chuyển trang
            // Backend Controller sẽ redirect sang uploadStatus.jsp
        });
    </script>
</body>
</html>