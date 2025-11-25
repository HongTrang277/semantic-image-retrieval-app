package com.dut.project.controller.worker;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PythonApiClient {
    //private static final String BASE_URL = "http://160.30.129.168:8386";
    private static final String BASE_URL = "http://localhost:8386";
    
    // Class nội bộ để lưu trữ tạm kết quả từ JSON phục vụ việc sort
    private static class SearchResultItem implements Comparable<SearchResultItem> {
        int id;
        double score;

        public SearchResultItem(int id, double score) {
            this.id = id;
            this.score = score;
        }

        // Sắp xếp giảm dần theo Score
        @Override
        public int compareTo(SearchResultItem other) {
            return Double.compare(other.score, this.score);
        }
    }
    
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
        
        System.out.println("DEBUG: Raw JSON from AI Server: " + response.toString());


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
    
 // HÃY THAY THẾ TOÀN BỘ PHƯƠNG THỨC callSearch() BẰNG ĐOẠN CODE SAU
    public List<Integer> callSearch(int userId, String query, int topK, double minScore) throws IOException {
        URL url = new URL(BASE_URL + "/search");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Accept", "application/json"); 
        conn.setDoOutput(true);

        String encodedQuery = java.net.URLEncoder.encode(query, "UTF-8");
        String urlParameters = "user_id=" + userId + "&query=" + encodedQuery + "&top_k=" + topK;

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = urlParameters.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
            os.flush();
        }

        int responseCode = conn.getResponseCode();
        StringBuilder response = new StringBuilder();
        
        java.io.InputStream stream;
        try {
            stream = conn.getInputStream();
        } catch (IOException e) {
            stream = conn.getErrorStream();
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }
        
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Server trả về lỗi: " + responseCode + ". Chi tiết: " + response.toString());
        }

        // Xử lý JSON trả về
        return processSearchResponse(response.toString(), minScore);
    }


    private List<Integer> processSearchResponse(String jsonResponse, double minScore) {
        List<SearchResultItem> items = new ArrayList<>();
        List<Integer> finalIds = new ArrayList<>();

        try {
            Gson gson = new Gson();
            JsonObject root = gson.fromJson(jsonResponse, JsonObject.class);
            
            // Đi vào path: data -> results
            if (root.has("data") && root.get("data").isJsonObject()) {
                JsonObject dataObj = root.getAsJsonObject("data");
                if (dataObj.has("results") && dataObj.get("results").isJsonArray()) {
                    JsonArray resultsArray = dataObj.getAsJsonArray("results");

                    for (JsonElement elem : resultsArray) {
                        JsonObject itemObj = elem.getAsJsonObject();
                        
                        // Lấy ID và Score
                        // Lưu ý: JSON trả về image_id là String "1299" -> cần parse int
                        int id = Integer.parseInt(itemObj.get("image_id").getAsString());
                        double score = itemObj.get("similarity_score").getAsDouble();

                        // 1. LỌC: Chỉ lấy score > 0
                        if (score > minScore) {
                            items.add(new SearchResultItem(id, score));
                        }
                    }
                }
            }

            // 2. SẮP XẾP: Giảm dần theo score (dùng Comparable đã định nghĩa ở trên)
            Collections.sort(items);

            // 3. TRÍCH XUẤT ID: Đưa vào danh sách kết quả cuối cùng
            for (SearchResultItem item : items) {
                finalIds.add(item.id);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi parse JSON search: " + e.getMessage());
        }

        return finalIds;
    }
}