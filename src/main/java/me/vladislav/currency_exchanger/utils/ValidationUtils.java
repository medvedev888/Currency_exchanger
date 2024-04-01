package me.vladislav.currency_exchanger.utils;

import me.vladislav.currency_exchanger.exceptions.IncorrectInputException;
import java.math.BigDecimal;


public class ValidationUtils {

    public static String validateCurrencyCodeFromPath(String pathInfo) throws IncorrectInputException {
        String code;
        if(pathInfo != null) {
            String[] parts = pathInfo.split("/");
            if(parts.length == 2 && isValidCode(parts[1])) {
                code = parts[1].toUpperCase();
                return code;
            } else {
                throw new IncorrectInputException("incorrect format of code");
            }
        } else {
            throw new IncorrectInputException("code is an empty string");
        }
    }

    public static String validateCurrencyCodesFromPath(String pathInfo) throws IncorrectInputException {
        String codes;
        if(pathInfo != null) {
            String[] parts = pathInfo.split("/");
            if(parts.length == 2 && isValidCode(parts[1].substring(0, 3)) && isValidCode(parts[1].substring(3))) {
                codes = parts[1].toUpperCase();
                return codes;
            } else {
                throw new IncorrectInputException("incorrect format of codes");
            }
        } else {
            throw new IncorrectInputException("codes is an empty string");
        }
    }

    public static BigDecimal validateDecimalParameterString(String decimalStr) throws IncorrectInputException {
        if(decimalStr != null && !(decimalStr.isEmpty()) && new BigDecimal(decimalStr).compareTo(BigDecimal.ZERO) != 0) {
            if(!(decimalStr.contains(","))) {
                return new BigDecimal(decimalStr);
            } else {
                throw new IncorrectInputException("incorrect format of rate");
            }
        } else {
            throw new IncorrectInputException("rate is an empty string");
        }
    }

    public static boolean isValidFullName(String fullName) {
        return fullName != null && !fullName.isEmpty() && fullName.matches("[a-zA-Z ]+");
    }

    public static boolean isValidCode(String code) {
        return code != null && code.length() == 3 && code.matches("[a-zA-Z]+");
    }

    public static boolean isValidSign(String sign) {
        return sign != null && !sign.isEmpty();
    }
}
