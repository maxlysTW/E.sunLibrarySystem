<template>
  <div class="borrowing-history">
    <div class="page-header">
      <h2>我的書單</h2>
    </div>

    <div class="history-tabs">
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="未歸還書籍" name="active">
          <el-table
            :data="activeBorrowings"
            v-loading="loading"
            style="width: 100%"
          >
            <el-table-column prop="bookName" label="書名" />
            <el-table-column prop="bookAuthor" label="作者" />
            <el-table-column prop="borrowingTime" label="借閱時間">
              <template #default="scope">
                {{ formatDate(scope.row.borrowingTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作">
              <template #default="scope">
                <el-button
                  type="success"
                  size="small"
                  @click="returnBook(scope.row.inventoryId)"
                  :loading="returningLoading === scope.row.inventoryId"
                >
                  還書
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-empty
            v-if="activeBorrowings.length === 0 && !loading"
            description="暫無未歸還的書籍"
          />
        </el-tab-pane>

        <el-tab-pane label="借閱歷史" name="history">
          <el-table
            :data="borrowingHistory"
            v-loading="loading"
            style="width: 100%"
          >
            <el-table-column prop="bookName" label="書名" />
            <el-table-column prop="bookAuthor" label="作者" />
            <el-table-column prop="borrowingTime" label="借閱時間">
              <template #default="scope">
                {{ formatDate(scope.row.borrowingTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="returnTime" label="歸還時間">
              <template #default="scope">
                {{
                  scope.row.returnTime
                    ? formatDate(scope.row.returnTime)
                    : "未歸還"
                }}
              </template>
            </el-table-column>
            <el-table-column label="狀態">
              <template #default="scope">
                <el-tag :type="scope.row.returnTime ? 'success' : 'warning'">
                  {{ scope.row.returnTime ? "已歸還" : "借閱中" }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>

          <el-empty
            v-if="borrowingHistory.length === 0 && !loading"
            description="暫無借閱紀錄"
          />
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import axios from "axios";

export default {
  name: "BorrowingHistory",
  setup() {
    const activeTab = ref("active");
    const activeBorrowings = ref([]);
    const borrowingHistory = ref([]);
    const loading = ref(false);
    const returningLoading = ref(null);

    const getAuthHeaders = () => ({
      Authorization: `Bearer ${localStorage.getItem("token")}`,
    });

    const formatDate = (dateString) => {
      if (!dateString) return "";
      const date = new Date(dateString);
      return date.toLocaleString("zh-TW");
    };

    const fetchActiveBorrowings = async () => {
      try {
        loading.value = true;
        const response = await axios.get(
          "http://localhost:8080/api/borrowing/active",
          {
            headers: getAuthHeaders(),
          }
        );
        // 適應新的API響應格式
        if (response.data.success && response.data.data) {
          activeBorrowings.value = response.data.data;
        } else {
          activeBorrowings.value = [];
          ElMessage.error(response.data.message || "獲取未歸還書籍失敗");
        }
      } catch (error) {
        if (error.response && error.response.data) {
          const errorData = error.response.data;
          ElMessage.error(errorData.message || "獲取未歸還書籍失敗");
        } else {
          ElMessage.error("獲取未歸還書籍失敗");
        }
        console.error(error);
      } finally {
        loading.value = false;
      }
    };

    const fetchBorrowingHistory = async () => {
      try {
        loading.value = true;
        const response = await axios.get(
          "http://localhost:8080/api/borrowing/history",
          {
            headers: getAuthHeaders(),
          }
        );
        // 適應新的API響應格式
        if (response.data.success && response.data.data) {
          borrowingHistory.value = response.data.data;
        } else {
          borrowingHistory.value = [];
          ElMessage.error(response.data.message || "獲取借閱歷史失敗");
        }
      } catch (error) {
        if (error.response && error.response.data) {
          const errorData = error.response.data;
          ElMessage.error(errorData.message || "獲取借閱歷史失敗");
        } else {
          ElMessage.error("獲取借閱歷史失敗");
        }
        console.error(error);
      } finally {
        loading.value = false;
      }
    };

    const returnBook = async (inventoryId) => {
      try {
        returningLoading.value = inventoryId;
        const response = await axios.post(
          "http://localhost:8080/api/borrowing/return",
          { inventoryId },
          { headers: getAuthHeaders() }
        );

        // 適應新的API響應格式
        if (response.data.success) {
          ElMessage.success(response.data.message || "還書成功");
          await fetchActiveBorrowings();
          await fetchBorrowingHistory();

          // 發送還書成功事件，通知父組件更新書籍列表
          window.dispatchEvent(
            new CustomEvent("bookReturned", {
              detail: { inventoryId },
            })
          );
        } else {
          ElMessage.error(response.data.message || "還書失敗");
        }
      } catch (error) {
        if (error.response && error.response.data) {
          const errorData = error.response.data;
          ElMessage.error(errorData.message || "還書失敗");
        } else {
          ElMessage.error("網路錯誤，請稍後再試");
        }
      } finally {
        returningLoading.value = null;
      }
    };

    const handleTabClick = () => {
      if (activeTab.value === "active") {
        fetchActiveBorrowings();
      } else {
        fetchBorrowingHistory();
      }
    };

    onMounted(() => {
      fetchActiveBorrowings();
      fetchBorrowingHistory();
    });

    return {
      activeTab,
      activeBorrowings,
      borrowingHistory,
      loading,
      returningLoading,
      formatDate,
      returnBook,
      handleTabClick,
    };
  },
};
</script>

<style scoped>
.borrowing-history {
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

.history-tabs {
  margin-top: 20px;
}

/* 調整標籤位置，讓"未歸還書籍"往右移動 */
.history-tabs :deep(.el-tabs__header) {
  margin-bottom: 20px;
}

.history-tabs :deep(.el-tabs__nav-wrap) {
  padding-left: 20px; /* 增加左邊距，讓標籤往右移動 */
}

.history-tabs :deep(.el-tabs__item) {
  font-size: 16px;
  padding: 0 30px;
  height: 40px;
  line-height: 40px;
}

.history-tabs :deep(.el-tabs__item.is-active) {
  color: #00c896;
  font-weight: bold;
}

.history-tabs :deep(.el-tabs__active-bar) {
  background-color: #00c896;
}

/* 表格樣式 */
.el-table {
  margin-top: 10px;
}

.el-table th {
  background-color: #f5f7fa;
  color: #606266;
  font-weight: bold;
}

.el-table td {
  padding: 12px 0;
}

/* 按鈕樣式 */
.el-button--success {
  background-color: #00c896;
  border-color: #00c896;
}

.el-button--success:hover {
  background-color: #00b386;
  border-color: #00b386;
}

/* 標籤樣式 */
.el-tag--success {
  background-color: #f0f9ff;
  border-color: #00c896;
  color: #00c896;
}

.el-tag--warning {
  background-color: #fff7e6;
  border-color: #ffa500;
  color: #ffa500;
}
</style>
