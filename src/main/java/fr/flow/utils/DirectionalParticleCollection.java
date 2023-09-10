package fr.flow.utils;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class DirectionalParticleCollection {

    private final Location location;
    private final Particle particle;
    private final Vector velocity;
    private double speed;

    public List<Location> particle_locations = new ArrayList<>();
    public List<Vector> particle_velocities = new ArrayList<>();

    public DirectionalParticleCollection(Particle particle, Location location, Vector velocity, int amount, double speed){
        this.location = location;
        this.particle = particle;
        this.velocity = velocity;
        this.speed = speed;

        for(int i=0; i<amount; i++){
            particle_locations.add(location.clone());
            particle_velocities.add(velocity.clone());
        }
    }

    public void setSpeed(double speed){
        this.speed = speed;
    }

    public void spawn(){
        for(int i=0; i<particle_locations.size(); i++)
            DirectionalParticle.spawn(particle, particle_locations.get(i), particle_velocities.get(i), speed);
    }

    public void adjustVelocities(){
        for(int i=0; i<particle_locations.size(); i++) particle_velocities.set(i, particle_locations.get(i).clone().subtract(location).toVector().normalize());

    }

    public void randomizeLocationsInDirection(){
        particle_locations.replaceAll(location1 -> LocationUtils.randomOffsetLocationInDirection(location1, velocity));
    }

    public void randomizeLocations(double offsetX, double offsetY, double offsetZ){
        particle_locations.replaceAll(location1 -> LocationUtils.randomOffsetLocation(location1, offsetX, offsetY, offsetZ));
    }

    public void randomizeLocations(double maxDistance){
        randomizeLocations(maxDistance, maxDistance, maxDistance);
    }
}