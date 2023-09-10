package fr.flow.exception;

public class NotEnoughFluidException extends Exception{

    public NotEnoughFluidException(){

    }

    public NotEnoughFluidException(String message){
        super(message);
    }
}
