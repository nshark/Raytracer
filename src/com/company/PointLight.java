package com.company;

import java.util.ArrayList;

public class PointLight extends Light{
    private final ArrayList<Double> position;
    public PointLight(double intensity, ArrayList<Double> position){
        super(intensity);
        this.position = position;
    }

    @Override
    public double computeLighting(ArrayList<Double> point, ArrayList<Double> normal, ArrayList<Double> v, double specular, ArrayList<renderable> objects) {
        ArrayList<Double> direction = Main.subVectors(position,point);
        return computeDirLight(point,direction,normal,v, specular, objects);
    }
}
