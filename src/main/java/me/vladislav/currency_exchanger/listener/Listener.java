package me.vladislav.currency_exchanger.listener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import me.vladislav.currency_exchanger.dao.CurrencyDataAccessObject;
import me.vladislav.currency_exchanger.dao.ExchangeRatesDataAccessObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@WebListener
public class Listener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String url;
        String username;
        String password;
        ServletContext context = sce.getServletContext();

        try {
            Properties properties = new Properties();
            InputStream is = context.getResourceAsStream("/WEB-INF/db.properties");
            properties.load(is);

            url = properties.getProperty("db.url");
            username = properties.getProperty("db.username");
            password = properties.getProperty("db.password");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        CurrencyDataAccessObject currencyDataAccessObject = new CurrencyDataAccessObject(url, username, password);
        ExchangeRatesDataAccessObject exchangeRatesDataAccessObject = new ExchangeRatesDataAccessObject(url, username, password, currencyDataAccessObject);


        context.setAttribute("currencyDataAccessObject", currencyDataAccessObject);
        context.setAttribute("exchangeRatesDataAccessObject", exchangeRatesDataAccessObject);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContextListener.super.contextDestroyed(sce);
    }
}
