package com.dsw.practica02.config;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.hibernate.cfg.JdbcSettings;
import org.hibernate.cfg.SchemaToolingSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class DatabaseFallbackConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseFallbackConfig.class);

    private static final String DEFAULT_POSTGRES_URL = "jdbc:postgresql://localhost:5432/dswdb?sslmode=disable";
    private static final String FALLBACK_H2_URL = "jdbc:h2:mem:dswdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE";

    @Bean
    public DataSourceConfiguration dataSourceConfiguration(Environment environment) {
        HikariDataSource productionDataSource = buildProductionDataSource(environment);

        if (canConnect(productionDataSource)) {
            log.info("Using PostgreSQL datasource at {}", productionDataSource.getJdbcUrl());
            return new DataSourceConfiguration(productionDataSource, false);
        }

        closeQuietly(productionDataSource);
        HikariDataSource fallbackDataSource = buildFallbackDataSource();
        log.warn("PostgreSQL datasource unavailable. Using temporary in-memory H2 datasource instead.");
        return new DataSourceConfiguration(fallbackDataSource, true);
    }

    @Bean
    public DataSource dataSource(DataSourceConfiguration configuration) {
        return configuration.dataSource();
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(DataSourceConfiguration configuration) {
        return properties -> {
            if (configuration.temporary()) {
                properties.put(JdbcSettings.DIALECT, "org.hibernate.dialect.H2Dialect");
                properties.put(SchemaToolingSettings.HBM2DDL_AUTO, "create-drop");
            }
        };
    }

    private HikariDataSource buildProductionDataSource(Environment environment) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(environment.getProperty("DB_URL", DEFAULT_POSTGRES_URL));
        dataSource.setUsername(environment.getProperty("DB_USERNAME", "postgres"));
        dataSource.setPassword(environment.getProperty("DB_PASSWORD", "postgres"));
        dataSource.setDriverClassName(environment.getProperty("DB_DRIVER_CLASS_NAME", "org.postgresql.Driver"));
        dataSource.setPoolName("practica02-postgres");
        dataSource.setMaximumPoolSize(5);
        dataSource.setMinimumIdle(1);
        dataSource.setConnectionTimeout(3000);
        return dataSource;
    }

    private HikariDataSource buildFallbackDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(FALLBACK_H2_URL);
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setPoolName("practica02-temporary-h2");
        dataSource.setMaximumPoolSize(2);
        dataSource.setMinimumIdle(1);
        dataSource.setConnectionTimeout(3000);
        return dataSource;
    }

    private boolean canConnect(HikariDataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            return true;
        } catch (SQLException ex) {
            log.warn("Unable to connect to configured database: {}", ex.getMessage());
            return false;
        }
    }

    private void closeQuietly(HikariDataSource dataSource) {
        try {
            dataSource.close();
        } catch (Exception ex) {
            log.debug("Ignored datasource close error", ex);
        }
    }

    public record DataSourceConfiguration(DataSource dataSource, boolean temporary) {
    }
}