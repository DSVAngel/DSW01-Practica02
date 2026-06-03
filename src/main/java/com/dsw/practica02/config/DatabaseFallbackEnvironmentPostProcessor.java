package com.dsw.practica02.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

public class DatabaseFallbackEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final Logger log = LoggerFactory.getLogger(DatabaseFallbackEnvironmentPostProcessor.class);
    private static final String FALLBACK_SOURCE_NAME = "temporaryDatabaseFallback";
    private static final String DEFAULT_POSTGRES_URL = "jdbc:postgresql://localhost:5432/dswdb?sslmode=disable";
    private static final String FALLBACK_H2_URL = "jdbc:h2:mem:dswdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String jdbcUrl = resolveJdbcUrl(environment);
        String username = resolve(environment, "DB_USERNAME", "postgres");
        String password = resolve(environment, "DB_PASSWORD", "postgres");
        String driverClassName = resolve(environment, "DB_DRIVER_CLASS_NAME", "org.postgresql.Driver");

        if (canConnect(jdbcUrl, username, password, driverClassName)) {
            log.info("Using configured datasource at {}", jdbcUrl);
            return;
        }

        Map<String, Object> fallbackProperties = new HashMap<>();
        fallbackProperties.put("spring.datasource.url", FALLBACK_H2_URL);
        fallbackProperties.put("spring.datasource.username", "sa");
        fallbackProperties.put("spring.datasource.password", "");
        fallbackProperties.put("spring.datasource.driver-class-name", "org.h2.Driver");
        fallbackProperties.put("spring.jpa.database-platform", "org.hibernate.dialect.H2Dialect");
        fallbackProperties.put("spring.jpa.hibernate.ddl-auto", "create-drop");

        environment.getPropertySources().addFirst(new MapPropertySource(FALLBACK_SOURCE_NAME, fallbackProperties));
        log.warn("Configured datasource is unavailable. Using temporary in-memory H2 datasource instead.");
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    private String resolveJdbcUrl(ConfigurableEnvironment environment) {
        return resolve(environment, "DB_URL", DEFAULT_POSTGRES_URL);
    }

    private String resolve(ConfigurableEnvironment environment, String key, String defaultValue) {
        String value = environment.getProperty(key);
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private boolean canConnect(String jdbcUrl, String username, String password, String driverClassName) {
        try {
            Class.forName(driverClassName);
            try (Connection ignored = DriverManager.getConnection(jdbcUrl, username, password)) {
                return true;
            }
        } catch (ClassNotFoundException | SQLException ex) {
            log.warn("Unable to connect to configured database: {}", ex.getMessage());
            return false;
        }
    }
}