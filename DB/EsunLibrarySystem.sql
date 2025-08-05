-- My milestone (update: 2025/08/06)
-- 1. Create the database
-- 2. Create the each table
-- 3. Select each table to check if it was created successfully
-- 4. collect book information, ready to insert
-- 5. insert book information into the books table

-- for cleaning up the database
drop table borrowing_records;
drop table users;
drop table inventory;
drop table books;


CREATE DATABASE EsunLibrarySystem;

USE EsunLibrarySystem;

-- user table
CREATE TABLE users (
  user_id INT PRIMARY KEY,
  phone_number VARCHAR(20) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  salt VARCHAR(255) NOT NULL,
  user_name VARCHAR(100),
  registration_time DATETIME,
  last_login_time DATETIME
);
-- select user table to check if it was created successfully
SELECT * FROM users;


-- inventory table
CREATE TABLE inventory (
  inventory_id INT PRIMARY KEY,
  isbn NVARCHAR(13),
  store_time DATETIME,
  status NVARCHAR(10) NOT NULL CHECK (status IN (N'在庫', N'出借中', N'整理中(歸還後未入庫)', N'遺失', N'損毀', N'廢棄')),
  FOREIGN KEY (isbn) REFERENCES books(isbn)
);
-- select inventory table to check if it was created successfully
SELECT * FROM inventory;


-- book table
CREATE TABLE books (
  isbn NVARCHAR(13) PRIMARY KEY,
  name NVARCHAR(255),
  author NVARCHAR(100),
  introduction NVARCHAR(MAX),
  image_url NVARCHAR(500)
);
-- select book table to check if it was created successfully
SELECT * FROM books;


-- borrowing table
CREATE TABLE borrowing_records (
  record_id INT PRIMARY KEY,
  user_id INT,
  inventory_id INT,
  borrowing_time DATETIME,
  return_time DATETIME,
  FOREIGN KEY (user_id) REFERENCES users(user_id),
  FOREIGN KEY (inventory_id) REFERENCES inventory(inventory_id)
);
-- select borrowing table to check if it was created successfully
SELECT * FROM borrowing_records;


-- insert book information into the books table
INSERT INTO books (isbn, name, author, introduction, image_url) VALUES
('9789863128359', N'生成式⇄AI: 52個零程式互動體驗, 打造新世代人工智慧素養', N'羅光志', N'在 AI 模型不停推陳出新，生成的內容越來越逼真，
你是不是也開始對 AI 躍躍欲試，但卻又不得其門而入呢？
這本書的誕生，就是為了讓你我都能看得懂、用得上、不被落下。', 'https://s2.eslite.com/unsafe/fit-in/x900/s.eslite.com/b2b/newItem/2025/07/17/172_111600119_629_mainCoverImage1.jpg'),
('9786264250559', N'LLM資安教戰手冊: 打造安全的AI應用程式', N'Steve Wilson', N'AI工具爆炸性成長，從ChatGPT到企業內部專屬LLM，生成式AI已大量融入我們的生活與工作。但我們真的準備好「安全上線」了嗎？LLM帶來前所未見的創新機會，但同時也伴隨著新型態的安全風險，從prompt injection、資訊外洩，到代理人失控、幻覺誤導，每一項都可能讓AI工具從生產力助力變成潛在風險來源。',
'https://s2.eslite.com/unsafe/fit-in/x900/s.eslite.com/b2b/newItem/2025/06/24/8113_112533581_874_mainCoverImage1.jpg'),
('9789865022686', N'Java SE 12基礎必修課',N'蔡文龍/ 何嘉益/ 張志成/ 張力元', N'※專家與教師共同執筆
由OCJP（原SCJP）與MTA Java認證講師、科技大學教授程式設計教師共同編著，針對初學者學習程式設計所編寫的入門教材。
※程式設計的技能養成
內容兼具理論與實務、範例操作皆以圖示表示。由書中範例說明、問題分析、程式架構解說，訓練初學者邏輯思考、解決問題能力，使初學者輕鬆進入Java程式設計的殿堂。
※OCJP觀念與實例導入
內文融入OCJP觀念，除資料庫、Swing視窗應用程式與專題的章節之外，所有章節提供OCJP與MTA Java認證具代表性的試題演練，訓練初學者考取OCJP與MTA Java認證的基本素養。
※豐富內容與實務應用
本書注重在Java程式設計基本流程、陣列、Java 10型別推論、物件導向程式設計、介面與泛型、多執行緒、例外處理、檔案I/O、Swing視窗應用程式、事件處理、JDBC資料庫程式設計、Lambda運算式與專題實作。每個單元由淺入深，循序漸進、範例貼近日常生活，讓初學者學以致用。
※遊戲資料庫專題實作
在PDF檔電子書中，介紹了拉霸遊戲與記憶大考驗兩個專題，帶您進入實作專題的領域，進而擁有設計專題的完整體驗。','https://s2.eslite.com/unsafe/fit-in/x900/s.eslite.com/Upload/Product/201910/o/637056885942171250.jpg')

-- insert to inventory table
INSERT INTO inventory (inventory_id, isbn, store_time, status) VALUES
(1, '9789863128359', getdate(), N'在庫'),
(2, '9786264250559', getdate(), N'在庫'),
(3, '9789865022686', getdate(), N'在庫');