package me.vladislav.currency_exchanger.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.vladislav.currency_exchanger.dao.ExchangeRatesDataAccessObject;
import me.vladislav.currency_exchanger.exceptions.CurrencyNotFoundException;
import me.vladislav.currency_exchanger.exceptions.DataAccessException;
import me.vladislav.currency_exchanger.exceptions.ExchangeRateNotFoundException;

import java.io.IOException;

@WebServlet(value = "/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private final ExchangeRatesDataAccessObject exchangeRatesDataAccessObject;
    private final ObjectMapper objectMapper;

    public ExchangeRateServlet() {
        // данные для подключения к БД - временная заглушка
        this.exchangeRatesDataAccessObject = new ExchangeRatesDataAccessObject("jdbc:postgresql://localhost:5432/currency_exchanger", "vladislavmedvedev", "");
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        String codes;
        if (pathInfo != null) {
            String[] parts = pathInfo.split("/");
            if (parts.length == 2 && parts[1].length() == 6) {
                codes = parts[1].toUpperCase();
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect currency codes in the address, example: .../currency/USDRUB");
                return;
            }
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect currency codes in the address, example: .../currency/USDRUB");
            return;
        }
        System.out.println(codes);
        try {
            resp.setStatus(HttpServletResponse.SC_OK);
            String json = objectMapper.writeValueAsString(exchangeRatesDataAccessObject.getByCode(codes));
            resp.getWriter().write(json);
        } catch (DataAccessException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database interaction error (" + e.getMessage() + ")");
        } catch (CurrencyNotFoundException | ExchangeRateNotFoundException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }

}
