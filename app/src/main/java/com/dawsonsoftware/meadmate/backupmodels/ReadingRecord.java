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
import com.dawsonsoftware.meadmate.models.Reading;

import java.time.LocalDate;
import java.util.UUID;

public class ReadingRecord extends Reading {
    private UUID uuid;
    private UUID meadUuid;

    public ReadingRecord()
    {
        this.uuid = UUID.randomUUID();
    }

    public ReadingRecord(Reading reading)
    {
        this.uuid = UUID.randomUUID();
        this.setId(reading.getId());
        this.setDate(reading.getDate());
        this.setMeadId(reading.getMeadId());
        this.setSpecificGravity(reading.getSpecificGravity());
    }

    public UUID getUuid() {
        return uuid;
    }
    public UUID getMeadUuid() { return meadUuid; }
    public void setMeadUuid(UUID uuid) { this.meadUuid = uuid; }
}
