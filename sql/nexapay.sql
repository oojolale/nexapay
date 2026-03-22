-- NexaPay SaaS Platform Database Schema
-- Multi-Tenant Financial Operations Platform
-- PostgreSQL

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================
-- Tenant (租户)
-- ============================================
CREATE TABLE IF NOT EXISTS tenant (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID DEFAULT uuid_generate_v4() UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    domain VARCHAR(255),
    status VARCHAR(20) DEFAULT 'ACTIVE' NOT NULL,
    plan VARCHAR(50) DEFAULT 'STANDARD',
    max_users INTEGER DEFAULT 10,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

-- ============================================
-- User (用户)
-- ============================================
-- 创建user表（注意：user是保留字，需要用双引号）
CREATE TABLE IF NOT EXISTS "user" (
                                      id BIGSERIAL PRIMARY KEY,
                                      uuid UUID DEFAULT uuid_generate_v4() UNIQUE NOT NULL,
    tenant_id BIGINT NOT NULL REFERENCES tenant(id),
    username VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'USER',
    status VARCHAR(20) DEFAULT 'ACTIVE',
    totp_secret VARCHAR(255),
    last_login_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0,
    UNIQUE(tenant_id, username),
    UNIQUE(tenant_id, email)
    );

-- ============================================
-- Order (订单 - ERP模块)
-- ============================================
CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID DEFAULT uuid_generate_v4() UNIQUE NOT NULL,
    tenant_id BIGINT NOT NULL REFERENCES tenant(id),
    order_number VARCHAR(100) NOT NULL,
    customer_name VARCHAR(255),
    customer_email VARCHAR(255),
    amount DECIMAL(15, 2) NOT NULL,
    currency VARCHAR(10) DEFAULT 'USD',
    status VARCHAR(50) DEFAULT 'PENDING',
    payment_status VARCHAR(50) DEFAULT 'UNPAID',
    items JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0,
    UNIQUE(tenant_id, order_number)
);

-- ============================================
-- Inventory (库存 - ERP模块)
-- ============================================
CREATE TABLE IF NOT EXISTS inventory (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID DEFAULT uuid_generate_v4() UNIQUE NOT NULL,
    tenant_id BIGINT NOT NULL REFERENCES tenant(id),
    sku VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(100),
    quantity INTEGER DEFAULT 0,
    unit_price DECIMAL(15, 2),
    low_stock_threshold INTEGER DEFAULT 10,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0,
    UNIQUE(tenant_id, sku)
);

-- ============================================
-- Contact (联系人 - CRM模块)
-- ============================================
CREATE TABLE IF NOT EXISTS contact (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID DEFAULT uuid_generate_v4() UNIQUE NOT NULL,
    tenant_id BIGINT NOT NULL REFERENCES tenant(id),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(50),
    company VARCHAR(255),
    position VARCHAR(100),
    pipeline_stage VARCHAR(50) DEFAULT 'LEAD',
    lead_value DECIMAL(15, 2),
    tags JSONB,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

-- ============================================
-- Transaction (交易 - 支付模块)
-- ============================================
CREATE TABLE IF NOT EXISTS transaction (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID DEFAULT uuid_generate_v4() UNIQUE NOT NULL,
    tenant_id BIGINT NOT NULL REFERENCES tenant(id),
    transaction_id VARCHAR(100) NOT NULL,
    order_id BIGINT REFERENCES orders(id),
    amount DECIMAL(15, 2) NOT NULL,
    currency VARCHAR(10) DEFAULT 'USD',
    payment_method VARCHAR(50),
    status VARCHAR(50) DEFAULT 'PENDING',
    risk_score INTEGER DEFAULT 0,
    risk_status VARCHAR(50) DEFAULT 'PASSED',
    metadata JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0,
    UNIQUE(tenant_id, transaction_id)
);

-- ============================================
-- RiskRule (风控规则 - 风控模块)
-- ============================================
CREATE TABLE IF NOT EXISTS risk_rule (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID DEFAULT uuid_generate_v4() UNIQUE NOT NULL,
    tenant_id BIGINT REFERENCES tenant(id),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    rule_type VARCHAR(50) NOT NULL,
    conditions JSONB NOT NULL,
    action VARCHAR(50) DEFAULT 'REVIEW',
    priority INTEGER DEFAULT 0,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

-- ============================================
-- ScheduledTask (调度任务)
-- ============================================
CREATE TABLE IF NOT EXISTS scheduled_task (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID DEFAULT uuid_generate_v4() UNIQUE NOT NULL,
    tenant_id BIGINT NOT NULL REFERENCES tenant(id),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    task_type VARCHAR(50) NOT NULL,
    cron_expression VARCHAR(100),
    payload JSONB,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    last_run_at TIMESTAMP,
    next_run_at TIMESTAMP,
    run_count BIGINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

-- ============================================
-- ApiKey (API密钥 - Settings模块)
-- ============================================
-- 修复api_key表，user是保留字需要用双引号
CREATE TABLE IF NOT EXISTS api_key (
                                       id BIGSERIAL PRIMARY KEY,
                                       uuid UUID DEFAULT uuid_generate_v4() UNIQUE NOT NULL,
                                       tenant_id BIGINT NOT NULL REFERENCES tenant(id),
                                       user_id BIGINT NOT NULL REFERENCES "user"(id),
                                       name VARCHAR(255) NOT NULL,
                                       key_hash VARCHAR(255) NOT NULL,
                                       permissions JSONB,
                                       last_used_at TIMESTAMP,
                                       expires_at TIMESTAMP,
                                       status VARCHAR(20) DEFAULT 'ACTIVE',
                                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       deleted INTEGER DEFAULT 0
);


-- ============================================
-- AuditLog (审计日志)
-- ============================================
CREATE TABLE IF NOT EXISTS audit_log (
                                         id BIGSERIAL PRIMARY KEY,
                                         uuid UUID DEFAULT uuid_generate_v4() UNIQUE NOT NULL,
                                         tenant_id BIGINT REFERENCES tenant(id),
                                         user_id BIGINT REFERENCES "user"(id),
                                         action VARCHAR(100) NOT NULL,
                                         resource_type VARCHAR(100),
                                         resource_id BIGINT,
                                         details JSONB,
                                         ip_address VARCHAR(50),
                                         user_agent TEXT,
                                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- ============================================
-- Indexes
-- ============================================
CREATE INDEX IF NOT EXISTS idx_user_tenant ON "user"(tenant_id);
CREATE INDEX IF NOT EXISTS idx_user_email ON "user"(email);
CREATE INDEX IF NOT EXISTS idx_orders_tenant ON orders(tenant_id);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);
CREATE INDEX IF NOT EXISTS idx_inventory_tenant ON inventory(tenant_id);
CREATE INDEX IF NOT EXISTS idx_contact_tenant ON contact(tenant_id);
CREATE INDEX IF NOT EXISTS idx_contact_stage ON contact(pipeline_stage);
CREATE INDEX IF NOT EXISTS idx_transaction_tenant ON transaction(tenant_id);
CREATE INDEX IF NOT EXISTS idx_transaction_status ON transaction(status);
CREATE INDEX IF NOT EXISTS idx_risk_rule_tenant ON risk_rule(tenant_id);
CREATE INDEX IF NOT EXISTS idx_scheduled_task_tenant ON scheduled_task(tenant_id);
CREATE INDEX IF NOT EXISTS idx_audit_log_tenant ON audit_log(tenant_id);
CREATE INDEX IF NOT EXISTS idx_audit_log_created ON audit_log(created_at DESC);

-- ============================================
-- Initial Data
-- ============================================
-- Default tenant
INSERT INTO tenant (id, uuid, name, domain, status, plan, max_users) VALUES
(1, '550e8400-e29b-41d4-a716-446655440001', 'NexaPay Platform', 'nexapay.com', 'ACTIVE', 'ENTERPRISE', 1000)
ON CONFLICT (id) DO NOTHING;

-- Admin user (password: admin123)
INSERT INTO "user" (id, uuid, tenant_id, username, email, password_hash, role, status) VALUES
(1, '550e8400-e29b-41d4-a716-446655440002', 1, 'admin', 'admin@nexapay.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN', 'ACTIVE')
ON CONFLICT DO NOTHING;

-- Sample tenant
INSERT INTO tenant (id, uuid, name, domain, status, plan, max_users) VALUES
(2, '550e8400-e29b-41d4-a716-446655440003', 'Demo Corp', 'demo.nexapay.com', 'ACTIVE', 'STANDARD', 50)
ON CONFLICT (id) DO NOTHING;

-- Sample users for tenant 2
INSERT INTO "user" (id, uuid, tenant_id, username, email, password_hash, role, status) VALUES
(2, '550e8400-e29b-41d4-a716-446655440004', 2, 'demo_admin', 'demo@company.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'TENANT_ADMIN', 'ACTIVE'),
(3, '550e8400-e29b-41d4-a716-446655440005', 2, 'operator', 'operator@company.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'OPERATOR', 'ACTIVE')
ON CONFLICT DO NOTHING;

-- Sample orders
INSERT INTO orders (tenant_id, order_number, customer_name, customer_email, amount, status, payment_status) VALUES
(2, 'ORD-2026-001', 'John Smith', 'john@example.com', 1500.00, 'COMPLETED', 'PAID'),
(2, 'ORD-2026-002', 'Alice Johnson', 'alice@example.com', 2800.50, 'PROCESSING', 'PAID'),
(2, 'ORD-2026-003', 'Bob Wilson', 'bob@example.com', 750.00, 'PENDING', 'UNPAID'),
(2, 'ORD-2026-004', 'Carol Davis', 'carol@example.com', 4200.00, 'COMPLETED', 'PAID'),
(2, 'ORD-2026-005', 'David Brown', 'david@example.com', 950.25, 'CANCELLED', 'REFUNDED')
ON CONFLICT DO NOTHING;

-- Sample inventory
INSERT INTO inventory (tenant_id, sku, name, category, quantity, unit_price, low_stock_threshold) VALUES
(2, 'PROD-001', 'Enterprise License', 'Software', 100, 499.99, 10),
(2, 'PROD-002', 'Professional License', 'Software', 250, 299.99, 20),
(2, 'PROD-003', 'Standard License', 'Software', 500, 149.99, 50),
(2, 'PROD-004', 'Support Package - Basic', 'Service', 50, 99.99, 5),
(2, 'PROD-005', 'Support Package - Premium', 'Service', 30, 299.99, 3)
ON CONFLICT DO NOTHING;

-- Sample contacts
INSERT INTO contact (tenant_id, name, email, phone, company, position, pipeline_stage, lead_value) VALUES
(2, 'Michael Scott', 'michael@scott.com', '+1-555-0100', 'Dunder Mifflin', 'Regional Manager', 'CUSTOMER', 50000.00),
(2, 'Jim Halpert', 'jim@scott.com', '+1-555-0101', 'Dunder Mifflin', 'Sales Rep', 'NEGOTIATION', 25000.00),
(2, 'Pam Beesly', 'pam@scott.com', '+1-555-0102', 'Dunder Mifflin', 'Office Admin', 'PROPOSAL', 15000.00),
(2, 'Dwight Schrute', 'dwight@schrute.com', '+1-555-0103', 'Schrute Farms', 'Sales Lead', 'QUALIFIED', 35000.00),
(2, 'Ryan Howard', 'ryan@temp.com', '+1-555-0104', 'Temp', 'Temp', 'LEAD', 5000.00)
ON CONFLICT DO NOTHING;

-- Sample transactions
INSERT INTO transaction (tenant_id, transaction_id, order_id, amount, currency, payment_method, status, risk_score, risk_status) VALUES
(2, 'TXN-2026-001', 1, 1500.00, 'USD', 'CREDIT_CARD', 'COMPLETED', 10, 'PASSED'),
(2, 'TXN-2026-002', 2, 2800.50, 'USD', 'BANK_TRANSFER', 'COMPLETED', 25, 'PASSED'),
(2, 'TXN-2026-003', 4, 4200.00, 'USD', 'CREDIT_CARD', 'COMPLETED', 85, 'REVIEW'),
(2, 'TXN-2026-004', NULL, 999.00, 'USD', 'CRYPTO', 'PENDING', 95, 'BLOCKED')
ON CONFLICT DO NOTHING;

-- Sample risk rules
INSERT INTO risk_rule (tenant_id, name, description, rule_type, conditions, action, priority) VALUES
(2, 'High Amount Transaction', 'Flag transactions over $5000', 'AMOUNT', '{"operator": ">", "threshold": 5000}', 'REVIEW', 10),
(2, 'High Risk Country', 'Block transactions from high-risk countries', 'COUNTRY', '{"countries": ["XX", "YY"]}', 'BLOCK', 20),
(2, 'Velocity Check', 'Block more than 5 transactions per hour', 'VELOCITY', '{"maxTransactions": 5, "windowMinutes": 60}', 'REVIEW', 15),
(2, 'New Customer Large Order', 'Flag orders > $2000 for new customers', 'CUSTOMER_AGE', '{"minAge": 0, "maxAmount": 2000}', 'REVIEW', 12)
ON CONFLICT DO NOTHING;

-- Sample scheduled tasks
INSERT INTO scheduled_task (tenant_id, name, description, task_type, cron_expression, status, next_run_at) VALUES
(2, 'Daily Report Generation', 'Generate daily sales reports', 'REPORT', '0 0 6 * * ?', 'ACTIVE', CURRENT_TIMESTAMP + INTERVAL '1 day'),
(2, 'Hourly Sync', 'Sync inventory with external system', 'SYNC', '0 0 * * * ?', 'ACTIVE', CURRENT_TIMESTAMP + INTERVAL '1 hour'),
(2, 'Risk Score Update', 'Update transaction risk scores', 'RISK_CALC', '0 */30 * * * ?', 'ACTIVE', CURRENT_TIMESTAMP + INTERVAL '30 minutes')
ON CONFLICT DO NOTHING;
