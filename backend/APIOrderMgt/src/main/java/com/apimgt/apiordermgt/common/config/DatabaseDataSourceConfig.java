package com.apimgt.apiordermgt.common.config;

import com.apimgt.apiordermgt.common.util.DatabaseFileUtils;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class DatabaseDataSourceConfig {

    private final DatabaseFileUtils databaseFileUtils;

    @Bean
    public DataSource dataSource() {
        databaseFileUtils.prepareDatabaseFile();
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(
                "jdbc:sqlite:" + databaseFileUtils.resolveDatabasePath()
        );
        dataSource.setDriverClassName("org.sqlite.JDBC");
        dataSource.setMaximumPoolSize(1);
        dataSource.setMinimumIdle(1);
        dataSource.setConnectionTimeout(5000);
        return dataSource;
    }

}
