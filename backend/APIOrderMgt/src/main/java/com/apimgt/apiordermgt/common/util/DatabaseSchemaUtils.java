package com.apimgt.apiordermgt.common.util;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DatabaseSchemaUtils {

    private final JdbcTemplate jdbcTemplate;

    public void initializeSchema() {
        jdbcTemplate.execute("PRAGMA foreign_keys = ON");
        jdbcTemplate.execute("PRAGMA busy_timeout = 5000");
        jdbcTemplate.execute("PRAGMA journal_mode = WAL");
        createProviderTable();
        createInvoiceTitleTable();
        createOrderTable();
        createInvoiceBatchTables();
        createIndexes();
    }

    private void createProviderTable() {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS api_provider (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL UNIQUE,
                    website_url TEXT NOT NULL,
                    created_at TEXT NOT NULL,
                    updated_at TEXT NOT NULL,
                    version INTEGER NOT NULL DEFAULT 0
                )
                """);
    }

    private void createInvoiceTitleTable() {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS invoice_title (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title_type TEXT NOT NULL,
                    name TEXT NOT NULL,
                    tax_code TEXT,
                    created_at TEXT NOT NULL,
                    updated_at TEXT NOT NULL,
                    version INTEGER NOT NULL DEFAULT 0,
                    UNIQUE(title_type, name)
                )
                """);
    }

    private void createOrderTable() {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS api_order (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    order_no TEXT NOT NULL UNIQUE,
                    provider_id INTEGER NOT NULL,
                    provider_name_snapshot TEXT NOT NULL,
                    provider_website_url_snapshot TEXT NOT NULL,
                    amount_cent INTEGER NOT NULL CHECK(amount_cent > 0),
                    payment_method TEXT NOT NULL,
                    invoice_status TEXT NOT NULL,
                    invoice_date TEXT,
                    invoice_no TEXT,
                    invoice_title_id INTEGER,
                    invoice_title_name_snapshot TEXT,
                    invoice_title_type_snapshot TEXT,
                    invoice_tax_code_snapshot TEXT,
                    deleted_at TEXT,
                    created_at TEXT NOT NULL,
                    updated_at TEXT NOT NULL,
                    version INTEGER NOT NULL DEFAULT 0
                )
                """);
    }

    private void createIndexes() {
        jdbcTemplate.execute("""
                CREATE UNIQUE INDEX IF NOT EXISTS uk_invoice_title_tax_code
                ON invoice_title(tax_code)
                WHERE tax_code IS NOT NULL AND tax_code <> ''
                """);
        jdbcTemplate.execute("""
                CREATE INDEX IF NOT EXISTS idx_api_order_active_created
                ON api_order(deleted_at, created_at DESC)
                """);
        jdbcTemplate.execute("""
                CREATE INDEX IF NOT EXISTS idx_api_order_provider
                ON api_order(provider_id, deleted_at)
                """);
        jdbcTemplate.execute("""
                CREATE INDEX IF NOT EXISTS idx_api_order_invoice_status
                ON api_order(invoice_status, deleted_at)
                """);
        jdbcTemplate.execute("""
                CREATE INDEX IF NOT EXISTS idx_invoice_batch_order_order
                ON invoice_batch_order(order_id)
                """);
        jdbcTemplate.execute("""
                CREATE INDEX IF NOT EXISTS idx_invoice_batch_status
                ON invoice_batch(status, created_at DESC)
                """);
    }

    private void createInvoiceBatchTables() {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS invoice_batch (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    invoice_date TEXT NOT NULL,
                    invoice_no TEXT NOT NULL UNIQUE,
                    invoice_title_id INTEGER NOT NULL,
                    invoice_title_name_snapshot TEXT NOT NULL,
                    invoice_title_type_snapshot TEXT NOT NULL,
                    invoice_tax_code_snapshot TEXT,
                    total_amount_cent INTEGER NOT NULL CHECK(total_amount_cent > 0),
                    status TEXT NOT NULL,
                    replaced_from_id INTEGER,
                    created_at TEXT NOT NULL,
                    updated_at TEXT NOT NULL,
                    version INTEGER NOT NULL DEFAULT 0
                )
                """);
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS invoice_batch_order (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    batch_id INTEGER NOT NULL,
                    order_id INTEGER NOT NULL,
                    amount_cent INTEGER NOT NULL CHECK(amount_cent > 0),
                    UNIQUE(batch_id, order_id),
                    FOREIGN KEY(batch_id) REFERENCES invoice_batch(id),
                    FOREIGN KEY(order_id) REFERENCES api_order(id)
                )
                """);
    }

}
