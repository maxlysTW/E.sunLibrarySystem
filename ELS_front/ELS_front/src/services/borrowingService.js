/**
 * 借閱相關 API 服務
 * 處理借書、還書、借閱歷史等功能
 */
import api from "./api";

export const borrowingService = {
  /**
   * 借閱書籍
   * @param {number} inventoryId - 庫存ID
   * @returns {Promise} 借閱結果
   */
  async borrowBook(inventoryId) {
    const response = await api.post("/borrowing/borrow", { inventoryId });
    return response.data;
  },

  /**
   * 歸還書籍
   * @param {number} inventoryId - 庫存ID
   * @returns {Promise} 歸還結果
   */
  async returnBook(inventoryId) {
    const response = await api.post("/borrowing/return", { inventoryId });
    return response.data;
  },

  /**
   * 獲取未歸還的借閱記錄
   * @returns {Promise} 未歸還書籍列表
   */
  async getActiveBorrowings() {
    const response = await api.get("/borrowing/active");
    return response.data;
  },

  /**
   * 獲取借閱歷史
   * @returns {Promise} 借閱歷史列表
   */
  async getBorrowingHistory() {
    const response = await api.get("/borrowing/history");
    return response.data;
  },

  /**
   * 獲取使用者的借閱統計
   * @returns {Promise} 借閱統計資料
   */
  async getBorrowingStats() {
    const response = await api.get("/borrowing/stats");
    return response.data;
  },
};
