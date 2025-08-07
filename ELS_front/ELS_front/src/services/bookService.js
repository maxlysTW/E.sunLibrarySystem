/**
 * 書籍相關 API 服務
 * 處理書籍查詢、借閱等功能
 */
import api from "./api";

export const bookService = {
  /**
   * 獲取可借閱的書籍列表
   * @returns {Promise} 書籍列表
   */
  async getAvailableBooks() {
    const response = await api.get("/books/available");
    return response.data;
  },

  /**
   * 根據書籍ID獲取書籍詳情
   * @param {number} bookId - 書籍ID
   * @returns {Promise} 書籍詳情
   */
  async getBookById(bookId) {
    const response = await api.get(`/books/${bookId}`);
    return response.data;
  },

  /**
   * 搜尋書籍
   * @param {Object} searchParams - 搜尋參數
   * @param {string} searchParams.keyword - 關鍵字
   * @param {string} searchParams.author - 作者
   * @param {string} searchParams.isbn - ISBN
   * @returns {Promise} 搜尋結果
   */
  async searchBooks(searchParams) {
    const response = await api.get("/books/search", { params: searchParams });
    return response.data;
  },
};
