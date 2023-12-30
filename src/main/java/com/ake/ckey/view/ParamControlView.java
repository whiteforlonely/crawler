package com.ake.ckey.view;

import com.ake.ckey.model.StructGraphicModel;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * 左边栏
 */
public class ParamControlView {

    /**
     * 约定
     * 参数1： n - 几格 3，4，5，6，7，，，100
     * 参数2：0b101010 - 竖线段标识，从上到下，总做到右，二进制文本，
     * 参数3：0b111111 - 横线段标识，从上到下，从左到右，二进制文本，
     * 参数4： 方块宽度
     * 参数5： 线段宽度
     * 参数6： 线段颜色
     * 参数7： 线段背景色
     * 参数8： 全局背景色
     * 参数9： 是否有边框
     * 参数10： 边框宽度
     * 参数11： 边框颜色
     */
    public List<ParamView<TextField>> paramViews;
    private final StructGraphicModel dataModel;
    private ParamChangeListener listener;

    private final String colorRegex = "([0-9]{1,3}\\s){2}[0-9]{1,3}(\\s[01]?\\.?[0-9]+)?";
    private final String doubleRegex = "[0-9]+([.][0-9]*)?";

    public ParamControlView(){
        paramViews = new ArrayList<>();
        paramViews.add(initParam("宫格数") );
        paramViews.add(initParam("竖线段(1101)") );
        paramViews.add(initParam("横线段(1101)") );
        paramViews.add(initParam("小方块宽度"));
        paramViews.add(initParam("线段宽度"));
        paramViews.add(initParam("线段颜色"));
        paramViews.add(initParam("线段背景色"));
        paramViews.add(initParam("全局背景色"));
        paramViews.add(initParam("是否有边框"));
        paramViews.add(initParam("边框宽度"));
        paramViews.add(initParam("边框颜色"));

        dataModel = new StructGraphicModel();
        initParamValue();
    }

    public void setListener(ParamChangeListener listener) {
        this.listener = listener;
    }

    private void initParamValue(){
        setRows(this.dataModel.getRows());
        setVerticalBits(this.dataModel.getVerticalBits());
        setHorizonBits(this.dataModel.getHorizontalBits());
        setCellWidth(this.dataModel.getCellWidth());
        setLineSegmentWidth(this.dataModel.getLineSegmentWidth());
        setLineSegmentColor(this.dataModel.getLineSegmentColor());
        setLineBackground(this.dataModel.getLineBackground());
        setBackground(this.dataModel.getBackgroundColor());
        enableBorder(this.dataModel.isHasBorder());
        setBorderWidth(this.dataModel.getBorderWidth());
        setBorderColor(this.dataModel.getBorderColor());

        // 监听rows变更
        paramViews.get(0).setListener( text -> {
            System.out.println("rows changed: " + text);
            text = text.trim();
            if (text.isBlank() || !text.matches("\\d+")) {
                return;
            }
            int rows = Integer.parseInt(text);
            if (rows < 2 || rows > 100) {
                return;
            }
            this.dataModel.setRows(rows);
            if (null != listener) {
                listener.handleModel(this.dataModel);
            }
        });

        paramViews.get(1).setListener( text -> {
            System.out.println("vertical bits changed: " + text);
            if (!text.isBlank() && !text.matches("[0-1]+")) {
                return;
            }
            this.dataModel.setVerticalBits(text);
            if (null != listener) {
                listener.handleModel(this.dataModel);
            }
        });

        paramViews.get(2).setListener( text -> {
            System.out.println("horizontal bits changed: " + text);
            text = text.trim();
            if (!text.isBlank() && !text.matches("[0-1]+")) {
                return;
            }
            this.dataModel.setHorizontalBits(text);
            if (null != listener) {
                listener.handleModel(this.dataModel);
            }
        });

        paramViews.get(3).setListener( text -> {
            System.out.println("cell width changed: " + text);
            text = text.trim();
            if (text.isBlank() || !text.isBlank() && !text.matches(doubleRegex)) {
                return;
            }
            double width = Double.parseDouble(text);
            if (width > 100) {
                width = 100;
            } else if (width < 20) {
                width = 20;
            }
            this.dataModel.setCellWidth(width);
            if (null != listener) {
                listener.handleModel(this.dataModel);
            }
        });

        paramViews.get(4).setListener( text -> {
            System.out.println("line segment width changed: " + text);
            text = text.trim();
            if (text.isBlank() || !text.isBlank() && !text.matches(doubleRegex)) {
                return;
            }
            double lineSegmentWidth = Double.parseDouble(text);
            if (lineSegmentWidth > 10) {
                lineSegmentWidth = 10;
            } if (lineSegmentWidth < 1) {
                lineSegmentWidth = 1;
            }
            this.dataModel.setLineSegmentWidth(lineSegmentWidth);
            if (null != listener) {
                listener.handleModel(this.dataModel);
            }
        });

        paramViews.get(5).setListener( text -> {
            System.out.println("line segment color changed: " + text);
            text = text.trim();
            if (!text.matches(colorRegex)) {
                return;
            }
            Color color = parseFromText(text);
            this.dataModel.setLineSegmentColor(color);
            if (null != listener) {
                listener.handleModel(this.dataModel);
            }
        });

        paramViews.get(6).setListener( text -> {
            System.out.println("line background color changed: " + text);
            text = text.trim();
            if (!text.matches(colorRegex)) {
                return;
            }
            Color color = parseFromText(text);
            this.dataModel.setLineBackground(color);
            if (null != listener) {
                listener.handleModel(this.dataModel);
            }
        });


        paramViews.get(7).setListener( text -> {
            System.out.println("background color changed: " + text);
            text = text.trim();
            if (!text.matches(colorRegex)) {
                return;
            }
            Color color = parseFromText(text);
            this.dataModel.setBackgroundColor(color);
            if (null != listener) {
                listener.handleModel(this.dataModel);
            }
        });

        paramViews.get(8).setListener( text -> {
            System.out.println("enable border changed: " + text);
            text = text.trim();
            if (!text.matches("(true|false)")) {
                return;
            }
            boolean hasBorder = Boolean.parseBoolean(text);
            this.dataModel.setHasBorder(hasBorder);
            if (null != listener) {
                listener.handleModel(this.dataModel);
            }
        });

        paramViews.get(9).setListener( text -> {
            System.out.println("border width changed: " + text);
            text = text.trim();
            if (text.isBlank() || !text.isBlank() && !text.matches(doubleRegex)) {
                return;
            }
            double borderWidth = Double.parseDouble(text);
            if (borderWidth > 20) {
                borderWidth = 20;
            } if (borderWidth < 2) {
                borderWidth = 2;
            }
            this.dataModel.setBorderWidth(borderWidth);
            if (null != listener) {
                listener.handleModel(this.dataModel);
            }
        });

        paramViews.get(10).setListener( text -> {
            System.out.println("border color changed: " + text);
            text = text.trim();
            if (!text.matches(colorRegex)) {
                return;
            }
            Color color = parseFromText(text);
            this.dataModel.setBorderColor(color);
            if (null != listener) {
                listener.handleModel(this.dataModel);
            }
        });
    }

    private ParamView<TextField> initParam(String labelName){
        TextField t = new TextField(labelName);
        return new ParamView<>(labelName, t);
    }

    public void setRows(int rows){
        ParamView<TextField> paramView = this.paramViews.get(0);
        TextField field = paramView.getInput();
        field.setText(String.valueOf(rows));
    }

    public void setVerticalBits(String vBits){
        ParamView<TextField> paramView = this.paramViews.get(1);
        TextField field = paramView.getInput();
        field.setText(vBits);
    }

    public void setHorizonBits(String hBits) {
        ParamView<TextField> paramView = this.paramViews.get(2);
        TextField field = paramView.getInput();
        field.setText(hBits);
    }

    public void setCellWidth(double cellWidth){
        ParamView<TextField> paramView = this.paramViews.get(3);
        TextField field = paramView.getInput();
        field.setText(String.valueOf(cellWidth));
    }

    public void setLineSegmentWidth(double lineSegmentWidth){
        ParamView<TextField> paramView = this.paramViews.get(4);
        TextField field = paramView.getInput();
        field.setText(String.valueOf(lineSegmentWidth));
    }

    public void setLineSegmentColor(Color lineSegmentColor){
        ParamView<TextField> paramView = this.paramViews.get(5);
        TextField field = paramView.getInput();
        field.setText(getTextFromColor(lineSegmentColor));
    }

    private Color parseFromText(String text){
        text = text.trim();
        if (!text.matches(colorRegex)) {
            return null;
        }

        String[] rgbc = text.split("\\s");
        int r = Integer.parseInt(rgbc[0])%256;
        int g = Integer.parseInt(rgbc[1])%256;
        int b = Integer.parseInt(rgbc[2])%256;
        if (rgbc.length == 4) {
            double opacity = Double.parseDouble(rgbc[3]);
            return Color.rgb(r, g, b, opacity);
        } else {
            return Color.rgb(r, g, b);
        }
    }

    private String getTextFromColor(Color color) {
        return (int)(color.getRed()*255) + " " + (int)(color.getGreen()*255) + " " + (int)(color.getBlue()*255) + " " + color.getOpacity();
    }

    public void setLineBackground(Color lineBackground){
        ParamView<TextField> paramView = this.paramViews.get(6);
        TextField field = paramView.getInput();
        field.setText(getTextFromColor(lineBackground));
    }

    public void setBackground(Color background){
        ParamView<TextField> paramView = this.paramViews.get(7);
        TextField field = paramView.getInput();
        field.setText(getTextFromColor(background));
    }

    public void enableBorder(boolean hasBorder){
        ParamView<TextField> paramView = this.paramViews.get(8);
        TextField field = paramView.getInput();
        field.setText(String.valueOf(hasBorder));
    }

    public void setBorderWidth(double borderWidth){
        ParamView<TextField> paramView = this.paramViews.get(9);
        TextField field = paramView.getInput();
        field.setText(String.valueOf(borderWidth));
    }

    public void setBorderColor(Color borderColor){
        ParamView<TextField> paramView = this.paramViews.get(10);
        TextField input = paramView.getInput();
        input.setText(getTextFromColor(borderColor));
    }

    public List<ParamView<TextField>> getParamViews() {
        return paramViews;
    }

    public interface ParamChangeListener{
        void handleModel(StructGraphicModel model);
    }

    public void invokeListener(){
        if (null != this.listener) {
            this.listener.handleModel(this.dataModel);
        }
    }

}
