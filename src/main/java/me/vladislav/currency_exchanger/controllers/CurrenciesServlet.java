package me.vladislav.currency_exchanger.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.vladislav.currency_exchanger.dao.CurrencyDataAccessObject;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet(value = "/currencies")
public class CurrenciesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        CurrencyDataAccessObject currencyDataAccessObject = new CurrencyDataAccessObject();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        String json = objectMapper.writeValueAsString(currencyDataAccessObject.getList());
        resp.getWriter().write(json);
    }
}
