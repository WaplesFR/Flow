package fr.flow;

import net.md_5.bungee.api.chat.hover.content.Item;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class EventListener implements Listener {


    private final Main main;


    public EventListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void join(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if(main.getUserManager().getUsersMap().containsKey(player.getUniqueId()))return;
        main.getUserManager().getUsersMap().put(player.getUniqueId(), new ServerUser(player));
    }

    @EventHandler
    public void interact(PlayerInteractEvent event){
        ItemStack item = event.getItem();
        if(item==null)return;
        Action action = event.getAction();
        if(!action.equals(Action.RIGHT_CLICK_AIR) && !action.equals(Action.RIGHT_CLICK_BLOCK))return;

        Player player = event.getPlayer();
        ServerUser user = main.getUserManager().getUser(player);
        if(event.getItem().getItemMeta()==null)return;

        if(event.getItem().getType().equals(Material.STICK) && event.getItem().getItemMeta().getDisplayName().equals("§5Magic Wand")){
            user.setCasting(!user.isCasting());
        }else if (event.getItem().getType().equals(Material.DIAMOND)){
            main.getSpellCaster().setupSpellBinding(user, event.getItem().getItemMeta().getDisplayName());
        }


    }

}
