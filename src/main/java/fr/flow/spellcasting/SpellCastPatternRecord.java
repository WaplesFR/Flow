package fr.flow.spellcasting;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class SpellCastPatternRecord {

    private final Vector normal_plane;
    private final List<Location> casting_points;

    public SpellCastPatternRecord(Vector normal_plane){
        this.normal_plane = normal_plane;
        casting_points = new ArrayList<>();
    }

    public Vector getNormalPlane() {
        return normal_plane;
    }

    public List<Location> getCastingPoints() {
        return casting_points;
    }

    public void addCastingPoint(Location point){
        casting_points.add(point);
    }
}