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
import me.vladislav.currency_exchanger.exceptions.CurrencyNotFoundException;
import me.vladislav.currency_exchanger.exceptions.DataAccessException;
import me.vladislav.currency_exchanger.exceptions.IncorrectInputException;
import me.vladislav.currency_exchanger.utils.ValidationUtils;

import java.io.IOException;

@WebServlet(value = "/currency/*")
public class CurrencyServlet extends HttpServlet {
    private CurrencyDataAccessObject currencyDataAccessObject;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        super.init();
        ServletContext context = getServletContext();
        currencyDataAccessObject = (CurrencyDataAccessObject) context.getAttribute("currencyDataAccessObject");
        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        String code;
        try {
            code = ValidationUtils.validateCurrencyCodeFromPath(pathInfo);
        } catch (IncorrectInputException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect currency code in the address, example: .../currency/RUB (" + e.getMessage() + ")");
            return;
        }
        try {
            String json = objectMapper.writeValueAsString(currencyDataAccessObject.getByCode(code));
            resp.getWriter().write(json);
        } catch (DataAccessException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database interaction error (" + e.getMessage() + ")");
        } catch (CurrencyNotFoundException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }
}
