-- My milestone (update: 2025/08/07)
-- EsunLibrarySystem DML script


USE EsunLibrarySystem;
GO

-- insert initial book data
INSERT INTO books (isbn, name, author, introduction, image_url) VALUES
('9789863128359', 'AI Artificial Intelligence Introduction', 'Luo Guangzhi', 
 'This is an introductory book about artificial intelligence, suitable for beginners.', 
 'https://s2.eslite.com/unsafe/fit-in/x900/s.eslite.com/b2b/newItem/2025/07/17/172_111600119_629_mainCoverImage1.jpg'),

('9786264250559', 'Cybersecurity Handbook', 'Steve Wilson', 
 'This is a practical handbook about information security.', 
 'https://s2.eslite.com/unsafe/fit-in/x900/s.eslite.com/b2b/newItem/2025/06/24/8113_112533581_874_mainCoverImage1.jpg'),

('9789865022686', 'Java Programming', 'Cai Wenlong', 
 'This is a basic Java programming textbook.', 
 'https://s2.eslite.com/unsafe/fit-in/x900/s.eslite.com/Upload/Product/201910/o/637056885942171250.jpg'),


-- insert inventory data
INSERT INTO inventory (isbn, status) VALUES
('9789863128359', 'Available'),
('9786264250559', 'Available'),
('9789865022686', 'Available');

-- insert test user data
INSERT INTO users (phone_number, password_hash, salt, user_name) VALUES
('0975913162', 'hashed_password_here', 'salt_here', 'max');

GO 