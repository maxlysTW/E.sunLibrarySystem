<template>
  <div class="book-list">
    <el-row :gutter="20">
      <el-col :span="24">
        <h2>可借閱書籍</h2>
        <p>歡迎，{{ userName }}！</p>
      </el-col>
    </el-row>

    <el-row :gutter="20" v-loading="loading">
      <el-col :span="8" v-for="book in books" :key="book.inventoryId">
        <el-card class="book-card" shadow="hover">
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

            <el-button
              type="primary"
              @click="borrowBook(book.inventoryId)"
              :disabled="book.status !== '在庫'"
              :loading="borrowingLoading === book.inventoryId"
            >
              借閱
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

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
        books.value = response.data;
      } catch (error) {
        ElMessage.error("獲取書籍列表失敗");
        console.error(error);
      } finally {
        loading.value = false;
      }
    };

    const borrowBook = async (inventoryId) => {
      try {
        borrowingLoading.value = inventoryId;
        await axios.post(
          "http://localhost:8080/api/borrowing/borrow",
          { inventoryId },
          { headers: getAuthHeaders() }
        );

        ElMessage.success("借閱成功");
        await fetchBooks(); // 重新獲取書籍列表
      } catch (error) {
        if (error.response) {
          ElMessage.error(error.response.data || "借閱失敗");
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

.book-card {
  margin-bottom: 20px;
  transition: transform 0.3s;
}

.book-card:hover {
  transform: translateY(-5px);
}

.book-image {
  text-align: center;
  margin-bottom: 15px;
}

.book-image img {
  max-width: 200px;
  max-height: 250px;
  object-fit: cover;
  border-radius: 8px;
}

.book-info h3 {
  margin: 0 0 10px 0;
  color: #2c3e50;
  font-size: 18px;
}

.book-info p {
  margin: 5px 0;
  color: #666;
}

.book-introduction {
  max-height: 60px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  margin-bottom: 15px;
}

.el-button {
  width: 100%;
}
</style>
