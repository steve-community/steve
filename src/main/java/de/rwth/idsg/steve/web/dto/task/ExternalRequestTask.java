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
public class ExternalRequestTask<S extends RequestType> extends RequestTask<S> {

    ExternalRequestTask(OcppVersion ocppVersion, S requestType,
                        List<ChargePointSelect> cpsList, String partnerName) {
        super(ocppVersion, requestType, cpsList, RequestTaskOrigin.EXTERNAL, partnerName);
    }

    // -------------------------------------------------------------------------
    // Custom builder
    // -------------------------------------------------------------------------

    public static <S extends RequestType> ExternalRequestTaskBuilder<S> builder(S request) {
        return new ExternalRequestTaskBuilder<>(request);
    }

    public static class ExternalRequestTaskBuilder<S extends RequestType> {
        private OcppVersion ocppVersion;
        private S request;
        private List<ChargePointSelect> cpsList;
        private String partnerName;

        ExternalRequestTaskBuilder(S request) {
            this.request = request;
        }

        public ExternalRequestTask.ExternalRequestTaskBuilder<S> ocppVersion(OcppVersion ocppVersion) {
            this.ocppVersion = ocppVersion;
            return this;
        }

        public ExternalRequestTask.ExternalRequestTaskBuilder<S> chargePoint(ChargePointSelect cps) {
            this.cpsList = Collections.singletonList(cps);
            return this;
        }

        public ExternalRequestTaskBuilder<S> partnerName(String partnerName) {
            this.partnerName = partnerName;
            return this;
        }

        public ExternalRequestTask<S> build() {
            return new ExternalRequestTask<>(ocppVersion, request, cpsList, partnerName);
        }
    }

}
