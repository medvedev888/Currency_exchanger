package me.vladislav.currency_exchanger.dao;

import me.vladislav.currency_exchanger.exceptions.*;
import me.vladislav.currency_exchanger.models.Currency;
import me.vladislav.currency_exchanger.utils.DatabaseUtils;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDataAccessObject implements DataAccessObject<Currency> {
    private BasicDataSource dataSource;
    private DatabaseUtils databaseUtils;

    public CurrencyDataAccessObject(String url, String username, String password) {
        dataSource = new BasicDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        databaseUtils = new DatabaseUtils(dataSource);
    }

    @Override
    public List<Currency> getList() throws DataAccessException {
        try {
            List<Currency> listOfCurrencies = new ArrayList<>();
            String query = "SELECT * FROM currencies;";
            databaseUtils.initializeDriverForJDBC();

            try (Connection connection = databaseUtils.getConnection();
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

    public Currency getById(int id) throws DataAccessException, CurrencyNotFoundException {
        try {
            Currency result;
            databaseUtils.initializeDriverForJDBC();
            String query = "SELECT * FROM currencies WHERE id = ?;";

            try (Connection connection = databaseUtils.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, id);
                try(ResultSet resultSet = preparedStatement.executeQuery()) {
                    if(resultSet.next()) {
                        String code = resultSet.getString("code");
                        String fullName = resultSet.getString("fullName");
                        String sign = resultSet.getString("sign");
                        result = new Currency(id, code, fullName, sign);
                    } else {
                        throw new CurrencyNotFoundException("Currency with id '" + id + "' not found");
                    }
                }
                return result;
            }

        } catch (SQLException | DriverInitializationException | NoConnectionToDataBaseException e) {
            throw new DataAccessException("Error retrieving currency", e);
        }
    }

    @Override
    public Currency getByCode(String code) throws DataAccessException, CurrencyNotFoundException {
        try {
            Currency result;
            databaseUtils.initializeDriverForJDBC();
            String query = "SELECT * FROM currencies WHERE code = ?;";

            try (Connection connection = databaseUtils.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, code);
                try(ResultSet resultSet = preparedStatement.executeQuery()) {
                    if(resultSet.next()) {
                        int id = resultSet.getInt("id");
                        resultSet.getString("code");
                        String fullName = resultSet.getString("fullName");
                        String sign = resultSet.getString("sign");
                        result = new Currency(id, code, fullName, sign);
                    } else {
                        throw new CurrencyNotFoundException("Currency with code '" + code + "' not found");
                    }
                }
                return result;
            }

        } catch (SQLException | DriverInitializationException | NoConnectionToDataBaseException e) {
            throw new DataAccessException("Error retrieving currency", e);
        }
    }

    @Override
    public void add(Currency currency) throws DataAccessException, CurrencyCodeAlreadyExistsException {
        try {
            String code = currency.getCode();
            String fullname = currency.getFullName();
            String sign = currency.getSign();

            databaseUtils.initializeDriverForJDBC();
            String query = "SELECT * FROM currencies WHERE code = ?;";
            String requestToAddAnElement = "INSERT INTO currencies (code, fullname, sign) VALUES (?, ?, ?)";

            try (Connection connection = databaseUtils.getConnection();
                 PreparedStatement preparedStatement1 = connection.prepareStatement(query)) {
                preparedStatement1.setString(1, code);
                try(ResultSet resultSet1 = preparedStatement1.executeQuery()){
                    if(!resultSet1.next()) {
                        try(PreparedStatement preparedStatement2 = connection.prepareStatement(requestToAddAnElement)){
                            preparedStatement2.setString(1, code);
                            preparedStatement2.setString(2, fullname);
                            preparedStatement2.setString(3, sign);
                            preparedStatement2.executeUpdate();
                        }
                    } else {
                        throw new CurrencyCodeAlreadyExistsException("A currency with code " + code + " already exists");
                    }
                }
            }
        } catch (SQLException | DriverInitializationException | NoConnectionToDataBaseException e) {
            throw new DataAccessException("Error retrieving currency", e);
        }
    }

    @Override
    public void update() {

    }

}
