package me.vladislav.currency_exchanger.dao;

import me.vladislav.currency_exchanger.exceptions.CurrencyNotFoundException;
import me.vladislav.currency_exchanger.exceptions.DataAccessException;
import me.vladislav.currency_exchanger.exceptions.DriverInitializationException;
import me.vladislav.currency_exchanger.exceptions.NoConnectionToDataBaseException;
import me.vladislav.currency_exchanger.models.Currency;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDataAccessObject implements DataAccessObject<Currency> {
    private BasicDataSource dataSource;

    public CurrencyDataAccessObject(String url, String username, String password) {
        dataSource = new BasicDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
    }

    public List<Currency> getList() throws DataAccessException {
        try {
            List<Currency> listOfCurrencies = new ArrayList<>();
            String query = "SELECT * FROM currencies;";
            initializeDriverForJDBC();

            try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String code = resultSet.getString("code");
                    String fullName = resultSet.getString("fullName");
                    String sign = resultSet.getString("sign");
                    listOfCurrencies.add(new Currency(id, code, fullName, sign));
                }

            }
            return listOfCurrencies;
        } catch (SQLException | DriverInitializationException | NoConnectionToDataBaseException e) {
            throw new DataAccessException("Error retrieving currency list", e);
        }
    }

    @Override
    public Currency getByCode(String code) throws DataAccessException, CurrencyNotFoundException {
        try {
            Currency result;
            initializeDriverForJDBC();
            String query = "SELECT * FROM currencies WHERE code = '" + code + "';";

            try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                if(resultSet.next()) {
                    int id = resultSet.getInt("id");
                    resultSet.getString("code");
                    String fullName = resultSet.getString("fullName");
                    String sign = resultSet.getString("sign");
                    result = new Currency(id, code, fullName, sign);
                } else {
                    throw new CurrencyNotFoundException("Currency with code '" + code + "' not found");
                }
                return result;
            }

        } catch (SQLException | DriverInitializationException | NoConnectionToDataBaseException e) {
            throw new DataAccessException("Error retrieving currency", e);
        }
    }

    @Override
    public void add(Currency currency) {

    }

    @Override
    public void update() {

    }

    private Connection getConnection() throws NoConnectionToDataBaseException {
        try {
            Connection connection = dataSource.getConnection();
            return connection;
        } catch (SQLException e) {
            throw new NoConnectionToDataBaseException("Connection to the database could not be established", e);
        }
    }

    private void initializeDriverForJDBC() throws DriverInitializationException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new DriverInitializationException("Failed to initialize JDBC driver", e);
        }
    }
}
