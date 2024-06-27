package pl.marcinchwedczuk.paintme.gui.extra;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakListener;
import javafx.beans.property.Property;

import java.lang.ref.WeakReference;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class GenericBidirectionalBinding<T,U> implements InvalidationListener, WeakListener {
    public static <T, U> Object bindBidirectional(Property<T> property1, Property<U> property2, Converter<T, U> converter) {
        final var binding = new GenericBidirectionalBinding<T, U>(property1, property2, converter);

        property1.setValue(converter.toFirst(property2.getValue()));
        property1.getValue();
        property1.addListener(binding);
        property2.addListener(binding);

        return binding;
    }

    private final Converter<T, U> converter;
    private final WeakReference<Property<T>> property1;
    private final WeakReference<Property<U>> property2;
    private final int cachedHashCode;

    private boolean updating = false;

    public GenericBidirectionalBinding(Property<T> property1, Property<U> property2, Converter<T, U> converter) {
        this.property1 = new WeakReference<>(requireNonNull(property1));
        this.property2 = new WeakReference<>(requireNonNull(property2));
        this.converter = requireNonNull(converter);
        this.cachedHashCode = Objects.hash(property1, property2, converter);
    }

    protected Object getProperty1() {
        return property1.get();
    }

    protected Object getProperty2() {
        return property2.get();
    }

    protected Object getConverter() {
        return converter;
    }

    @Override
    public boolean wasGarbageCollected() {
        return (getProperty1() == null) || (getProperty2() == null);
    }

    @Override
    public int hashCode() {
        return cachedHashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        final Object propertyA1 = getProperty1();
        final Object propertyA2 = getProperty2();
        final Object converterA = getConverter();
        if ((propertyA1 == null) || (propertyA2 == null)) {
            return false;
        }

        if (obj instanceof GenericBidirectionalBinding<?,?> otherBinding) {
            final Object propertyB1 = otherBinding.getProperty1();
            final Object propertyB2 = otherBinding.getProperty2();
            final Object converterB = otherBinding.getConverter();
            if ((propertyB1 == null) || (propertyB2 == null)) {
                return false;
            }

            if (propertyA1 == propertyB1 && propertyA2 == propertyB2 && converterA == converterB) {
                return true;
            }
            if (propertyA1 == propertyB2 && propertyA2 == propertyB1 && converterA == converterB) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void invalidated(Observable observable) {
        if (!updating) {
            final Property<T> property1 = this.property1.get();
            final Property<U> property2 = this.property2.get();
            final Converter<T, U> converter = this.converter;

            if ((property1 == null) || (property2 == null)) {
                if (property1 != null) {
                    property1.removeListener(this);
                }
                if (property2 != null) {
                    property2.removeListener(this);
                }
            } else {
                try {
                    updating = true;
                    if (property1 == observable) {
                        try {
                            property2.setValue(converter.toSecond(property1.getValue()));
                            property2.getValue();
                        } catch (Exception e) {
                            e.printStackTrace();
                            property2.setValue(null);
                            property2.getValue();
                        }
                    } else {
                        try {
                            property1.setValue(converter.toFirst(property2.getValue()));
                            property1.getValue();
                        } catch (Exception e) {
                            e.printStackTrace();
                            property1.setValue(null);
                            property1.getValue();
                        }
                    }
                } finally {
                    updating = false;
                }
            }
        }
    }
}

