package be.vdab.exceptions;

public abstract class RekeningException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    public  String getMessage() {
        return null;
    }
}

// BL: de foutmelding wordt vanuit het uitvoerend programma meegegeven
// een return van null is niet zinvol !
