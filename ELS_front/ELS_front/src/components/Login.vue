<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <h2>玉山圖書館系統 - 登入</h2>
      </template>

      <el-form
        :model="loginForm"
        :rules="rules"
        ref="loginFormRef"
        label-width="100px"
      >
        <el-form-item label="手機號碼" prop="phoneNumber">
          <el-input
            v-model="loginForm.phoneNumber"
            placeholder="請輸入手機號碼"
            maxlength="10"
          />
        </el-form-item>

        <el-form-item label="密碼" prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="請輸入密碼"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading">
            登入
          </el-button>
          <el-button @click="$router.push('/register')"> 註冊新帳號 </el-button>
        </el-form-item>
      </el-form>
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

        if (response.data.token) {
          localStorage.setItem("token", response.data.token);
          localStorage.setItem("userName", response.data.userName);
          ElMessage.success("登入成功");
          router.push("/books");
        }
      } catch (error) {
        if (error.response) {
          ElMessage.error(error.response.data || "登入失敗");
        } else {
          ElMessage.error("網路錯誤，請稍後再試");
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

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 400px;
}

.login-card :deep(.el-card__header) {
  text-align: center;
  background: #f8f9fa;
}

.login-card h2 {
  margin: 0;
  color: #2c3e50;
}
</style>
