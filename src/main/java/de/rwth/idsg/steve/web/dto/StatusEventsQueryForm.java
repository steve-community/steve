package de.rwth.idsg.steve.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class StatusEventsQueryForm extends SecurityEventsQueryForm {

    @Schema(description = "If not set, all types of events will be returned")
    private StatusEventType eventType;

    @Schema(description = "The identifier of the job")
    private Integer jobId;

    @Schema(hidden = true)
    public boolean isJobIdSet() {
        return jobId != null;
    }
}
