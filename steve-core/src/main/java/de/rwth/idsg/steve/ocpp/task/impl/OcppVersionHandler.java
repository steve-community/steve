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
package de.rwth.idsg.steve.ocpp.task.impl;

import de.rwth.idsg.ocpp.jaxb.RequestType;
import de.rwth.idsg.ocpp.jaxb.ResponseType;
import de.rwth.idsg.steve.ocpp.CommunicationTask;
import de.rwth.idsg.steve.web.dto.ocpp.ChargePointSelection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class OcppVersionHandler<S extends ChargePointSelection, U> {
    private final RequestMapper<? extends CommunicationTask<S, U>, ? extends RequestType> requestMapper;
    private final ResponseMapper<? extends ResponseType, U> responseMapper;
}
