-- Insert initial users (admin and customers)
INSERT INTO users (id, email, password, role) VALUES
                                                     ('123e4567-e89b-12d3-a456-426614174000', 'admin', '$2a$10$wQb2v/2XHxsfsdksldjsfaXnL0K9RIJYuk9JMGa', 'ADMIN'),  -- Password is 'password'
                                                     ('223e4567-e89b-12d3-a456-426614174001', 'john_doe', '$2a$10$h5T0vCp2v8qlLdBv0i9gNOaP7.KFTB7pG6YQJGXUIbr5MmAxFk8Xa', 'CUSTOMER'),  -- Password is 'customer1'
                                                     ('323e4567-e89b-12d3-a456-426614174002', 'jane_doe', '$2a$10$5qA3KcfvKmZgfpiHYscJNuA4fdI0n4.MZGnnhXbFwRoAObmtTqvnW', 'CUSTOMER');  -- Password is 'customer2'


-- Insert assets for users
INSERT INTO assets (id, customer_id, asset_name, size, usable_size) VALUES
                                                                    ('423e4567-e89b-12d3-a456-426614174003', '223e4567-e89b-12d3-a456-426614174001', 'AAPL', 100, 80),
                                                                    ('523e4567-e89b-12d3-a456-426614174004', '223e4567-e89b-12d3-a456-426614174001', 'GOOG', 50, 50),
                                                                    ('623e4567-e89b-12d3-a456-426614174005', '323e4567-e89b-12d3-a456-426614174002', 'TSLA', 200, 150),
                                                                    ('723e4567-e89b-12d3-a456-426614174006', '323e4567-e89b-12d3-a456-426614174002', 'AMZN', 300, 300);


-- Insert orders for users
INSERT INTO orders (id, customer_id, asset_name, order_side, size, price, status, create_date) VALUES
                                                                                                     ('823e4567-e89b-12d3-a456-426614174007', '223e4567-e89b-12d3-a456-426614174001', 'AAPL', 'BUY', 20, 150.00, 'PENDING', NOW()),  -- John Doe places a BUY order for 20 AAPL shares
                                                                                                     ('923e4567-e89b-12d3-a456-426614174008', '223e4567-e89b-12d3-a456-426614174001', 'GOOG', 'SELL', 10, 2000.00, 'MATCHED', NOW()), -- John Doe places a SELL order for 10 GOOG shares
                                                                                                     ('a23e4567-e89b-12d3-a456-426614174009', '323e4567-e89b-12d3-a456-426614174002', 'TSLA', 'BUY', 50, 700.00, 'CANCELED', NOW()), -- Jane Doe places a BUY order for 50 TSLA shares (canceled)
                                                                                                     ('b23e4567-e89b-12d3-a456-426614174010', '323e4567-e89b-12d3-a456-426614174002', 'AMZN', 'BUY', 100, 3200.00, 'PENDING', NOW()); -- Jane Doe places a BUY order for 100 AMZN shares
