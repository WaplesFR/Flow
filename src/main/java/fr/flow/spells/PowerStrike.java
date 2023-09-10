package fr.flow.spells;

import fr.flow.BaseSpell;
import fr.flow.SpellManager;
import fr.flow.effects.LargeExplosion;
import fr.flow.effects.MediumExplosion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PowerStrike extends BaseSpell {

    private final List<Location> explosion_locations = new ArrayList<>();
    private boolean move;
    private int explosions_per_tick;
    int power;

    @Override
    public void init(SpellManager spell_manager, Player player, int id, int parent_id, String name){
        super.init(spell_manager, player, id, parent_id, name);
        this.max_distance = 100;
        this.max_lifetime = 300;
        this.velocity = player.getLocation().getDirection().multiply(3);
        this.move = true;
        this.explosions_per_tick = 1;
        this.size = 1;
        this.power = 2000;
    }

    @Override
    public void tick() {
        super.tick();
        if(move){
            double divide = 1;
            for(int i=0; i<(1/divide); i++){
                position = position.add(velocity.clone().multiply(divide));
                world.spawnParticle(Particle.SONIC_BOOM, position, 10, 0.2, 0.2, 0.2, 0);
            }
            explosion_locations.add(position.clone());
        }

        if(lifetime > 12){
            int explosions = 0;
            while(explosion_locations.size() > 0 && explosions < explosions_per_tick){
                Location explosionLocation = explosion_locations.remove(0);
                new MediumExplosion(world, explosionLocation, true);
                explosions++;
            }
        }
        if(!move && explosion_locations.isEmpty()){
            alive = false;
        }
    }



    @Override
    public void onBlockHit(Location location, Block block, int wall_thickness) {
        if(move){
            Material material = block.getType();
            if(material.equals(Material.BEDROCK)){
                move = false;
            } else {
                power -= material.getBlastResistance();
                if(power < 0){
                    move = false;
                }
            }
        }
    }

    @Override
    public void onOutOfRange() {
        move = false;
    }


    @Override
    public void terminate(Location location){
        new LargeExplosion(world, location, true);
    }
}