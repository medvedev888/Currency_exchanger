package me.vladislav.currency_exchanger.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.vladislav.currency_exchanger.dao.CurrencyDataAccessObject;
import me.vladislav.currency_exchanger.dao.ExchangeRatesDataAccessObject;
import me.vladislav.currency_exchanger.dto.ErrorResponse;
import me.vladislav.currency_exchanger.dto.Exchange;
import me.vladislav.currency_exchanger.exceptions.CurrencyNotFoundException;
import me.vladislav.currency_exchanger.exceptions.DataAccessException;
import me.vladislav.currency_exchanger.exceptions.ExchangeRateNotFoundException;
import me.vladislav.currency_exchanger.exceptions.IncorrectInputException;
import me.vladislav.currency_exchanger.models.Currency;
import me.vladislav.currency_exchanger.models.Rate;
import me.vladislav.currency_exchanger.utils.ValidationUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

@WebServlet(value = "/exchange")
public class ExchangeServlet extends HttpServlet {
    private  ExchangeRatesDataAccessObject exchangeRatesDataAccessObject;
    private  ObjectMapper objectMapper;
    private  CurrencyDataAccessObject currencyDataAccessObject;

    @Override
    public void init() throws ServletException {
        super.init();
        ServletContext context = getServletContext();
        exchangeRatesDataAccessObject = (ExchangeRatesDataAccessObject) getServletContext().getAttribute("exchangeRatesDataAccessObject");
        currencyDataAccessObject = (CurrencyDataAccessObject) context.getAttribute("currencyDataAccessObject");
        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = req.getParameter("from");
        String targetCurrencyCode = req.getParameter("to");
        String amountStr = req.getParameter("amount");
        Rate rateObj;
        BigDecimal amount;
        BigDecimal rate;
        BigDecimal convertedAmount;
        Currency baseCurrency;
        Currency targetCurrency;

        if (ValidationUtils.isValidCode(baseCurrencyCode) && ValidationUtils.isValidCode(targetCurrencyCode)) {
            try {
                amount = ValidationUtils.validateDecimalParameterString(amountStr);
            } catch (IncorrectInputException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect input parameters, example: .../exchangeRates?baseCurrencyCode=USD&targetCurrencyCode=RUB&rate=92.4 (" + e.getMessage() + ")");
                return;
            }
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect input parameters, example: .../exchangeRates?baseCurrencyCode=USD&targetCurrencyCode=RUB&rate=92.4");
            return;
        }
        try {
            rateObj = exchangeRatesDataAccessObject.getByCode(baseCurrencyCode + targetCurrencyCode);
            baseCurrency = currencyDataAccessObject.getByCode(baseCurrencyCode);
            targetCurrency = currencyDataAccessObject.getByCode(targetCurrencyCode);
            rate = rateObj.getRate();
            convertedAmount = rate.multiply(amount);

            Exchange exchange = new Exchange(baseCurrency, targetCurrency, rate, amount, convertedAmount);

            resp.setStatus(HttpServletResponse.SC_OK);
            String json = objectMapper.writeValueAsString(exchange);
            resp.getWriter().write(json);

        } catch (CurrencyNotFoundException e){
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            ErrorResponse errorResponse = new ErrorResponse("Currency not found");
            String json = objectMapper.writeValueAsString(errorResponse);
            resp.getWriter().write(json);
        } catch (DataAccessException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database interaction error (" + e.getMessage() + ")");
        } catch (ExchangeRateNotFoundException e) {
            try {
                rateObj = exchangeRatesDataAccessObject.getByCode(targetCurrencyCode + baseCurrencyCode);
                baseCurrency = currencyDataAccessObject.getByCode(baseCurrencyCode);
                targetCurrency = currencyDataAccessObject.getByCode(targetCurrencyCode);
                rate = rateObj.getRate();
                convertedAmount = (BigDecimal.ONE.divide(rate, 3, RoundingMode.HALF_UP)).multiply(amount);
                Exchange exchange = new Exchange(baseCurrency, targetCurrency, rate, amount, convertedAmount);

                resp.setStatus(HttpServletResponse.SC_OK);
                String json = objectMapper.writeValueAsString(exchange);
                resp.getWriter().write(json);

            }  catch (CurrencyNotFoundException e2){
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                ErrorResponse errorResponse = new ErrorResponse("Currency not found");
                String json = objectMapper.writeValueAsString(errorResponse);
                resp.getWriter().write(json);
            } catch (DataAccessException e2) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database interaction error (" + e2.getMessage() + ")");
            } catch (ExchangeRateNotFoundException e2) {
                try {
                    Rate rateObj1 = exchangeRatesDataAccessObject.getByCode("USD" + baseCurrencyCode);
                    Rate rateObj2 = exchangeRatesDataAccessObject.getByCode("USD" + targetCurrencyCode);
                    baseCurrency = currencyDataAccessObject.getByCode(baseCurrencyCode);
                    targetCurrency = currencyDataAccessObject.getByCode(targetCurrencyCode);
                    BigDecimal rate1 = rateObj1.getRate();
                    BigDecimal rate2 = rateObj2.getRate();
                    rate = rate2.divide(rate1, 3, RoundingMode.HALF_UP);
                    convertedAmount = rate.multiply(amount);
                    Exchange exchange = new Exchange(baseCurrency, targetCurrency, rate, amount, convertedAmount);

                    resp.setStatus(HttpServletResponse.SC_OK);
                    String json = objectMapper.writeValueAsString(exchange);
                    resp.getWriter().write(json);

                } catch (DataAccessException e3) {
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database interaction error (" + e.getMessage() + ")");
                } catch (ExchangeRateNotFoundException e3) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    ErrorResponse errorResponse = new ErrorResponse("Exchange rate not found");
                    String json = objectMapper.writeValueAsString(errorResponse);
                    resp.getWriter().write(json);
                } catch (CurrencyNotFoundException e3){
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    ErrorResponse errorResponse = new ErrorResponse("Currency not found");
                    String json = objectMapper.writeValueAsString(errorResponse);
                    resp.getWriter().write(json);
                }
            }
        }
    }
}
