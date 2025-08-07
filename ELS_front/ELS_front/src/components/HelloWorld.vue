<!-- 此Component是Vue+Vite的示例組件，用於展示基本功能和開發工具 -->

<script setup>
import { ref } from "vue";

// 接收從父組件傳入的訊息屬性
defineProps({
  msg: String,
});

// 計數器狀態，用於展示響應式資料
const count = ref(0);
</script>

<template>
  <!-- 顯示從父組件傳入的訊息 -->
  <h1>{{ msg }}</h1>

  <div class="card">
    <!-- 互動式計數器按鈕，展示事件處理 -->
    <button type="button" @click="count++">count is {{ count }}</button>
    <p>
      Edit
      <code>components/HelloWorld.vue</code> to test HMR
    </p>
  </div>

  <!-- 開發相關連結和說明 -->
  <p>
    Check out
    <a href="https://vuejs.org/guide/quick-start.html#local" target="_blank"
      >create-vue</a
    >, the official Vue + Vite starter
  </p>
  <p>
    Learn more about IDE Support for Vue in the
    <a
      href="https://vuejs.org/guide/scaling-up/tooling.html#ide-support"
      target="_blank"
      >Vue Docs Scaling up Guide</a
    >.
  </p>
  <p class="read-the-docs">Click on the Vite and Vue logos to learn more</p>
</template>

<style scoped>
.read-the-docs {
  color: #888;
}
</style>
