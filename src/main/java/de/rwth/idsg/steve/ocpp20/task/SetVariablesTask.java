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

import de.rwth.idsg.steve.ocpp20.model.SetVariablesRequest;
import de.rwth.idsg.steve.ocpp20.model.SetVariablesResponse;
import de.rwth.idsg.steve.ocpp20.model.SetVariableData;
import de.rwth.idsg.steve.ocpp20.model.Component;
import de.rwth.idsg.steve.ocpp20.model.Variable;
import de.rwth.idsg.steve.ocpp20.model.AttributeEnum;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SetVariablesTask extends Ocpp20Task<SetVariablesRequest, SetVariablesResponse> {

    private final List<VariableSet> variableSets;

    public SetVariablesTask(List<String> chargeBoxIds, List<VariableSet> variableSets) {
        super("SetVariables", chargeBoxIds);
        this.variableSets = variableSets;
    }

    @Override
    public SetVariablesRequest createRequest() {
        SetVariablesRequest request = new SetVariablesRequest();
        List<SetVariableData> setVariableData = new ArrayList<>();

        for (VariableSet varSet : variableSets) {
            SetVariableData data = new SetVariableData();

            Component component = new Component();
            component.setName(varSet.getComponentName());
            if (varSet.getComponentInstance() != null) {
                component.setInstance(varSet.getComponentInstance());
            }
            if (varSet.getComponentEvseId() != null) {
                component.setEvse(new de.rwth.idsg.steve.ocpp20.model.EVSE());
                component.getEvse().setId(varSet.getComponentEvseId());
                if (varSet.getComponentConnectorId() != null) {
                    component.getEvse().setConnectorId(varSet.getComponentConnectorId());
                }
            }
            data.setComponent(component);

            Variable variable = new Variable();
            variable.setName(varSet.getVariableName());
            if (varSet.getVariableInstance() != null) {
                variable.setInstance(varSet.getVariableInstance());
            }
            data.setVariable(variable);

            data.setAttributeValue(varSet.getAttributeValue());

            if (varSet.getAttributeType() != null) {
                data.setAttributeType(varSet.getAttributeType());
            }

            setVariableData.add(data);
        }

        request.setSetVariableData(setVariableData);
        return request;
    }

    @Override
    public Class<SetVariablesResponse> getResponseClass() {
        return SetVariablesResponse.class;
    }

    @Getter
    public static class VariableSet {
        private final String componentName;
        private final String componentInstance;
        private final Integer componentEvseId;
        private final Integer componentConnectorId;
        private final String variableName;
        private final String variableInstance;
        private final String attributeValue;
        private final AttributeEnum attributeType;

        public VariableSet(String componentName, String variableName, String attributeValue) {
            this(componentName, null, null, null, variableName, null, attributeValue, null);
        }

        public VariableSet(String componentName, String variableName, String attributeValue, AttributeEnum attributeType) {
            this(componentName, null, null, null, variableName, null, attributeValue, attributeType);
        }

        public VariableSet(String componentName, String componentInstance,
                          Integer componentEvseId, Integer componentConnectorId,
                          String variableName, String variableInstance,
                          String attributeValue, AttributeEnum attributeType) {
            this.componentName = componentName;
            this.componentInstance = componentInstance;
            this.componentEvseId = componentEvseId;
            this.componentConnectorId = componentConnectorId;
            this.variableName = variableName;
            this.variableInstance = variableInstance;
            this.attributeValue = attributeValue;
            this.attributeType = attributeType;
        }
    }
}