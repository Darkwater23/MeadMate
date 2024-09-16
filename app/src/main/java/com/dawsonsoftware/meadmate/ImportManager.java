package com.dawsonsoftware.meadmate;

import android.content.Context;

public class ImportManager {

    private MeadMateData meadData;

    public ImportManager(Context context)
    {
        //_backupModel = new MeadMateBackup();
        meadData = new MeadMateData(context);
    }
}
