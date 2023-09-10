package fr.flow;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class UserManager {

    private final HashMap<UUID, ServerUser> users = new HashMap<>();
    private final Main main;

    public UserManager(Main main){
        this.main = main;
    }

    public ServerUser getUser(Player player){
        return users.get(player.getUniqueId());
    }
    public ServerUser getUser(UUID id){
        return users.get(id);
    }

    public ServerUser getUser(String player_name){
        for(UUID player_id : users.keySet())
            if(users.get(player_id).getPlayer().getName().equals(player_name))
                return users.get(player_id);

        return null;
    }

    public List<ServerUser> getUsers(){
        return new ArrayList<>(users.values());
    }

    public void tick(){
        for(ServerUser user: users.values()){
            user.tick();
        }
    }

    public void castSpell(ServerUser user, SpellWrapper spell_wrapper){
        user.removeFluid(spell_wrapper.getFluidCost());
        user.addCoolDown(spell_wrapper.getSpellName(), spell_wrapper.getCoolDown());
    }

    public HashMap<UUID, ServerUser> getUsersMap() {
        return users;
    }
}