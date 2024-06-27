package pl.marcinchwedczuk.paintme.gui.extra;

public interface Converter<T,U> {
    T toFirst(U u);
    U toSecond(T t);
}
