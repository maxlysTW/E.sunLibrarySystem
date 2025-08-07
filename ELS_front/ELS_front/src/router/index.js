/**
 * 路由配置
 * 統一管理所有路由定義和守衛
 */
import { createRouter, createWebHistory } from "vue-router";
import { ElMessage } from "element-plus";

// 導入組件
import Login from "../components/Login.vue";
import Register from "../components/Register.vue";
import BookList from "../components/BookList.vue";
import BorrowingHistory from "../components/BorrowingHistory.vue";

// 路由配置
const routes = [
  {
    path: "/",
    redirect: "/login",
  },
  {
    path: "/login",
    name: "Login",
    component: Login,
    meta: {
      requiresAuth: false,
      title: "登入 - 玉山圖書館系統",
    },
  },
  {
    path: "/register",
    name: "Register",
    component: Register,
    meta: {
      requiresAuth: false,
      title: "註冊 - 玉山圖書館系統",
    },
  },
  {
    path: "/books",
    name: "Books",
    component: BookList,
    meta: {
      requiresAuth: true,
      title: "圖書館 - 玉山圖書館系統",
    },
  },
  {
    path: "/history",
    name: "History",
    component: BorrowingHistory,
    meta: {
      requiresAuth: true,
      title: "我的書單 - 玉山圖書館系統",
    },
  },
];

// 建立路由實例
const router = createRouter({
  history: createWebHistory(),
  routes,
});

// 全域前置守衛
router.beforeEach((to, from, next) => {
  // 設定頁面標題
  if (to.meta.title) {
    document.title = to.meta.title;
  }

  const token = localStorage.getItem("token");
  const requiresAuth = to.meta.requiresAuth;

  // 檢查認證需求
  if (requiresAuth && !token) {
    ElMessage.warning("請先登入");
    next("/login");
  } else if (
    !requiresAuth &&
    token &&
    (to.path === "/login" || to.path === "/register")
  ) {
    // 已登入用戶訪問登入/註冊頁面，重導向到書籍頁面
    next("/books");
  } else {
    next();
  }
});

// 全域後置守衛
router.afterEach((to) => {
  // 觸發登入狀態更新事件（如果需要）
  if (to.meta.requiresAuth) {
    window.dispatchEvent(new CustomEvent("loginStateChanged"));
  }
});

export default router;
