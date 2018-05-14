package de.rwth.idsg.steve.ocpp.ws.ocpp12;

import de.rwth.idsg.steve.ocpp.ws.AbstractTypeStore;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 17.03.2015
 */
public final class Ocpp12TypeStore extends AbstractTypeStore {

    public static final Ocpp12TypeStore INSTANCE = new Ocpp12TypeStore();

    private Ocpp12TypeStore() {
        super(
                ocpp.cs._2010._08.ObjectFactory.class.getPackage().getName(),
                ocpp.cp._2010._08.ObjectFactory.class.getPackage().getName()
        );
    }
}
