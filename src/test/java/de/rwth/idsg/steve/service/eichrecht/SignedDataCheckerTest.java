/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2026 SteVe Community Team
 * All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package de.rwth.idsg.steve.service.eichrecht;

import de.rwth.idsg.steve.service.eichrecht.SignedDataCheck.Result;
import de.rwth.idsg.steve.repository.dto.Transaction;
import de.rwth.idsg.steve.repository.dto.TransactionDetails;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;

/**
 * Uses signed samples of the S.A.F.E. transparency software
 */
public class SignedDataCheckerTest {

    // eBee with DZG meter: begin and end in separate records, values in Wh
    private static final String EBEE_BEGIN = """
        OCMF|{"FV":"1.0","GI":"Heinz_CSA","GS":"","GV":"1.6.0","PG":"T28",\
        "MV":"DZG","MM":"DVH4013","MS":"000033022620","MF":"","IS":true,\
        "IT":"ISO14443","ID":"7bcf78ad","RD":[{"TM":"2021-08-25T10:01:04,000+0200 I",\
        "TX":"B","RV":7753,"RI":"1-b:1.8.0","RU":"Wh","RT":"AC","EF":"",\
        "ST":"G"}]}|{"SD":"3045022100AA010A609E2A6D5E5DAC54CB469FA4A430BFBADC0CDF5F82F5291A6C9CEACE9B02\
        2052E7ECC79DEF081C303703ABC67C07C4CFA842AF6EE21B12C1263C21E80D92FF"}""";

    private static final String EBEE_END = """
        OCMF|{"FV":"1.0","GI":"Heinz_CSA","GS":"","GV":"1.6.0","PG":"T29",\
        "MV":"DZG","MM":"DVH4013","MS":"000033022620","MF":"","IS":true,\
        "IT":"ISO14443","ID":"7bcf78ad","RD":[{"TM":"2021-08-25T10:01:09,000+0200 R",\
        "TX":"E","RV":7753,"RI":"1-b:1.8.0","RU":"Wh","RT":"AC","EF":"",\
        "ST":"G"}]}|{"SD":"30450220342732A56E5C95BF70673351D5CBEC5E8C44C435BD2A389CF0BA8777A8B67BF40221\
        00D88647A1D2B1712B077D490E8AFD14E00E5E3FAB94F70FDCFFCAC653D214A484"}""";

    private static final String EBEE_KEY =
        "3059301306072A8648CE3D020106082A8648CE3D03010703420004E5CE2801D716BF55B71477CA666BE6A8B8994FEBBF05"
        + "B04BC0AE7879A62AE77460A14335F3F30657DD7ADC4338398C837DD2DE52D8FCAAE78601D1476FE1C21F";

    // KEBA: one record with begin and end, values in kWh
    private static final String KEBA = """
        OCMF|{"FV":"1.0","GI":"KEBA_KCP30","GS":"17619300","GV":"2.8.5",\
        "PG":"T32","IS":false,"IL":"NONE","IF":["RFID_NONE","OCPP_NONE",\
        "ISO15118_NONE","PLMN_NONE"],"IT":"NONE","ID":"","RD":[{"TM":"2019-08-13T10:03:15,000+0000 I",\
        "TX":"B","EF":"","ST":"G","RV":0.2596,"RI":"1-b:1.8.0","RU":"kWh"},\
        {"TM":"2019-08-13T10:03:36,000+0000 R","TX":"E","EF":"","ST":"G",\
        "RV":0.2597,"RI":"1-b:1.8.0","RU":"kWh"}]}|{"SD":"304502200E2F107C987A300AC1695CA89EA149A8CDFA1\
        6188AF0A33EE64B67964AA943F9022100889A72B6D65364BEA8562E7F6A0253157ACFF84FE4929A93B5964D23C42656\
        99"}""";

    private static final String KEBA_KEY =
        "3059301306072A8648CE3D020106082A8648CE3D030107034200043AEEB45C392357820A58FDFB0857BD77ADA31585C61C"
        + "430531DFA53B440AFBFDD95AC887C658EA55260F808F55CA948DF235C2108A0D6DC7D4AB1A5E1A7955BE";

    private final EichrechtService eichrechtService = new EichrechtService();

    @Test
    public void allPassed() {
        var checks = check(tx("7753", "7753"), EBEE_KEY, EBEE_BEGIN, EBEE_END);

        assertResults(checks, Result.Passed, Result.Passed, Result.Passed, Result.Passed);
        Assertions.assertEquals("Signed: 7753 Wh, transaction: 7753 Wh", checks.get(1).note());
    }

    @Test
    public void base64Key() {
        String key = Base64.getEncoder().encodeToString(HexFormat.of().parseHex(EBEE_KEY));
        var checks = check(tx("7753", "7753"), key, EBEE_BEGIN, EBEE_END);

        assertResults(checks, Result.Passed, Result.Passed, Result.Passed, Result.Passed);
    }

    @Test
    public void oneRecordWithBeginAndEnd_kWh() {
        // The transaction has whole Wh (259.6 kWh -> 260 Wh), so less than 1 Wh difference is fine
        var checks = check(tx("260", "260"), KEBA_KEY, KEBA);

        assertResults(checks, Result.Passed, Result.Passed, Result.Passed);
        Assertions.assertEquals("Signature of the begin value", checks.get(0).description());
    }

    @Test
    public void wrongKey() {
        var checks = check(tx("7753", "7753"), KEBA_KEY, EBEE_BEGIN, EBEE_END);

        assertResults(checks, Result.Failed, Result.Passed, Result.Failed, Result.Passed);
    }

    @Test
    public void tamperedValue() {
        String original = """
            "RV":7753""";

        String tampered = EBEE_BEGIN.replace(original, original.replace("7753", "7700"));
        var checks = check(tx("7700", "7753"), EBEE_KEY, tampered, EBEE_END);

        assertResults(checks, Result.Failed, Result.Passed, Result.Passed, Result.Passed);
    }

    @Test
    public void valuesDoNotMatchTransaction() {
        var checks = check(tx("7000", "8000"), EBEE_KEY, EBEE_BEGIN, EBEE_END);

        assertResults(checks, Result.Passed, Result.Failed, Result.Passed, Result.Failed);
    }

    @Test
    public void unknownKey() {
        var checks = check(tx("7753", "7753"), null, EBEE_BEGIN, EBEE_END);

        assertResults(checks, Result.NotChecked, Result.Passed, Result.NotChecked, Result.Passed);
        Assertions.assertEquals("The public key of the meter is unknown", checks.get(0).note());
    }

    @Test
    public void unreadableKey() {
        var checks = check(tx("7753", "7753"), "ABCD", EBEE_BEGIN, EBEE_END);

        Assertions.assertEquals(Result.NotChecked, checks.get(0).result());
    }

    @Test
    public void transactionNotStopped() {
        var checks = check(tx("7753", null), EBEE_KEY, EBEE_BEGIN);

        assertResults(checks, Result.Passed, Result.Passed, Result.NotChecked);
    }

    @Test
    public void endMissing() {
        var checks = check(tx("7753", "7753"), EBEE_KEY, EBEE_BEGIN);

        assertResults(checks, Result.Passed, Result.Passed, Result.Failed);
    }

    @Test
    public void beginMissing() {
        var checks = check(tx("7753", "7753"), EBEE_KEY, EBEE_END);

        assertResults(checks, Result.Failed, Result.Passed, Result.Passed);
    }

    @Test
    public void noBeginOrEnd() {
        Assertions.assertTrue(check(tx("7753", "7753"), EBEE_KEY, "<edl40/>").isEmpty());
        Assertions.assertTrue(check(tx("7753", "7753"), EBEE_KEY).isEmpty());
    }

    private List<SignedDataCheck> check(Transaction transaction, String publicKey, String... rawValues) {
        var meterValues = Arrays.stream(rawValues)
            .map(v -> TransactionDetails.MeterValues.builder()
                .valueTimestamp(DateTime.now())
                .value(v)
                .format("SignedData")
                .build())
            .toList();
        return eichrechtService.toSignedTransaction(transaction, eichrechtService.parse(meterValues), publicKey)
            .checks();
    }

    private static Transaction tx(String startValue, String stopValue) {
        return Transaction.builder().id(1).startValue(startValue).stopValue(stopValue).build();
    }

    private static void assertResults(List<SignedDataCheck> checks, Result... expected) {
        var actual = checks.stream().map(SignedDataCheck::result).toList();
        Assertions.assertEquals(List.of(expected), actual, checks.toString());
    }
}
