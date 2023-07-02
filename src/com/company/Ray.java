package com.company;

import java.util.ArrayList;

public class Ray {
    public final ArrayList<Double> origin;
    public final ArrayList<Double> direction;
    public Ray(ArrayList<Double> origin, ArrayList<Double> direction){
        this.origin = origin;
        this.direction = direction;
    }
}
