package me.vladislav.currency_exchanger.dao;

import me.vladislav.currency_exchanger.exceptions.CurrencyCodeAlreadyExistsException;
import me.vladislav.currency_exchanger.exceptions.CurrencyNotFoundException;
import me.vladislav.currency_exchanger.exceptions.DataAccessException;
import me.vladislav.currency_exchanger.exceptions.DriverInitializationException;

import java.util.List;

public interface DataAccessObject<T> {
    public List<T> getList() throws DataAccessException;
    public T getByCode(String code) throws DataAccessException, CurrencyNotFoundException;
    public void add(String fullname, String code, String sign) throws DataAccessException, CurrencyCodeAlreadyExistsException;
    public void update();
}
