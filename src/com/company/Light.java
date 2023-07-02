package com.company;

import java.util.ArrayList;

public class Light {
    protected final double intensity;
    public Light(double intensity){
        this.intensity = intensity;
    }
    public double computeLighting(ArrayList<Double> point, ArrayList<Double> normal, ArrayList<Double> v, double specular, ArrayList<renderable> objects){
        return intensity;
    }
    protected double computeDirLight(ArrayList<Double> point, ArrayList<Double> direction, ArrayList<Double> normal, ArrayList<Double> v, double specular, ArrayList<renderable> objects){
        intersectionInfo shadowInfo = Main.closestIntersection(new Ray(point, direction), Main.epsilon, Double.MAX_VALUE, objects);
        if (shadowInfo.collider() != null){
            return 0;
        }
        double n_dot_l = Main.dotProduct(normal,direction);
        double i = 0d;
        if(n_dot_l > 0){
            i += intensity *  n_dot_l/(Main.magnitude(normal) * Main.magnitude(direction));
        }
        if (specular!=-1){
            ArrayList<Double> R = Main.ReflectRay(direction, normal);
            double r_dot_v = Main.dotProduct(R,v);
            if (r_dot_v>0){
                i+=intensity*Math.pow((r_dot_v/(Main.magnitude(R)*Main.magnitude(v))),specular);
            }
        }
        return i;
    }

}
