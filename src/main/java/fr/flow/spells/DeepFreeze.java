package fr.flow.spells;

import fr.flow.BaseSpell;
import fr.flow.SpellManager;
import fr.flow.utils.DirectionalParticle;
import fr.flow.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class DeepFreeze extends BaseSpell {

    private List<Block> blocks_to_freeze, frozen_blocks;
    private int freeze_distance;
    private boolean freeze;
    private Location hit_location;
    private Random r;

    @Override
    public void init(SpellManager spell_manager, Location location, Player player, int id, int parent_id, String name){
        super.init(spell_manager, location, player, id, parent_id, name);
        this.max_lifetime = 120;
        this.max_distance = 50;
        this.collide_with_liquids = true;
        this.velocity = player.getLocation().getDirection().multiply(2);
        this.size = 1;

        this.blocks_to_freeze = new ArrayList<>();
        this.frozen_blocks = new ArrayList<>();
        this.freeze_distance = 30;
        this.freeze = false;
        this.r = new Random();
    }

    @Override
    public void tick(){
        super.tick();
        if(freeze){
            if(blocks_to_freeze.isEmpty()){
                alive = false;
                return;
            }
            List<Block> new_blocks = new ArrayList<>();
            for(Block block: blocks_to_freeze){
                Location loc = block.getLocation();
                double distance = loc.distance(hit_location);
                if(distance <= freeze_distance && !frozen_blocks.contains(block) && !block.getType().isAir()){
                    double prop = 1 - (distance / freeze_distance) + 0.1;
                    if(prop > r.nextDouble()) {
                        boolean placed = false;
                        switch (block.getType()) {
                            case LAVA:
                                continue;
                            case WATER:
                                block.setType(Material.ICE);
                                placed = true;
                                new_blocks.addAll(getNeighbours(block));
                                break;
                            default:
                                if(!block.isPassable()) {
                                    Block temp = block.getRelative(0, 1, 0);
                                    Material m = temp.getType();
                                    if (m.isAir() || m == Material.FIRE) {
                                        temp.setType(Material.SNOW);
                                        placed = true;
                                        double speed = Utils.randomDouble(0.1, 0.5);
                                        DirectionalParticle.spawn(Particle.SNOWFLAKE, block.getLocation(), new Vector(0, 1, 0), speed);
                                        new_blocks.addAll(getNeighbours(block));
                                    }
                                } else new_blocks.addAll(getNeighbours(block));

                        }
                        if(placed){
                            PotionEffect effect = new PotionEffect(PotionEffectType.SLOW, 5, 20, false, false);
                            Collection<Entity> entities = loc.getWorld().getNearbyEntities(loc, 2, 2, 2);
                            for(Entity entity: entities)
                                if(entity instanceof LivingEntity){
                                    LivingEntity e = (LivingEntity) entity;
                                    e.damage(2);
                                    e.addPotionEffect(effect);
                                }

                        }
                    }
                }
                frozen_blocks.add(block);
            }
            blocks_to_freeze.clear();
            blocks_to_freeze.addAll(new_blocks);
        } else {
            position = position.add(velocity);
            for (int i = 0; i < 4; i++) {
                double x = Utils.randomDouble(-0.5, 0.5);
                double y = Utils.randomDouble(-0.1, -1);
                double z = Utils.randomDouble(-0.5, 0.5);
                Vector vel = new Vector(x, y, z);
                DirectionalParticle.spawn(Particle.END_ROD, position, vel, 0.3);
            }
        }
    }

    private List<Block> getNeighbours(Block block){
        List<Block> neighbours = new ArrayList<>();
        for(int i=-1; i<2; i++)
            for(int j=-1; j<2; j++)
                for(int k=-1; k<2; k++)
                    if(!(i == 0 && j == 0 && k == 0)) {
                        Block temp = block.getRelative(i, j, k);
                        if(!frozen_blocks.contains(temp)) neighbours.add(temp);
                    }


        return neighbours;
    }

    @Override
    public void onEntityHit(Location location, Entity entity) {
        if(freeze)return;
        freeze = true;
    }

    @Override
    public void onPlayerHit(Location location, Player player) {
        if(freeze)return;
        freeze = true;
    }

    @Override
    public void onBlockHit(Location location, Block block, int wallThickness) {
        if(freeze)return;
        freeze = true;
        hit_location = block.getLocation();
        blocks_to_freeze.add(block);
    }

    @Override
    public void onOutOfRange() {
        alive = false;
    }
}
