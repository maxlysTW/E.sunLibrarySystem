<template>
  <div class="auth-container">
    <el-card class="auth-card">
      <template #header>
        <div class="auth-card-header">
          <h2>玉山圖書館系統 - 註冊</h2>
        </div>
      </template>

      <div class="auth-card-body">
        <el-form
          :model="registerForm"
          :rules="rules"
          ref="registerFormRef"
          label-width="100px"
          class="auth-form"
        >
          <el-form-item label="手機號碼" prop="phoneNumber">
            <el-input
              v-model="registerForm.phoneNumber"
              placeholder="請輸入手機號碼"
              maxlength="10"
              clearable
            />
          </el-form-item>

          <el-form-item label="使用者名稱" prop="userName">
            <el-input
              v-model="registerForm.userName"
              placeholder="請輸入使用者名稱"
              clearable
            />
          </el-form-item>

          <el-form-item label="密碼" prop="password">
            <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="請輸入密碼"
              show-password
              clearable
            />
          </el-form-item>

          <el-form-item label="確認密碼" prop="confirmPassword">
            <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="請再次輸入密碼"
              show-password
              clearable
            />
          </el-form-item>

          <div class="auth-buttons">
            <el-button
              type="primary"
              @click="handleRegister"
              :loading="loading"
            >
              註冊
            </el-button>
            <el-button @click="$router.push('/login')"> 返回登入 </el-button>
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
        {
          min: 2,
          max: 20,
          message: "使用者名稱長度應在2-20個字元之間",
          trigger: "blur",
        },
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

        // 檢查新的API響應格式
        if (response.data.success) {
          ElMessage.success(
            response.data.message || "註冊成功！請使用新帳號登入"
          );
          router.push("/login");
        } else {
          ElMessage.error(response.data.message || "註冊失敗");
        }
      } catch (error) {
        if (error.response && error.response.data) {
          // 處理新的API錯誤格式
          const errorData = error.response.data;
          const errorMessage = errorData.message || "註冊失敗";
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
      registerForm,
      registerFormRef,
      rules,
      loading,
      handleRegister,
    };
  },
};
</script>
