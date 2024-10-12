CREATE TABLE assets (
                       id UUID PRIMARY KEY,
                       customer_id UUID NOT NULL,
                       asset_name VARCHAR(255) NOT NULL,
                       size DECIMAL(15, 2) NOT NULL,
                       usable_size DECIMAL(15, 2) NOT NULL,
                        CONSTRAINT fk_assetss_user FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE CASCADE

);
