package fr.flow.effects;

import fr.flow.utils.DirectionalParticleCollection;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class MediumExplosion {

    public MediumExplosion(World world, Location location, boolean spawn_fire){
        List<DirectionalParticleCollection> particles = new ArrayList<>();
        Vector velocity = new Vector();
        particles.add(new DirectionalParticleCollection(Particle.SMALL_FLAME, location, velocity, 80, 0.3));
        particles.add(new DirectionalParticleCollection(Particle.SMOKE_LARGE, location, velocity, 30, 0.3));
        particles.add(new DirectionalParticleCollection(Particle.SMOKE_NORMAL, location, velocity, 40, 0.3));
        particles.add(new DirectionalParticleCollection(Particle.CAMPFIRE_COSY_SMOKE, location, velocity, 40, 0.1));

        new RealisticExplosion(world, location, 5, particles, 0.2, spawn_fire);
    }
}