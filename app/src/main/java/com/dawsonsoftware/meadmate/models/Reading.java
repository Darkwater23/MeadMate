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

import java.time.LocalDate;

public class Reading {

    private int id;
    private int meadId;
    private LocalDate date;
    private String specificGravity;

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

    public LocalDate getDate() { return date; }

    public void setDate(LocalDate date) { this.date = date; }

    public String getSpecificGravity() {
        return specificGravity;
    }

    public void setSpecificGravity(String specificGravity) { this.specificGravity = specificGravity; }
}
