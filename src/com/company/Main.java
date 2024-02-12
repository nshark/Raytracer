package com.company;

import jdk.jfr.Description;
import jdk.jfr.Label;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main {
    public static final double epsilon = 0.001d;
    private static final double canvasWidth = 500;
    private static final double canvasHeight = 500;
    private static final double distance = 1;
    private static final double viewpointWidth = 1;
    private static final double viewpointHeight = 1;
    private static final double moveAmount = 0.05;
    private static final double rotAmount = 0.05;
    private static final double[] cameraPos = {0, 0, 0};
    private static double[][] cameraRot = {{1d, 0d, 0d}, {0d, 1d, 0d}, {0d, 0d, 1d}};
    private static final double[] rotVector = {0d, 0d, 0d};

    //private static double[][] cameraRot = {{1d,0d,0d},{0d, Math.cos(Math.toRadians(45)), -1d*Math.sin(Math.toRadians(45))},{0d,Math.sin(Math.toRadians(45)),Math.cos(Math.toRadians(45))}};
    public static void main(String[] args) {
        // write your code here
        cameraRot = rotationVecToRotMatrix(rotVector);
        gui GUI = new gui();
        ArrayList<renderable> objects = new ArrayList<>();
        ArrayList<Light> lights = new ArrayList<>();
        objects.add(new Sphere(0, -1, 3, 1, 500, 0.2, Color.RED));
        objects.add(new Sphere(2, 0, 4, 1, 500, 0.3, Color.BLUE));
        objects.add(new Sphere(-2, 0, 4, 1, 10, 0.4, Color.GREEN));
        objects.add(new Triangle(new double[][]{{0d, 0d, 1d}, {1d, 1d, 1d}, {-1d, 1d, 1d}},500,0.4, Color.MAGENTA));
        objects.add(new Sphere(0, -5001, 0, 5000, 1000, 0.1, Color.YELLOW));
        lights.add(new Light(0.2));
        lights.add(new PointLight(0.6, new double[]{2d, 1d, 0d}));
        lights.add(new DirectionalLight(0.2, new double[]{1d, 4d, 4d}));
        while (true) {
            ExecutorService es = Executors.newCachedThreadPool();
            Future<ArrayList<ArrayList<Color>>> ColorA = es.submit(new Callable<ArrayList<ArrayList<Color>>>() {
                @Override
                public ArrayList<ArrayList<Color>> call() {
                    return raytraceSection(-250, -250, 0, 0, objects, lights);
                }
            });
            Future<ArrayList<ArrayList<Color>>> ColorB = es.submit(new Callable<ArrayList<ArrayList<Color>>>() {
                @Override
                public ArrayList<ArrayList<Color>> call() {
                    return raytraceSection(0, -250, 250, 0, objects, lights);
                }
            });
            Future<ArrayList<ArrayList<Color>>> ColorC = es.submit(new Callable<ArrayList<ArrayList<Color>>>() {
                @Override
                public ArrayList<ArrayList<Color>> call() {
                    return raytraceSection(-250, 0, 0, 250, objects, lights);
                }
            });
            Future<ArrayList<ArrayList<Color>>> ColorD = es.submit(new Callable<ArrayList<ArrayList<Color>>>() {
                @Override
                public ArrayList<ArrayList<Color>> call() {
                    return raytraceSection(0, 0, 250, 250, objects, lights);
                }
            });
            es.shutdown();
            try {
                es.awaitTermination(1, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                blitArray(ColorA.get(), -250, -250, GUI);
                blitArray(ColorB.get(), 0, -250, GUI);
                blitArray(ColorC.get(), -250, 0, GUI);
                blitArray(ColorD.get(), 0, 0, GUI);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            GUI.update();
            updateCameraPos(GUI.listener);
            /*System.out.println(Arrays.toString(GUI.listener.directionalKeys));
            System.out.println(Arrays.toString(GUI.listener.rotationalKeys));
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            //cameraPos.set(0,cameraPos.get(0)+0.01d);
        }
    }
    private static void blitArray(ArrayList<ArrayList<Color>> color, int x1, int y1, gui GUI){
        for (int i = 0; i < color.size(); i++) {
            for (int j = 0; j < color.get(0).size(); j++) {
                GUI.putPixel(i+x1, j+y1,color.get(i).get(j));
            }
        }
    }
    private static ArrayList<ArrayList<Color>> raytraceSection(int x1, int y1, int x2, int y2, ArrayList<renderable> objects, ArrayList<Light> lights) {
        ArrayList<ArrayList<Color>> screen = new ArrayList<>();
        for (int x = x1; x < x2; x++) {
            ArrayList<Color> screenRow = new ArrayList<>();
            for (int y = y1; y < y2; y++) {
                Ray ray = new Ray(cameraPos, matrixMultiplication(canvasToViewport(x, y), cameraRot));
                Color c = traceRay(ray, 1, Double.MAX_VALUE, objects, lights, 1);
                screenRow.add(c);
            }
            screen.add(screenRow);
        }

        return screen;
    }

    public static double[] canvasToViewport(int x, int y) {
        double[] vector = new double[3];
        vector[0] = ((double) (x) * (viewpointWidth) / (canvasWidth));
        vector[1] = ((double) (y) * (viewpointHeight) / (canvasHeight));
        vector[2] = (distance);
        return vector;
    }

    public static double[] matrixMultiplication(double[] vector, double[][] matrix) {
        double x = vector[0];
        double y = vector[1];
        double z = vector[2];
        return new double[]{
                matrix[0][0] * x + matrix[0][1] * y + matrix[0][2] * z,
                matrix[1][0] * x + matrix[1][1] * y + matrix[1][2] * z,
                matrix[2][0] * x + matrix[2][1] * y + matrix[2][2] * z};
    }

    public static double dotProduct(double[] v1, double[] v2) {
        double toReturn = 0;
        for (int i = 0; i < v1.length; i++) {
            toReturn += v1[i] * v2[i];
        }
        return toReturn;
    }
    public static double[] crossProduct(double[] v, double[] w){
        double[] product = new double[3];
        product[0] = v[1]*w[2] - v[2]*w[1];
        product[1] = v[2]*w[0]- v[0]*w[2];
        product[2] = v[0]*w[1] - v[1]*w[0];
        return product;
    }
    public static double[] addVectors(double[] v1, double[] v2) {
        return new double[]{v1[0] + v2[0],
                v1[1] + v2[1],
                v1[2] + v2[2]};
    }

    public static double[] subVectors(double[] v1, double[] v2) {
        return new double[]{v1[0] - v2[0], v1[1] - v2[1], v1[2] - v2[2]};
    }

    public static double[] scaleVector(double[] v1, double d1) {
        return new double[]{v1[0] * d1, v1[1] * d1, v1[2] * d1};
    }

    public static double magnitude(double[] v1) {
        return Math.sqrt(Math.pow(v1[0], 2) + Math.pow(v1[1], 2) + Math.pow(v1[2], 2));
    }

    public static double[] normalizeVector(double[] v1) {
        double mag = magnitude(v1);
        if (mag > 0) {
            return new double[]{v1[0] / mag, v1[1] / mag, v1[2] / mag};
        }
        return new double[]{0d, 0d, 0d};
    }

    public static Color traceRay(Ray ray, double min_t, double max_t, ArrayList<renderable> objects, ArrayList<Light> lights, int recursion_depth) {
        intersectionInfo info = closestIntersection(ray, min_t, max_t, objects);
        if (info.collider() == null) {
            return Color.black;
        }
        double[] point = addVectors(cameraPos, scaleVector(ray.direction, info.closest_t()));
        double[] normal = info.collider().getNormal(point, ray);
        normal = normalizeVector(normal);
        float lightAtPoint = (float) computeLighting(point, normal, scaleVector(ray.direction, -1d), info.collider().getSpecular(), lights, objects);
        var color = info.collider().getColor();
        var local_color = new Color(getScaledColor(color.getRed(), lightAtPoint),
                getScaledColor(color.getGreen(), lightAtPoint),
                getScaledColor(color.getBlue(), lightAtPoint));
        if (recursion_depth <= 0 || info.collider().getReflective() <= 0) {
            return local_color;
        }
        var reflectedRay = ReflectRay(scaleVector(ray.direction, -1d), normal);
        var reflected_color = traceRay(new Ray(point, reflectedRay), epsilon, Double.MAX_VALUE, objects, lights, recursion_depth - 1);
        return lerpColor(local_color, reflected_color, info.collider().getReflective());
    }

    private static Color lerpColor(Color A, Color B, double scalar) {
        var decA = decomposeColor(A);
        var decB = decomposeColor(B);
        var scaledDecA = scaleVector(decA, (1 - scalar));
        var scaledDecB = scaleVector(decB, scalar);
        var sum = addVectors(scaledDecA, scaledDecB);
        return reconstructColor(sum);
    }

    private static double[] decomposeColor(Color A) {
        return new double[]{(double) A.getRed(), (double) A.getGreen(), (double) A.getBlue()};
    }

    private static Color reconstructColor(double[] A) {
        return new Color((int) Math.round(A[0]), (int) Math.round(A[1]), (int) Math.round(A[2]));
    }
    public static intersectionInfo closestIntersection(Ray ray, double min_t, double max_t, ArrayList<renderable> objects) {
        double closest_t = Double.MAX_VALUE;
        renderable closestObject = null;
        for (renderable s : objects) {
            double[] ts = s.collides(ray);
            if (ts[0] < closest_t && ts[0] > min_t && ts[0] < max_t) {
                closest_t = ts[0];
                closestObject = s;
            }
            if (ts.length>1) {
                if (ts[1] < closest_t && ts[1] > min_t && ts[1] < max_t) {
                    closest_t = ts[1];
                    closestObject = s;
                }
            }
        }
        return new intersectionInfo(closest_t, closestObject);
    }

    /**
     * A version of closestIntersection that returns early if it finds any intersections - this is slightly more efficient
     */
    public static intersectionInfo isInShadow(Ray ray, double min_t, double max_t, ArrayList<renderable> objects) {
        double closest_t = Double.MAX_VALUE;
        renderable closestObject = null;
        for (renderable s : objects) {
            double[] ts = s.collides(ray);
            if (ts[0] < closest_t && ts[0] > min_t && ts[0] < max_t) {
                closest_t = ts[0];
                closestObject = s;
            }
            if (ts.length>1) {
                if (ts[1] < closest_t && ts[1] > min_t && ts[1] < max_t) {
                    closest_t = ts[1];
                    closestObject = s;
                }
            }
            if (closestObject != null) {
                return new intersectionInfo(closest_t, closestObject);
            }
        }
        return new intersectionInfo(closest_t, null);
    }
    public static double computeLighting(double[] point, double[] normal, double[] v, double specular, ArrayList<Light> lights, ArrayList<renderable> objects) {
        double returnValue = 0;
        for (Light l : lights) {
            returnValue += l.computeLighting(point, normal, v, specular, objects);
        }
        return returnValue;
    }

    public static int getScaledColor(int colorValue, float lightAtPoint) {
        return Math.max(0, Math.min(255, Math.round(colorValue * lightAtPoint)));
    }

    public static double[] ReflectRay(double[] direction, double[] normal) {
        return Main.subVectors(Main.scaleVector(normal, 2 * Main.dotProduct(normal, direction)), direction);
    }

    private static void updateCameraPos(keyListener listener) {
        boolean[] dirKeys = listener.directionalKeys;
        boolean[] rotKeys = listener.rotationalKeys;
        for (int i = 0; i < dirKeys.length; i++) {
            if (dirKeys[i]) {
                int axis = i / 2;
                int sign = (i % 2) * 2 - 1;

                cameraPos[axis] = cameraPos[axis] + moveAmount * sign;
            }
        }
        boolean shouldRecompute = false;
        for (int i = 0; i < rotKeys.length; i++) {
            if (rotKeys[i]) {
                int axis = i / 2;
                int sign = (i % 2) * 2 - 1;
                shouldRecompute = true;
                rotVector[axis] = rotVector[axis] + rotAmount * sign;
            }
        }
        if (shouldRecompute){
            cameraRot = rotationVecToRotMatrix(rotVector);
        }
    }

    public static double[][] rotationVecToRotMatrix(double[] rVector) {
        double a = rVector[0];
        double b = rVector[1];
        double c = rVector[2];
        double[][] rotMatrix =
                {{Math.cos(b) * Math.cos(c),
                        Math.sin(a) * Math.sin(b) * Math.cos(c) - Math.cos(a) * Math.sin(c),
                        Math.cos(a) * Math.sin(b) * Math.cos(c) + Math.sin(a) * Math.sin(c)},
                {Math.cos(b) * Math.sin(c),
                        Math.sin(a) * Math.sin(b) * Math.sin(c) + Math.cos(a) * Math.cos(c),
                        Math.cos(a) * Math.sin(b) * Math.sin(c) - Math.sin(a) * Math.cos(c)},
                {-1 * Math.sin(b),
                        Math.sin(a) * Math.cos(b),
                        Math.cos(a) * Math.cos(b)}};
        return rotMatrix;
    }
    public static double determinant(double[] v1, double[] v2, double[] v3){
        return v1[0] * v2[1] * v3[2] + v1[1] * v2[2] * v3[0] + v1[2] * v2[0] * v3[1] -
                (v1[2] * v2[1] * v3[0] + v1[1] * v2[0] * v3[2] + v1[0] * v2[2] * v3[1]);
    }
}
