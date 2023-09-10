package fr.flow.exception;

public class SpellCoolDownException extends Exception{

    public SpellCoolDownException(){

    }

    public SpellCoolDownException(String message){
        super(message);
    }
}