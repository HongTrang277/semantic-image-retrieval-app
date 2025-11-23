<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Job Status Monitoring</title>
    <style>
        .status-box {
            padding: 15px;
            border-radius: 5px;
            margin-top: 20px;
        }
        .pending { background-color: #fff3cd; color: #856404; border: 1px solid #ffeeba; }
        .success { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
        .failed { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
    </style>
</head>
<body>
    
    <h2>✅ Job Khởi tạo Thành công!</h2>
    <% 
        // Lấy Image ID từ tham số URL
        String imageIdStr = request.getParameter("id");
        int imageId = -1;
        try {
            imageId = Integer.parseInt(imageIdStr);
        } catch (NumberFormatException e) {
            // Xử lý nếu ID không hợp lệ
        }
    %>
    
    <p>ID Ảnh đang xử lý: <strong><span id="imageIdDisplay"><%= imageId %></span></strong></p>
    
    <div id="statusContainer" class="status-box pending">
        <span id="currentStatusText">Đang chờ xử lý (PENDING)...</span>
        <div id="loadingIndicator">⏱️ Đang chờ Worker Thread lấy Job...</div>
    </div>
    
    <div id="resultContainer" style="margin-top: 20px; display: none;">
        <h3>Kết quả Vectorization:</h3>
        <p>Ảnh đã được tối ưu hóa và Vector hóa thành công!</p>
        <img id="processedImage" src="images/placeholder.png" style="max-width: 300px; border: 1px solid #ccc;">
    </div>

<script>
    const IMAGE_ID = <%= imageId %>;
    let checkInterval;

    if (IMAGE_ID > 0) {
        // Bắt đầu vòng lặp kiểm tra AJAX sau mỗi 2 giây
        checkInterval = setInterval(checkJobStatus, 2000);
        console.log("Bắt đầu kiểm tra trạng thái Job ID:", IMAGE_ID);
    } else {
        document.getElementById('currentStatusText').innerText = "Lỗi: ID ảnh không hợp lệ.";
        document.getElementById('statusContainer').classList.remove('pending');
        document.getElementById('statusContainer').classList.add('failed');
    }

    function checkJobStatus() {
        // Gọi Servlet CheckStatusController
        fetch('checkStatus?id=' + IMAGE_ID)
            .then(response => response.json())
            .then(data => {
                const status = data.status;
                const statusElement = document.getElementById('statusContainer');
                const textElement = document.getElementById('currentStatusText');
                
                // Cập nhật trạng thái
                textElement.innerText = status;
                statusElement.classList.remove('pending');
                statusElement.classList.remove('failed');

                if (status === 'SUCCESS' || status === 'DONE') {
                    // 1. Dừng vòng lặp kiểm tra
                    clearInterval(checkInterval);
                    
                    // 2. Cập nhật giao diện
                    statusElement.classList.add('success');
                    textElement.innerText = '✅ HOÀN THÀNH. Vector đã được lưu.';
                    document.getElementById('loadingIndicator').style.display = 'none';
                    document.getElementById('resultContainer').style.display = 'block';
                    
                    // LƯU Ý: Nếu ảnh đã được lưu là WebP, cần cập nhật đường dẫn ảnh tại đây
                    // Giả định: Ứng dụng có thể tìm ảnh optimized/XXX.webp
                    // document.getElementById('processedImage').src = 'uploads/optimized/' + IMAGE_ID + '.webp'; 

                } else if (status === 'FAILED') {
                    // Dừng vòng lặp nếu thất bại
                    clearInterval(checkInterval);
                    statusElement.classList.add('failed');
                    textElement.innerText = '❌ THẤT BẠI. Lỗi trong quá trình xử lý AI.';

                } else if (status === 'RUNNING') {
                    // Hiển thị trạng thái đang xử lý
                    document.getElementById('loadingIndicator').innerText = '⏳ Worker Thread đang gửi ảnh lên Module 1...';
                }
            })
            .catch(error => {
                console.error('Lỗi khi gọi checkStatus:', error);
                // Dừng vòng lặp nếu lỗi mạng
                clearInterval(checkInterval);
            });
    }
</script>
</body>
</html>