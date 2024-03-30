package me.vladislav.currency_exchanger.dao;

import me.vladislav.currency_exchanger.exceptions.*;

import java.util.List;

public interface DataAccessObject<T> {
    public List<T> getList() throws DataAccessException, CurrencyNotFoundException;
    public T getByCode(String code) throws DataAccessException, CurrencyNotFoundException, ExchangeRateNotFoundException;
    public void add(T t) throws DataAccessException, CurrencyCodeAlreadyExistsException, ExchangeRateAlreadyExistsException;
    public void update();
}
