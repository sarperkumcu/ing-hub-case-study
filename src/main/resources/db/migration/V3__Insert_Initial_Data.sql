INSERT INTO asset (id, customer_id, asset_name, size, usable_size) VALUES
                                                                       (UUID(), UUID(), 'TRY', 10000.00, 10000.00),
                                                                       (UUID(), UUID(), 'AAPL', 50.00, 50.00);

INSERT INTO orders (order_id, customer_id, asset_name, order_side, size, price, status) VALUES
    (UUID(), UUID(), 'AAPL', 'BUY', 10, 150.00, 'PENDING');
