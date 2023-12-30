package com.ake.ckey.model;

import javafx.scene.paint.Color;

/**
 * 结构参数信息
 */
public class StructGraphicModel {

    /** 宫格数量 */
    private int rows = 5;

    /** 竖线段的显示标志 */
    private String verticalBits="0000011111";

    /** 横的显示标志 */
    private String horizontalBits;

    /** 每一个的宽度 */
    private double cellWidth = 50;

    /** 小线段的宽度 */
    private double lineSegmentWidth = 2;

    /** 小线段的颜色 */
    private Color lineSegmentColor = Color.ALICEBLUE;

    /** 线段背景色 */
    private Color lineBackground = Color.rgb(255, 255, 255, 0.125);

    /** 结构图背景色 */
    private Color backgroundColor = Color.BLACK;

    /** 是否有边框 */
    private boolean hasBorder = true;

    /** 边框宽度 */
    private double borderWidth = 5;

    /** 边框颜色 */
    private Color borderColor = Color.rgb(13,125,147, 1.0);

    public Color getLineBackground() {
        return lineBackground;
    }

    public void setLineBackground(Color lineBackground) {
        this.lineBackground = lineBackground;
    }

    public double getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(double borderWidth) {
        this.borderWidth = borderWidth;
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    public Color getLineSegmentColor() {
        return lineSegmentColor;
    }

    public void setLineSegmentColor(Color lineSegmentColor) {
        this.lineSegmentColor = lineSegmentColor;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public String getVerticalBits() {
        return verticalBits;
    }

    public void setVerticalBits(String verticalBits) {
        this.verticalBits = verticalBits;
    }

    public String getHorizontalBits() {
        return horizontalBits;
    }

    public void setHorizontalBits(String horizontalBits) {
        this.horizontalBits = horizontalBits;
    }

    public double getCellWidth() {
        return cellWidth;
    }

    public void setCellWidth(double cellWidth) {
        this.cellWidth = cellWidth;
    }

    public double getLineSegmentWidth() {
        return lineSegmentWidth;
    }

    public void setLineSegmentWidth(double lineSegmentWidth) {
        this.lineSegmentWidth = lineSegmentWidth;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public boolean isHasBorder() {
        return hasBorder;
    }

    public void setHasBorder(boolean hasBorder) {
        this.hasBorder = hasBorder;
    }

}

