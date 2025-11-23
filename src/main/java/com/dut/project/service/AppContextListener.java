package com.dut.project.service;
import javax.servlet.ServletContextEvent;
import javax.imageio.ImageIO;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {

    private WorkerScheduler scheduler;

    // Khởi động hệ thống Worker khi ứng dụng web được triển khai
    @Override
    public void contextInitialized(ServletContextEvent sce) {
    	ImageIO.scanForPlugins(); 
        
        System.out.println("DEBUG: Các định dạng ảnh được hỗ trợ sau khi quét:");
        for (String format : ImageIO.getWriterFormatNames()) {
            System.out.println("  - " + format);
        }
        System.out.println(">>> [SYSTEM] Khởi tạo WorkerScheduler...");
        scheduler = new WorkerScheduler();
        scheduler.startScheduling(); 
    }

    // Đóng hệ thống Worker khi ứng dụng web ngừng chạy (Tomcat tắt)
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (scheduler != null) {
            System.out.println(">>> [SYSTEM] Tắt WorkerScheduler...");
            scheduler.stopScheduling(); 
        }
    }
}
