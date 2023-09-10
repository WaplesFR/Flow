package fr.flow.utils;

import java.util.Arrays;
import java.util.Random;

public class Utils {

    public static float randomFloat(float min, float max){
        Random r = new Random();
        return r.nextFloat() * (max-min) + min;
    }

    public static double randomDouble(double min, double max){
        Random r = new Random();
        return r.nextDouble() * (max-min) + min;
    }

    public static boolean arraysEqual(Integer[] array, Integer[] other){
        if(array.length != other.length) return false;
        Arrays.sort(array);
        Arrays.sort(other);
        return Arrays.equals(array, other);
    }
}
