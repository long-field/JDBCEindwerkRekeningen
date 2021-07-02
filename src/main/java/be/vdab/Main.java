package be.vdab;

import be.vdab.exceptions.RekeningException;
import be.vdab.repositories.RekeningRepository;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        var scanner = new Scanner(System.in);
        var repository = new RekeningRepository();
        String keuze = "";
        String rekNr;
        while (!(keuze.equals("0"))) {
            System.out.println();
            System.out.println("Geef je keuze(1:nieuwe rekening toevoegen, 2:saldo consulteren, 3:overschrijven en 0 om te stoppen");
            switch (keuze = scanner.nextLine()) {
                case "1":
                    System.out.println("Geef het nieuwe rekeningnummer: ");
                    rekNr = scanner.nextLine();
                    try {
                        repository.nieuweRekening(rekNr);
                        System.out.println("Rekening: " + rekNr + " toegevoegd.");
                    }
                    catch (RekeningException ex) {
                        System.out.println(ex.getMessage());
                    }
                    catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    break;
                case "2":
                    System.out.println("Geef het te consulteren rekeningnummer: ");
                    rekNr = scanner.nextLine();
                    try {
                        System.out.println("Huidig saldo: " + repository.saldoConsulteren(rekNr));
                    }
                    catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    catch (RekeningException ex) {
                        System.out.println(ex.getMessage());
                    }
                    break;
                case "3":
                    try {
                        System.out.println("Geef het betalende rekeningnummer: ");
                        var van = scanner.nextLine();
                        System.out.println("Geef het ontvangende rekeningnummer: ");
                        var naar = scanner.nextLine();
                        System.out.println("Geef het bedrag: ");
                        var bedrag = scanner.nextLine();
                        repository.overschrijving(van, naar, BigDecimal.valueOf(Double.parseDouble(bedrag)));
                        System.out.println("Overschrijving uitgevoerd");
                        System.out.println("Nieuw saldo: " + repository.saldoConsulteren(van));
                    }
                    catch (RekeningException ex) {
                        System.out.println(ex.getMessage());
                    }
                    catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    break;
                case "0":
                    break;
                default:
                    keuze = "9";
                    System.out.println("Geef een correcte keuze");
            }
        }
        System.out.println();
        System.out.println();
        System.out.println("___PROGRAM TERMINATED___");
        System.out.println();
    }
}