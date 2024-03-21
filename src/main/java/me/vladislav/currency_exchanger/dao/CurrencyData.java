package me.vladislav.currency_exchanger.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class CurrencyData implements getData{

    @Override
    public List getList() {
        return null;
    }

    @Override
    public Object getByID() {
        return null;
    }

    @Override
    public void add(Object o) {

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
}
