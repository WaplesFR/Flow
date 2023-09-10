package fr.flow;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface SpellInteraction {

    public void init(SpellManager spell_manager, Location location, Player player, int id, int pID, String name);
    public void init(SpellManager spell_manager, Player player, int id, int pID, String name);
    public void tick();
    public void onEntityHit(Location location, Entity entity);
    public void onPlayerHit(Location location, Player player);
    public void onBlockHit(Location location, Block block, int wall_thickness);
    public void onOutOfRange();
    public void onLifetimeEnd();
    public void terminate(Location location);
    public void kill();

}
