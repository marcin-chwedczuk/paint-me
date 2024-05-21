package pl.marcinchwedczuk.paintme.domain;

public class Util {
    private Util() { }

    public static String quote(String s) {
        return String.format("'%s'", s);
    }
}
