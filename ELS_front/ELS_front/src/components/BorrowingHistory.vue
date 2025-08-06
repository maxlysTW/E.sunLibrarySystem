<template>
  <div class="borrowing-history">
    <el-row :gutter="20">
      <el-col :span="24">
        <h2>借閱紀錄</h2>
      </el-col>
    </el-row>

    <el-tabs v-model="activeTab" @tab-click="handleTabClick">
      <el-tab-pane label="未歸還書籍" name="active">
        <el-table
          :data="activeBorrowings"
          v-loading="loading"
          style="width: 100%"
        >
          <el-table-column prop="inventory.book.name" label="書名" />
          <el-table-column prop="inventory.book.author" label="作者" />
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
                @click="returnBook(scope.row.inventory.inventoryId)"
                :loading="returningLoading === scope.row.inventory.inventoryId"
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
          <el-table-column prop="inventory.book.name" label="書名" />
          <el-table-column prop="inventory.book.author" label="作者" />
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
        activeBorrowings.value = response.data;
      } catch (error) {
        ElMessage.error("獲取未歸還書籍失敗");
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
        borrowingHistory.value = response.data;
      } catch (error) {
        ElMessage.error("獲取借閱歷史失敗");
        console.error(error);
      } finally {
        loading.value = false;
      }
    };

    const returnBook = async (inventoryId) => {
      try {
        returningLoading.value = inventoryId;
        await axios.post(
          "http://localhost:8080/api/borrowing/return",
          { inventoryId },
          { headers: getAuthHeaders() }
        );

        ElMessage.success("還書成功");
        await fetchActiveBorrowings();
        await fetchBorrowingHistory();
      } catch (error) {
        if (error.response) {
          ElMessage.error(error.response.data || "還書失敗");
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

.el-table {
  margin-top: 20px;
}
</style>
