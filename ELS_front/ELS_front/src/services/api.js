/**
 * API 基礎配置
 * 統一管理 axios 配置、攔截器、錯誤處理
 */
import axios from "axios";
import { ElMessage } from "element-plus";
import router from "../router";

// 建立 axios 實例
const api = axios.create({
  baseURL: "http://localhost:8080/api",
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
  },
});

// 請求攔截器 - 自動添加認證token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 響應攔截器 - 統一處理錯誤
api.interceptors.response.use(
  (response) => {
    // 如果後端返回的 success 為 false，視為業務錯誤
    if (response.data && response.data.success === false) {
      ElMessage.error(response.data.message || "操作失敗");
      return Promise.reject(new Error(response.data.message || "操作失敗"));
    }
    return response;
  },
  (error) => {
    // 處理HTTP錯誤狀態碼
    if (error.response) {
      const { status, data } = error.response;

      switch (status) {
        case 401:
          // Token 過期或無效，清除本地存儲並跳轉到登入頁
          localStorage.removeItem("token");
          localStorage.removeItem("userName");
          ElMessage.error("登入已過期，請重新登入");
          router.push("/login");
          break;
        case 403:
          ElMessage.error("權限不足");
          break;
        case 404:
          ElMessage.error("請求的資源不存在");
          break;
        case 500:
          ElMessage.error("伺服器內部錯誤");
          break;
        default:
          ElMessage.error(data?.message || `請求失敗 (${status})`);
      }
    } else if (error.request) {
      // 網路錯誤
      ElMessage.error("網路連線錯誤，請檢查網路設定");
    } else {
      // 其他錯誤
      ElMessage.error(error.message || "發生未知錯誤");
    }

    return Promise.reject(error);
  }
);

export default api;
