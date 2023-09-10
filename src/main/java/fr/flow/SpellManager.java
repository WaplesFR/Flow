package fr.flow;

import fr.flow.exception.NotEnoughFluidException;
import fr.flow.exception.SpellCoolDownException;
import fr.flow.spells.DeepFreeze;
import fr.flow.spells.PowerStrike;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.logging.Logger;

public class SpellManager {

    private final Logger logger;
    private final UserManager user_manager;

    private final HashMap<String, SpellWrapper> spells = new HashMap<>();
    private final HashMap<Integer, BaseSpell> active_spells = new HashMap<>();
    private int active_spell_id;

    public SpellManager(Logger log, UserManager user_manager) {
        this.logger = log;
        this.user_manager = user_manager;
        active_spell_id = 0;
    }

    public void setup() {
          SpellWrapper wraper = new SpellWrapper("PowerStrike", new PowerStrike(), 100, 0);
          registerSpell(wraper);
          wraper = new SpellWrapper("DeepFreeze", new DeepFreeze(), 150, 0);
          registerSpell(wraper);
//        wraper = new SpellWrapper("Explosion", new Explosion(), 100, 100);
//        registerSpell(wraper);
//        wraper = new SpellWrapper("ArrowStorm", new ArrowStorm(), 150, 100);
//        registerSpell(wraper);
//        wraper = new SpellWrapper("DeepFreeze", new DeepFreeze(), 250, 120);
//        registerSpell(wraper);
    }

    public void registerSpell(SpellWrapper wrapper) {
        String spell_name = wrapper.getSpellName();
        if (!spells.containsKey(spell_name)) spells.put(spell_name, wrapper);
        else logger.warning(String.format("Failed to register spell %s as there already is one with the same name!", spell_name));

    }

    public List<String> getSpellNames() {
        Set<String> set = spells.keySet();
        return new ArrayList<>(set);
    }

    public Integer[] getActiveSpellIDs() {
        return active_spells.keySet().toArray(new Integer[0]);
    }

    public BaseSpell getSpell(int spell_id) {
        return active_spells.get(spell_id);
    }

    private SpellWrapper getSpellWrapper(String name) {
        return spells.get(name);
    }

    private BaseSpell[] getChildSpells(int parent_id) {
        ArrayList<BaseSpell> children = new ArrayList<>();
        active_spells.values().stream()
                .filter(spell -> (spell.getParentID() == parent_id))
                .forEach(children::add);
        return children.toArray(new BaseSpell[0]);
    }

    public int castSpell(String name, Player player) {
        SpellWrapper spell_wrapper = getSpellWrapper(name);
        if (spell_wrapper != null) {
            ServerUser user = user_manager.getUser(player);

            try {
                user.canCastSpell(spell_wrapper);
            } catch (NotEnoughFluidException e) {
                player.sendMessage("Not enough Mana to cast spell");
                return -1;
            } catch (SpellCoolDownException e) {
                player.sendMessage("Spell has cooldown");
                return -1;
            }
            BaseSpell spell = spell_wrapper.getSpell();
            user_manager.castSpell(user, spell_wrapper);
            spawnSpell(spell, player, name, 0);
            return spell.getId();

        } else {
            player.sendMessage("No such spell");
            return -1;
        }
    }

    public BaseSpell castSpell(String name, int parent_id, Player player, Location location) {
        SpellWrapper spell_wrapper = getSpellWrapper(name);
        if (spell_wrapper != null) {
            BaseSpell spell = spell_wrapper.getSpell();
            spawnSpell(spell, player, name, parent_id, location);
            return spell;
        }
        return null;
    }

    public void tick() {

        for (int id : active_spells.keySet()) {
            BaseSpell spell = active_spells.get(id);
            if (spell.isAlive()) {
                World world = spell.getWorld();
                Vector velocity = spell.getVelocity();
                Location position = spell.getPosition();
                FluidCollisionMode liquid_collision = FluidCollisionMode.NEVER;
                if (spell.doLiquidCollision())
                    liquid_collision = FluidCollisionMode.ALWAYS;

                velocity = velocity.length() != 0 ? velocity : new Vector(0, 0.000001, 0);
                BlockIterator block_iterator = new BlockIterator(world, position.toVector(), velocity, 0, (int) Math.ceil(velocity.length()));
                while (block_iterator.hasNext()) {
                    Block block = block_iterator.next();
                    RayTraceResult result = block.rayTrace(position, velocity, velocity.length(), liquid_collision);
                    if (result != null) {
                        BlockIterator iterator = new BlockIterator(world, result.getHitPosition(), velocity, 0, 50);
                        int wall_thickness = 0;
                        while (iterator.hasNext())
                            if (!iterator.next().isPassable())
                                wall_thickness++;
                            else break;

                        spell.onBlockHit(result.getHitPosition().toLocation(world), block, wall_thickness);
                        if (!spell.isAlive())
                            terminateSpell(spell, result.getHitPosition().toLocation(world));

                    }
                    Collection<Entity> nearby_entities = world.getNearbyEntities(block.getLocation(), spell.getSize(), spell.getSize(), spell.getSize());
                    for (Entity entity : nearby_entities) {
                        if (!spell.isAlive()) {
                            terminateSpell(spell, block.getLocation());
                            break;
                        }
                        if (entity instanceof Player)
                            if (!entity.equals(spell.getPlayer()))
                                spell.onPlayerHit(block.getLocation(), (Player) entity);
                            else if (!(entity instanceof Item))
                                spell.onEntityHit(block.getLocation(), entity);

                    }
                }
                if (spell.isAlive())
                    spell.tick();

                if (spell.getLifeTime() > spell.getMaxLifeTime()) {
                    spell.onLifetimeEnd();
                    spell.setAlive(false);
                } else if (spell.getPosition().distance(spell.getStartPosition()) > spell.getMaxDistance())
                    spell.onOutOfRange();
                //spell.setAlive(false);

            } else terminateSpell(spell);

        }
    }

    public void terminateSpell(int spell_id) {
        terminateSpell(getSpell(spell_id));
    }

    public void terminateSpell(BaseSpell spell) {
        terminateSpell(spell, spell.getPosition());
    }

    public void terminateSpell(BaseSpell spell, Location location) {
        if (spell != null) {
            spell.terminate(location);
            killSpell(spell);
        }
    }

    public void killSpell(int spell_id) {
        killSpell(getSpell(spell_id));
    }

    public void killSpell(BaseSpell spell) {
        if (spell != null) {
            spell.kill();
            int id = spell.getId();
            active_spells.remove(id);
            for (BaseSpell child_spell : getChildSpells(id))
                if (child_spell.isDaemon())
                    child_spell.setAlive(false);


        }
    }

    private int generateSpellID() {
        do {
            if (active_spell_id == Integer.MAX_VALUE)
                active_spell_id = 1;
            else
                active_spell_id++;

        } while (active_spells.containsKey(active_spell_id));
        return active_spell_id;
    }

    public void spawnSpell(BaseSpell spell, Player player, String name, int parent_id) {
        int id = generateSpellID();
        spell.setAlive(true);
        spell.init(this, player, id, parent_id, name);
        active_spells.put(id, spell);
    }

    public void spawnSpell(BaseSpell spell, Player player, String name, int parent_id, Location location) {
        int id = generateSpellID();
        spell.setAlive(true);
        spell.init(this, location, player, id, parent_id, name);
        active_spells.put(id, spell);
    }

}