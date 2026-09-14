/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2026 SteVe Community Team
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
package de.rwth.idsg.steve.messaging;

import de.rwth.idsg.steve.ocpp.ws.data.CommunicationContext;
import org.springframework.messaging.Message;

/**
 * Typed boundaries around OCPP JSON messaging.
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 27.08.2026
 */
public interface Messaging {

    interface In {

        interface Producer {
            void send(Message<CommunicationContext.In> message);
        }

        interface Consumer {
            void processIn(Message<CommunicationContext.In> message);
        }
    }

    interface Out {

        interface Producer {
            void send(Message<CommunicationContext.Out> message);
        }

        interface Consumer {
            void processOut(Message<CommunicationContext.Out> message);
        }
    }

    interface OutCall {

        interface Producer {
            void send(Message<CommunicationContext.OutCall> message);
        }

        interface Consumer {
            void processOutCall(Message<CommunicationContext.OutCall> message);
        }
    }
}
