<template>
  <div class="book-list">
    <div class="page-header">
      <h2>圖書館藏書</h2>
    </div>

    <div class="book-grid" v-loading="loading">
      <div class="book-card" v-for="book in books" :key="book.inventoryId">
        <div class="book-image">
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
            <el-tag :type="getStatusType(book.status)">
              {{ getStatusText(book.status) }}
            </el-tag>
          </p>
          <p class="book-introduction">{{ book.book?.introduction }}</p>

          <div class="book-actions">
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
    const books = ref([]);
    const loading = ref(false);
    const borrowingLoading = ref(null);
    const userName = ref(localStorage.getItem("userName") || "");

    const getAuthHeaders = () => {
      const token = localStorage.getItem("token");
      console.log("Current token:", token); // 調試用
      return {
        Authorization: `Bearer ${token}`,
      };
    };

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

    const fetchBooks = async () => {
      try {
        loading.value = true;
        const response = await axios.get(
          "http://localhost:8080/api/books/available"
        );
        // 適應新的API響應格式
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

        // 適應新的API響應格式
        if (response.data.success) {
          ElMessage.success(response.data.message || "借閱成功");
          // 直接更新當前書籍的狀態，而不是重新獲取整個列表
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

    const handleBookReturned = (event) => {
      const { inventoryId } = event.detail;
      // 更新對應書籍的狀態為可借閱
      const bookIndex = books.value.findIndex(
        (book) => book.inventoryId === inventoryId
      );
      if (bookIndex !== -1) {
        books.value[bookIndex].status = "Available";
      }
    };

    onMounted(() => {
      fetchBooks();
      // 監聽還書事件
      window.addEventListener("bookReturned", handleBookReturned);
    });

    onUnmounted(() => {
      // 移除事件監聽器
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

<style scoped>
.book-list {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
  text-align: center;
}

.page-header h2 {
  color: #00c896;
  font-size: 24px;
  margin-bottom: 10px;
}

.page-header p {
  color: white;
  font-size: 16px;
}

.book-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.book-card {
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease-in-out;
  display: flex;
  flex-direction: column;
}

.book-card:hover {
  transform: translateY(-5px);
}

.book-image {
  width: 100%;
  height: 200px; /* Fixed height for images */
  overflow: hidden;
  padding: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.book-image img {
  max-width: 100%;
  max-height: 100%;
  width: auto;
  height: auto;
  object-fit: contain;
  object-position: center;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.book-info {
  padding: 15px;
  flex-grow: 1;
}

.book-info h3 {
  margin: 0 0 10px 0;
  color: #2c3e50;
  font-size: 16px;
  font-weight: bold;
  line-height: 1.3;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  word-wrap: break-word;
  word-break: break-word;
}

.book-info p {
  margin: 5px 0;
  color: #666;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.book-introduction {
  font-size: 13px;
  color: #333;
  line-height: 1.4;
  height: 55px; /* Fixed height for introduction */
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  margin-bottom: 15px;
  word-wrap: break-word;
  word-break: break-word;
}

.book-actions {
  display: flex;
  justify-content: flex-end;
}

.el-button {
  width: 100%;
}

.borrowed-button {
  background-color: #909399 !important;
  border-color: #909399 !important;
  color: #fff !important;
  opacity: 0.7;
}
</style>
