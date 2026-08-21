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
                    invoice_no TEXT UNIQUE,
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
    }

}
