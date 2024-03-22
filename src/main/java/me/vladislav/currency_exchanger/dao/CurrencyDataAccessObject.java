package me.vladislav.currency_exchanger.dao;

import me.vladislav.currency_exchanger.model.Currency;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDataAccessObject implements DataAccessObject<Currency> {

    public List<Currency> getList() {
        try {
            List<Currency> listOfCurrencies = new ArrayList<>();
            initializeDriverForJDBC();
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM currencies;");
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                int id = resultSet.getInt("id");
                String code = resultSet.getString("code");
                String fullName = resultSet.getString("fullName");
                String sign = resultSet.getString("sign");
                listOfCurrencies.add(new Currency(id, code, fullName, sign));
            }
            return listOfCurrencies;
        } catch (SQLException e){
            throw new RuntimeException(e);
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

    private Connection getConnection(){
        try{
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/currency_exchanger",
                    "vladislavmedvedev", "");
            return connection;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    private void initializeDriverForJDBC() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e){
            throw new RuntimeException(e);
        }
    }
}
