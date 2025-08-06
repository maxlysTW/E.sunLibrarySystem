<template>
  <div class="register-container">
    <el-card class="register-card">
      <template #header>
        <h2>玉山圖書館系統 - 註冊</h2>
      </template>

      <el-form
        :model="registerForm"
        :rules="rules"
        ref="registerFormRef"
        label-width="100px"
      >
        <el-form-item label="手機號碼" prop="phoneNumber">
          <el-input
            v-model="registerForm.phoneNumber"
            placeholder="請輸入手機號碼"
            maxlength="10"
          />
        </el-form-item>

        <el-form-item label="使用者名稱" prop="userName">
          <el-input
            v-model="registerForm.userName"
            placeholder="請輸入使用者名稱"
          />
        </el-form-item>

        <el-form-item label="密碼" prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="請輸入密碼"
            show-password
          />
        </el-form-item>

        <el-form-item label="確認密碼" prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="請再次輸入密碼"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleRegister" :loading="loading">
            註冊
          </el-button>
          <el-button @click="$router.push('/login')"> 返回登入 </el-button>
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
  name: "Register",
  setup() {
    const router = useRouter();
    const registerFormRef = ref();
    const loading = ref(false);

    const registerForm = reactive({
      phoneNumber: "",
      userName: "",
      password: "",
      confirmPassword: "",
    });

    const validateConfirmPassword = (rule, value, callback) => {
      if (value === "") {
        callback(new Error("請再次輸入密碼"));
      } else if (value !== registerForm.password) {
        callback(new Error("兩次輸入密碼不一致"));
      } else {
        callback();
      }
    };

    const rules = {
      phoneNumber: [
        { required: true, message: "請輸入手機號碼", trigger: "blur" },
        {
          pattern: /^09\d{8}$/,
          message: "手機號碼格式不正確",
          trigger: "blur",
        },
      ],
      userName: [
        { required: true, message: "請輸入使用者名稱", trigger: "blur" },
      ],
      password: [
        { required: true, message: "請輸入密碼", trigger: "blur" },
        { min: 6, message: "密碼長度至少6位", trigger: "blur" },
      ],
      confirmPassword: [
        { required: true, validator: validateConfirmPassword, trigger: "blur" },
      ],
    };

    const handleRegister = async () => {
      try {
        await registerFormRef.value.validate();
        loading.value = true;

        const response = await axios.post(
          "http://localhost:8080/api/auth/register",
          {
            phoneNumber: registerForm.phoneNumber,
            userName: registerForm.userName,
            password: registerForm.password,
          }
        );

        ElMessage.success("註冊成功，請登入");
        router.push("/login");
      } catch (error) {
        if (error.response) {
          ElMessage.error(error.response.data || "註冊失敗");
        } else {
          ElMessage.error("網路錯誤，請稍後再試");
        }
      } finally {
        loading.value = false;
      }
    };

    return {
      registerForm,
      registerFormRef,
      rules,
      loading,
      handleRegister,
    };
  },
};
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.register-card {
  width: 400px;
}

.register-card :deep(.el-card__header) {
  text-align: center;
  background: #f8f9fa;
}

.register-card h2 {
  margin: 0;
  color: #2c3e50;
}
</style>
