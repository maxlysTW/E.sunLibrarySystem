<script setup>
import { computed } from "vue";
import { useRouter, useRoute } from "vue-router";

const router = useRouter();
const route = useRoute();

const isLoggedIn = computed(() => {
  return localStorage.getItem("token") !== null;
});

const userName = computed(() => {
  return localStorage.getItem("userName") || "";
});

// 檢查當前是否在登入或註冊頁面
const isAuthPage = computed(() => {
  return route.path === "/login" || route.path === "/register";
});

const logout = () => {
  localStorage.removeItem("token");
  localStorage.removeItem("userName");
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
    <!-- 登入/註冊頁面 - 沒有導航欄和側邊欄 -->
    <div v-if="isAuthPage" class="auth-layout">
      <router-view />
    </div>

    <!-- 登入後的主畫面 - 有導航欄和側邊欄 -->
    <el-container v-else-if="isLoggedIn" class="main-layout">
      <!-- 頂部導航欄 -->
      <el-header class="header">
        <div class="header-content">
          <div class="header-left">
            <h2>玉山圖書館系統</h2>
          </div>
          <div class="header-right">
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
            <el-menu-item index="/books">
              <span>圖書館</span>
            </el-menu-item>
            <el-menu-item index="/history">
              <span>我的書單</span>
            </el-menu-item>
          </el-menu>
        </el-aside>

        <!-- 主要內容區域 -->
        <el-container>
          <el-main>
            <router-view />
          </el-main>
        </el-container>
      </el-container>
    </el-container>

    <!-- 未登入且不在登入/註冊頁面時，重定向到登入頁面 -->
    <div v-else>
      <router-view />
    </div>
  </div>
</template>
