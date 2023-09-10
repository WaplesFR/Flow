package fr.flow.spellcasting;

import fr.flow.ServerUser;
import fr.flow.SpellManager;
import fr.flow.UserManager;
import fr.flow.utils.DirectionalParticle;
import fr.flow.utils.LocationUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.logging.Logger;

public class SpellCaster {

    private final Logger logger;
    private final SpellManager spell_manager;
    private final UserManager user_manager;
    private final Map<ServerUser, SpellCastPatternRecord> casting_map = new HashMap<>();
    private final Map<ServerUser, SpellCastPatternMapping> pattern_mappings = new HashMap<>();
    private final Map<ServerUser, String> pending_spell_binding = new HashMap<>();

    public SpellCaster(SpellManager spell_manager, UserManager user_manager, Logger logger){
        this.spell_manager = spell_manager;
        this.user_manager = user_manager;
        this.logger = logger;
    }

    public void tick(){
        for(ServerUser user: user_manager.getUsers()){

            if(!pattern_mappings.containsKey(user)) pattern_mappings.put(user, SpellCastPatternMapping.DEFAULT);


            Player player = user.getPlayer();
            ItemStack item = player.getInventory().getItemInMainHand();

            if(!player.isFlying() && item.getType() == Material.STICK && item.getItemMeta().getDisplayName().equals("§5Magic Wand")){
                if(user.isCasting()){
                    Location point = player.getEyeLocation().add(player.getEyeLocation().getDirection().normalize().multiply(3));
                    if(isCasting(user)) {
                        SpellCastPatternRecord spell_cast_pattern_record = getSpellcastPatternRecord(user);
                        List<Location> casting_points = spell_cast_pattern_record.getCastingPoints();
                        if(casting_points.size() > 0) {
                            Vector casting_normal_plane = spell_cast_pattern_record.getNormalPlane();
                            point = LocationUtils.projectPointOnPlane(casting_points.get(0), casting_normal_plane, point);
                            Location last_point = casting_points.get(casting_points.size() - 1);

                            if (point.distance(last_point) > 0.1) {
                                Location between = last_point.clone().add(point).multiply(0.5);
                                spell_cast_pattern_record.addCastingPoint(between);
                                spell_cast_pattern_record.addCastingPoint(point);
                            }

                            for (Location loc : casting_points) DirectionalParticle.spawn(Particle.ELECTRIC_SPARK, loc, new Vector(), 0);

                        } else spell_cast_pattern_record.addCastingPoint(point);


                    } else {
                        Vector normalized_plane = player.getEyeLocation().getDirection().normalize().multiply(-1);
                        SpellCastPatternRecord spell_cast_pattern_record = new SpellCastPatternRecord(normalized_plane);
                        casting_map.put(user, spell_cast_pattern_record);
                    }

                } else if(isCasting(user)){
                    SpellCastPatternRecord spell_cast_pattern_record = getSpellcastPatternRecord(user);
                    List<Location> casting_points = spell_cast_pattern_record.getCastingPoints();
                    Vector casting_normal_plane = spell_cast_pattern_record.getNormalPlane();
                    List<Location> transformed_locations = new ArrayList<>();
                    double angle = Math.acos(casting_normal_plane.dot(new Vector(0, 0, 1))/casting_normal_plane.length());
                    boolean mirror = false;
                    if(angle > Math.PI/2){
                        angle -= Math.PI;
                        mirror = true;
                    }
                    Vector axis = casting_normal_plane.clone().crossProduct(new Vector(0, 0, 1)).normalize();
                    for(int i = 1; i < casting_points.size(); i++){
                        Vector temp = casting_points.get(i).clone().toVector().subtract(casting_points.get(0).toVector());
                        temp.rotateAroundAxis(axis, angle);
                        temp.setZ(0);
                        if(mirror) temp.rotateAroundY(Math.PI);

                        Location loc = casting_points.get(0).clone().add(temp);
                        transformed_locations.add(loc);
                    }
                    List<int[]> filteredList = pointsToFilteredVectors(transformed_locations, 4);
                    List<Integer> lines = filteredVectorsToPattern(filteredList);
                    logger.info(String.format("Recorded pattern %s", Arrays.toString(lines.toArray())));

                    if(isSpellBinding(user))
                        completeSpellBinding(user, lines);
                    else {
                        SpellCastPatternMapping pattern_mapping = pattern_mappings.get(user);
                        String spell_name = pattern_mapping.mapPattern(lines);
                        if (spell_name != null) {
                            logger.info(String.format("Triggering spell %s", spell_name));
                            spell_manager.castSpell(spell_name, player);
                        }
                    }
                    casting_map.remove(user);
                }
            } else casting_map.remove(user);

        }
    }


    private List<int[]> pointsToFilteredVectors(List<Location> points, int min_sequence_size){
        List<int[]> filtered_temp_list = new ArrayList<>();
        List<int[]> filtered_list = new ArrayList<>();
        for(int i = 1; i < points.size(); i++){

            Vector temp = points.get(i).clone().toVector().subtract(points.get(i-1).clone().toVector()).normalize();
            List<Double> values = Arrays.asList(temp.getX(), temp.getY());
            int[] indexes = new int[2];

            for(int j = 0; j < indexes.length; j++){
                double value = values.get(j);
                double index;

                if(value > 0) index = Math.floor(value+0.7);
                 else index = Math.ceil(value-0.7);

                indexes[j] = (int) Math.round(index);
            }
            if(filtered_temp_list.isEmpty() || Arrays.equals(filtered_temp_list.get(0), indexes)){
               if(!filtered_list.isEmpty() && Arrays.equals(filtered_list.get(filtered_list.size()-1), indexes))
                   continue;
                filtered_temp_list.add(indexes);
            } else {
                if(filtered_temp_list.size() >= min_sequence_size)filtered_list.add(filtered_temp_list.get(0));

                filtered_list.clear();
                filtered_list.add(indexes);
            }
        }

        if(filtered_temp_list.size() >= min_sequence_size) filtered_list.add(filtered_temp_list.get(0));
        return filtered_list;
    }


    private List<Integer> filteredVectorsToPattern(List<int[]> filtered_list){
        List<int[]> line_types = Arrays.asList(
                new int[]{1, 1}, new int[]{1, 0}, new int[]{1, -1},
                new int[]{0, 1}, new int[]{0, 0}, new int[]{0, -1},
                new int[]{-1, 1}, new int[]{-1, 0}, new int[]{-1, -1}
        );
        List<Integer> lines = new ArrayList<>();
        for(int[] line: filtered_list)
            for(int i = 0; i < line_types.size(); i++)
                if(Arrays.equals(line_types.get(i), line))lines.add(i);

        return lines;
    }


    public void setupSpellBinding(ServerUser user, String spell_name){
        Player player = user.getPlayer();
        if(spell_manager.getSpellNames().contains(spell_name)){
            if(isSpellBinding(user)) player.sendMessage(String.format("You are already binding a spell (%s)!", pending_spell_binding.get(user)));
            else {
                pending_spell_binding.put(user, spell_name);
                player.sendMessage(String.format("Please draw the shape you want to bind %s to using your magic wand", spell_name));
            }
        } else player.sendMessage("No such spell");
    }

    private void completeSpellBinding(ServerUser user, List<Integer> pattern){
        Player player = user.getPlayer();
        SpellCastPatternMapping pattern_mapping = pattern_mappings.get(user);
        String spell_name = pattern_mapping.mapPattern(pattern);
        if(spell_name != null){
            pattern_mapping.unbindPattern(pattern);
            player.sendMessage(String.format("You already have a spell mapped to this pattern (%s), this binding will be overwritten", spell_name));
        }
        spell_name = pending_spell_binding.get(user);
        pattern_mapping.bindPattern(pattern, spell_name);
        pending_spell_binding.remove(user);
        player.sendMessage(String.format("Successfully bound spell %s to the pattern you just drew!", spell_name));
    }

    private boolean isSpellBinding(ServerUser user){
        return pending_spell_binding.containsKey(user);
    }

    public SpellCastPatternRecord getSpellcastPatternRecord(ServerUser user){
        return casting_map.get(user);
    }

    public boolean isCasting(ServerUser user){
        return casting_map.containsKey(user);
    }

}