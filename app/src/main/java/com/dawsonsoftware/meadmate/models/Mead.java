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

package com.dawsonsoftware.meadmate.models;

public class Mead {

    private int id;
    private String name;
    private String startDate;
    private String description;
    private String originalGravity;
    private boolean archived;

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() { return startDate; }

    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOriginalGravity() {
        return originalGravity;
    }

    public void setOriginalGravity(String originalGravity) { this.originalGravity = originalGravity; }

    public boolean getArchived() { return archived; }

    public void setArchived(boolean archived) { this.archived = archived; }
}
