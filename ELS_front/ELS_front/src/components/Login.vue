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
import { ref, reactive, nextTick } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import axios from "axios";

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

        // 發送登入請求
        const response = await axios.post(
          "http://localhost:8080/api/auth/login",
          loginForm
        );

        // 檢查 API 回應格式並處理登入成功
        if (response.data.success && response.data.data) {
          const loginData = response.data.data;
          console.log("Login response data:", loginData); // 調試用

          // 儲存使用者資訊到本地儲存
          localStorage.setItem("token", loginData.token);
          localStorage.setItem("userName", loginData.userName);
          console.log("Stored token:", localStorage.getItem("token")); // 調試用

          ElMessage.success(response.data.message || "登入成功！");

          // 觸發登入狀態更新事件，通知其他組件
          window.dispatchEvent(new CustomEvent("loginStateChanged"));

          // 等待 DOM 更新完成後再跳轉到書籍頁面
          await nextTick();
          await router.push("/books");
        } else {
          ElMessage.error(response.data.message || "登入失敗");
        }
      } catch (error) {
        // 錯誤處理
        if (error.response && error.response.data) {
          // 處理 API 錯誤回應
          const errorData = error.response.data;
          const errorMessage = errorData.message || "登入失敗";
          ElMessage.error(errorMessage);
        } else if (error.request) {
          ElMessage.error("網路連線錯誤，請檢查網路設定");
        } else {
          ElMessage.error("發生未知錯誤，請稍後再試");
        }
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
