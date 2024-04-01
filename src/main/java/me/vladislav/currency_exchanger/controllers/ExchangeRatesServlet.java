package me.vladislav.currency_exchanger.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.vladislav.currency_exchanger.dao.CurrencyDataAccessObject;
import me.vladislav.currency_exchanger.dao.ExchangeRatesDataAccessObject;
import me.vladislav.currency_exchanger.exceptions.*;
import me.vladislav.currency_exchanger.models.Currency;
import me.vladislav.currency_exchanger.models.Rate;
import me.vladislav.currency_exchanger.utils.ValidationUtils;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet(value = "/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private final ExchangeRatesDataAccessObject exchangeRatesDataAccessObject;
    private final ObjectMapper objectMapper;
    private final CurrencyDataAccessObject currencyDataAccessObject;

    public ExchangeRatesServlet() {
        // данные для подключения к БД - временная заглушка
        this.exchangeRatesDataAccessObject = new ExchangeRatesDataAccessObject("jdbc:postgresql://localhost:5432/currency_exchanger", "vladislavmedvedev", "");
        this.currencyDataAccessObject = new CurrencyDataAccessObject("jdbc:postgresql://localhost:5432/currency_exchanger", "vladislavmedvedev", "");
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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String baseCurrencyCode = req.getParameter("baseCurrencyCode");
            String targetCurrencyCode = req.getParameter("targetCurrencyCode");
            String rateStr = req.getParameter("rate");
            BigDecimal rate;

            if(ValidationUtils.isValidCode(baseCurrencyCode) && ValidationUtils.isValidCode(targetCurrencyCode)){
                try {
                    rate = ValidationUtils.validateDecimalParameterString(rateStr);
                } catch (IncorrectInputException e) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect input parameters, example: .../exchangeRates?baseCurrencyCode=USD&targetCurrencyCode=RUB&rate=92.4 (" + e.getMessage() + ")");
                    return;
                }
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect input parameters, example: .../exchangeRates?baseCurrencyCode=USD&targetCurrencyCode=RUB&rate=92.4");
                return;
            }

            exchangeRatesDataAccessObject.add(new Rate(currencyDataAccessObject.getByCode(baseCurrencyCode), currencyDataAccessObject.getByCode(targetCurrencyCode), rate));
            resp.setStatus(HttpServletResponse.SC_CREATED);
            try {
                String json = objectMapper.writeValueAsString(exchangeRatesDataAccessObject.getByCode(baseCurrencyCode + targetCurrencyCode));
                resp.getWriter().write(json);
            } catch (CurrencyNotFoundException | ExchangeRateNotFoundException e) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database interaction error (" + e.getMessage() + ")");
            }
        } catch (DataAccessException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database interaction error (" + e.getMessage() + ")");
        } catch (ExchangeRateAlreadyExistsException e){
            resp.sendError(HttpServletResponse.SC_CONFLICT, "Failed to add new exchange rate (" + e.getMessage() + ")");
        } catch (CurrencyNotFoundException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }

}
