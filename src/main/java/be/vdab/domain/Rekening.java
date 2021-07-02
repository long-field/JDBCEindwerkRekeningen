package be.vdab.domain;

import java.math.BigDecimal;

public class Rekening {
    private final BigDecimal saldo;
    private final String rekeningnummer;

    public Rekening(BigDecimal saldo, String rekeningnummer) {
        this.saldo = saldo;
        this.rekeningnummer = rekeningnummer;  //BL: controle van het rekeningnummer hoort in deze classe te zitten,
                                               // niet in de repository klasse!
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public String getRekeningnummer() {
        return rekeningnummer;
    }

    @Override
    public String toString() {
        return "saldo=" + saldo + ", rekeningnummer='" + rekeningnummer;
    }
}

//BL: equals() ?  daarmee kan je vergelijken of je van-rek = naar-rek

