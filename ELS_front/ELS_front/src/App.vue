<!-- 此Component是應用程式的根組件，負責整體布局、路由管理和使用者認證狀態 -->

<script setup>
import { computed, ref, watch } from "vue";
import { useRouter, useRoute } from "vue-router";

const router = useRouter();
const route = useRoute();

// 使用 ref 來追蹤登入狀態，確保響應性
const loginState = ref({
  token: localStorage.getItem("token"),
  userName: localStorage.getItem("userName") || "",
});

// 計算屬性：檢查使用者是否已登入
const isLoggedIn = computed(() => {
  return loginState.value.token !== null;
});

// 計算屬性：取得使用者名稱
const userName = computed(() => {
  return loginState.value.userName;
});

// 計算屬性：檢查當前是否在登入或註冊頁面
const isAuthPage = computed(() => {
  return route.path === "/login" || route.path === "/register";
});

// 更新登入狀態的函式
const updateLoginState = () => {
  loginState.value = {
    token: localStorage.getItem("token"),
    userName: localStorage.getItem("userName") || "",
  };
};

// 監聽 localStorage 變化（跨標籤頁同步）
window.addEventListener("storage", updateLoginState);

// 監聽登入狀態變化事件
window.addEventListener("loginStateChanged", updateLoginState);

// 監聽路由變化並更新登入狀態
watch(
  () => route.path,
  () => {
    updateLoginState();
  }
);

// 登出功能
const logout = () => {
  localStorage.removeItem("token");
  localStorage.removeItem("userName");
  updateLoginState();
  router.push("/login");
};
</script>

<style scoped>
/* 登入/註冊頁面樣式 */
.auth-layout {
  min-height: 100vh;
  background: linear-gradient(135deg, #019e97 0%, #017a75 100%);
}

/* 主應用程式樣式 */
.main-layout {
  min-height: 100vh;
}

.header {
  background-color: #019e97;
  color: white;
  padding: 0;
  height: 60px;
  line-height: 60px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  padding: 0 20px;
}

.header-left h2 {
  margin: 0;
  color: white;
  font-size: 24px;
  font-weight: 600;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.welcome-text {
  color: white;
  font-size: 14px;
  font-weight: 500;
}

.sidebar-menu {
  height: calc(100vh - 60px);
  border-right: none;
  background-color: #019e97;
}

.el-aside {
  background-color: #019e97;
  color: white;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
}

.el-main {
  background-color: #f5f7fa;
  padding: 20px;
}

/* 側邊欄選單樣式 */
.sidebar-menu .el-menu-item {
  color: white;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
}

.sidebar-menu .el-menu-item:hover {
  background-color: rgba(255, 255, 255, 0.1);
  color: white;
}

.sidebar-menu .el-menu-item.is-active {
  background-color: rgba(255, 255, 255, 0.2);
  color: white;
  border-right: 3px solid #ffd04b;
}

.sidebar-menu .el-menu-item .el-icon {
  margin-right: 10px;
  font-size: 18px;
}
</style>

<template>
  <div id="app">
    <!-- 登入/註冊頁面布局 - 沒有導航欄和側邊欄 -->
    <div v-if="isAuthPage" class="auth-layout">
      <router-view />
    </div>

    <!-- 登入後的主畫面布局 - 有導航欄和側邊欄 -->
    <el-container v-else-if="isLoggedIn" class="main-layout">
      <!-- 頂部導航欄 -->
      <el-header class="header">
        <div class="header-content">
          <div class="header-left">
            <h2>玉山圖書館系統</h2>
          </div>
          <div class="header-right">
            <!-- 歡迎訊息和登出按鈕 -->
            <span class="welcome-text">歡迎，{{ userName }}！</span>
            <el-button type="danger" @click="logout" size="small">
              登出
            </el-button>
          </div>
        </div>
      </el-header>

      <el-container>
        <!-- 左側導覽欄 -->
        <el-aside width="200px">
          <el-menu
            :default-active="$route.path"
            class="sidebar-menu"
            router
            background-color="#019e97"
            text-color="#fff"
            active-text-color="#ffd04b"
          >
            <!-- 圖書館頁面選單項目 -->
            <el-menu-item index="/books">
              <span>圖書館</span>
            </el-menu-item>
            <!-- 借閱歷史頁面選單項目 -->
            <el-menu-item index="/history">
              <span>我的書單</span>
            </el-menu-item>
          </el-menu>
        </el-aside>

        <!-- 主要內容區域 -->
        <el-container>
          <el-main>
            <!-- 路由視圖，顯示當前頁面的組件 -->
            <router-view />
          </el-main>
        </el-container>
      </el-container>
    </el-container>

    <!-- 未登入且不在登入/註冊頁面時的處理 -->
    <div v-else>
      <router-view />
    </div>
  </div>
</template>
