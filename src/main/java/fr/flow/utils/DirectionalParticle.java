package fr.flow.utils;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

public class DirectionalParticle {

    public static void spawn(Particle particle, Location location, Vector velocity, double speed){
        location.getWorld().spawnParticle(particle, location, 0, velocity.getX(), velocity.getY(), velocity.getZ(), speed);
    }
}