package org.goafabric.personservice.persistence.extensions;

import org.goafabric.personservice.extensions.UserContext;
import org.springframework.jdbc.datasource.DelegatingDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SchemaAwareDataSource extends DelegatingDataSource {

    public SchemaAwareDataSource(DataSource targetDataSource) {
        super(targetDataSource);
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = super.getConnection();
        setSchema(connection);
        return connection;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        Connection connection = super.getConnection(username, password);
        setSchema(connection);
        return connection;
    }

    private void setSchema(Connection connection) throws SQLException {
        String tenant = UserContext.getTenantId();
        if (tenant != null) {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("SET search_path TO " + tenant);
            }
        }
    }


}