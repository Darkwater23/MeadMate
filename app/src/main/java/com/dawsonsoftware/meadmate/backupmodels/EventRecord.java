/*
This file is part of Mead Mate.

Mead Mate is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Mead Mate is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Mead Mate.  If not, see <https://www.gnu.org/licenses/>.
*/

package com.dawsonsoftware.meadmate.backupmodels;

import com.dawsonsoftware.meadmate.models.Event;

import java.util.UUID;

public class EventRecord extends Event {
    private final UUID uuid;
    private UUID meadUuid;

    public EventRecord()
    {
        this.uuid = UUID.randomUUID();
    }

    public EventRecord(Event event)
    {
        this.uuid = UUID.randomUUID();
        this.setDate(event.getDate());
        this.setId(event.getId());
        this.setDescription(event.getDescription());
        this.setMeadId(event.getMeadId());
        this.setTypeId(event.getTypeId());
        this.setTypeName(event.getTypeName());
    }

    public UUID getUuid() {
        return uuid;
    }
    public UUID getMeadUuid() { return meadUuid; }
    public void setMeadUuid(UUID uuid) { this.meadUuid = uuid; }
}
