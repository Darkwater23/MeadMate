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

import com.dawsonsoftware.meadmate.models.Recipe;

import java.util.ArrayList;
import java.util.List;

// This model will include the logical object hierarchy
// Leaving the other models as is to avoid breaking any contracts
public class MeadMateBackup
{
    private final List<MeadRecord> meadRecords;
    private final List<Recipe> recipes;

    public MeadMateBackup()
    {
        meadRecords = new ArrayList<>();
        recipes = new ArrayList<>();
    }

    public List<MeadRecord> getMeadRecords()
    {
        return meadRecords;
    }

    public List<Recipe> getRecipes() { return recipes; }
}
