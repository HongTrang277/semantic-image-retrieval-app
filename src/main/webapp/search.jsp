<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Tìm kiếm Ảnh theo Ngữ nghĩa</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f4f4f9;
            margin: 0;
            padding: 20px;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
        }
        /* Search Box Styling */
        .search-box {
            text-align: center;
            margin-bottom: 40px;
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
        }
        .search-box h2 {
            margin-top: 0;
            color: #333;
        }
        .search-form {
            display: flex;
            justify-content: center;
            gap: 10px;
        }
        input[type="text"] {
            width: 60%;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-size: 16px;
        }
        button {
            padding: 12px 25px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 16px;
            transition: background 0.3s;
        }
        button:hover {
            background-color: #0056b3;
        }

        /* Grid Layout */
        .image-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
            gap: 20px;
        }
        .image-card {
            background: white;
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
            transition: transform 0.2s;
        }
        .image-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 5px 15px rgba(0,0,0,0.2);
        }
        .image-card img {
            width: 100%;
            height: 200px;
            object-fit: cover; /* Cắt ảnh cho vừa khung mà không méo */
            display: block;
        }
        .image-info {
            padding: 10px;
            font-size: 14px;
            color: #666;
            text-align: center;
        }
        .no-results {
            text-align: center;
            color: #777;
            margin-top: 50px;
        }
        .error-msg {
            color: red;
            text-align: center;
        }
    </style>
</head>
<body>

<div class="container">
    <!-- Khu vực Tìm kiếm -->
    <div class="search-box">
        <h2>Tìm kiếm hình ảnh thông minh</h2>
        <p>Nhập mô tả hình ảnh bạn muốn tìm (Ví dụ: "con mèo đang ngủ", "bãi biển hoàng hôn")</p>
        
        <form action="search" method="get" class="search-form">
            <input type="text" name="query" placeholder="Nhập từ khóa tìm kiếm..." value="${currentQuery}" required>
            <button type="submit">Tìm kiếm</button>
        </form>
        
        <c:if test="${not empty error}">
            <p class="error-msg">${error}</p>
        </c:if>
    </div>

    <!-- Khu vực hiển thị kết quả -->
    <c:choose>
        <c:when test="${not empty searchResults}">
            <div class="image-grid">
                <c:forEach var="img" items="${searchResults}">
                    <div class="image-card">
                        <!-- 
                             Lưu ý: img.filePath có thể là đường dẫn tuyệt đối trên server hoặc URL.
                             Bạn cần xử lý mapping đường dẫn này trong Tomcat config hoặc copy ảnh vào thư mục WebContent 
                        -->
                        <img src="${img.filePath}" alt="Image ID: ${img.id}" onerror="this.src='https://via.placeholder.com/250?text=Image+Not+Found'">
                        <div class="image-info">
                            Status: ${img.status}<br>
                            ID: ${img.id}
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:when>
        <c:when test="${not empty currentQuery}">
            <div class="no-results">
                <h3>Không tìm thấy hình ảnh nào phù hợp với "${currentQuery}"</h3>
            </div>
        </c:when>
    </c:choose>
</div>

</body>
</html>