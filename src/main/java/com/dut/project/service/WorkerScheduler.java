package com.dut.project.service;

import com.dut.project.bo.imageBO;
import com.dut.project.model.image;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
public class WorkerScheduler {
	private static final int BATCH_SIZE = 12;
	private static final int WORKER_POOL_SIZE = 4;
	
	private final imageBO imageBO = new imageBO();
	
	private final ExecutorService workerPool = Executors.newFixedThreadPool(WORKER_POOL_SIZE);
	
	private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	
	public void startScheduling() {
		scheduler.scheduleAtFixedRate(this::findAndDispatchPendingJobs, 
                0, 2, TimeUnit.SECONDS);
		
	}
	private void findAndDispatchPendingJobs() {
		try {
			while (true) {
				List<image> batch = imageBO.getPendingAndMarkAsRunning(BATCH_SIZE);
				
				if(batch.isEmpty()) {
					System.out.println("Scheduler: Hàng đợi DB trống. Đang chờ chu kỳ quét tiếp theo.");
	                break;
				}
				System.out.println("Scheduler: Phát hiện và điều phối " + batch.size() + " Job mới.");
				for(image img : batch) {
					workerPool.submit(new ImageProcessingWorker(img));
				}
			}
		}catch(Exception e) {
			System.err.println("LỖI LỚN trong quá trình quét/điều phối Job: " + e.getMessage());
		}
	}
	public void stopScheduling() {
		System.out.println("--- WorkerScheduler: Tắt hệ thống xử lý bất đồng bộ. ---");
        scheduler.shutdown();
        workerPool.shutdown();
	}
}
