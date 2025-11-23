<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head><title>Upload Page</title></head>
<body>
    <h2>Upload Ảnh Mới</h2>
    <form action="upload" method="post" enctype="multipart/form-data">
	    <input 
	        type="file" 
	        name="files" 
	        webkitdirectory 
	        directory 
	        multiple 
	    />
	    <input type="submit" value="Tải Lên" />
	</form>

</body>
</html>