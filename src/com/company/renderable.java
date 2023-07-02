package com.company;

import java.awt.*;
import java.util.ArrayList;

public interface renderable {
    Color getColor();
    double[] collides(Ray ray);
    ArrayList<Double> getCenter();
    int getSpecular();
    double getReflective();
}
