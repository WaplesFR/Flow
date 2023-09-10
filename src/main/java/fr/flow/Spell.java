package fr.flow;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public abstract class Spell {


    protected Player player;

    protected Location position, start_position;

    protected Vector velocity;

    protected double max_distance;
    protected int id, parent_id;

    protected int lifetime, max_lifetime;

    protected float size;

    protected boolean alive, daemon;

    protected boolean collide_with_liquids;

    protected String name;

    protected SpellManager spell_manager;

    protected World world;

    public Player getPlayer() {
        return player;
    }

    public Location getPosition() {
        return position.clone();
    }

    public Location getStartPosition() {
        return start_position.clone();
    }

    public Vector getVelocity() {
        return velocity.clone();
    }

    public double getMaxDistance() {
        return max_distance;
    }

    public int getId() {
        return id;
    }

    public int getParentID() {
        return parent_id;
    }

    public int getLifeTime() {
        return lifetime;
    }

    public int getMaxLifeTime() {
        return max_lifetime;
    }

    public double getSize(){
        return size;
    }

    public boolean isAlive() {
        return alive;
    }

    public boolean isDaemon() {
        return daemon;
    }

    public boolean doLiquidCollision(){
        return collide_with_liquids;
    }

    public String getName() {
        return name;
    }

    public SpellManager getSpellManager() {
        return spell_manager;
    }

    public World getWorld(){
        return world;
    }

    public void setAlive(boolean bool){
        alive = bool;
    }

    public void setPosition(Location position) {
        this.position = position;
    }

    public void setStartPosition(Location startPosition) {
        this.start_position = startPosition;
    }

    public void setVelocity(Vector velocity) {
        this.velocity = velocity;
    }

    public void setDaemon(boolean daemon) {
        this.daemon = daemon;
    }
}