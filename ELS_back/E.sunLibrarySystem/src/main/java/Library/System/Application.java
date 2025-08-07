/**
 * 玉山圖書館系統 - Spring Boot 應用程式入口類別
 * 
 * 此類別是整個圖書館管理系統的啟動入口點，負責：
 * 1. 初始化 Spring Boot 應用程式
 * 2. 自動配置所有相關元件（控制器、服務、資料庫連接等）
 * 3. 啟動嵌入式 Web 伺服器
 * 
 * 系統功能包括：
 * - 使用者註冊與登入認證
 * - 圖書查詢與搜尋
 * - 圖書借閱與歸還管理
 * - 借閱歷史記錄查詢
 * - JWT Token 身份驗證
 * 
 * @author E.sun Library System Team
 * @version 1.0
 * @since 2025
 */
package Library.System;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	/**
	 * 應用程式主要入口方法
	 * 
	 * @param args 命令行參數
	 */
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
