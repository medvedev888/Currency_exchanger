package me.vladislav.currency_exchanger.dao;

import java.util.List;

public interface DataAccessObject<T> {
    public List<T> getList();
    public T getByID(int id);
    public void add(T t);
    public void update();
}
