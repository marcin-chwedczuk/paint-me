package pl.marcinchwedczuk.paintme.gui.extra;

import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

import java.util.List;

public class BeveledPane extends Pane {
    private final int BORDER_WIDTH = 1;

    private SimpleObjectProperty<Node> childProperty = new SimpleObjectProperty<>(this, "child", null);

    private Rectangle leftBevel;
    private Rectangle rightBevel;
    private Rectangle topBevel;
    private Rectangle bottomBevel;

    private Polygon leftBevelTopCorner;
    private Polygon leftBevelBottomCorner;

    private Polygon rightBevelTopCorner;
    private Polygon rightBevelBottomCorner;

    private Polygon topBevelLeftCorner;
    private Polygon topBevelRightCorner;

    private Polygon bottomBevelLeftCorner;
    private Polygon bottomBevelRightCorner;

    public BeveledPane(Node child) {
        childProperty.addListener((o, oldValue, newValue) -> {
            if (oldValue != null) {
                BeveledPane.this.getChildren().remove(oldValue);
            }

            if (newValue != null) {
                BeveledPane.this.getChildren().add(0, newValue);
            }
        });
        childProperty.set(child);

        createBevel(BORDER_WIDTH);


        // Set size for bevel
        requestLayout();
    }

    private void createBevel(int bevelWidth) {
        double w = getWidth();
        double h = getHeight();

        leftBevel = new Rectangle(bevelWidth, h);
        rightBevel = new Rectangle(bevelWidth, h);
        topBevel = new Rectangle(bevelWidth, w);
        bottomBevel = new Rectangle(bevelWidth, w);

        leftBevelTopCorner = new Polygon(
                0, 0,
                bevelWidth, bevelWidth,
                        0, bevelWidth);

        leftBevelBottomCorner = new Polygon(
                0, 0,
                bevelWidth, 0,
                0, bevelWidth);


        rightBevelTopCorner = new Polygon(
                bevelWidth, 0,
                        bevelWidth, bevelWidth,
                        0, bevelWidth);

        rightBevelBottomCorner = new Polygon(
                0, 0,
                bevelWidth, bevelWidth,
                bevelWidth, 0);

        topBevelLeftCorner = new Polygon(
                0, 0,
                bevelWidth, 0,
                bevelWidth, bevelWidth);

        topBevelRightCorner = new Polygon(
                0, 0,
                bevelWidth, 0,
                0, bevelWidth);

        bottomBevelLeftCorner = new Polygon(
                0, bevelWidth,
                bevelWidth, 0,
                bevelWidth, bevelWidth);

        bottomBevelRightCorner = new Polygon(
                0, 0,
                bevelWidth, bevelWidth,
                0, bevelWidth);

        // Remove all non-children
        getChildren().removeIf(elem -> elem != childProperty.get());
        // Add bevel elements
        getChildren().addAll(leftBevel, leftBevelTopCorner, leftBevelBottomCorner,
                rightBevel, rightBevelTopCorner, rightBevelBottomCorner,
                topBevel, topBevelLeftCorner, topBevelRightCorner,
                bottomBevel, bottomBevelLeftCorner, bottomBevelRightCorner);


        topBevel.setFill(Color.web("#0a0a0a"));
        topBevelLeftCorner.setFill(topBevel.getFill());
        topBevelRightCorner.setFill(topBevel.getFill());

        bottomBevel.setFill(Color.web("#fff"));
        bottomBevelLeftCorner.setFill(bottomBevel.getFill());
        bottomBevelRightCorner.setFill(bottomBevel.getFill());

        leftBevel.setFill(Color.web("grey"));
        leftBevelTopCorner.setFill(leftBevel.getFill());
        leftBevelBottomCorner.setFill(leftBevel.getFill());

        rightBevel.setFill(Color.web("#dfdfdf") );
        rightBevelTopCorner.setFill(rightBevel.getFill());
        rightBevelBottomCorner.setFill(rightBevel.getFill());
    }

    private Node getChild() {
        return getChildren().get(0);
    }

    @Override
    protected double computeMinWidth(double height) {
        // TODO: snapSizeX
        return getInsets().getLeft()
                + getChild().minWidth(height)
                + getInsets().getRight() + 2*BORDER_WIDTH;
    }

    @Override
    protected double computeMinHeight(double width) {
        return getInsets().getTop() + getChild().minHeight(width) + getInsets().getBottom() + 2*BORDER_WIDTH;
    }

    @Override
    protected double computePrefWidth(double height) {
        return getInsets().getLeft() + getChild().prefWidth(height) + getInsets().getRight() + 2*BORDER_WIDTH;
    }

    @Override
    protected double computePrefHeight(double width) {
        return getInsets().getTop() + getChild().prefHeight(width) + getInsets().getBottom() + 2*BORDER_WIDTH;
    }

    @Override
    protected double computeMaxWidth(double height) {
        return getInsets().getLeft()
                + getChild().maxWidth(height)
                + getInsets().getRight() + 2*BORDER_WIDTH;
    }

    @Override
    protected double computeMaxHeight(double width) {
        return getInsets().getTop() + getChild().maxHeight(width) + getInsets().getBottom() + 2*BORDER_WIDTH;
    }

    @Override
    protected void layoutChildren() {
        List<Node> managed = getManagedChildren();
        Insets insets = getInsets();

        double width = getWidth();
        double height = getHeight();
        double top = snapSpaceY(insets.getTop());
        double left = snapSpaceX(insets.getLeft());
        double bottom = snapSpaceY(insets.getBottom());
        double right = snapSpaceX(insets.getRight());

        double childTop = top + BORDER_WIDTH;
        double childLeft = left + BORDER_WIDTH;
        double childWidth = width - left - right - 2*BORDER_WIDTH;
        double childHeight = height - top - bottom - 2*BORDER_WIDTH;

        layoutInArea(getChild(), childLeft, childTop, childWidth, childHeight, 0, HPos.LEFT, VPos.TOP);

        // Left bevel
        this.leftBevel.setHeight(childHeight);
        this.leftBevel.setWidth(BORDER_WIDTH);
        this.leftBevel.relocate(left, childTop);

        this.leftBevelTopCorner.relocate(left, top);
        this.leftBevelBottomCorner.relocate(left, childTop + childHeight);

        // Right bevel
        this.rightBevel.setHeight(childHeight);
        this.rightBevel.setWidth(BORDER_WIDTH);
        this.rightBevel.relocate(left + BORDER_WIDTH + childWidth, childTop);

        this.rightBevelTopCorner.relocate(left + BORDER_WIDTH + childWidth, top);
        this.rightBevelBottomCorner.relocate(left + BORDER_WIDTH + childWidth, childTop + childHeight);

        // Top bevel
        this.topBevel.setHeight(BORDER_WIDTH);
        this.topBevel.setWidth(childWidth);
        this.topBevel.relocate(left + BORDER_WIDTH, top);

        this.topBevelLeftCorner.relocate(left, top);
        this.topBevelRightCorner.relocate(left + BORDER_WIDTH + childWidth, top);

        // Bottom bevel
        this.bottomBevel.setHeight(BORDER_WIDTH);
        this.bottomBevel.setWidth(childWidth);
        this.bottomBevel.relocate(left + BORDER_WIDTH, top + BORDER_WIDTH + childHeight);

        this.bottomBevelLeftCorner.relocate(left, top + BORDER_WIDTH + childHeight);
        this.bottomBevelRightCorner.relocate(left + BORDER_WIDTH + childWidth, top + BORDER_WIDTH + childHeight);
    }
}
