package me.vladislav.currency_exchanger.dao;

import me.vladislav.currency_exchanger.exceptions.*;
import me.vladislav.currency_exchanger.models.Rate;
import me.vladislav.currency_exchanger.utils.DatabaseUtils;
import org.apache.commons.dbcp2.BasicDataSource;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRatesDataAccessObject implements DataAccessObject<Rate> {
    private BasicDataSource dataSource;
    private DatabaseUtils databaseUtils;
    private CurrencyDataAccessObject currencyDataAccessObject;

    public ExchangeRatesDataAccessObject(String url, String username, String password) {
        dataSource = new BasicDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        databaseUtils = new DatabaseUtils(dataSource);
        currencyDataAccessObject = new CurrencyDataAccessObject(url, username, password);
    }


    @Override
    public List<Rate> getList() throws DataAccessException {
        try {
            List<Rate> listOfRates = new ArrayList<>();
            String query = "SELECT * FROM exchangerates;";
            databaseUtils.initializeDriverForJDBC();

            try (Connection connection = databaseUtils.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    int baseCurrencyId = resultSet.getInt("basecurrencyid");
                    int targetCurrencyId = resultSet.getInt("targetcurrencyid");
                    BigDecimal rate = resultSet.getBigDecimal("rate");
                    listOfRates.add(new Rate(id, currencyDataAccessObject.getById(baseCurrencyId), currencyDataAccessObject.getById(targetCurrencyId), rate));
                }
            }
            return listOfRates;
        } catch (SQLException | DriverInitializationException | NoConnectionToDataBaseException |
                 CurrencyNotFoundException e) {
            throw new DataAccessException("Error retrieving currency list", e);
        }
    }

    @Override
    public Rate getByCode(String code) throws DataAccessException, CurrencyNotFoundException {
        return null;
    }

    @Override
    public void add(Rate rate) throws DataAccessException, CurrencyCodeAlreadyExistsException {

    }

    @Override
    public void update() {

    }
}
