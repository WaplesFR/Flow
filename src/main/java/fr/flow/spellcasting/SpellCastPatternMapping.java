package fr.flow.spellcasting;

import fr.flow.utils.Utils;

import java.util.*;

public class SpellCastPatternMapping {

    public static SpellCastPatternMapping DEFAULT;
    static {
        DEFAULT = new SpellCastPatternMapping();
        DEFAULT.bindPattern(Arrays.asList(8, 1, 6), "Fireball");
        DEFAULT.bindPattern(Arrays.asList(5, 0, 7, 0), "PowerStrike");
        DEFAULT.bindPattern(Arrays.asList(5, 6, 1, 8), "ArrowStorm");
        DEFAULT.bindPattern(Arrays.asList(8, 2, 3), "Explosion");
    }
    private final Map<List<Integer>, String> pattern_map = new HashMap<>();


    public void bindPattern(List<Integer> pattern, String spell_name){
        pattern_map.put(pattern, spell_name);
    }

    public void unbindPattern(List<Integer> pattern){
        pattern_map.remove(pattern);
    }

    public String mapPattern(List<Integer> pattern){
        List<Integer> extended_pattern = new ArrayList<>(pattern);
        extended_pattern.addAll(pattern);
        for(List<Integer> temp: pattern_map.keySet()) {
            boolean same = false;
            if (!Utils.arraysEqual(pattern.toArray(new Integer[]{}), temp.toArray(new Integer[]{}))) continue;

            for (int i = 0; i < temp.size(); i++) {
                for (int j = 0; j < temp.size(); j++)
                    if ((i + j) <= extended_pattern.size()) {
                        same = extended_pattern.get(i + j).equals(temp.get(j));
                        if(!same)break;
                    }
                if (same) return pattern_map.get(temp);
            }
        }
        return null;
    }
}
