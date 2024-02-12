package com.company;

import java.awt.*;
import java.util.ArrayList;

public interface renderable {
    Color getColor();
    double[] collides(Ray ray);
    double[] getNormal(double[] point, Ray ray);
    int getSpecular();
    double getReflective();
}
