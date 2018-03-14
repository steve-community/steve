package de.rwth.idsg.steve.ocpp.converter;

import java.util.function.Function;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 08.03.2018
 */
public class Convert {

    public static <T, R> Function<T, R> start(T arg, Function<T, R> function) {
        return function;
    }

}
