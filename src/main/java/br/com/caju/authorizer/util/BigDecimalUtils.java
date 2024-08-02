package br.com.caju.authorizer.util;

import org.springframework.util.Assert;

import java.math.BigDecimal;

public class BigDecimalUtils {

    private BigDecimalUtils() {
    }

    public static boolean isLessThan(BigDecimal val1, BigDecimal val2) {
        checkParams(val1, val2);
        return val1.compareTo(val2) < 0;
    }

    public static boolean equals(BigDecimal val1, BigDecimal val2) {
        checkParams(val1, val2);
        return val1.compareTo(val2) == 0;
    }

    public static boolean isGreaterThan(BigDecimal val1, BigDecimal val2) {
        checkParams(val1, val2);
        return val1.compareTo(val2) > 0;
    }

    public static boolean isLessOrEqualsThan(BigDecimal val1, BigDecimal val2) {
        checkParams(val1, val2);
        return val1.compareTo(val2) <= 0;
    }

    public static boolean isGreaterOrEqualsThan(BigDecimal val1, BigDecimal val2) {
        checkParams(val1, val2);
        return val1.compareTo(val2) >= 0;
    }

    private static void checkParams(BigDecimal val1, BigDecimal val2) {
        Assert.notNull(val1, "val1 cannot be null");
        Assert.notNull(val2, "val2 cannot be null");
    }

}
