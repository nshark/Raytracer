package com.company;

import java.util.ArrayList;

public class DirectionalLight extends Light{
    private final double[] direction;
    public DirectionalLight(double intensity, double[] direction) {
        super(intensity);
        this.direction = direction;
    }

    @Override
    public double computeLighting(double[] point, double[] normal, double[] v, double specular, ArrayList<renderable> objects) {
        return computeDirLight(point, direction, normal, v , specular, objects);
    }
}
