package com.company;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final double canvasWidth = 500;
    private static final double canvasHeight = 500;
    private static final double distance = 1;
    public static final double epsilon = 0.001d;
    private static final double viewpointWidth = 1;
    private static final double viewpointHeight = 1;
    private static ArrayList<Double> cameraPos = new ArrayList<>(List.of(0d,0d,0d));
    public static void main(String[] args) {
	// write your code here
        gui GUI = new gui();
        ArrayList<renderable> objects = new ArrayList<>();
        ArrayList<Light> lights = new ArrayList<>();
        objects.add(new Sphere(0,-1,3, 1, 500, 0.2, Color.RED));
        objects.add(new Sphere(2,0,4,1, 500,0.3, Color.BLUE));
        objects.add(new Sphere(-2,0,4,1, 10,0.4, Color.GREEN));
        objects.add(new Sphere(0,-5001,0,5000,1000, 0.5, Color.YELLOW));
        lights.add(new Light(0.2));
        lights.add(new PointLight(0.6, new ArrayList<>(List.of(2d, 1d, 0d))));
        lights.add(new DirectionalLight(0.2, new ArrayList<>(List.of(1d, 4d, 4d))));
        while(true){
            for (int x = -250; x < canvasWidth/2; x++) {
                for (int y = -250; y < canvasHeight/2; y++) {
                    Ray ray = new Ray(cameraPos, canvasToViewport(x,y));
                    Color c = traceRay(ray, 1, Double.MAX_VALUE, objects, lights, 3);
                    GUI.putPixel(x,y,c);
                }
            }
            GUI.update();
            //cameraPos.set(0,cameraPos.get(0)+0.01d);
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
    public static Color traceRay(Ray ray, double min_t, double max_t, ArrayList<renderable> objects, ArrayList<Light> lights, int recursion_depth){
        intersectionInfo info = closestIntersection(ray, min_t,max_t,objects);
        if(info.collider() == null){
            return Color.black;
        }
        ArrayList<Double> point = addVectors(cameraPos, scaleVector(ray.direction, info.closest_t()));
        ArrayList<Double> normal = subVectors(point, info.collider().getCenter());
        normal = normalizeVector(normal);
        float lightAtPoint = (float) computeLighting(point,normal,scaleVector(ray.direction,-1d),info.collider().getSpecular(),lights,objects, recursion_depth);
        var color = info.collider().getColor();
        var local_color = new Color(getScaledColor(color.getRed(),lightAtPoint),
                getScaledColor(color.getGreen(),lightAtPoint),
                getScaledColor(color.getBlue(),lightAtPoint));
        if (recursion_depth <= 0 || info.collider().getReflective() <= 0){
            return local_color;
        }
        var reflectedRay = ReflectRay(scaleVector(ray.direction, -1d), normal);
        var reflected_color = traceRay(new Ray(ray.origin, reflectedRay), epsilon, Double.MAX_VALUE, objects, lights, recursion_depth-1);
        return lerpColor(local_color, reflected_color, info.collider().getReflective());
    }
    private static Color lerpColor(Color A, Color B, double scalar){
        var decA = decomposeColor(A);
        var decB = decomposeColor(B);
        var scaledDecA = scaleVector(decA, (1-scalar));
        var scaledDecB = scaleVector(decB, scalar);
        var sum = addVectors(scaledDecA, scaledDecB);
        return reconstructColor(sum);
    }
    private static ArrayList<Double> decomposeColor(Color A){
        return new ArrayList<Double>(List.of((double) A.getRed(), (double) A.getGreen(), (double) A.getBlue()));
    }
    private static Color reconstructColor(ArrayList<Double> A){
        return new Color((int) Math.round(A.get(0)), (int) Math.round(A.get(1)), (int) Math.round(A.get(2)));
    }
    public static intersectionInfo closestIntersection(Ray ray, double min_t, double max_t, ArrayList<renderable> objects){
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
        return new intersectionInfo(closest_t, closestObject);
    }
    public static double computeLighting(ArrayList<Double> point, ArrayList<Double> normal, ArrayList<Double> v, double specular, ArrayList<Light> lights, ArrayList<renderable> objects, int recursion_depth){
        double returnValue = 0;
        for(Light l : lights){
            returnValue += l.computeLighting(point, normal, v, specular, objects);
        }
        return returnValue;
    }
    public static int getScaledColor(int colorValue, float lightAtPoint){
        return Math.max(0,Math.min(255,Math.round(colorValue*lightAtPoint)));
    }
    public static ArrayList<Double> ReflectRay(ArrayList<Double> direction, ArrayList<Double> normal) {
        ArrayList<Double> R = Main.subVectors(Main.scaleVector(normal,2*Main.dotProduct(normal, direction)), direction);
        return R;
    }
}
