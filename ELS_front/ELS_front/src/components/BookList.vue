<template>
  <div class="book-list">
    <div class="page-header">
      <h2>可借閱書籍</h2>
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
            <el-tag :type="book.status === '在庫' ? 'success' : 'warning'">
              {{ book.status }}
            </el-tag>
          </p>
          <p class="book-introduction">{{ book.book?.introduction }}</p>

          <div class="book-actions">
            <el-button
              type="primary"
              @click="borrowBook(book.inventoryId)"
              :disabled="book.status !== '在庫' && book.status !== '??'"
              :loading="borrowingLoading === book.inventoryId"
            >
              借閱
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <el-empty
      v-if="books.length === 0 && !loading"
      description="暫無可借閱的書籍"
    />
  </div>
</template>

<script>
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import axios from "axios";

export default {
  name: "BookList",
  setup() {
    const books = ref([]);
    const loading = ref(false);
    const borrowingLoading = ref(null);
    const userName = ref(localStorage.getItem("userName") || "");

    const getAuthHeaders = () => ({
      Authorization: `Bearer ${localStorage.getItem("token")}`,
    });

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
        const response = await axios.post(
          "http://localhost:8080/api/borrowing/borrow",
          { inventoryId },
          { headers: getAuthHeaders() }
        );

        // 適應新的API響應格式
        if (response.data.success) {
          ElMessage.success(response.data.message || "借閱成功");
          await fetchBooks(); // 重新獲取書籍列表
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

    onMounted(() => {
      fetchBooks();
    });

    return {
      books,
      loading,
      borrowingLoading,
      userName,
      borrowBook,
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
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
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
}

.book-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.book-info {
  padding: 15px;
  flex-grow: 1;
}

.book-info h3 {
  margin: 0 0 10px 0;
  color: #2c3e50;
  font-size: 18px;
  font-weight: bold;
}

.book-info p {
  margin: 5px 0;
  color: #666;
  font-size: 14px;
}

.book-introduction {
  font-size: 14px;
  color: #333;
  line-height: 1.4;
  height: 60px; /* Fixed height for introduction */
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  margin-bottom: 15px;
}

.book-actions {
  display: flex;
  justify-content: flex-end;
}

.el-button {
  width: 100%;
}
</style>
