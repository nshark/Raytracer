package com.company;

import java.awt.*;
import java.util.ArrayList;

public class Triangle implements renderable{
    private final double[][] pos;
    private final Color color;
    private final int specular;
    private final double reflective;
    private final double[] normal;
    private final double k;
    private final double[][] edges;
    Triangle(double[][] pos, int specular, double reflective, Color color){
        this.color = color;
        this.pos = pos;
        this.specular = specular;
        this.reflective = reflective;
        double[] A = Main.subVectors(pos[1],pos[0]);
        double[] B = Main.subVectors(pos[2],pos[0]);
        normal = Main.crossProduct(A, B);
        k = -1d*Main.dotProduct(normal, pos[0]);
        edges = new double[3][3];
        edges[0] = (Main.subVectors(pos[1], pos[0]));
        edges[1] = (Main.subVectors(pos[2], pos[1]));
        edges[2] = (Main.subVectors(pos[0], pos[2]));

    }
    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public double[] collides(Ray ray) {
        double denom = Main.dotProduct(normal, ray.direction);
        if (denom==0){
            return new double[]{-1d};
        }
        double t = -1*((Main.dotProduct(ray.origin,normal)+k)/denom);
        if (t<=0){
            return new double[]{-1d};
        }
        if(isInside(Main.addVectors(ray.origin, Main.scaleVector(ray.direction, t)))){
            return new double[]{t};
        }
        return new double[]{-1d};
    }
    private boolean isInside(double[] point){
        for (int i = 0; i < 3; i++) {
            double[] C = Main.subVectors(point, pos[i]);
            if(Main.dotProduct(normal, Main.crossProduct(edges[i], C)) <= 0){
                return false;
            }
        }
        return true;
    }
    @Override
    public double[] getNormal(double[] point, Ray ray) {
        double det = Main.determinant(Main.subVectors(ray.origin, pos[0]), Main.subVectors(ray.origin, pos[1]), Main.subVectors(ray.origin, pos[2]));
        if(det > 0) {
            return normal;
        }
        return Main.scaleVector(normal, -1);
    }

    @Override
    public int getSpecular() {
        return specular;
    }

    @Override
    public double getReflective() {
        return reflective;
    }
}
