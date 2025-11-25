package com.dut.project.service;
import javax.servlet.ServletContextEvent;
import javax.imageio.ImageIO;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
//thực hiện các tác vụ khởi tạo và dọn dẹp khi ứng dụng web bắt đầu hoặc kết thúc.
@WebListener
public class AppContextListener implements ServletContextListener {

    private WorkerScheduler scheduler;

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

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (scheduler != null) {
            System.out.println(">>> [SYSTEM] Tắt WorkerScheduler...");
            scheduler.stopScheduling(); 
        }
    }
}
