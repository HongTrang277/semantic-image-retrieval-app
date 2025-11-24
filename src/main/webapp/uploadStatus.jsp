<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.dut.project.dao.imageDAO" %>
<%@ page import="com.dut.project.model.image" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.File" %>

<%
    // 1. LẤY DỮ LIỆU TỪ CONTROLLER (URL: ?ids=1,2,3)
    String idsParam = request.getParameter("ids");
    List<image> imageList = new ArrayList<>();
    
    if (idsParam != null && !idsParam.isEmpty()) {
        try {
            String[] idArray = idsParam.split(",");
            List<Integer> idList = new ArrayList<>();
            for (String s : idArray) {
                idList.add(Integer.parseInt(s.trim()));
            }
            
            // Gọi DAO để lấy thông tin chi tiết các ảnh
            imageDAO dao = new imageDAO();
            imageList = dao.getImagesByIds(idList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
%>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Theo dõi trạng thái xử lý AI</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    
    <style>
        /* CSS GIAO DIỆN HIỆN ĐẠI */
        :root {
            --primary: #4e73df;
            --success: #1cc88a;
            --warning: #f6c23e;
            --danger: #e74a3b;
            --secondary: #858796;
            --bg-color: #f3f4f6;
        }

        body {
            font-family: 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
            background-color: var(--bg-color);
            margin: 0;
            padding: 40px;
            color: #5a5c69;
        }

        .dashboard-card {
            width: 98%;           /* Chiếm 98% chiều ngang màn hình */
            max-width: none;      /* Bỏ giới hạn 1000px cũ */
            margin: 0 auto;
            background: white;
            border-radius: 12px;
            box-shadow: 0 10px 25px rgba(0,0,0,0.05);
            overflow: hidden;
            position: relative;
        }

        /* Banner hoàn tất */
        #completion-banner {
            background: linear-gradient(135deg, #1cc88a 0%, #13855c 100%);
            color: white;
            padding: 20px 30px;
            display: none;
            animation: slideDown 0.5s ease-out;
            border-bottom: 4px solid #0e6645;
        }

        .completion-content {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .completion-text h3 { margin: 0 0 5px 0; font-size: 20px; }
        .completion-text p { margin: 0; opacity: 0.9; font-size: 14px; }

        .btn-go-search {
            background: white;
            color: #13855c;
            padding: 10px 25px;
            border-radius: 30px;
            text-decoration: none;
            font-weight: 700;
            box-shadow: 0 4px 10px rgba(0,0,0,0.2);
            transition: transform 0.2s;
            display: flex;
            align-items: center;
            gap: 8px;
        }
        .btn-go-search:hover { transform: scale(1.05); }

        @keyframes slideDown {
            from { transform: translateY(-100%); opacity: 0; }
            to { transform: translateY(0); opacity: 1; }
        }

        /* Header */
        .header {
            background: linear-gradient(135deg, var(--primary) 0%, #224abe 100%);
            color: white;
            padding: 25px 30px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .header-title h2 { margin: 0; font-size: 22px; font-weight: 700; }
        .header-title p { margin: 5px 0 0; opacity: 0.85; font-size: 13px; }

        .btn-action {
            background: rgba(255,255,255,0.2);
            color: white;
            padding: 10px 20px;
            border-radius: 30px;
            text-decoration: none;
            font-weight: 600;
            font-size: 14px;
            border: 1px solid rgba(255,255,255,0.4);
            display: flex; align-items: center; gap: 8px;
            transition: all 0.3s;
        }
        .btn-action:hover { background: white; color: var(--primary); }

        /* Table */
        .table-container { width: 100%; overflow-x: auto; min-height: 300px; }
        table { width: 100%; border-collapse: collapse; }
        thead th {
            background-color: #f8f9fc; color: var(--secondary);
            font-weight: 700; text-transform: uppercase; font-size: 0.8rem;
            padding: 15px 20px; text-align: left; border-bottom: 2px solid #eaecf4;
        }
        tbody td { padding: 15px 20px; vertical-align: middle; border-bottom: 1px solid #eaecf4; font-size: 14px; }
        tbody tr { background: white; transition: background 0.3s; }
        tbody tr:hover { background-color: #fafbfc; }

        .img-thumb { width: 60px; height: 60px; object-fit: cover; border-radius: 8px; border: 1px solid #ddd; }
        .file-meta { display: flex; flex-direction: column; }
        .filename { font-weight: 700; color: #333; margin-bottom: 3px; }
        .filepath { font-size: 11px; color: #aaa; font-family: monospace;
        display: block;
            white-space: nowrap;      /* Bắt buộc hiển thị trên 1 dòng */
            overflow: hidden;         /* Ẩn phần bị tràn ra ngoài */
            text-overflow: ellipsis;  /* Thêm dấu ... ở cuối */
            max-width: 100%;          /* Tự động nương theo độ rộng cột */ }
            
         table th:nth-child(3), table td:nth-child(3) {
        max-width: 400px; /* Đặt giới hạn chiều rộng để dấu ... hoạt động */
    }
        
        .badge {
            display: inline-flex; align-items: center; padding: 6px 12px;
            border-radius: 50px; font-size: 11px; font-weight: 800;
            text-transform: uppercase; letter-spacing: 0.5px;
        }
        .badge i { margin-right: 5px; }
        
        /* Trạng thái */
        .badge-pending { background-color: #fff3cd; color: #856404; border: 1px solid #ffeeba; }
        .badge-running { background-color: #e3f2fd; color: #0d47a1; border: 1px solid #bbdefb; }
        .badge-success { background-color: #d1e7dd; color: #0f5132; border: 1px solid #badbcc; }
        .badge-failed  { background-color: #f8d7da; color: #842029; border: 1px solid #f5c2c7; }
        
        .fa-spin-custom { animation: spin 1.5s linear infinite; }
        @keyframes spin { 100% { transform: rotate(360deg); } }

        .footer-info { padding: 15px; background-color: #f8f9fc; text-align: center; font-size: 12px; color: #888; border-top: 1px solid #eaecf4; }
        .live-dot { display: inline-block; width: 8px; height: 8px; background-color: var(--success); border-radius: 50%; margin-right: 5px; animation: pulse 2s infinite; }
        @keyframes pulse { 0% { box-shadow: 0 0 0 0 rgba(28, 200, 138, 0.4); } 70% { box-shadow: 0 0 0 6px rgba(28, 200, 138, 0); } 100% { box-shadow: 0 0 0 0 rgba(28, 200, 138, 0); } }
    </style>
</head>
<body>

    <div class="dashboard-card">
        <div id="completion-banner">
            <div class="completion-content">
                <div class="completion-text">
                    <h3><i class="fas fa-check-double"></i> Quá trình xử lý đã hoàn tất!</h3>
                    <p id="completion-summary">Đã xử lý xong tất cả ảnh. Bạn có thể bắt đầu tìm kiếm.</p>
                </div>
                <a href="search.jsp" class="btn-go-search">
                    Đến trang Tìm kiếm <i class="fas fa-arrow-right"></i>
                </a>
            </div>
        </div>

        <div class="header">
            <div class="header-title">
                <h2><i class="fas fa-server"></i> Hệ thống Xử lý Ảnh AI</h2>
                <p>Theo dõi tiến trình trích xuất Vector</p>
            </div>
            <a href="upload.jsp" class="btn-action">
                <i class="fas fa-cloud-upload-alt"></i> Tải ảnh khác
            </a>
        </div>

        <div class="table-container">
            <table>
                <thead>
                    <tr>
                        <th style="width: 5%">ID</th>
                        <th style="width: 10%">Ảnh</th>
                        <th style="width: 35%">Thông tin File</th>
                        <th style="width: 20%">Thời gian Upload</th>
                        <th style="width: 30%">Trạng thái</th>
                    </tr>
                </thead>
                <tbody id="table-body">
                    <% 
                    if (imageList.isEmpty()) { 
                    %>
                        <tr><td colspan="5" style="text-align:center;">Không tìm thấy ảnh vừa upload. Vui lòng thử lại.</td></tr>
                    <% 
                    } else {
                        for (image img : imageList) {
                            // Tách tên file để hiển thị đẹp hơn
                            String fullPath =img.getFilePath();
                            String fileName = "";
                            if(fullPath != null){
                                File f = new File(fullPath);
                                fileName = f.getName();
                            }
                            
                            // Xác định class CSS ban đầu
                            String badgeClass = "badge-pending";
                            String iconClass = "fa-clock";
                            String statusText = "Đang chờ (Pending)";
                            
                            if ("RUNNING".equals(img.getStatus())) {
                                badgeClass = "badge-running";
                                iconClass = "fa-circle-notch fa-spin-custom";
                                statusText = "Đang xử lý...";
                            } else if ("SUCCESS".equals(img.getStatus())) {
                                badgeClass = "badge-success";
                                iconClass = "fa-check-circle";
                                statusText = "Hoàn tất";
                            } else if ("FAILED".equals(img.getStatus())) {
                                badgeClass = "badge-failed";
                                iconClass = "fa-exclamation-triangle";
                                statusText = "Gặp lỗi";
                            }
                    %>
                    <tr id="row-<%= img.getId() %>" data-status="<%= img.getStatus() %>">
                        <td><strong>#<%= img.getId() %></strong></td>
                        <td>
                            <img src="uploads/<%= fileName %>" class="img-thumb" alt="Img" 
                                 onerror="this.src='https://via.placeholder.com/60?text=Wait...'">
                        </td>
                        <td>
                            <div class="file-meta">
                                <span class="filename"><%= fileName %></span>
                                <span class="filepath" title="<%= fullPath %>">
                                    <%= fullPath %>
                                </span>
                            </div>
                        </td>
                        <td><%= img.getUploadTime() %></td>
                        <td class="status-cell">
                            <span class="badge <%= badgeClass %>" id="badge-<%= img.getId() %>">
                                <i class="fas <%= iconClass %>" id="icon-<%= img.getId() %>"></i> 
                                <span id="text-<%= img.getId() %>"><%= statusText %></span>
                            </span>
                        </td>
                    </tr>
                    <% 
                        } 
                    } 
                    %>
                </tbody>
            </table>
        </div>

        <div class="footer-info">
            <span class="live-dot" id="live-indicator"></span> 
            <span id="status-text">Đang kết nối thời gian thực với Server Worker... (Cập nhật 2s/lần)</span>
        </div>
    </div>

    <script>
        // Lấy tất cả dòng có data-status
        const rows = document.querySelectorAll('tr[data-status]');
        
        function updateStatus() {
            let allDone = true;
            let hasError = false;
            let activeCount = 0;

            rows.forEach(row => {
                const status = row.getAttribute('data-status');
                const id = row.id.replace('row-', '');

                // Nếu chưa xong thì gọi API check
                if (status !== 'SUCCESS' && status !== 'FAILED') {
                    allDone = false;
                    activeCount++;
                    
                    fetch('checkStatus?id=' + id)
                        .then(response => response.json())
                        .then(data => {
                            if (data.status && data.status !== status) {
                                updateRowUI(id, data.status);
                            }
                        })
                        .catch(err => console.error('Lỗi check status:', err));
                }
                
                if (status === 'FAILED') hasError = true;
            });

            // Nếu không còn job nào đang chạy -> Hiện banner hoàn tất
            if (activeCount === 0 && rows.length > 0) {
                showCompletion(hasError);
            }
        }

        function updateRowUI(id, newStatus) {
            const row = document.getElementById('row-' + id);
            const badge = document.getElementById('badge-' + id);
            const icon = document.getElementById('icon-' + id);
            const text = document.getElementById('text-' + id);
            
            row.setAttribute('data-status', newStatus);

            // Reset class
            badge.className = 'badge'; 
            icon.className = 'fas';

            if (newStatus === 'RUNNING') {
                badge.classList.add('badge-running');
                icon.classList.add('fa-circle-notch', 'fa-spin-custom');
                text.innerText = 'Đang xử lý AI...';
            } else if (newStatus === 'SUCCESS') {
                badge.classList.add('badge-success');
                icon.classList.add('fa-check-circle');
                text.innerText = 'Hoàn tất';
                badge.style.animation = 'popIn 0.5s'; // Hiệu ứng nảy
            } else if (newStatus === 'FAILED') {
                badge.classList.add('badge-failed');
                icon.classList.add('fa-exclamation-triangle');
                text.innerText = 'Gặp lỗi';
            }
        }

        function showCompletion(hasError) {
            const banner = document.getElementById('completion-banner');
            const summary = document.getElementById('completion-summary');
            const indicator = document.getElementById('live-indicator');
            const footerText = document.getElementById('status-text');

            if (banner.style.display !== 'block') {
                banner.style.display = 'block';
                
                if (hasError) {
                    summary.innerHTML = 'Quá trình kết thúc nhưng có <strong>ảnh bị lỗi</strong>. Hãy kiểm tra lại.';
                    banner.style.background = 'linear-gradient(135deg, #f6c23e 0%, #dda20a 100%)';
                    banner.style.borderBottom = '4px solid #c69500';
                } else {
                    summary.innerHTML = 'Tất cả ảnh đã được AI xử lý xong. Bạn có thể bắt đầu tìm kiếm.';
                }

                // Tắt đèn live
                indicator.style.backgroundColor = '#ccc';
                indicator.style.animation = 'none';
                footerText.innerText = "Hệ thống đang nghỉ (Idle)";
                
                clearInterval(pollingInterval);
            }
        }

        // Thêm CSS animation động
        const styleSheet = document.createElement("style");
        styleSheet.innerText = `@keyframes popIn { 0% { transform: scale(0.8); opacity: 0; } 100% { transform: scale(1); opacity: 1; } }`;
        document.head.appendChild(styleSheet);

        // Chạy loop check mỗi 2 giây
        const pollingInterval = setInterval(updateStatus, 2000);
        updateStatus(); // Chạy ngay lần đầu
    </script>
</body>
</html>