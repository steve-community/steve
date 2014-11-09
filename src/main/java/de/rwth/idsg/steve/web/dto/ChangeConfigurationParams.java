package de.rwth.idsg.steve.web.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 29.09.2014
 */
@Getter
@Setter
public class ChangeConfigurationParams {

    private String[] cp_items;

    private String confKey;

    private String value;
}
