package be.vdab.repositories;

import be.vdab.exceptions.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;


public class RekeningRepository extends AbstractRepository {

    public boolean checkRekeningNr(String rekNr) {
        if (rekNr.trim().length() == 16 && rekNr.trim().startsWith("BE")) {
            if (Integer.parseInt(rekNr.trim().substring(2, 4)) >= 2 || Integer.parseInt(rekNr.trim().substring(2, 4)) <= 98) {
                // BL: moet AND zijn, niet OR:                      &&   ipv ||
                if (Long.parseLong(rekNr.trim().substring(4) + "1114" + rekNr.trim().substring(2, 4)) % 97 == 1) {
                    return true;
                }
            }
        }
        return false;
    }

    public void nieuweRekening(String rekNr) throws SQLException {
        if (checkRekeningNr(rekNr)) {
            try (var connection = super.getConnection();
                 //hier kan je try-blok starten
                 var statementInsert = connection.prepareStatement("insert into rekeningen(nummer) values (?)")) {
                statementInsert.setString(1, rekNr);
                connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                connection.setAutoCommit(false);
                try { //BL: dit try-blok mag al eerder starten (zie hoger in de code)
                    statementInsert.executeUpdate();
                    connection.commit();
                }
                catch (SQLException ex) {
                    try (var statementSelect = connection.prepareStatement("select nummer from rekeningen where nummer = ?")) {
                        statementSelect.setString(1, rekNr);
                        if (statementSelect.executeQuery().next()) {
                            connection.commit();
                            throw new RekeningBestaatAlException();
                        }
                        connection.commit();
                        throw ex;
                    }
                }
            }
        }
        else {
            throw new FouteRekeningException();
        }
    }

    public BigDecimal saldoConsulteren(String rekNr) throws SQLException {
        if (checkRekeningNr(rekNr)) {
            try (var connection = super.getConnection();
                 var statement = connection.prepareStatement("select saldo from rekeningen where nummer = ?")) {
                connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                connection.setAutoCommit(false);
                statement.setString(1, rekNr);
                var result = statement.executeQuery();
                if (result.next()) {
                    // BL:commit() uitvoeren (transactie committen om de vergrendelde records terug vrij te geven)
                    return result.getBigDecimal("saldo");
                }
            }
            //BL: commit() uitvoeren (transactie committen om de vergrendelde records terug vrij te geven)
            throw new RekeningBestaatNietException();
        }
        throw new FouteRekeningException();
    }

    public void overschrijving(String rekNrVan, String rekNrNaar, BigDecimal bedrag) throws SQLException {

        if (rekNrVan.equals(rekNrNaar)) {
            throw new RekeningNaarBestaatNietException(); //BL: onjuiste foutmelding
        }
        if (bedrag.compareTo(BigDecimal.ZERO) < 1) {
            throw new FoutBedragException();
        }

        //BL: sql-statements kunnen zeker geoptimaliseerd worden door geen gebruikt te maken van: for update
        //BL: zie document met feedback

        try (var connection = super.getConnection();
             var statementSelectVan = connection.prepareStatement("select saldo from rekeningen where nummer = ? for update")) {
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            connection.setAutoCommit(false);
            statementSelectVan.setString(1, rekNrVan);
            var resultVan = statementSelectVan.executeQuery();

            if (resultVan.next()) {
                var nieuwSaldoVan = resultVan.getBigDecimal("saldo").subtract(bedrag);
                if (nieuwSaldoVan.compareTo(BigDecimal.ZERO) >= 0) {
                    try (var statementSelectNaar = connection.prepareStatement("select saldo from rekeningen where nummer = ? for update")) {
                        statementSelectNaar.setString(1, rekNrNaar);
                        var resultNaar = statementSelectNaar.executeQuery();
                        if (resultNaar.next()) {
                            var nieuwSaldoNaar = resultNaar.getBigDecimal("saldo").add(bedrag);
                            try (var sqlUpdateVan = connection.prepareStatement("update rekeningen set saldo = ? where nummer = ?");
                                 var sqlUpdateNaar = connection.prepareStatement("update rekeningen set saldo = ? where nummer = ?")) {
                                sqlUpdateVan.setBigDecimal(1, nieuwSaldoVan);
                                sqlUpdateVan.setString(2, rekNrVan);
                                sqlUpdateNaar.setBigDecimal(1, nieuwSaldoNaar);
                                sqlUpdateNaar.setString(2, rekNrNaar);
                                sqlUpdateVan.executeUpdate();
                                sqlUpdateNaar.executeUpdate();
                                connection.commit();
                                return;
                            }
                        }
                        connection.rollback();
                        throw new RekeningNaarBestaatNietException();
                    }
                }
                connection.rollback();
                throw new OntoereikendSaldoException();
            }
            connection.rollback();
            throw new RekeningBestaatNietException();
        }
    }
}