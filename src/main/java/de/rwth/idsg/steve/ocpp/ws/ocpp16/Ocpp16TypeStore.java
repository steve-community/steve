package de.rwth.idsg.steve.ocpp.ws.ocpp16;

import de.rwth.idsg.steve.ocpp.ws.AbstractTypeStore;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 13.03.2018
 */
public final class Ocpp16TypeStore extends AbstractTypeStore {

    public static final Ocpp16TypeStore INSTANCE = new Ocpp16TypeStore();

    private Ocpp16TypeStore() {
        super(
                ocpp.cs._2015._10.ObjectFactory.class.getPackage().getName(),
                ocpp.cp._2015._10.ObjectFactory.class.getPackage().getName()
        );
    }
}
