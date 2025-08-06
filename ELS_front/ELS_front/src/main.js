import { createApp } from "vue";
import { createRouter, createWebHistory } from "vue-router";
import ElementPlus from "element-plus";
import "element-plus/dist/index.css";
import * as ElementPlusIconsVue from "@element-plus/icons-vue";
import App from "./App.vue";
import "./styles/main.css";

// 路由配置
import Login from "./components/Login.vue";
import Register from "./components/Register.vue";
import BookList from "./components/BookList.vue";
import BorrowingHistory from "./components/BorrowingHistory.vue";

const routes = [
  { path: "/", redirect: "/login" },
  { path: "/login", component: Login },
  { path: "/register", component: Register },
  { path: "/books", component: BookList },
  { path: "/history", component: BorrowingHistory },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// 路由守衛
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem("token");
  const publicPages = ["/login", "/register"];
  const authRequired = !publicPages.includes(to.path);

  if (authRequired && !token) {
    // 需要登入但沒有token，重定向到登入頁面
    next("/login");
  } else if (token && (to.path === "/login" || to.path === "/register")) {
    // 已登入但訪問登入/註冊頁面，重定向到圖書館頁面
    next("/books");
  } else {
    next();
  }
});

const app = createApp(App);

// 註冊所有圖標
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component);
}

app.use(router);
app.use(ElementPlus);
app.mount("#app");
