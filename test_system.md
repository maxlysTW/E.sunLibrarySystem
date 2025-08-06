# 圖書館系統功能測試

## 已實現的功能

### 1. ✅ 註冊功能

- 使用者可以透過註冊功能註冊帳號，以手機號碼進行註冊與登入
- API: `POST /api/auth/register`
- 測試數據: 手機號碼 `0975913163`, 密碼 `test123`, 用戶名 `Max`

### 2. ✅ 登入驗證功能

- 使用者必須登入帳號才能借閱書籍
- 系統實作身份驗證功能，以確保只有登入的使用者可以借閱書籍或還書
- API: `POST /api/auth/login`
- 使用 JWT Token 進行身份驗證

### 3. ✅ 借還書功能

- 每個使用者可以借閱多本書籍，但每本書籍只能被借閱一次
- 當使用者借閱書籍時，系統會將書籍狀態改為「已借閱」，並在借閱紀錄表中新增一筆借閱紀錄
- 當使用者還書時，系統會將書籍狀態改為「可借閱」，並更新借閱紀錄表中的還書時間
- 使用資料庫交易 (Transaction) 來確保在寫入資料庫時的完整性

### 4. ✅ 前端功能

- 左側導航選單（圖書館、我的書單）
- 登入後才能訪問借書功能（路由保護）
- 借書按鈕正常工作
- 淺綠色背景 (`#20b2aa`)

## 測試步驟

1. **註冊用戶**

   ```bash
   curl -X POST http://localhost:8080/api/auth/register \
     -H "Content-Type: application/json" \
     -d '{"phoneNumber":"0975913163","password":"test123","userName":"Max"}'
   ```

2. **登入**

   ```bash
   curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"phoneNumber":"0975913163","password":"test123"}'
   ```

3. **查看可借閱書籍**

   ```bash
   curl http://localhost:8080/api/books/available
   ```

4. **借書**

   ```bash
   curl -X POST http://localhost:8080/api/borrowing/borrow \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer <TOKEN>" \
     -d '{"inventoryId":1}'
   ```

5. **查看借閱記錄**
   ```bash
   curl -H "Authorization: Bearer <TOKEN>" \
     http://localhost:8080/api/borrowing/active
   ```

## 已知問題

1. **中文字符顯示問題**: 後端 API 返回的中文字符顯示為 `??`

   - 原因: 數據庫編碼問題
   - 影響: 書籍名稱和作者顯示不正確，但功能正常
   - 狀態: 已添加 UTF-8 編碼配置，需要進一步調試

2. **借閱記錄 API**: 返回 403 錯誤
   - 原因: CORS 配置問題
   - 狀態: 已修復 CORS 配置

## 系統狀態

- ✅ 後端服務器運行正常
- ✅ 數據庫連接正常
- ✅ 用戶註冊/登入功能正常
- ✅ 借書功能正常
- ✅ 前端路由保護正常
- ✅ 左側導航選單正常
- ⚠️ 中文字符顯示問題（功能不受影響）
