package me.vladislav.currency_exchanger.dao;

import me.vladislav.currency_exchanger.exceptions.CurrencyNotFoundException;
import me.vladislav.currency_exchanger.exceptions.DataAccessException;
import me.vladislav.currency_exchanger.exceptions.DriverInitializationException;

import java.util.List;

public interface DataAccessObject<T> {
    public List<T> getList() throws DataAccessException;
    public T getByCode(String code) throws DriverInitializationException, DataAccessException, CurrencyNotFoundException;
    public void add(T t);
    public void update();
}
