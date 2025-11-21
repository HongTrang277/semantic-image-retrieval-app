Semantic Image Retrieval System (Hệ thống Tìm kiếm Ảnh theo Ngữ nghĩa)

Dự án xây dựng hệ thống lưu trữ và tìm kiếm hình ảnh thông minh dựa trên nội dung (Semantic Search) sử dụng mô hình AI (SIGLip2) kết hợp với ứng dụng Web Java.

🚀 Tính năng chính

Upload ảnh: Người dùng tải ảnh lên hệ thống, xử lý bất đồng bộ.

AI Extraction: Tự động trích xuất đặc trưng (vector) từ ảnh bằng Deep Learning.

Semantic Search: Tìm kiếm ảnh bằng ngôn ngữ tự nhiên (Ví dụ: "con mèo đang ngủ").

Quản lý: Đăng ký, Đăng nhập, Quản lý lịch sử upload.

🏗 Kiến trúc hệ thống

Dự án được chia làm 4 Module chính:

Module 1 (AI Service - Python): API trích xuất vector và so khớp ảnh.

Module 2 (Core & DB - Java): Quản lý Database, Xác thực người dùng (Auth), DAO layer.

Module 3 (Search & Integration - Java): Giao diện tìm kiếm và hiển thị kết quả.

Module 4 (Async Worker - Java): Xử lý Upload và luồng chạy ngầm gọi AI.

🛠 Công nghệ sử dụng

Backend: Java Servlet/JSP (JDK 11+), Maven.

Database: MySQL 8.0.

AI Engine: Python, PyTorch, CLIP Model.

Server: Apache Tomcat 9.0.

IDE: Eclipse Enterprise / VS Code.

📂 Cấu trúc thư mục

/src/main/java: Mã nguồn Java (Controller, Model, DAO).

/src/main/webapp: Giao diện (JSP, CSS, JS).

/ai-service: Mã nguồn Python (nằm ở repo riêng).
