package com.dawsonsoftware.meadmate;

import android.util.Log;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class Utilities {
    private Utilities() {
        throw new UnsupportedOperationException();
    }

    public static LocalDate ConvertDateString(String date)
    {
        try
        {
            String pattern = null;

            if(date.contains("/"))
            {
                pattern = "MM/dd/yyyy";
            }

            if(date.contains("."))
            {
                pattern = "dd.MM.yyyy";
            }

            if(date.contains("-"))
            {
                pattern = "yyyy-MM-dd";
            }

            if(pattern == null)
            {
                throw new IllegalArgumentException("Date string is not in an expected format");
            }

            return LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern));
        }
        catch(Exception ex)
        {
            // Log the error
            Log.e("ConvertDateString", ex.toString());

            // What to return? I'll use a fixed, old date to so I know it broke here.
            return LocalDate.of(2024, 1, 1);
        }
    }
}