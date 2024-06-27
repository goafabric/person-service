package org.goafabric.personservice.persistence.extensions;


import org.goafabric.personservice.extensions.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.AbstractDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Configuration
public class TenantAwareDatasourceConfiguration {
    @Autowired
    private DataSourceProperties properties;

    @Bean
    public DataSource dataSource() {
        final DataSource delegate = DataSourceBuilder.create()
                .driverClassName(properties.getDriverClassName())
                .url(properties.getUrl())
                .username(properties.getUsername())
                .password(properties.getPassword())
                .build();

        return new AbstractDataSource() {
            @Override
            public Connection getConnection() throws SQLException {
                Connection connection = delegate.getConnection();
                setTenantId(connection);
                return connection;
            }

            @Override
            public Connection getConnection(String username, String password) throws SQLException {
                Connection connection = delegate.getConnection(username, password);
                setTenantId(connection);
                return connection;
            }

            private void setTenantId(Connection connection) throws SQLException {
                if (properties.getUrl().contains("jdbc:postgresql")) { //needs to be checked for H2 embedded Database
                    try (Statement statement = connection.createStatement()) {
                        String sql = "SET app.tenant_id = '" + TenantContext.getTenantId() + "'";
                        statement.execute(sql);
                    }
                }
            }
        };
    }
}
