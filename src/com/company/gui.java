package com.company;

import javax.swing.*;
import java.awt.*;

public class gui {
    private JFrame jFrame;
    private Canvas canvas;
    private Graphics2D g;
    public keyListener listener = new keyListener();
    public gui(){
        jFrame = new JFrame("Raytracer");
        canvas = new Canvas();
        jFrame.add(canvas);
        jFrame.setSize(500,500);
        jFrame.setVisible(true);
        canvas.addKeyListener(listener);
        jFrame.requestFocus();
        canvas.createBufferStrategy(2);
        g = (Graphics2D) canvas.getBufferStrategy().getDrawGraphics();
        g.clearRect(0, 0, 500, 500);
    }
    public void update(){
        canvas.getBufferStrategy().show();
        canvas.update(g);
        g.dispose();
        g = (Graphics2D) canvas.getBufferStrategy().getDrawGraphics();
        g.clearRect(0, 0, 500, 500);
    }
    public void putPixel(int x, int y, Color color){
        g.setColor(color);
        g.drawRect(x+250,-1*y+250,1,1);
    }
}
