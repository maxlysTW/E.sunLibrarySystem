<template>
  <div class="auth-container">
    <el-card class="auth-card">
      <template #header>
        <div class="auth-card-header">
          <h2>玉山圖書館系統 - 登入</h2>
        </div>
      </template>

      <div class="auth-card-body">
        <el-form
          :model="loginForm"
          :rules="rules"
          ref="loginFormRef"
          label-width="100px"
          class="auth-form"
        >
          <el-form-item label="手機號碼" prop="phoneNumber">
            <el-input
              v-model="loginForm.phoneNumber"
              placeholder="請輸入手機號碼"
              maxlength="10"
              clearable
            />
          </el-form-item>

          <el-form-item label="密碼" prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="請輸入密碼"
              show-password
              clearable
            />
          </el-form-item>

          <div class="auth-buttons">
            <el-button type="primary" @click="handleLogin" :loading="loading">
              登入
            </el-button>
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
import axios from "axios";

export default {
  name: "Login",
  setup() {
    const router = useRouter();
    const loginFormRef = ref();
    const loading = ref(false);

    const loginForm = reactive({
      phoneNumber: "",
      password: "",
    });

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

    const handleLogin = async () => {
      try {
        await loginFormRef.value.validate();
        loading.value = true;

        const response = await axios.post(
          "http://localhost:8080/api/auth/login",
          loginForm
        );

        // 檢查新的API響應格式
        if (response.data.success && response.data.data) {
          const loginData = response.data.data;
          localStorage.setItem("token", loginData.token);
          localStorage.setItem("userName", loginData.userName);
          ElMessage.success(response.data.message || "登入成功！");
          router.push("/books");
        } else {
          ElMessage.error(response.data.message || "登入失敗");
        }
      } catch (error) {
        if (error.response && error.response.data) {
          // 處理新的API錯誤格式
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
