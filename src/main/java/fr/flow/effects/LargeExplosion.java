package fr.flow.effects;


import fr.flow.utils.DirectionalParticleCollection;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class LargeExplosion {

    public LargeExplosion(World world,Location location, boolean spawn_fire){
        List<DirectionalParticleCollection> particles = new ArrayList<>();
        Vector velocity = new Vector();
        particles.add(new DirectionalParticleCollection(Particle.SMALL_FLAME, location, velocity, 200, 0.5));
        particles.add(new DirectionalParticleCollection(Particle.SMOKE_LARGE, location, velocity, 100, 0.5));
        particles.add(new DirectionalParticleCollection(Particle.SMOKE_NORMAL, location, velocity, 160, 0.5));
        particles.add(new DirectionalParticleCollection(Particle.CAMPFIRE_COSY_SMOKE, location, velocity, 160, 0.2));

        new RealisticExplosion(world, location, 10, particles, 0.2, spawn_fire);
    }
}


