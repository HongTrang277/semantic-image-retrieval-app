package com.dut.project.controller.worker;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PythonApiClient {
    private static final String BASE_URL = "http://160.30.129.168:8386";
    
    public void callExtract(String userId, String imageId, File imageFile) throws Exception {
        String boundary = "----" + System.currentTimeMillis();
        
        URL url = new URL(BASE_URL + "/extract");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        conn.setDoInput(true);

        try (OutputStream outputStream = conn.getOutputStream();
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true)) {
            
            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"user_id\"\r\n\r\n");
            writer.append(userId).append("\r\n");
            writer.flush();

            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"image_id\"\r\n\r\n");
            writer.append(imageId).append("\r\n");
            writer.flush();

            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(imageFile.getName()).append("\"\r\n");
            writer.append("Content-Type: ").append(Files.probeContentType(imageFile.toPath())).append("\r\n");
            writer.append("Content-Transfer-Encoding: binary\r\n\r\n");
            writer.flush();

            Files.copy(imageFile.toPath(), outputStream);
            outputStream.flush();

            writer.append("\r\n");
            writer.append("--").append(boundary).append("--\r\n");
            writer.flush();
        }
        
        int responseCode = conn.getResponseCode(); //Lấy mã phản hồi
        
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                    responseCode == 200 ? conn.getInputStream() : conn.getErrorStream(), 
                    "UTF-8"))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine);
            }
        }


        if (responseCode != 200) {
            throw new Exception("API /extract thất bại! Status: " + responseCode + ", Response: " + response.toString());
        }
        
        System.out.println("API /extract thành công!");
    }
    
    //Yêu cầu gửi dữ liệu định dạng multipart/form-data
    // Định dạng bắt buộc phải có boundary làm ranh giới 
    //Dữ liệu input khi gửi đi có dạng như sau:
//    --boundary_string_12345
//    Content-Disposition: form-data; name="user_id"
//
//    205
//    --boundary_string_12345
//    Content-Disposition: form-data; name="image_id"
//
//    9012
//    --boundary_string_12345
//    Content-Disposition: form-data; name="file"; filename="landscape.png"
//    Content-Type: image/png
//    Content-Transfer-Encoding: binary
//
//    [Dữ liệu nhị phân (bytes) của tệp tin landscape.png được đặt chính xác vào đây]
//    [Ví dụ: \x89PNG\r\n\x1A\n\x00\x00\x00IHDR... (Hàng ngàn byte dữ liệu hình ảnh)]
//    --boundary_string_12345--
    
    public List<Integer> callSearch(int userId, String query, int topK) throws IOException {
    	URL url = new URL(BASE_URL + "/search");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Accept", "application/json"); // Thêm header Accept
        conn.setDoOutput(true);

        // Tạo body request
        // Lưu ý: Cần encode query để tránh lỗi với ký tự đặc biệt hoặc tiếng Việt
        String encodedQuery = java.net.URLEncoder.encode(query, "UTF-8");
        String urlParameters = "user_id=" + userId + "&query=" + encodedQuery + "&top_k=" + topK;

        // Gửi request
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = urlParameters.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // Kiểm tra mã phản hồi
        int responseCode = conn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Server trả về lỗi: " + responseCode);
        }

        // Đọc response
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        // Parse kết quả
        return extractIdsFromJson(response.toString());
    }

    // Hàm tách số ID từ JSON trả về
    // Cấu trúc JSON: { "data": { "results": [ { "image_id": "1", ... } ] } }
    private List<Integer> extractIdsFromJson(String jsonResponse) {
        List<Integer> ids = new ArrayList<>();
        
        // Regex tìm chuỗi: "image_id": "1" hoặc "image_id": 1
        // Giải thích Regex:
        // \"image_id\"  : Tìm chính xác key "image_id"
        // \s*:\s* : Dấu hai chấm và khoảng trắng tùy ý
        // \"?           : Dấu ngoặc kép mở (có thể có hoặc không)
        // (\d+)         : Nhóm cần lấy (các chữ số)
        // \"?           : Dấu ngoặc kép đóng (có thể có hoặc không)
        Pattern p = Pattern.compile("\"image_id\"\\s*:\\s*\"?(\\d+)\"?");
        Matcher m = p.matcher(jsonResponse);
        
        while(m.find()) {
            try {
                // m.group(1) là phần số nằm trong dấu ngoặc đơn (\d+)
                ids.add(Integer.parseInt(m.group(1)));
            } catch (NumberFormatException e) {
                // Bỏ qua nếu không phải số hợp lệ
            }
        }
        return ids;
    }
}