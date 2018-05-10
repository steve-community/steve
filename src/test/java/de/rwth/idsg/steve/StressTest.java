package de.rwth.idsg.steve;

import de.rwth.idsg.steve.utils.__DatabasePreparer__;
import ocpp.cs._2015._10.MeterValue;
import ocpp.cs._2015._10.SampledValue;
import org.joda.time.DateTime;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 10.05.2018
 */
public abstract class StressTest {

    // higher values -> more stress
    //
    protected static final int THREAD_COUNT = 50;
    protected static final int REPEAT_COUNT_PER_THREAD = 50;

    // lower values -> more stress
    //
    // reason: these only specify the size of the values "bag" from which a test picks a value randomly. if there are
    // less values to pick from, it is more likely that tests will use the same value at the same time. this produces
    // more overhead for steve (especially db) when multiple threads "fight" for inserting/updating a db row/cell.
    //
    protected static final int ID_TAG_COUNT = 50;
    protected static final int CHARGE_BOX_COUNT = THREAD_COUNT;
    protected static final int CONNECTOR_COUNT_PER_CHARGE_BOX = 25;

    protected void attack() throws Exception {
        Assert.assertEquals(ApplicationProfile.TEST, SteveConfiguration.CONFIG.getProfile());
        Assert.assertTrue(SteveConfiguration.CONFIG.getOcpp().isAutoRegisterUnknownStations());

        __DatabasePreparer__.prepare();

        Application app = new Application();
        try {
            app.start();
            attackInternal();
        } finally {
            try {
                app.stop();
            } finally {
                __DatabasePreparer__.cleanUp();
            }
        }
    }

    protected abstract void attackInternal() throws Exception;

    protected static List<MeterValue> getMeterValues(int transactionStart, int transactionStop) {
        final int size = 4;
        int delta = (transactionStop - transactionStart) / size;
        if (delta == 0) {
            return Collections.emptyList();
        }

        List<MeterValue> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            int meterValue = transactionStart + delta * (i + 1);
            list.add(createMeterValue(meterValue));
        }
        return list;
    }

    protected static MeterValue createMeterValue(int val) {
        return new MeterValue().withTimestamp(DateTime.now())
                               .withSampledValue(new SampledValue().withValue(Integer.toString(val)));
    }
}
