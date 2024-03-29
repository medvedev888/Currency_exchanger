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
    public Rate getByCode(String codes) throws DataAccessException, CurrencyNotFoundException, ExchangeRateNotFoundException {
        try {
            String code1 = codes.substring(0, 3);
            String code2 = codes.substring(3);
            Rate result = null;
            databaseUtils.initializeDriverForJDBC();
            String query = "SELECT * FROM exchangerates e\n" +
                    "    INNER JOIN currencies c1 on e.basecurrencyid = c1.id\n" +
                    "    INNER JOIN currencies c2 on c2.id = e.targetcurrencyid\n" +
                    "WHERE c1.code = ? AND c2.code = ?;";

            try (Connection connection = databaseUtils.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, code1);
                preparedStatement.setString(2, code2);
                try(ResultSet resultSet = preparedStatement.executeQuery()){
                    if(resultSet.next()) {
                        int id = resultSet.getInt("id");
                        int baseCurrencyId = resultSet.getInt("basecurrencyid");
                        int targetCurrencyId = resultSet.getInt("targetcurrencyid");
                        BigDecimal rate = resultSet.getBigDecimal("rate");
                        result = new Rate(id, currencyDataAccessObject.getByCode(code1), currencyDataAccessObject.getByCode(code2), rate);
                    } else {
                        throw new ExchangeRateNotFoundException("Exchange rate " + code1 + " to " + code2 + " not found");
                    }
                }
                return result;
            }
        } catch (SQLException | DriverInitializationException | NoConnectionToDataBaseException e) {
            throw new DataAccessException("Error retrieving currency", e);
        }
    }

    @Override
    public void add(Rate rate) throws DataAccessException, CurrencyCodeAlreadyExistsException {

    }

    @Override
    public void update() {

    }
}
