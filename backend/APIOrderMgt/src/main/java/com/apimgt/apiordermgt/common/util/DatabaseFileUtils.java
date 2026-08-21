package com.apimgt.apiordermgt.common.util;

import com.apimgt.apiordermgt.common.config.DatabaseProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class DatabaseFileUtils {

    private final DatabaseProperties databaseProperties;

    public void prepareDatabaseFile() {
        Path databasePath = resolveDatabasePath();
        Path parentPath = databasePath.getParent();

        try {
            if (parentPath != null) {
                Files.createDirectories(parentPath);
            }
            if (Files.notExists(databasePath)) {
                Files.createFile(databasePath);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("无法创建 SQLite 数据库文件", exception);
        }
    }

    public Path resolveDatabasePath() {
        return Path.of(databaseProperties.getFilePath())
                .toAbsolutePath()
                .normalize();
    }

}
