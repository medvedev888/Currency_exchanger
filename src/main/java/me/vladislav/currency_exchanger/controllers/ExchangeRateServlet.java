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
import me.vladislav.currency_exchanger.exceptions.CurrencyNotFoundException;
import me.vladislav.currency_exchanger.exceptions.DataAccessException;
import me.vladislav.currency_exchanger.exceptions.ExchangeRateNotFoundException;
import me.vladislav.currency_exchanger.exceptions.IncorrectInputException;
import me.vladislav.currency_exchanger.models.Rate;
import me.vladislav.currency_exchanger.utils.ValidationUtils;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet(value = "/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
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
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getMethod().equalsIgnoreCase("PATCH")) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        String codes;

        try {
            codes = ValidationUtils.validateCurrencyCodesFromPath(pathInfo);
        } catch (IncorrectInputException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect currency codes in the address, example: .../currency/USDRUB (" + e.getMessage() + ")");
            return;
        }
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

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String rateStr = req.getParameter("rate");
        String pathInfo = req.getPathInfo();
        String codes;
        BigDecimal rate;

        try {
            try {
                codes = ValidationUtils.validateCurrencyCodesFromPath(pathInfo);
                rate = ValidationUtils.validateDecimalParameterString(rateStr);
            } catch (IncorrectInputException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect input parameters, example: .../exchangeRate/USDRUB?rate=93.5 (" + e.getMessage() + ")");
                return;
            }
            try {
                exchangeRatesDataAccessObject.update(new Rate(currencyDataAccessObject.getByCode(codes.substring(0, 3)),
                        currencyDataAccessObject.getByCode(codes.substring(3)), rate));
            } catch (ExchangeRateNotFoundException e) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
                return;
            }

            resp.setStatus(HttpServletResponse.SC_OK);
            String json = objectMapper.writeValueAsString(exchangeRatesDataAccessObject.getByCode(codes));
            resp.getWriter().write(json);

        } catch (DataAccessException | CurrencyNotFoundException | ExchangeRateNotFoundException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database interaction error (" + e.getMessage() + ")");
        }
    }

}
