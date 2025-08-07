/**
 * 認證相關 API 服務
 * 處理登入、註冊等認證功能
 */
import api from "./api";

export const authService = {
  /**
   * 使用者登入
   * @param {Object} credentials - 登入憑證
   * @param {string} credentials.phoneNumber - 手機號碼
   * @param {string} credentials.password - 密碼
   * @returns {Promise} API 回應
   */
  async login(credentials) {
    const response = await api.post("/auth/login", credentials);
    return response.data;
  },

  /**
   * 使用者註冊
   * @param {Object} userData - 使用者資料
   * @param {string} userData.phoneNumber - 手機號碼
   * @param {string} userData.userName - 使用者名稱
   * @param {string} userData.password - 密碼
   * @returns {Promise} API 回應
   */
  async register(userData) {
    const response = await api.post("/auth/register", userData);
    return response.data;
  },

  /**
   * 登出 (清除本地存儲)
   */
  logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("userName");
    // 觸發登入狀態變化事件
    window.dispatchEvent(new CustomEvent("loginStateChanged"));
  },
};
