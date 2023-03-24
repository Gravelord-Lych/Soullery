package lych.soullery.util.redirectable;

public abstract class StringRedirector extends Redirector<String, String> implements StringRedirectable {
    private final boolean trim;

    protected StringRedirector(String value, boolean trim, String... aliases) {
        super(value, aliases);
        this.trim = trim;
    }

    public static StringRedirectable caseSensitive(String value, String... aliases) {
        return new Cased(value, true, aliases);
    }

    public static StringRedirectable caseSensitive(String value, boolean trim, String... aliases) {
        return new Cased(value, trim, aliases);
    }

    public static StringRedirectable caseInsensitive(String value, String... aliases) {
        return new Caseless(value, true, aliases);
    }

    public static StringRedirectable caseInsensitive(String value, boolean trim, String... aliases) {
        return new Caseless(value, trim, aliases);
    }

    @SuppressWarnings("all")
    @Override
    protected boolean isEqual(String s, String alias) {
        if (trim) {
            s = s.trim();
            alias = alias.trim();
        }
        if (s == alias) {
            return true;
        }
        if (s == null) {
            return false;
        }
        return isEqualIn(s, alias);
    }

    protected abstract boolean isEqualIn(String s, String alias);

    private static class Cased extends StringRedirector {
        private Cased(String value, boolean trim, String... aliases) {
            super(value, trim, aliases);
        }

        @Override
        protected boolean isEqualIn(String s, String alias) {
            return s.equals(alias);
        }
    }

    private static class Caseless extends StringRedirector {
        private Caseless(String value, boolean trim, String... aliases) {
            super(value, trim, aliases);
        }

        @Override
        protected boolean isEqualIn(String s, String alias) {
            return s.equalsIgnoreCase(alias);
        }
    }
}
