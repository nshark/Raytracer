package com.company;

import javax.swing.*;
import java.awt.*;

public class gui {
    public JFrame jFrame;
    public Canvas canvas;
    public Graphics2D g;
    public gui(){
        jFrame = new JFrame("Raytracer");
        canvas = new Canvas();
        jFrame.add(canvas);
        jFrame.setSize(500,500);
        jFrame.setVisible(true);
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
