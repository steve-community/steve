package de.rwth.idsg.steve;

import com.google.common.collect.Sets;
import de.rwth.idsg.steve.config.BeanConfiguration;
import jooq.steve.db.DefaultCatalog;
import jooq.steve.db.tables.SchemaVersion;
import jooq.steve.db.tables.Settings;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.jooq.impl.DSL;

import java.util.Set;
import java.util.function.Consumer;

import static jooq.steve.db.tables.ChargeBox.CHARGE_BOX;
import static jooq.steve.db.tables.OcppTag.OCPP_TAG;

/**
 * This is a dangerous class. It performs database operations no class should do, like truncating all tables and
 * inserting data while bypassing normal mechanisms of SteVe. However, for integration testing with reproducible
 * results we need a clean and isolated database.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 21.03.2018
 */
public class __DatabasePreparer__ {

    private static final String SCHEMA_TO_TRUNCATE = "stevedb_test_2aa6a783d47d";
    private static final String REGISTERED_CHARGE_BOX_ID = "charge_box_2aa6a783d47d";
    private static final String REGISTERED_OCPP_TAG = "id_tag_2aa6a783d47d";

    public static void prepare() {
        runOperation(ctx -> {
            truncateTables(ctx);
            insertChargeBox(ctx);
            insertOcppIdTag(ctx);
        });
    }

    public static void cleanUp() {
        runOperation(__DatabasePreparer__::truncateTables);
    }

    public static String getRegisteredChargeBoxId() {
        return REGISTERED_CHARGE_BOX_ID;
    }

    public static String getRegisteredOcppTag() {
        return REGISTERED_OCPP_TAG;
    }

    private static void runOperation(Consumer<DSLContext> consumer) {
        BeanConfiguration beanConfiguration = new BeanConfiguration();
        DSLContext ctx = beanConfiguration.dslContext();
        try {
            consumer.accept(ctx);
        } finally {
            beanConfiguration.shutDown();
        }
    }

    private static void truncateTables(DSLContext ctx) {
        Set<Table<?>> skipList = Sets.newHashSet(SchemaVersion.SCHEMA_VERSION, Settings.SETTINGS);
        ctx.transaction(configuration -> {
            DSLContext internalCtx = DSL.using(configuration);
            internalCtx.execute("SET FOREIGN_KEY_CHECKS=0");
            DefaultCatalog.DEFAULT_CATALOG.getSchemas()
                                          .stream()
                                          .filter(schema -> SCHEMA_TO_TRUNCATE.equals(schema.getName()))
                                          .forEach(schema -> schema.getTables()
                                                                   .stream()
                                                                   .filter(t -> !skipList.contains(t))
                                                                   .forEach(t -> internalCtx.truncate(t).execute()));
            internalCtx.execute("SET FOREIGN_KEY_CHECKS=1");
        });
    }

    private static void insertChargeBox(DSLContext ctx) {
        ctx.insertInto(CHARGE_BOX)
           .set(CHARGE_BOX.CHARGE_BOX_ID, getRegisteredChargeBoxId())
           .execute();
    }

    private static void insertOcppIdTag(DSLContext ctx) {
        ctx.insertInto(OCPP_TAG)
           .set(OCPP_TAG.ID_TAG, getRegisteredOcppTag())
           .set(OCPP_TAG.BLOCKED, false)
           .set(OCPP_TAG.IN_TRANSACTION, false)
           .execute();
    }
}
