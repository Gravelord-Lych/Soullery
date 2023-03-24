package lych.soullery.util.redirectable;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RegexRedirectable implements StringRedirectable {
    private final Pattern pattern;
    private final Function<? super String, ? extends String> valueFunc;
    private final boolean trim;

    public RegexRedirectable(Pattern pattern, Function<? super String, ? extends String> valueFunc) {
        this(pattern, valueFunc, true);
    }

    public RegexRedirectable(Pattern pattern, Function<? super String, ? extends String> valueFunc, boolean trim) {
        this.pattern = pattern;
        this.valueFunc = valueFunc;
        this.trim = trim;
    }

    @Override
    public String redirect(String s, Function<? super String, ? extends String> ifNotFound) {
        if (trim) {
            s = s.trim();
        }
        Matcher matcher = pattern.matcher(s);
        return matcher.matches() ? valueFunc.apply(s) : ifNotFound.apply(s);
    }
}
