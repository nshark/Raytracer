package com.company;

import java.util.ArrayList;

public class PointLight extends Light{
    private final double[] position;
    public PointLight(double intensity, double[] position){
        super(intensity);
        this.position = position;
    }

    @Override
    public double computeLighting(double[] point, double[] normal, double[] v, double specular, ArrayList<renderable> objects) {
        double[] direction = Main.subVectors(position, point);
        return computeDirLight(point,direction,normal,v, specular, objects);
    }
}
