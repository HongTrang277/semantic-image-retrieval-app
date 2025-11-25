<%-- src/main/webapp/uploadHistory.jsp --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.dut.project.model.user" %>
<%@ page import="com.dut.project.model.ImageBatch" %>
<%@ page import="java.util.List" %>
<%
    // Kiểm tra đăng nhập (Bảo vệ trang)
    user currentUser = (user) session.getAttribute("account");
    if (currentUser == null) {
        response.sendRedirect("loginPage.jsp");
        return;
    }
    // Lấy danh sách batches đã được Controller xử lý
    List<ImageBatch> imageBatches = (List<ImageBatch>) request.getAttribute("imageBatches");
    if (imageBatches == null) {
        imageBatches = new java.util.ArrayList<>();
    }
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Lịch sử Upload Ảnh (Theo Phiên)</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
    
    <script>
        // Cấu hình Tailwind CSS
        tailwind.config = {
            theme: {
                extend: {
                    fontFamily: { sans: ['"Plus Jakarta Sans"', 'sans-serif'] },
                    colors: { brand: { 500: '#0ea5e9', 600: '#0284c7' } }
                }
            }
        }
    </script>
    <style>
        /* Tái sử dụng các styles cho Badge trạng thái (từ mockup) */
        .badge {
            display: inline-flex;
            align-items: center; 
            padding: 6px 12px;
            border-radius: 50px; 
            font-size: 11px; 
            font-weight: 800;
            text-transform: uppercase; 
            letter-spacing: 0.5px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        }
        .badge i { margin-right: 5px; }
        .badge-pending { background-color: #fff3cd; color: #856404; border: 1px solid #ffeeba; }
        .badge-running { 
            background-color: #e3f2fd; 
            color: #0d47a1; 
            border: 1px solid #bbdefb; 
            animation: pulse-ring 1s infinite;
        }
        .badge-success { background-color: #d1e7dd; color: #0f5132; border: 1px solid #badbcc; }
        .badge-failed  { background-color: #f8d7da; color: #842029; border: 1px solid #f5c2c7; }
        .badge-partial { background-color: #fcebeb; color: #cc3333; border: 1px solid #f5c2c7; } /* Trạng thái mới: Có lỗi */

        .img-thumb { width: 60px; height: 60px; object-fit: cover; border-radius: 8px; border: 1px solid #ddd; }
        .filename { font-weight: 700; color: #333; margin-bottom: 3px; }
        .filepath { 
            font-size: 11px; 
            color: #94a3b8; 
            font-family: monospace;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }

        /* Hiệu ứng Pulse cho trạng thái Running */
        @keyframes pulse-ring {
            0% { box-shadow: 0 0 0 0 rgba(13, 71, 161, 0.4); }
            100% { box-shadow: 0 0 0 6px rgba(13, 71, 161, 0); }
        }

        /* Custom scrollbar cho bảng */
        .custom-scrollbar-table::-webkit-scrollbar { width: 6px; height: 6px; }
        .custom-scrollbar-table::-webkit-scrollbar-track { background: #f1f1f1; }
        .custom-scrollbar-table::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 3px; }
        
        /* Style cho hàng tóm tắt Batch */
        .batch-summary-row {
            cursor: pointer;
            transition: background-color 0.2s;
        }
        .batch-summary-row:hover {
            background-color: #f3f4f6; /* light gray hover */
        }
        
        /* Style cho bảng chi tiết ảnh trong Batch */
        .detail-table {
            width: 100%;
            border-collapse: collapse;
        }
        .detail-table th {
            background-color: #fafafa;
            color: #64748b;
            font-size: 0.75rem;
            padding: 8px 12px;
            text-align: left;
        }
        .detail-table td {
            padding: 8px 12px;
            border-bottom: 1px solid #f1f1f1;
            font-size: 0.875rem;
            vertical-align: top;
        }
        .detail-table tr:last-child td {
            border-bottom: none;
        }
        
        /* Hiệu ứng xoay mũi tên */
        .rotate-180 { transform: rotate(180deg); }
        .transition-transform { transition: transform 0.2s ease-in-out; }
    </style>
</head>
<body class="bg-slate-50 text-slate-800 min-h-screen flex flex-col font-sans">
    <%-- Đường dẫn context path cần thiết cho src ảnh --%>
    <% String contextPath = request.getContextPath(); %>

        <nav class="bg-white border-b border-gray-200 px-6 py-4 flex justify-between items-center sticky top-0 z-50 shadow-sm">
        <div class="flex items-center gap-2 cursor-pointer" onclick="window.location.href='index.jsp'">
            <div class="w-8 h-8 bg-gradient-to-br from-brand-500 to-blue-600 rounded-lg text-white flex items-center justify-center font-bold">S</div>
            <span class="font-bold text-lg tracking-tight">SmartSearch</span>
        </div>
        <div class="flex items-center gap-4">
            <a href="search" class="text-sm text-slate-500 hover:text-brand-600 font-medium"><i class="fas fa-search mr-1"></i> Tìm kiếm</a>
            <a href="upload.jsp" class="text-sm text-slate-500 hover:text-brand-600 font-medium"><i class="fas fa-cloud-upload-alt mr-1"></i> Upload</a>
            <a href="uploadHistory" class="text-sm font-bold text-brand-600 hidden sm:block"><i class="fas fa-history mr-1"></i> Lịch sử</a>
            <span class="text-sm text-slate-500 hidden sm:block">Xin chào, <b><%= currentUser.getFullName() %></b></span>
            <a href="logout" class="text-sm text-red-500 hover:text-red-700 font-medium">Đăng xuất</a>
        </div>
    </nav>
    
        <main class="flex-grow p-4 lg:p-8">
        <div class="container max-w-7xl mx-auto">
            
            <div class="mb-6 flex justify-between items-center">
                <h1 class="text-3xl font-extrabold text-slate-900 flex items-center gap-3">
                    <i class="fas fa-history text-brand-500"></i> Lịch sử Upload Ảnh
                </h1>
                <button onclick="window.location.href='upload.jsp'" class="px-4 py-2 bg-brand-500 hover:bg-brand-600 text-white rounded-full font-semibold text-sm transition shadow-md">
                    <i class="fas fa-plus-circle mr-1"></i> Tải ảnh mới
                </button>
            </div>
            
                        <div class="bg-white rounded-xl shadow-2xl overflow-hidden border border-slate-200">
                
                <div class="p-4 bg-slate-50 border-b border-slate-200 flex justify-between items-center">
                    <% 
                        int totalImages = 0;
                        for(ImageBatch batch : imageBatches) { totalImages += batch.getTotalImages(); }
                    %>
                    <h3 class="font-bold text-slate-700">Tổng cộng <span id="total-batch-count"><%= imageBatches.size() %></span> phiên upload (<span id="total-image-count"><%= totalImages %></span> ảnh)</h3>
                    <div class="text-sm text-slate-500">
                        <i class="fas fa-filter mr-1"></i> Lọc theo: 
                        <select id="statusFilter" class="border border-slate-300 rounded-md p-1 ml-2 text-xs" onchange="filterBatches(this.value)">
                            <option value="ALL">Tất cả trạng thái</option>
                            <option value="SUCCESS">Hoàn tất (100% OK)</option>
                            <option value="PARTIAL">Có lỗi / Đang chờ</option>
                            <option value="RUNNING">Đang xử lý</option>
                            <option value="PENDING">Đang chờ</option>
                            <option value="FAILED">Lỗi (100% Error)</option>
                        </select>
                    </div>
                </div>
                
                <div class="table-container w-full overflow-x-auto custom-scrollbar-table">
                    <table class="min-w-full divide-y divide-gray-200">
                        <thead>
                            <tr class="text-xs font-semibold uppercase tracking-wider text-left text-gray-500 bg-gray-50">
                                <th class="px-6 py-3 w-[5%]"></th>
                                <th class="px-6 py-3 w-[15%]">ID Phiên</th>
                                <th class="px-6 py-3 w-[25%]">Thời gian Upload</th>
                                <th class="px-6 py-3 w-[20%]">Số lượng Ảnh</th>
                                <th class="px-6 py-3 w-[35%]">Trạng thái Tổng quát</th>
                            </tr>
                        </thead>
                        <tbody class="bg-white divide-y divide-gray-200" id="batch-table-body">
                            <c:set var="imageBatches" value="<%= imageBatches %>" scope="request"/>
                            <c:choose>
                                <c:when test="${not empty imageBatches}">
                                    <c:forEach var="batch" items="${imageBatches}">
                                        <%-- Hàng tóm tắt (Master Row) --%>
                                        <tr id="batch-summary-${batch.id}" class="batch-summary-row border-b border-gray-200"
    onclick="toggleBatchDetails(this)" data-status="${batch.overallStatus}">
                                            <td class="px-6 py-4 whitespace-nowrap"><i id="icon-${batch.id}" class="fas fa-chevron-down transition-transform"></i></td>
                                            <td class="px-6 py-4 whitespace-nowrap font-bold text-slate-800">#${batch.id}</td>
                                            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-700">${batch.uploadTime}</td>
                                            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-700">${batch.totalImages} ảnh</td>
                                            <td class="px-6 py-4 whitespace-nowrap">
                                                <span class="badge 
                                                    <c:choose>
                                                        <c:when test="${batch.overallStatus == 'SUCCESS'}">badge-success"><i class="fas fa-check-double"></i> Hoàn tất</c:when>
                                                        <c:when test="${batch.overallStatus == 'RUNNING'}">badge-running"><i class="fas fa-circle-notch fa-spin"></i> Đang xử lý</c:when>
                                                        <c:when test="${batch.overallStatus == 'PENDING'}">badge-pending"><i class="fas fa-clock"></i> Đang chờ</c:when>
                                                        <c:when test="${batch.overallStatus == 'FAILED'}">badge-failed"><i class="fas fa-times-circle"></i> Lỗi (100% Error)</c:when>
                                                        <c:when test="${batch.overallStatus == 'PARTIAL'}">badge-partial"><i class="fas fa-exclamation-triangle"></i> Có lỗi / Đang chờ</c:when>
                                                        <c:otherwise>badge-pending"><i class="fas fa-clock"></i> Đang chờ</c:otherwise>
                                                    </c:choose>
                                                </span>
                                            </td>
                                        </tr>
                                        
                                        <%-- Hàng chi tiết ẩn (Detail Row) --%>
                                        <tr id="batch-detail-${batch.id}" class="hidden bg-gray-50 detail-row">
                                            <td colspan="5" class="p-4">
                                                <h4 class="font-bold text-sm text-slate-700 mb-3 ml-2">Chi tiết ${batch.totalImages} ảnh trong Phiên Upload #${batch.id}:</h4>
                                                
                                                <table class="detail-table w-full rounded-lg overflow-hidden border border-slate-200">
                                                    <thead>
                                                        <tr>
                                                            <th class="w-1/12">ID</th>
                                                            <th class="w-1/12">Preview</th>
                                                            <th class="w-5/12">File</th>
                                                            <th class="w-2/12">Trạng thái</th>
                                                            <th class="w-3/12">Thời gian</th>
                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        <c:forEach var="img" items="${batch.images}">
                                                            <c:set var="fullPath" value="${img.filePath}" />
                                                            <c:set var="fileName" value="${fullPath.substring(fullPath.lastIndexOf('/') + 1)}" />
                                                            <c:set var="displayPath" value="${fullPath}" />
                                                            <%-- Chuẩn hóa displayPath cho src ảnh --%>
                                                            <c:set var="cleanPath" value="${fullPath.replace('\\\\', '/')}" />
                                                            <c:set var="index" value="${cleanPath.indexOf('uploads/')}" />
                                                            <c:if test="${index != -1}">
                                                                <c:set var="displayPath" value="${cleanPath.substring(index)}" />
                                                            </c:if>
                                                            
                                                            <tr>
                                                                <td>#${img.id}</td>
                                                                <td>
                                                                    <img src="<%= contextPath %>/${displayPath}" 
                                                                         class="w-10 h-10 object-cover rounded border border-slate-300"
                                                                         onerror="this.src='https://placehold.co/40x40/f0f9ff/0ea5e9?text=IMG';"
                                                                         alt="Image Preview"
                                                                    >
                                                                </td>
                                                                <td>
                                                                    <div class="file-meta">
                                                                        <span class="filename">${fileName}</span>
                                                                        <%-- Loại bỏ hoặc ẩn đường dẫn đầy đủ để giao diện gọn gàng hơn --%>
                                                                        </div>
                                                                </td>
                                                                <td>
                                                                    <span class="badge 
                                                                        <c:choose>
                                                                            <c:when test="${img.status == 'SUCCESS'}">badge-success"><i class="fas fa-check-circle"></i> Hoàn tất</c:when>
                                                                            <c:when test="${img.status == 'RUNNING'}">badge-running"><i class="fas fa-circle-notch fa-spin"></i> Xử lý</c:when>
                                                                            <c:when test="${img.status == 'FAILED'}">badge-failed"><i class="fas fa-exclamation-triangle"></i> Lỗi</c:when>
                                                                            <c:otherwise>badge-pending"><i class="fas fa-clock"></i> Chờ</c:otherwise>
                                                                        </c:choose>
                                                                    </span>
                                                                </td>
                                                                <td>${img.uploadTime}</td>
                                                            </tr>
                                                        </c:forEach>
                                                    </tbody>
                                                </table>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <%-- Đây là trường hợp không có dữ liệu từ Controller --%>
                                    <tr><td colspan="5" class="text-center py-12 text-gray-500">Bạn chưa có phiên upload nào.</td></tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                    
                    <div id="no-data-message" class="text-center py-12 text-gray-500 hidden">
                        <i class="fas fa-box-open text-4xl mb-3"></i>
                        <p class="text-lg font-medium">Không tìm thấy phiên upload nào với bộ lọc này.</p>
                    </div>

                </div>
            </div>
            
        </div>
    </main>

        <footer class="bg-white border-t border-slate-200 py-4 mt-auto">
        <div class="max-w-7xl mx-auto text-center text-xs text-slate-500">
            &copy; 2024 SmartSearch Project.
        </div>
    </footer>

    <script>
        // Lấy dữ liệu Batch đã được đổ từ Server ra biến JS
        const allBatchesData = [
            <c:forEach var="batch" items="${imageBatches}" varStatus="status">
                {
                    id: "${batch.id}",
                    uploadTime: "${batch.uploadTime}",
                    overallStatus: "${batch.overallStatus}",
                    totalImages: ${batch.totalImages},
                    images: [
                        <c:forEach var="img" items="${batch.images}" varStatus="imgStatus">
                            {
                                id: ${img.id},
                                filePath: "${img.filePath}",
                                status: "${img.status}",
                                uploadTime: "${img.uploadTime}"
                            }<c:if test="${!imgStatus.last}">,</c:if>
                        </c:forEach>
                    ]
                }<c:if test="${!status.last}">,</c:if>
            </c:forEach>
        ];
        
        const noDataMessage = document.getElementById('no-data-message');
        
        // 1. Hàm Bật/Tắt chi tiết phiên upload (Tái sử dụng logic mockup)
        function toggleBatchDetails(summaryRowElement) { // Chỉ nhận phần tử HTML
    
    // Tìm icon bên trong hàng hiện tại
    const icon = summaryRowElement.querySelector('i'); 
    
    // Tìm hàng chi tiết kế tiếp (detail row là sibling ngay sau summary row)
    const detailRow = summaryRowElement.nextElementSibling;
    
    // KIỂM TRA PHẦN TỬ CÓ TỒN TẠI KHÔNG
    if (!detailRow || !icon || !detailRow.classList.contains('detail-row')) {
        // Có thể in ra lỗi để debug nhưng sẽ không làm sập chức năng
        console.warn("Không thể mở chi tiết. Có thể bị lỗi DOM.");
        return; 
    }

    // 2. Logic mở/đóng (Toggle)
    if (detailRow.classList.contains('hidden')) {
        // Mở hàng hiện tại
        detailRow.classList.remove('hidden');
        icon.classList.add('rotate-180');
        
        // CHÚ Ý: Bỏ qua việc đóng các hàng khác để tránh lỗi null khi gọi document.getElementById cho các hàng đã bị lọc.
        // Nếu bạn muốn đóng các hàng khác, bạn phải làm điều đó trong hàm filterBatches().
        
    } else {
        // Đóng hàng hiện tại
        detailRow.classList.add('hidden');
        icon.classList.remove('rotate-180');
    }
}
        
        // 2. Hàm Lọc phiên upload (Sử dụng dữ liệu JS đã được đổ từ Server)
        function filterBatches(statusFilter) {
            const allRows = document.querySelectorAll('#batch-table-body > tr.batch-summary-row');
            let visibleCount = 0;
            
            allRows.forEach(row => {
                const batchStatus = row.getAttribute('data-status');
                const detailRow = document.getElementById(row.id.replace('summary', 'detail'));
                
                // Ẩn/hiện row chi tiết trước
                if(!detailRow.classList.contains('hidden')) {
                    detailRow.classList.add('hidden');
                    row.querySelector('i').classList.remove('rotate-180');
                }
                
                let isMatch = false;
                if (statusFilter === 'ALL') {
                    isMatch = true;
                } else if (statusFilter === 'PARTIAL') {
                    isMatch = batchStatus === 'PARTIAL';
                } else if (statusFilter === 'FAILED') {
                    isMatch = batchStatus === 'FAILED';
                } else if (statusFilter === 'SUCCESS') {
                    isMatch = batchStatus === 'SUCCESS';
                } else if (statusFilter === 'RUNNING' || statusFilter === 'PENDING') {
                    // Nếu người dùng lọc chi tiết trạng thái RUNNING/PENDING:
                    // Ta kiểm tra chi tiết Batch đó trong dữ liệu JS gốc.
                    const batchId = row.id.replace('batch-summary-', '');
                    const batchData = allBatchesData.find(b => b.id === batchId);
                    if(batchData) {
                        // Match nếu bất kỳ ảnh nào trong batch đó có status đang được lọc
                        if(batchData.images.some(img => img.status === statusFilter)) {
                            isMatch = true;
                        }
                    }
                } 

                if (isMatch) {
                    row.style.display = 'table-row';
                    visibleCount++;
                } else {
                    row.style.display = 'none';
                }
            });

            if (visibleCount === 0) {
                noDataMessage.classList.remove('hidden');
                document.getElementById('total-batch-count').innerText = 0;
            } else {
                noDataMessage.classList.add('hidden');
                // Không cập nhật total count khi lọc, chỉ khi load trang
            }
        }

        // Chạy lần đầu
        document.addEventListener('DOMContentLoaded', () => {
            window.toggleBatchDetails = toggleBatchDetails;
            window.filterBatches = filterBatches;
            
            // Xử lý thông báo không có dữ liệu ban đầu
            if (allBatchesData.length === 0) {
                noDataMessage.innerHTML = '<i class="fas fa-box-open text-4xl mb-3"></i><p class="text-lg font-medium">Bạn chưa có phiên upload nào.</p>';
                noDataMessage.classList.remove('hidden');
            }
        });
    </script>
</body>
</html>