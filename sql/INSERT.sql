-- Insert placeholder data into ACCOUNTS table
INSERT INTO ACCOUNTS (username, email, password_hash) VALUES
('john_doe', 'john.doe@example.com', 'hashed_password_123'),
('jane_doe', 'jane.doe@example.com', 'hashed_password_456');

-- Insert placeholder data into BILLS table
INSERT INTO BILLS (account_id, name, date, continuous, period, paid) VALUES
(1, 'Electricity Bill', '2023-09-01', TRUE, 'P1M', FALSE),
(2, 'Internet Bill', '2023-09-10', TRUE, 'P1M', TRUE);

-- Insert placeholder data into ITEMS table
INSERT INTO ITEMS (account_id, name) VALUES
(1, 'Milk'),
(2, 'Bread'),
(1, 'Eggs');

-- Insert placeholder data into SHOPPING_CARTS table
INSERT INTO SHOPPING_CARTS (account_id) VALUES
(1),
(2);

-- Insert placeholder data into ITEM_SHOPPING_CART table
INSERT INTO ITEM_SHOPPING_CART (item_id, shopping_cart_id, in_cart) VALUES
(1, 1, TRUE),
(2, 2, FALSE),
(3, 1, TRUE);
