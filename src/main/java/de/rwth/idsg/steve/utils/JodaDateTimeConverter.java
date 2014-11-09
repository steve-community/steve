package de.rwth.idsg.steve.utils;

import org.joda.time.DateTime;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Joda-Time and XSD represent data and time information according to ISO 8601.
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 20.10.2014
 */
public class JodaDateTimeConverter extends XmlAdapter<String, DateTime> {

    @Override
    public DateTime unmarshal(String v) throws Exception {
        return new DateTime(v);
    }

    @Override
    public String marshal(DateTime v) throws Exception {
        return v.toString();
    }
}