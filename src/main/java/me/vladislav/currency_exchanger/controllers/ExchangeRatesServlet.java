package me.vladislav.currency_exchanger.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.vladislav.currency_exchanger.dao.ExchangeRatesDataAccessObject;
import me.vladislav.currency_exchanger.exceptions.DataAccessException;

import java.io.IOException;

@WebServlet(value = "/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private final ExchangeRatesDataAccessObject exchangeRatesDataAccessObject;
    private final ObjectMapper objectMapper;

    public ExchangeRatesServlet() {
        // данные для подключения к БД - временная заглушка
        this.exchangeRatesDataAccessObject = new ExchangeRatesDataAccessObject("jdbc:postgresql://localhost:5432/currency_exchanger", "vladislavmedvedev", "");
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String json = objectMapper.writeValueAsString(exchangeRatesDataAccessObject.getList());
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(json);
        } catch (DataAccessException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database interaction error (" + e.getMessage() + ")");
        }
    }
}
