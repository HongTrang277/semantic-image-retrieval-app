package com.dut.project.controller.worker;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class PythonApiClient {
	private static final String BASE_URL = "http://localhost:5000/api/v1";
	
	private final HttpClient httpClient;
	
	public PythonApiClient() {
		this.httpClient = HttpClient.newBuilder()
				.connectTimeout(java.time.Duration.ofSeconds(10)).build();
	}
	
	public void callExtract(int userId, int imageId, File imageFile) throws Exception{
		String boundary = UUID.randomUUID().toString();
		
		HttpRequest.BodyPublisher bodyPublisher = buildMultipartBody(
	            userId, imageId, imageFile, boundary
	        );
		
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(BASE_URL + "/extract"))
				.header("Content-Type", "multipart/form-data; boundary=" + boundary)
				.timeout(Duration.ofSeconds(120))
				.POST(bodyPublisher)
				.build();
		System.out.println("Gửi request POST /extract cho ảnh ID: " + imageId);
		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		
		if(response.statusCode() != 200) {
			throw new Exception("API /extract thất bại! Status: " + response.statusCode() + ", Response: " + response.body());
		}
		
	}
	
	private HttpRequest.BodyPublisher buildMultipartBody(int userId, int imageId, File file, String boundary) throws IOException{
		List<byte[]> byteArrays = new ArrayList<>();
		String header = "--" + boundary + "\r\n";
        String footer = "\r\n--" + boundary + "--\r\n";
        
     // Thêm trường text user_id
        String userIdPart = header + 
            "Content-Disposition: form-data; name=\"user_id\"\r\n\r\n" + 
            userId + "\r\n";
        byteArrays.add(userIdPart.getBytes());
        
        // Thêm trường text image_id
        String imageIdPart = header + 
            "Content-Disposition: form-data; name=\"image_id\"\r\n\r\n" + 
            imageId + "\r\n";
        byteArrays.add(imageIdPart.getBytes());

        // Thêm trường file
        String filePartHeader = header +
            "Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"\r\n" +
            "Content-Type: application/octet-stream\r\n\r\n";
        byteArrays.add(filePartHeader.getBytes());
        //Đọc nội dung file
        byteArrays.add(Files.readAllBytes(file.toPath())); // Nội dung file
        byteArrays.add("\r\n".getBytes());
        
        byteArrays.add(footer.getBytes());

        // Trả về BodyPublisher
        return HttpRequest.BodyPublishers.ofByteArrays(byteArrays);
	}
}
