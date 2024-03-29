package me.vladislav.currency_exchanger.utils;

import me.vladislav.currency_exchanger.exceptions.DriverInitializationException;
import me.vladislav.currency_exchanger.exceptions.NoConnectionToDataBaseException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseUtils {
    private DataSource dataSource;

    public DatabaseUtils(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Connection getConnection() throws NoConnectionToDataBaseException {
        try {
            Connection connection = dataSource.getConnection();
            return connection;
        } catch (SQLException e) {
            throw new NoConnectionToDataBaseException("Connection to the database could not be established", e);
        }
    }

    public void initializeDriverForJDBC() throws DriverInitializationException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new DriverInitializationException("Failed to initialize JDBC driver", e);
        }
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}
