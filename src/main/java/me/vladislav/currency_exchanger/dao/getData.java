package me.vladislav.currency_exchanger.dao;

import java.util.List;

public interface getData<T> {
    public List<T> getList();
    public T getByID();
    public void add(T t);
    public void update();
}
