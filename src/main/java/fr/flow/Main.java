package fr.flow;

import fr.flow.spellcasting.SpellCaster;
import org.bukkit.Bukkit;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Spellcaster;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.logging.Logger;

public class Main extends JavaPlugin {

    private Logger logger;
    private BukkitScheduler scheduler;
    private SpellManager spell_manager;
    private UserManager user_manager;
    private SpellCaster spell_caster;

    @Override
    public void onEnable(){
        logger = this.getLogger();

        user_manager = new UserManager(this);
        spell_manager = new SpellManager(logger, user_manager);
        spell_caster = new SpellCaster(spell_manager, user_manager, logger);
        scheduler = Bukkit.getScheduler();


        this.getServer().getPluginManager().registerEvents(new EventListener(this),this);



        spell_manager.setup();
        setup();

        logger.info("Plugin enabled");
    }
    @Override
    public void onDisable(){
        logger.info("Plugin disabled");
    }

    public void setup(){
        for(Player player: Bukkit.getOnlinePlayers()){
            user_manager.getUsersMap().put(player.getUniqueId(),new ServerUser(player));
            // NEED USER DATA HANDLER CLASS..
        }
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                user_manager.tick();
                spell_manager.tick();
                spell_caster.tick();
            }
        }, 1, 1);
    }

    public SpellCaster getSpellCaster() {
        return spell_caster;
    }

    public UserManager getUserManager() {
        return user_manager;
    }

    public SpellManager getSpellManager() {
        return spell_manager;
    }
}
