CREATE TABLE users (
    id              BIGSERIAL PRIMARY KEY,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    full_name       VARCHAR(255) NOT NULL,
    role            VARCHAR(32)  NOT NULL,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE products (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    brand           VARCHAR(128) NOT NULL,
    description     TEXT         NOT NULL DEFAULT '',
    price_cents     BIGINT       NOT NULL CHECK (price_cents >= 0),
    stock           INT          NOT NULL CHECK (stock >= 0),
    specs_json      TEXT         NOT NULL DEFAULT '{}',
    image_path      VARCHAR(512),
    active          BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_products_brand ON products (brand);
CREATE INDEX idx_products_price ON products (price_cents);
CREATE INDEX idx_products_active ON products (active);

CREATE TABLE cart_items (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    product_id      BIGINT NOT NULL REFERENCES products (id),
    quantity        INT    NOT NULL CHECK (quantity > 0),
    UNIQUE (user_id, product_id)
);

CREATE TABLE orders (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT       NOT NULL REFERENCES users (id),
    status              VARCHAR(32)  NOT NULL,
    total_cents         BIGINT       NOT NULL CHECK (total_cents >= 0),
    contact_name        VARCHAR(255) NOT NULL,
    contact_phone       VARCHAR(64)  NOT NULL,
    delivery_method     VARCHAR(32)  NOT NULL,
    delivery_address    VARCHAR(512),
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE order_items (
    id              BIGSERIAL PRIMARY KEY,
    order_id        BIGINT       NOT NULL REFERENCES orders (id) ON DELETE CASCADE,
    product_id      BIGINT       NOT NULL REFERENCES products (id),
    product_name    VARCHAR(255) NOT NULL,
    unit_price_cents BIGINT      NOT NULL CHECK (unit_price_cents >= 0),
    quantity        INT          NOT NULL CHECK (quantity > 0)
);

CREATE TABLE payments (
    id                  BIGSERIAL PRIMARY KEY,
    order_id            BIGINT       NOT NULL UNIQUE REFERENCES orders (id),
    provider            VARCHAR(32)  NOT NULL,
    provider_payment_id VARCHAR(128),
    status              VARCHAR(32)  NOT NULL,
    amount_cents        BIGINT       NOT NULL CHECK (amount_cents >= 0),
    confirmation_url    VARCHAR(1024),
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_payments_provider_payment_id ON payments (provider_payment_id);
