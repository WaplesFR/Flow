package fr.flow;

public class SpellWrapper {


    private final int fluid_cost;

    private final int cool_down;

    private final String name;

    private final BaseSpell spell;

    public SpellWrapper(String name, BaseSpell spell, int fluid_cost, int cool_down){
        this.name = name;
        this.spell = spell;
        this.fluid_cost = fluid_cost;
        this.cool_down = cool_down;
    }

    public int getFluidCost(){
        return fluid_cost;
    }

    public int getCoolDown(){
        return cool_down;
    }

    public String getSpellName(){
        return name;
    }

    public BaseSpell getSpell(){
        try{
            return spell.getClass().newInstance();
        } catch(IllegalAccessException | InstantiationException e){
            e.printStackTrace();
        }
        return null;
    }

}