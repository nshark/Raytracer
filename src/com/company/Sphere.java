package com.company;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
public class Sphere implements renderable{
    private final double x;
    private final double y;
    private final double z;
    private final double radius;
    private final Color color;
    private final int specular;
    private final double reflective;
    public Sphere(double x, double y, double z, double radius, int specular, double reflective, Color color){
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
        this.color = color;
        this.specular = specular;
        this.reflective = reflective;
    }

    @Override
    public Color getColor() {
        return color;
    }
    @Override
    public double[] collides(Ray ray) {
        double[] oc = new double[]{ray.origin[0] - x, ray.origin[1] - y, ray.origin[2] - z};
        double k1 = Main.dotProduct(ray.direction, ray.direction);
        double k2 = 2*Main.dotProduct(oc, ray.direction);
        double k3 = Main.dotProduct(oc, oc) - this.radius*this.radius;

        double discriminant = k2*k2 - 4*k1*k3;

        if (discriminant < 0){
            return new double[]{Double.MAX_VALUE, Double.MAX_VALUE};
        }

        double t1 = (-k2 + Math.sqrt(discriminant)) / (2*k1);
        double t2 = (-k2 - Math.sqrt(discriminant)) / (2*k1);
        return new double[]{t1, t2};
    }

    @Override
    public double[] getNormal(double[] point, Ray ray) {
        return Main.subVectors(point, new double[]{x,y,z});
    }

    @Override
    public int getSpecular() {
        return specular;
    }

    @Override
    public double getReflective() {
        return this.reflective;
    }
}
