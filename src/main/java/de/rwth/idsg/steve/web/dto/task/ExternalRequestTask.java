package de.rwth.idsg.steve.web.dto.task;

import de.rwth.idsg.steve.ocpp.OcppVersion;
import de.rwth.idsg.steve.ocpp.RequestType;
import de.rwth.idsg.steve.repository.dto.ChargePointSelect;

import java.util.Collections;
import java.util.List;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 23.11.2015
 */
public class ExternalRequestTask extends RequestTask {

    ExternalRequestTask(OcppVersion ocppVersion, RequestType requestType,
                        List<ChargePointSelect> cpsList, String partnerName) {
        super(ocppVersion, requestType, cpsList, RequestTaskOrigin.EXTERNAL, partnerName);
    }

    // -------------------------------------------------------------------------
    // Custom builder
    // -------------------------------------------------------------------------

    public static ExternalRequestTaskBuilder builder() {
        return new ExternalRequestTaskBuilder();
    }

    public static class ExternalRequestTaskBuilder {
        private OcppVersion ocppVersion;
        private RequestType requestType;
        private List<ChargePointSelect> cpsList;
        private String partnerName;

        ExternalRequestTaskBuilder() { }

        public ExternalRequestTask.ExternalRequestTaskBuilder ocppVersion(OcppVersion ocppVersion) {
            this.ocppVersion = ocppVersion;
            return this;
        }

        public ExternalRequestTask.ExternalRequestTaskBuilder request(RequestType requestType) {
            this.requestType = requestType;
            return this;
        }

        public ExternalRequestTask.ExternalRequestTaskBuilder chargePoint(ChargePointSelect cps) {
            this.cpsList = Collections.singletonList(cps);
            return this;
        }

        public ExternalRequestTask.ExternalRequestTaskBuilder partnerName(String partnerName) {
            this.partnerName = partnerName;
            return this;
        }

        public ExternalRequestTask build() {
            return new ExternalRequestTask(ocppVersion, requestType, cpsList, partnerName);
        }
    }

}
