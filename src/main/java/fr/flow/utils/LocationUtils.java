package fr.flow.utils;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LocationUtils {

    private static double randomDouble(double min, double max) {
        Random random = new Random();
        return min + random.nextDouble() * (max - min);
    }

    public static Location randomOffsetLocation(Location location, double offsetX, double offsetY, double offsetZ) {
        double posX = randomDouble(-offsetX, offsetX);
        double posY = randomDouble(-offsetY, offsetY);
        double posZ = randomDouble(-offsetZ, offsetZ);
        return location.clone().add(posX, posY, posZ);
    }

    public static Location randomOffsetLocationInDirection(Location location, Vector direction) {
        Vector temp = direction.clone().normalize();
        double offsetX = temp.getX();
        double offsetY = temp.getY();
        double offsetZ = temp.getZ();

        double posX = randomDouble(offsetX - 0.5, offsetX + 0.5);
        double posY = randomDouble(offsetY - 0.5, offsetY + 0.5);
        double posZ = randomDouble(offsetZ - 0.5, offsetZ + 0.5);
        Location l = location.clone();
        l.add(posX, posY, posZ);
        return l;
    }

    public static List<Location> generateCircle(Location location, double radius, int num_points) {
        List<Location> locations = new ArrayList<>();
        for (double i = 0; i < Math.PI * 2; i += (double) 1 / num_points) {
            double x = Math.cos(i) * 1;
            double y = 0;
            double z = Math.sin(i) * 1;
            Vector relative_position = new Vector(x, y, z).normalize();
            locations.add(location.clone().add(relative_position.multiply(radius)));
        }
        return locations;
    }

    public static Location randomPointInCircle(Location location, double radius) {
        double value = Utils.randomDouble(0, Math.PI * 2);
        double x = Math.cos(value) * 1;
        double y = 0;
        double z = Math.sin(value) * 1;
        Vector relative_position = new Vector(x, y, z).normalize();
        return location.clone().add(relative_position.multiply(Utils.randomDouble(0, radius)));
    }

    public static Location projectPointOnPlane(Location plane_point, Vector normal, Location point) {
        double distance = point.clone().subtract(plane_point).toVector().dot(normal);
        return point.clone().subtract(normal.clone().multiply(distance));
    }
}