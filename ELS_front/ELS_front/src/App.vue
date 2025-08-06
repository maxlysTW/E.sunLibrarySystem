<script setup>
import { computed } from "vue";
import { useRouter } from "vue-router";

export default {
  name: "App",
  setup() {
    const router = useRouter();

    const isLoggedIn = computed(() => {
      return localStorage.getItem("token") !== null;
    });

    const logout = () => {
      localStorage.removeItem("token");
      localStorage.removeItem("userName");
      router.push("/login");
    };

    return {
      isLoggedIn,
      logout,
    };
  },
};
</script>

<template>
  <div id="app">
    <el-container v-if="isLoggedIn">
      <el-header>
        <el-menu mode="horizontal" router>
          <el-menu-item index="/books">
            <el-icon><Reading /></el-icon>
            書籍列表
          </el-menu-item>
          <el-menu-item index="/history">
            <el-icon><Document /></el-icon>
            借閱紀錄
          </el-menu-item>
          <el-menu-item style="margin-left: auto" @click="logout">
            <el-icon><SwitchButton /></el-icon>
            登出
          </el-menu-item>
        </el-menu>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
    <router-view v-else />
  </div>
</template>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}

.el-header {
  padding: 0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.el-main {
  padding: 20px;
}
</style>
