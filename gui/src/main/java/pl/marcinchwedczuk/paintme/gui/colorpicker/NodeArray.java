package pl.marcinchwedczuk.paintme.gui.colorpicker;

import javafx.util.Pair;
import org.w3c.dom.Node;

import java.util.Iterator;

public class NodeArray<T> implements Iterable<T> {
    private final int nrows;
    private final int ncols;
    private final Object[] nodes;

    public NodeArray(int rowNumber, int columnNumber) {
        this.nrows = rowNumber;
        this.ncols = columnNumber;
        this.nodes = new Object[rowNumber * columnNumber];
    }

    public void setNode(T node, int row, int col) {
        nodes[row*nrows + col] = node;
    }

    @SuppressWarnings("unchecked")
    public T getNode(int row, int col) {
        return (T) nodes[row*nrows + col];
    }

    public T getNodeAbove(T other) {
        Pair<Integer, Integer> position = findElement(other);
        if (position == null) {
            return null;
        }

        int newRow = (nrows + position.getKey() - 1) % nrows;
        int newCol = position.getValue();

        return getNode(newRow, newCol);
    }

    public T getNodeBelow(T other) {
        Pair<Integer, Integer> position = findElement(other);
        if (position == null) {
            return null;
        }

        int newRow = (position.getKey() + 1) % nrows;
        int newCol = position.getValue();

        return getNode(newRow, newCol);
    }

    public T getNodeOnLeft(T other) {
        Pair<Integer, Integer> position = findElement(other);
        if (position == null) {
            return null;
        }

        int newRow = position.getKey();
        int newCol = (ncols + position.getValue() - 1) % ncols;

        return getNode(newRow, newCol);
    }

    public T getNodeOnRight(T other) {
        Pair<Integer, Integer> position = findElement(other);
        if (position == null) {
            return null;
        }

        int newRow = position.getKey();
        int newCol = (position.getValue() + 1) % ncols;

        return getNode(newRow, newCol);
    }

    private Pair<Integer, Integer> findElement(T elem) {
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] == elem) {
                int row = i / nrows;
                int col = i % nrows;

                return new Pair<>(row, col);
            }
        }

        return null;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int currentRow = 0;
            private int currentCol = 0;

            @Override
            public boolean hasNext() {
                return (currentRow < nrows);
            }

            @SuppressWarnings("unchecked")
            @Override
            public T next() {
                T current = (T)nodes[currentRow*nrows + currentCol];

                currentCol++;
                if (currentCol >= ncols) {
                    currentCol = 0;
                    currentRow++;
                }

                return current;
            }
        };
    }
}
