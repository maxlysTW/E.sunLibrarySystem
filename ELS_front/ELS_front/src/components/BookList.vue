<!-- 此Component負責顯示圖書館的書籍列表 -->

<template>
  <div class="book-list">
    <div class="page-header">
      <h2>圖書館藏書</h2>
    </div>

    <!-- 書籍列表區塊，顯示 loading 狀態 -->
    <div class="book-grid" v-loading="loading">
      <!-- 遍歷書籍資料，渲染每本書 -->
      <div class="book-card" v-for="book in books" :key="book.inventoryId">
        <div class="book-image">
          <!-- 書籍封面圖片，若無則顯示預設圖片 -->
          <img
            :src="book.book?.imageUrl || '/default-book.jpg'"
            :alt="book.book?.name"
          />
        </div>
        <div class="book-info">
          <h3>{{ book.book?.name }}</h3>
          <p><strong>作者：</strong>{{ book.book?.author }}</p>
          <p><strong>ISBN：</strong>{{ book.isbn }}</p>
          <p>
            <strong>狀態：</strong>
            <!-- 狀態標籤，顏色依據狀態決定 -->
            <el-tag :type="getStatusType(book.status)">
              {{ getStatusText(book.status) }}
            </el-tag>
          </p>
          <p class="book-introduction">{{ book.book?.introduction }}</p>

          <div class="book-actions">
            <!-- 根據書籍狀態顯示不同按鈕 -->
            <el-button
              v-if="book.status === 'Available'"
              type="primary"
              @click="borrowBook(book.inventoryId)"
              :loading="borrowingLoading === book.inventoryId"
            >
              借閱
            </el-button>
            <el-button
              v-else-if="book.status === 'Borrowed'"
              type="info"
              disabled
              class="borrowed-button"
            >
              已借閱
            </el-button>
            <el-button v-else type="warning" disabled>
              {{ getStatusText(book.status) }}
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 無書籍時顯示空狀態 -->
    <el-empty v-if="books.length === 0 && !loading" description="暫無書籍" />
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted } from "vue";
import { ElMessage } from "element-plus";
import axios from "axios";

export default {
  name: "BookList",
  setup() {
    const books = ref([]); // 書籍資料
    const loading = ref(false); // 載入狀態
    const borrowingLoading = ref(null); // 借閱按鈕 loading 狀態
    const userName = ref(localStorage.getItem("userName") || ""); // 使用者名稱

    // 取得授權標頭
    const getAuthHeaders = () => {
      const token = localStorage.getItem("token");
      console.log("Current token:", token); // 調試用
      return {
        Authorization: `Bearer ${token}`,
      };
    };

    // 根據狀態取得標籤顏色
    const getStatusType = (status) => {
      switch (status) {
        case "Available":
          return "success";
        case "Borrowed":
          return "danger";
        case "Processing":
          return "warning";
        case "Lost":
          return "danger";
        case "Damaged":
          return "warning";
        case "Discarded":
          return "info";
        default:
          return "warning";
      }
    };

    // 根據狀態取得顯示文字
    const getStatusText = (status) => {
      switch (status) {
        case "Available":
          return "可借閱";
        case "Borrowed":
          return "已借閱";
        case "Processing":
          return "處理中";
        case "Lost":
          return "遺失";
        case "Damaged":
          return "損毀";
        case "Discarded":
          return "廢棄";
        default:
          return status;
      }
    };

    // 從 API 取得書籍資料
    const fetchBooks = async () => {
      try {
        loading.value = true;
        const response = await axios.get(
          "http://localhost:8080/api/books/available"
        );
        // 根據 API 回傳格式設定書籍資料
        if (response.data.success && response.data.data) {
          books.value = response.data.data;
        } else {
          books.value = [];
          ElMessage.error(response.data.message || "獲取書籍列表失敗");
        }
      } catch (error) {
        if (error.response && error.response.data) {
          const errorData = error.response.data;
          ElMessage.error(errorData.message || "獲取書籍列表失敗");
        } else {
          ElMessage.error("獲取書籍列表失敗");
        }
        console.error(error);
      } finally {
        loading.value = false;
      }
    };

    // 借閱書籍
    const borrowBook = async (inventoryId) => {
      try {
        borrowingLoading.value = inventoryId;
        console.log("Attempting to borrow book with inventoryId:", inventoryId);
        console.log("Request headers:", getAuthHeaders());

        const response = await axios.post(
          "http://localhost:8080/api/borrowing/borrow",
          { inventoryId },
          { headers: getAuthHeaders() }
        );

        // 借閱成功後更新狀態
        if (response.data.success) {
          ElMessage.success(response.data.message || "借閱成功");
          // 只更新該書籍狀態
          const bookIndex = books.value.findIndex(
            (book) => book.inventoryId === inventoryId
          );
          if (bookIndex !== -1) {
            books.value[bookIndex].status = "Borrowed";
          }
        } else {
          ElMessage.error(response.data.message || "借閱失敗");
        }
      } catch (error) {
        if (error.response && error.response.data) {
          const errorData = error.response.data;
          ElMessage.error(errorData.message || "借閱失敗");
        } else {
          ElMessage.error("網路錯誤，請稍後再試");
        }
      } finally {
        borrowingLoading.value = null;
      }
    };

    // 處理還書事件，將書籍狀態設為可借閱
    const handleBookReturned = (event) => {
      const { inventoryId } = event.detail;
      const bookIndex = books.value.findIndex(
        (book) => book.inventoryId === inventoryId
      );
      if (bookIndex !== -1) {
        books.value[bookIndex].status = "Available";
      }
    };

    // 組件掛載時載入書籍並監聽還書事件
    onMounted(() => {
      fetchBooks();
      window.addEventListener("bookReturned", handleBookReturned);
    });

    // 組件卸載時移除事件監聽
    onUnmounted(() => {
      window.removeEventListener("bookReturned", handleBookReturned);
    });

    return {
      books,
      loading,
      borrowingLoading,
      userName,
      borrowBook,
      getStatusType,
      getStatusText,
    };
  },
};
</script>
