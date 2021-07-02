package be.vdab.exceptions;

public class FoutBedragException extends RekeningException{
    private static final long serialVersionUID = 1L;
    @Override
    public String getMessage() {
        return "Het bedrag kan niet 0 of lager zijn.";
    }

    // BL: de foutmelding wordt vanuit het uitvoerend programma meegegeven en is
    // meestal geen vaste waarde (tekst) in de exception klasse zelf

    // Dit geldt voor alle exception klasses !
}