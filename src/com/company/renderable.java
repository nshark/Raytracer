package com.company;

import java.awt.*;
import java.util.ArrayList;
import java.util.Vector;

public interface renderable {
    public Color getColor();
    public double[] collides(Ray ray);
    public ArrayList<Double> getCenter();
    public int getSpecular();
    public double getReflective();
}
