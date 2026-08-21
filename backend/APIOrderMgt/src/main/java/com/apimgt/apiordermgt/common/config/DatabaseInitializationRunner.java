package com.apimgt.apiordermgt.common.config;

import com.apimgt.apiordermgt.common.util.DatabaseSchemaUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DatabaseInitializationRunner implements ApplicationRunner {

    private final DatabaseSchemaUtils databaseSchemaUtils;

    @Override
    public void run(ApplicationArguments args) {
        databaseSchemaUtils.initializeSchema();
    }

}
