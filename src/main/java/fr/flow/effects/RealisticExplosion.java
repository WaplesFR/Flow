package fr.flow.effects;

import fr.flow.utils.DirectionalParticleCollection;
import fr.flow.utils.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class RealisticExplosion {

    public RealisticExplosion(World world, Location location, int power, List<DirectionalParticleCollection> particles, double flying_block_threshold, boolean spawn_fire){
        Location block_location = location.getBlock().getLocation();
        List<Block> removed_blocks = new ArrayList<>();
        List<Material> removed_blocks_materials = new ArrayList<>();
        int rays = (int)(Math.pow(power*7, 2));
        for(int i=0; i<rays; i++) {
            Random r = new Random();
            double x = r.nextGaussian();
            double y = r.nextGaussian();
            double z = r.nextGaussian();
            double ratio = 1/Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
            x *= ratio*power;
            y *= ratio*power;
            z *= ratio*power;
            Vector vel = new Vector(x, y, z);
            double length = vel.length()/2;
            RayTraceResult result = world.rayTraceBlocks(block_location, vel, length, FluidCollisionMode.SOURCE_ONLY);
            if(result != null){
                Block hitBlock = result.getHitBlock();
                Material material = hitBlock.getType();
                if(!material.isAir()){
                    float blastResistance = material.getBlastResistance();
                    if(blastResistance < power*20){
                        hitBlock.setType(Material.AIR);
                        removed_blocks.add(hitBlock);
                        removed_blocks_materials.add(material);
                    }
                }
            }
        }
        if(spawn_fire) {
            int numFire = 0;
            for (int i = 0; i < power*10; i++) {
                Random r = new Random();
                double x = r.nextGaussian();
                double y = r.nextGaussian();
                double z = r.nextGaussian();
                Vector vel = new Vector(x, y, z).normalize();
                double length = Math.min(power, 120);
                RayTraceResult result = world.rayTraceBlocks(block_location, vel, length, FluidCollisionMode.NEVER);
                if (result != null) {
                    Block block = result.getHitPosition().toLocation(world).getBlock();
                    if(block.getType().getBlastResistance() < power*10) {
                        block.setType(Material.FIRE);
                        numFire++;
                    }
                }
            }
        }
        int falling_block_limit = 1000;
        int falling_blocks = 0;
        for(int i=0; i<removed_blocks.size(); i++){
            if(falling_blocks > falling_block_limit)break;

            Block temp = removed_blocks.get(i);
            if(Utils.randomFloat(0, 1) < flying_block_threshold) {
                Location l = temp.getLocation().add(0, 1, 0);
                Material material = removed_blocks_materials.get(i);
                if(!material.equals(Material.WATER) && !material.equals(Material.LAVA)) {
                    FallingBlock falling_block = world.spawnFallingBlock(l, material.createBlockData());
                    try {
                        falling_block.setVelocity(l.subtract(location).toVector().normalize().setY(Utils.randomFloat(0.1F, 1)).multiply((double) power / 10));
                        falling_blocks++;
                    } catch (IllegalArgumentException e) {
                        System.out.println("Rerun");
                        falling_block.remove();
                        i--;
                    }
                }
            }
        }

        for(DirectionalParticleCollection temp: particles){
            temp.randomizeLocations(power/2d);
            temp.adjustVelocities();
            temp.setSpeed((double)power/25);
            temp.spawn();
        }

        Collection<Entity> nearby_entities = world.getNearbyEntities(location, power, power, power);
        for(Entity entity: nearby_entities){
            Vector velocity = entity.getLocation().subtract(location).toVector();
            double distance = velocity.length();
            double distance_power = (double) power/distance < power ? (double) power/distance: power;
            if(!(entity instanceof FallingBlock))entity.setVelocity(velocity.normalize().multiply(distance_power));

            if(entity instanceof LivingEntity)((LivingEntity) entity).damage(distance_power*2);

        }

        world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 10F, 0.6F);
    }
}
