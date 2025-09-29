/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2025 SteVe Community Team
 * All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package de.rwth.idsg.steve.ocpp20.task;

import de.rwth.idsg.steve.ocpp20.model.GetVariablesRequest;
import de.rwth.idsg.steve.ocpp20.model.GetVariablesResponse;
import de.rwth.idsg.steve.ocpp20.model.GetVariableData;
import de.rwth.idsg.steve.ocpp20.model.Component;
import de.rwth.idsg.steve.ocpp20.model.Variable;
import de.rwth.idsg.steve.ocpp20.model.AttributeEnum;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GetVariablesTask extends Ocpp20Task<GetVariablesRequest, GetVariablesResponse> {

    private final List<VariableRequest> variableRequests;

    public GetVariablesTask(List<String> chargeBoxIds, List<VariableRequest> variableRequests) {
        super("GetVariables", chargeBoxIds);
        this.variableRequests = variableRequests;
    }

    @Override
    public GetVariablesRequest createRequest() {
        GetVariablesRequest request = new GetVariablesRequest();
        List<GetVariableData> getVariableData = new ArrayList<>();

        for (VariableRequest varReq : variableRequests) {
            GetVariableData data = new GetVariableData();

            Component component = new Component();
            component.setName(varReq.getComponentName());
            if (varReq.getComponentInstance() != null) {
                component.setInstance(varReq.getComponentInstance());
            }
            if (varReq.getComponentEvseId() != null) {
                component.setEvse(new de.rwth.idsg.steve.ocpp20.model.EVSE());
                component.getEvse().setId(varReq.getComponentEvseId());
                if (varReq.getComponentConnectorId() != null) {
                    component.getEvse().setConnectorId(varReq.getComponentConnectorId());
                }
            }
            data.setComponent(component);

            Variable variable = new Variable();
            variable.setName(varReq.getVariableName());
            if (varReq.getVariableInstance() != null) {
                variable.setInstance(varReq.getVariableInstance());
            }
            data.setVariable(variable);

            if (varReq.getAttributeType() != null) {
                data.setAttributeType(varReq.getAttributeType());
            }

            getVariableData.add(data);
        }

        request.setGetVariableData(getVariableData);
        return request;
    }

    @Override
    public Class<GetVariablesResponse> getResponseClass() {
        return GetVariablesResponse.class;
    }

    @Getter
    public static class VariableRequest {
        private final String componentName;
        private final String componentInstance;
        private final Integer componentEvseId;
        private final Integer componentConnectorId;
        private final String variableName;
        private final String variableInstance;
        private final AttributeEnum attributeType;

        public VariableRequest(String componentName, String variableName) {
            this(componentName, null, null, null, variableName, null, null);
        }

        public VariableRequest(String componentName, String variableName, AttributeEnum attributeType) {
            this(componentName, null, null, null, variableName, null, attributeType);
        }

        public VariableRequest(String componentName, String componentInstance,
                             Integer componentEvseId, Integer componentConnectorId,
                             String variableName, String variableInstance,
                             AttributeEnum attributeType) {
            this.componentName = componentName;
            this.componentInstance = componentInstance;
            this.componentEvseId = componentEvseId;
            this.componentConnectorId = componentConnectorId;
            this.variableName = variableName;
            this.variableInstance = variableInstance;
            this.attributeType = attributeType;
        }
    }
}