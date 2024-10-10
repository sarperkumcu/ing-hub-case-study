CREATE TABLE orders (
                        order_id UUID PRIMARY KEY,
                        customer_id UUID NOT NULL,
                        asset_name VARCHAR(255) NOT NULL,
                        order_side VARCHAR(10) NOT NULL,
                        size INT NOT NULL,
                        price DECIMAL(15, 2) NOT NULL,
                        status VARCHAR(20) NOT NULL,
                        create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
