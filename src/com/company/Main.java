package com.company;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final double canvasWidth = 500;
    private static final double canvasHeight = 500;
    private static final double distance = 1;
    public static final double epsilon = 0.00001d;
    private static final double viewpointWidth = 1;
    private static final double viewpointHeight = 1;
    private static ArrayList<Double> cameraPos = new ArrayList<>(List.of(-3d,0d,0d));
    public static void main(String[] args) {
	// write your code here
        gui GUI = new gui();
        ArrayList<renderable> objects = new ArrayList<>();
        ArrayList<Light> lights = new ArrayList<>();
        objects.add(new Sphere(0,-1,3, 1, 500, Color.RED));
        objects.add(new Sphere(2,0,4,1, 500, Color.BLUE));
        objects.add(new Sphere(-2,0,4,1, 10, Color.GREEN));
        objects.add(new Sphere(0,-5001,0,5000,1000,Color.YELLOW));
        lights.add(new Light(0.2));
        lights.add(new PointLight(0.6, new ArrayList<>(List.of(2d, 1d, 0d))));
        lights.add(new DirectionalLight(0.2, new ArrayList<>(List.of(1d, 4d, 4d))));
        while(true){
            for (int x = -250; x < canvasWidth/2; x++) {
                for (int y = -250; y < canvasHeight/2; y++) {
                    Ray ray = new Ray(cameraPos, canvasToViewport(x,y));
                    Color c = traceRay(ray, 1, Double.MAX_VALUE, objects, lights);
                    GUI.putPixel(x,y,c);
                }
            }
            GUI.update();
            cameraPos.set(0,cameraPos.get(0)+0.01d);
        }
    }
    public static ArrayList<Double> canvasToViewport(int x, int y){
        ArrayList<Double> vector = new ArrayList<>(3);
        vector.add((double)(x) * (viewpointWidth)/(canvasWidth));
        vector.add((double)(y) * (viewpointHeight)/(canvasHeight));
        vector.add(distance);
        return vector;
    }
    public static double dotProduct(ArrayList<Double> v1, ArrayList<Double> v2){
        return v1.get(0)*v2.get(0)+v1.get(1)*v2.get(1)+v1.get(2)*v2.get(2);
    }
    public static ArrayList<Double> addVectors(ArrayList<Double> v1, ArrayList<Double> v2){
        return new ArrayList<>(List.of(v1.get(0) + v2.get(0),
                v1.get(1) + v2.get(1),
                v1.get(2) + v2.get(2)));
    }
    public static ArrayList<Double> subVectors(ArrayList<Double> v1, ArrayList<Double> v2){
        return new ArrayList<>(List.of(v1.get(0) - v2.get(0),
                v1.get(1) - v2.get(1),
                v1.get(2) - v2.get(2)));
    }
    public static ArrayList<Double> scaleVector(ArrayList<Double> v1, double d1){
        return new ArrayList<>(List.of(v1.get(0) * d1,
                v1.get(1) * d1,
                v1.get(2) * d1));
    }
    public static double magnitude(ArrayList<Double> v1){
        return Math.sqrt(Math.pow(v1.get(0), 2) + Math.pow(v1.get(1), 2) + Math.pow(v1.get(2), 2));
    }
    public static ArrayList<Double> normalizeVector(ArrayList<Double> v1){
        double mag = magnitude(v1);
        if (mag > 0) {
            return new ArrayList<>(List.of(v1.get(0) / mag, v1.get(1) / mag, v1.get(2) / mag));
        }
        return new ArrayList<>(List.of(0d, 0d, 0d));
    }
    public static Color traceRay(Ray ray, double min_t, double max_t, ArrayList<renderable> objects, ArrayList<Light> lights){
        double closest_t = Double.MAX_VALUE;
        renderable closestObject = null;
        for(renderable s : objects){
            double[] ts = s.collides(ray);
            if (ts[0] < closest_t && ts[0] > min_t && ts[0] < max_t){
                closest_t = ts[0];
                closestObject = s;
            }
            if (ts[1] < closest_t && ts[1] > min_t && ts[1] < max_t){
                closest_t = ts[1];
                closestObject = s;
            }
        }
        if(closestObject == null){
            return Color.white;
        }
        ArrayList<Double> point = addVectors(cameraPos, scaleVector(ray.direction, closest_t));
        ArrayList<Double> normal = subVectors(point, closestObject.getCenter());
        normal = normalizeVector(normal);
        float lightAtPoint = (float) computeLighting(point,normal,scaleVector(ray.direction,-1d),closestObject.getSpecular(),lights);
        var color = closestObject.getColor();
        return new Color(getScaledColor(color.getRed(),lightAtPoint),
                getScaledColor(color.getGreen(),lightAtPoint),
                getScaledColor(color.getBlue(),lightAtPoint));
    }
    public static double computeLighting(ArrayList<Double> point, ArrayList<Double> normal, ArrayList<Double> v, double specular, ArrayList<Light> lights){
        double returnValue = 0;
        for(Light l : lights){
            returnValue += l.computeLighting(point, normal, v, specular);
        }
        return returnValue;
    }
    public static int getScaledColor(int colorValue, float lightAtPoint){
        return Math.max(0,Math.min(255,Math.round(colorValue*lightAtPoint)));
    }

}
