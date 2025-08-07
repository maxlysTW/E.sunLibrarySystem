# 玉山圖書館借閱系統
這是一個基於 Spring Boot + Vue.js 的前後分離技術之線上圖書借閱系統。

## 開發者
- 開發者：林渝舜
- 專案名稱：玉山圖書館借閱系統
- 技術要求：Spring Boot + Vue.js + SQL Server
- 完成日期：2025 年 8 月 7 日

## 系統架構
### 後端技術棧
- **Spring Boot 3.5.4** - 主要框架
- **Spring Security** - 身份驗證與授權
- **Spring Data JPA** - 資料存取層
- **SQL Server** - 關聯式資料庫
- **JWT** - Token 認證
- **Maven** - 專案管理工具

### 前端技術棧
- **Vue.js 3** - 前端框架
- **Vue Router** - 路由管理
- **Element Plus** - UI 組件庫
- **Axios** - HTTP 客戶端
- **Vite** - 建構工具

## 功能特色
### 1. 使用者管理
- 手機號碼註冊與登入
- 密碼加鹽雜湊儲存
- JWT Token 身份驗證
- 防止 SQL Injection 和 XSS 攻擊

### 2. 圖書管理
- 書籍資訊管理（ISBN、書名、作者、簡介）
- 庫存狀態管理（在庫、出借中、整理中等）
- 可借閱書籍查詢

### 3. 借還書功能
- 借書功能（含交易完整性）
- 還書功能
- 借閱紀錄查詢
- 未歸還書籍管理

### 4. 系統安全
- 使用 Stored Procedure 存取資料庫
- 資料庫交易 (Transaction) 確保資料完整性
- 密碼加鹽雜湊
- JWT Token 驗證
- 輸入驗證與防護

## 專案結構
```
E.sunLibrarySystem/
├── DB/                          # 資料庫相關
│   └── EsunLibrarySystem.sql   # 資料庫建置腳本
├── ELS_back/                    # 後端專案
│   └── E.sunLibrarySystem/
│       ├── src/main/java/Library/System/
│       │   ├── Application.java           # 主應用程式
│       │   ├── common/                    # 共用層
│       │   │   ├── PasswordUtil.java     # 密碼工具
│       │   │   └── JwtUtil.java          # JWT 工具
│       │   ├── controller/                # 展示層
│       │   │   ├── AuthController.java   # 認證控制器
│       │   │   ├── BookController.java   # 書籍控制器
│       │   │   └── BorrowingController.java # 借閱控制器
│       │   ├── service/                   # 業務層
│       │   │   ├── UserService.java      # 使用者服務
│       │   │   ├── BookService.java      # 書籍服務
│       │   │   └── BorrowingService.java # 借閱服務
│       │   ├── repository/                # 資料層
│       │   │   ├── UserRepository.java   # 使用者資料存取
│       │   │   ├── BookRepository.java   # 書籍資料存取
│       │   │   ├── InventoryRepository.java # 庫存資料存取
│       │   │   └── BorrowingRecordRepository.java # 借閱紀錄資料存取
│       │   └── entity/                    # 實體類別
│       │       ├── User.java             # 使用者實體
│       │       ├── Book.java             # 書籍實體
│       │       ├── Inventory.java        # 庫存實體
│       │       └── BorrowingRecord.java  # 借閱紀錄實體
│       └── pom.xml                        # Maven 配置
└── ELS_front/                    # 前端專案
    └── ELS_front/
        ├── src/
        │   ├── components/               # Vue 組件
        │   │   ├── Login.vue            # 登入組件
        │   │   ├── Register.vue         # 註冊組件
        │   │   ├── BookList.vue         # 書籍列表組件
        │   │   └── BorrowingHistory.vue # 借閱紀錄組件
        │   ├── App.vue                  # 主應用組件
        │   └── main.js                  # 應用程式入口
        └── package.json                 # NPM 配置
```

## 安裝與執行
### 前置需求
- Java 21
- Node.js 18+
- SQL Server 2019+
- Maven 3.6+

### 1. 資料庫設定
```sql
-- 執行 DB/EsunLibrarySystem.sql 建立資料庫和資料表
```

### 2. 後端設定
```bash
cd ELS_back/E.sunLibrarySystem

# 修改 application.properties 中的資料庫連線設定
# spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=EsunLibrarySystem
# spring.datasource.username=your_username
# spring.datasource.password=your_password

# 啟動後端服務
mvn spring-boot:run
```

### 3. 前端設定
```bash
cd ELS_front/ELS_front

# 安裝依賴
npm install

# 啟動開發伺服器
npm run dev
```

### 4. 存取系統
- 前端：http://localhost:5173
- 後端 API：http://localhost:8080

## API 文件
### 認證相關
- `POST /api/auth/register` - 使用者註冊
- `POST /api/auth/login` - 使用者登入

### 書籍相關
- `GET /api/books/available` - 查詢可借閱書籍
- `GET /api/books/{isbn}` - 根據 ISBN 查詢書籍

### 借閱相關
- `POST /api/borrowing/borrow` - 借書
- `POST /api/borrowing/return` - 還書
- `GET /api/borrowing/history` - 查詢借閱歷史
- `GET /api/borrowing/active` - 查詢未歸還書籍

## 資料庫設計

### 主要資料表
1. **users** - 使用者資料
2. **books** - 書籍基本資料
3. **inventory** - 庫存管理
4. **borrowing_records** - 借閱紀錄

### Stored Procedures
- `sp_RegisterUser` - 使用者註冊
- `sp_UserLogin` - 使用者登入
- `sp_BorrowBook` - 借書
- `sp_ReturnBook` - 還書
- `sp_GetAvailableBooks` - 查詢可借閱書籍
- `sp_GetUserBorrowingHistory` - 查詢使用者借閱歷史

## 安全措施
1. **密碼安全**：使用 SHA-256 加鹽雜湊
2. **身份驗證**：JWT Token 機制
3. **資料庫安全**：使用 Stored Procedure 防止 SQL Injection
4. **輸入驗證**：前後端雙重驗證
5. **CORS 設定**：限制跨域存取


