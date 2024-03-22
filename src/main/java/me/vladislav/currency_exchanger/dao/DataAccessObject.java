package me.vladislav.currency_exchanger.dao;

import java.sql.SQLException;
import java.util.List;

public interface DataAccessObject<T> {
    public List<T> getList() throws SQLException;
    public T getByID(int id);
    public void add(T t);
    public void update();
}
