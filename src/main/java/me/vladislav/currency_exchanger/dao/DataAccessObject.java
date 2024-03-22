package me.vladislav.currency_exchanger.dao;

import me.vladislav.currency_exchanger.exceptions.DataAccessException;

import java.util.List;

public interface DataAccessObject<T> {
    public List<T> getList() throws DataAccessException;
    public T getByID(int id);
    public void add(T t);
    public void update();
}
