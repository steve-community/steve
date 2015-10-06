package de.rwth.idsg.steve.web.dto.common;

import de.rwth.idsg.steve.utils.StringUtils;
import lombok.Getter;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 06.10.2015
 */
@Getter
public abstract class AbstractNoteForm {

    private String note;

    public void setNote(String note) {
        this.note = (StringUtils.isNullOrEmpty(note)) ? null : note.trim();
    }
}
