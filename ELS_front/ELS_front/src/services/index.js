/**
 * Service 層統一匯出
 * 提供所有 API 服務的統一入口
 */

// 先導入各個服務模組
import { authService } from "./authService";
import { bookService } from "./bookService";
import { borrowingService } from "./borrowingService";

// 匯出各個服務模組
export { authService, bookService, borrowingService };

// 也可以匯出 api 實例供特殊用途
export { default as api } from "./api";

// 統一的服務對象，方便使用
export const services = {
  auth: authService,
  book: bookService,
  borrowing: borrowingService,
};

// 預設匯出
export default services;
