package lych.soullery.util.redirectable;

public interface StringRedirectable extends Redirectable<String, String> {
    @Override
    default StringRedirectable or(Redirectable<String, String> redirectable) {
        return (s, f) -> {
            String res = Redirectable.super.or(redirectable).redirect(s);
            return res == null ? f.apply(s) : res;
        };
    }
}
