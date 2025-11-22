<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head><title>Upload Page</title></head>
<body>
    <h2>Upload Ảnh Mới</h2>
    <form action="upload" method="POST" enctype="multipart/form-data">
        <label for="file">Chọn ảnh:</label><br>
        <input type="file" id="file" name="imageFile" required multiple><br><br>
        <button type="submit">Upload & Khởi tạo Job</button>
    </form>
</body>
</html>