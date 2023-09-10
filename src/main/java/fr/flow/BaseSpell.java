package fr.flow;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class BaseSpell extends Spell implements SpellInteraction{

    @Override
    public void init(SpellManager spell_manager, Location location, Player player, int id, int parent_id, String name){
        this.world = location.getWorld();
        this.spell_manager = spell_manager;
        this.player = player;
        this.id = id;
        this.parent_id = parent_id;
        this.name = name;
        this.velocity = new Vector(0, 0, 0);
        this.start_position = location;
        this.position = start_position.clone();
        this.lifetime = 0;
    }

    public void init(SpellManager spell_manager, Player player, int id, int parent_id, String name){
        Location loc = player.getEyeLocation().add(player.getEyeLocation().getDirection().normalize());
        init(spell_manager, loc, player, id, parent_id, name);
    }



    @Override
    public void tick() {
        lifetime++;
    }

    @Override
    public void onEntityHit(Location location, Entity entity) {

    }

    @Override
    public void onPlayerHit(Location location, Player player) {

    }

    @Override
    public void onBlockHit(Location location, Block block, int wallThickness) {

    }

    @Override
    public void onOutOfRange() {

    }

    @Override
    public void onLifetimeEnd() {

    }

    @Override
    public void terminate(Location location) {

    }


    @Override
    public void kill() {

    }

}