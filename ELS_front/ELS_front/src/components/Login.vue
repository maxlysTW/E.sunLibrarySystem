<!-- 此Component負責使用者登入功能 -->

<template>
  <div class="auth-container">
    <!-- 登入表單卡片 -->
    <el-card class="auth-card">
      <template #header>
        <div class="auth-card-header">
          <h2>玉山圖書館系統 - 登入</h2>
        </div>
      </template>

      <div class="auth-card-body">
        <!-- 登入表單，包含驗證規則 -->
        <el-form
          :model="loginForm"
          :rules="rules"
          ref="loginFormRef"
          label-width="100px"
          class="auth-form"
        >
          <!-- 手機號碼輸入欄位 -->
          <el-form-item label="手機號碼" prop="phoneNumber">
            <el-input
              v-model="loginForm.phoneNumber"
              placeholder="請輸入手機號碼"
              maxlength="10"
              clearable
            />
          </el-form-item>

          <!-- 密碼輸入欄位 -->
          <el-form-item label="密碼" prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="請輸入密碼"
              show-password
              clearable
            />
          </el-form-item>

          <!-- 操作按鈕區域 -->
          <div class="auth-buttons">
            <!-- 登入按鈕，顯示載入狀態 -->
            <el-button type="primary" @click="handleLogin" :loading="loading">
              登入
            </el-button>
            <!-- 註冊按鈕，導向註冊頁面 -->
            <el-button @click="$router.push('/register')">
              註冊新帳號
            </el-button>
          </div>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script>
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { authService } from "../services";

export default {
  name: "Login",
  setup() {
    const router = useRouter();
    const loginFormRef = ref(); // 表單引用
    const loading = ref(false); // 登入按鈕載入狀態

    // 登入表單資料
    const loginForm = reactive({
      phoneNumber: "",
      password: "",
    });

    // 表單驗證規則
    const rules = {
      phoneNumber: [
        { required: true, message: "請輸入手機號碼", trigger: "blur" },
        {
          pattern: /^09\d{8}$/,
          message: "手機號碼格式不正確",
          trigger: "blur",
        },
      ],
      password: [{ required: true, message: "請輸入密碼", trigger: "blur" }],
    };

    // 處理登入提交
    const handleLogin = async () => {
      try {
        // 驗證表單
        await loginFormRef.value.validate();
        loading.value = true;

        // 使用 authService 進行登入
        const response = await authService.login(loginForm);

        // 檢查登入成功
        if (response.success && response.data) {
          const loginData = response.data;

          // 儲存使用者資訊到本地儲存
          localStorage.setItem("token", loginData.token);
          localStorage.setItem("userName", loginData.userName);

          ElMessage.success(response.message || "登入成功！");

          // 觸發登入狀態更新事件
          window.dispatchEvent(new CustomEvent("loginStateChanged"));

          // 跳轉到書籍頁面
          await router.push("/books");
        }
      } catch (error) {
        // 錯誤已由 API 攔截器處理，這裡只需要記錄
        console.error("Login failed:", error);
      } finally {
        loading.value = false;
      }
    };

    return {
      loginForm,
      loginFormRef,
      rules,
      loading,
      handleLogin,
    };
  },
};
</script>
