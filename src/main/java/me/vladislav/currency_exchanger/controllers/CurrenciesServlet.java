package me.vladislav.currency_exchanger.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.vladislav.currency_exchanger.dao.CurrencyDataAccessObject;
import me.vladislav.currency_exchanger.exceptions.CurrencyCodeAlreadyExistsException;
import me.vladislav.currency_exchanger.exceptions.CurrencyNotFoundException;
import me.vladislav.currency_exchanger.exceptions.DataAccessException;
import me.vladislav.currency_exchanger.models.Currency;
import me.vladislav.currency_exchanger.utils.ValidationUtils;

import java.io.IOException;

@WebServlet(value = "/currencies")
public class CurrenciesServlet extends HttpServlet {
    private final CurrencyDataAccessObject currencyDataAccessObject;
    private final ObjectMapper objectMapper;

    public CurrenciesServlet() {
        // данные для подключения к БД - временная заглушка
        this.currencyDataAccessObject = new CurrencyDataAccessObject("jdbc:postgresql://localhost:5432/currency_exchanger", "vladislavmedvedev", "");
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String json = objectMapper.writeValueAsString(currencyDataAccessObject.getList());
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(json);
        } catch (DataAccessException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database interaction error (" + e.getMessage() + ")");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String fullName = req.getParameter("fullname");
            String code = req.getParameter("code");
            String sign = req.getParameter("sign");

            if(ValidationUtils.isValidCode(code) && ValidationUtils.isValidFullName(fullName) && ValidationUtils.isValidSign(sign)){
                currencyDataAccessObject.add(new Currency(code, fullName, sign));
                resp.setStatus(HttpServletResponse.SC_CREATED);
                try {
                    String json = objectMapper.writeValueAsString(currencyDataAccessObject.getByCode(code));
                    resp.getWriter().write(json);
                } catch (CurrencyNotFoundException e) {
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database interaction error (" + e.getMessage() + ")");
                }
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect input parameters, example: .../currencies?fullname=Russian Ruble&code=RUB&sign=₽");
            }

        } catch (DataAccessException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database interaction error (" + e.getMessage() + ")");
        } catch (CurrencyCodeAlreadyExistsException e){
            resp.sendError(HttpServletResponse.SC_CONFLICT, "Failed to add new currency (" + e.getMessage() + ")");
        }
    }
}
