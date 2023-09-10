package fr.flow;

import fr.flow.exception.NotEnoughFluidException;
import fr.flow.exception.SpellCoolDownException;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class ServerUser {

    private final Player player;
    private float fluid, max_fluid, fluid_regeneration;
    private final HashMap<String, Integer> spell_in_cool = new HashMap<>();

    private boolean isCasting = false;

    public ServerUser(Player p){
        player = p;
        fluid = 0;
        max_fluid = 1000;
        fluid_regeneration = 1F;
    }

    public boolean isCasting() {
        return isCasting;
    }

    public void setCasting(boolean casting) {
        isCasting = casting;
    }

    public Player getPlayer() {
        return player;
    }

    public float getFluid() {
        return fluid;
    }

    public float getMaxFluid() {
        return max_fluid;
    }

    public float getFluidRegeneration() {
        return fluid_regeneration;
    }

    public void setFluid(float fluid) {
        this.fluid = fluid;
    }

    public void setMaxFluid(float max_mana) {
        this.max_fluid = max_mana;
    }

    public void setFluidRegeneration(float mana_regeneration) {
        this.fluid_regeneration = mana_regeneration;
    }

    public boolean hasCoolDown(String spell_name){
        return spell_in_cool.containsKey(spell_name);
    }

    public void tick(){
        if(fluid < max_fluid) fluid += fluid_regeneration;

        if(fluid > max_fluid) fluid = max_fluid;
        else if(fluid < 0) fluid = 0;

        for(String spell: spell_in_cool.keySet()){
            int cool_down = spell_in_cool.get(spell)-1;
            if(cool_down <= 0) spell_in_cool.remove(spell);
            else spell_in_cool.put(spell, cool_down);
        }
        int fluid_display = (int) Math.floor(fluid);
        float fluid_ratio = fluid / max_fluid;
        String progress_bar = generateFluidProgressbar(fluid_ratio);
        String message = String.format("%sFluid: |%s%s%s| %d", ChatColor.GRAY, ChatColor.LIGHT_PURPLE, progress_bar, ChatColor.GRAY, fluid_display);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }

    private String generateFluidProgressbar(float fluid_ratio){
        String fill = "█";
        String empty = "▒";
        return fill.repeat((int) Math.floor(fluid_ratio*10)) + empty.repeat((int) Math.ceil(10-fluid_ratio*10));
    }

    public void addFluid(float value){
        fluid += value;
    }

    public void removeFluid(float value){
        fluid -= value;}

    public void addCoolDown(String spell_name, int cool_down){
        spell_in_cool.put(spell_name, cool_down);
    }

    public boolean canCastSpell(SpellWrapper spell_wrapper) throws NotEnoughFluidException, SpellCoolDownException {
        if(fluid >= spell_wrapper.getFluidCost())
            if(!hasCoolDown(spell_wrapper.getSpellName())) return true;
            else throw new SpellCoolDownException();
        else throw new NotEnoughFluidException();
    }
}
