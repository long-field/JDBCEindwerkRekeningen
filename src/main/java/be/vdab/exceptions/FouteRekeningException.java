package be.vdab.exceptions;

public class FouteRekeningException extends RekeningException{
    private static final long serialVersionUID = 1L;

    @Override
    public String getMessage() {
        return "Het rekeningnummer is niet correct.";
    }
}

// BL: de foutmelding wordt vanuit het uitvoerend programma meegegeven en is
// meestal geen vaste waarde (tekst) in de exception klasse zelf
