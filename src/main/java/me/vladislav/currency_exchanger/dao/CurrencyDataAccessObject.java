package me.vladislav.currency_exchanger.dao;

import me.vladislav.currency_exchanger.exceptions.DataAccessException;
import me.vladislav.currency_exchanger.exceptions.DriverInitializationException;
import me.vladislav.currency_exchanger.exceptions.NoConnectionToDataBaseException;
import me.vladislav.currency_exchanger.models.Currency;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDataAccessObject implements DataAccessObject<Currency> {

    public List<Currency> getList() throws DataAccessException {
        try {
            List<Currency> listOfCurrencies = new ArrayList<>();
            initializeDriverForJDBC();
            Connection connection = getConnection();
            String query = "SELECT * FROM currencies;";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query); ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String code = resultSet.getString("code");
                    String fullName = resultSet.getString("fullName");
                    String sign = resultSet.getString("sign");
                    listOfCurrencies.add(new Currency(id, code, fullName, sign));
                }
            }
            return listOfCurrencies;
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving currency list", e);
        }
    }

    @Override
    public Currency getByID(int id) {
        return null;
    }

    @Override
    public void add(Currency currency) {

    }

    @Override
    public void update() {

    }

    private Connection getConnection() throws NoConnectionToDataBaseException {
        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/currency_exchanger", "vladislavmedvedev", "");
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
