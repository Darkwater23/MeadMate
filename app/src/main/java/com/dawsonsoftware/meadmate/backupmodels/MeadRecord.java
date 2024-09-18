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
import com.dawsonsoftware.meadmate.models.Mead;
import com.dawsonsoftware.meadmate.models.Reading;

import java.util.ArrayList;
import java.util.List;

public class MeadRecord extends Mead {

    private final List<String> tags;
    private final List<Event> events;
    private final List<Reading> readings;

    public MeadRecord(Mead mead)
    {
        this.setId(mead.getId());
        this.setArchived(mead.getArchived());
        this.setName(mead.getName());
        this.setDescription(mead.getDescription());
        this.setOriginalGravity(mead.getOriginalGravity());
        this.setStartDate(mead.getStartDate());

        this.tags = new ArrayList<>();
        this.events = new ArrayList<>();
        this.readings = new ArrayList<>();
    }

    public List<String> getTags()
    {
        return this.tags;
    }
    public List<Event> getEvents() { return this.events; }
    public List<Reading> getReadings() { return this.readings; }
}
