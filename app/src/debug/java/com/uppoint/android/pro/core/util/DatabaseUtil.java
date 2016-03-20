package com.uppoint.android.pro.core.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 */
public class DatabaseUtil {

    private DatabaseUtil() {
        // deny instantiation
    }

    public static void extractDatabase(Context context, String databaseName) throws IOException {
        final File databaseFile = context.getApplicationContext().getDatabasePath(databaseName);
        if (databaseFile.exists()) {
            final File databaseDumpFile = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), databaseName);
            FileChannel source = null;
            FileChannel destination = null;
            try {
                source = new FileInputStream(databaseFile).getChannel();
                destination = new FileOutputStream(databaseDumpFile).getChannel();
                destination.transferFrom(source, 0, source.size());
            } finally {
                if (source != null) {
                    source.close();
                }

                if (destination != null) {
                    destination.close();
                }
            }
        }

    }
}
