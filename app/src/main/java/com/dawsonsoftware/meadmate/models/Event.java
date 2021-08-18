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

public class Event {
    private int id;
    private int meadId;
    private String date;
    private int typeId;
    private String typeName;
    private String description;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMeadId() {
        return meadId;
    }

    public void setMeadId(int meadId) {
        this.meadId = meadId;
    }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) { this.typeId = typeId; }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) { this.description = description; }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
