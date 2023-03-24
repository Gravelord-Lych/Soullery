package lych.soullery.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import lych.soullery.config.ConfigHelper;

public final class RomanNumeralGenerator {
    public static final int MAX_CONVERTIBLE = 3999;
    private static final String[] INITIAL_CACHE = new String[10];
    private static final ImmutableList<RomanNumeralPair> ROMANS;

    static {
        ImmutableList.Builder<RomanNumeralPair> builder = ImmutableList.builder();
        builder.add(RomanNumeralPair.of(1000, "M"));
        builder.add(RomanNumeralPair.of(900, "CM"));
        builder.add(RomanNumeralPair.of(500, "D"));
        builder.add(RomanNumeralPair.of(400, "CD"));
        builder.add(RomanNumeralPair.of(100, "C"));
        builder.add(RomanNumeralPair.of(90, "XC"));
        builder.add(RomanNumeralPair.of(50, "L"));
        builder.add(RomanNumeralPair.of(40, "XL"));
        builder.add(RomanNumeralPair.of(10, "X"));
        builder.add(RomanNumeralPair.of(9, "IX"));
        builder.add(RomanNumeralPair.of(5, "V"));
        builder.add(RomanNumeralPair.of(4, "IV"));
        builder.add(RomanNumeralPair.of(1, "I"));
        ROMANS = builder.build();
        for (int i = 0; i < INITIAL_CACHE.length; i++) {
            INITIAL_CACHE[i] = doGetRomanNumeral(i + 1);
        }
    }

    private RomanNumeralGenerator() {}

    public static String getRomanNumeral(int num) {
        Preconditions.checkArgument(num > 0, "Number must be positive");
        if (num <= INITIAL_CACHE.length) {
            return INITIAL_CACHE[num - 1];
        }
        if (num > ConfigHelper.getRomanLimit()) {
            return String.valueOf(num);
        }
        return doGetRomanNumeral(num);
    }

    private static String doGetRomanNumeral(int num) {
        StringBuilder builder = new StringBuilder();
        int index = 0;
        while (num > 0) {
            RomanNumeralPair pair = ROMANS.get(index);
            int value = pair.getValue();
            if (value <= num) {
                builder.append(pair.getSymbol());
                num -= value;
            } else {
                index++;
            }
        }
        return builder.toString();
    }

    private static class RomanNumeralPair {
        private final int value;
        private final String symbol;

        private RomanNumeralPair(int value, String symbol) {
            this.value = value;
            this.symbol = symbol;
        }

        public static RomanNumeralPair of(int value, String symbol) {
            return new RomanNumeralPair(value, symbol);
        }

        public int getValue() {
            return value;
        }

        public String getSymbol() {
            return symbol;
        }
    }
}
