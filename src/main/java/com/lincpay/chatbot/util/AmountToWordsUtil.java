package com.lincpay.chatbot.util;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AmountToWordsUtil {

    private static final String[] units = {
            "", "One", "Two", "Three", "Four", "Five",
            "Six", "Seven", "Eight", "Nine", "Ten", "Eleven",
            "Twelve", "Thirteen", "Fourteen", "Fifteen",
            "Sixteen", "Seventeen", "Eighteen", "Nineteen"
    };

    private static final String[] tens = {
            "", "", "Twenty", "Thirty", "Forty", "Fifty",
            "Sixty", "Seventy", "Eighty", "Ninety"
    };

    public static String convertToIndianCurrencyWords(BigDecimal amount) {
        long rupees = amount.longValue();
        int paise = amount.remainder(BigDecimal.ONE).movePointRight(2).intValue();

        StringBuilder result = new StringBuilder();
        result.append(convert(BigDecimal.valueOf(rupees))).append(" Rupees");

        if (paise > 0) {
            result.append(" and ").append(convert(BigDecimal.valueOf(paise))).append(" Paise");
        }

        result.append(" Only");
        return result.toString().trim().replaceAll("\\s+", " ");
    }

    private static String convert(BigDecimal number) {
        BigDecimal crore = new BigDecimal("10000000");
        BigDecimal lakh = new BigDecimal("100000");
        BigDecimal thousand = new BigDecimal("1000");
        BigDecimal hundred = new BigDecimal("100");

        if (number.compareTo(BigDecimal.valueOf(20)) < 0) {
            return units[number.intValue()];
        }
        if (number.compareTo(BigDecimal.valueOf(100)) < 0) {
            return tens[number.intValue() / 10] + " " + units[number.intValue() % 10];
        }
        if (number.compareTo(thousand) < 0) {
            return units[number.intValue() / 100] + " Hundred " +
                    convert(number.remainder(hundred));
        }
        if (number.compareTo(lakh) < 0) {
            return convert(number.divide(thousand)) + " Thousand " +
                    convert(number.remainder(thousand));
        }
        if (number.compareTo(crore) < 0) {
            return convert(number.divide(lakh)) + " Lakh " +
                    convert(number.remainder(lakh));
        }
        return convert(number.divide(crore)) + " Crore " +
                convert(number.remainder(crore));
    }
}
