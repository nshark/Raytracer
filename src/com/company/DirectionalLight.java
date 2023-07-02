package com.company;

import java.util.ArrayList;

public class DirectionalLight extends Light{
    private final ArrayList<Double> direction;
    public DirectionalLight(double intensity, ArrayList<Double> direction) {
        super(intensity);
        this.direction = direction;
    }

    @Override
    public double computeLighting(ArrayList<Double> point, ArrayList<Double> normal, ArrayList<Double> v, double specular, ArrayList<renderable> objects) {
        return computeDirLight(point, direction,normal,v,specular, objects);
    }
}
