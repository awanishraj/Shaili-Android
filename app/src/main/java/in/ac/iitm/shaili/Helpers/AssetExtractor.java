package in.ac.iitm.shaili.Helpers;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import in.ac.iitm.shaili.Utils.Constants;

/**
 * Created by Awanish Raj on 20/06/15.
 */
public class AssetExtractor {

    private static final String LOG_TAG = "AssetExtractor";


    public static void unpack(Context context) throws IOException {
        AssetManager am = context.getAssets();
        for (String pack : Constants.dataPacks) {
            Log.d(LOG_TAG, "Extracting " + pack + "...");
            createFileFromInputStream(am, pack);
        }
    }

    private static File createFileFromInputStream(AssetManager am, String fileName) throws IOException {

        InputStream inputStream = am.open(fileName);
        File appDirectory = new File(Constants.SHAILI_TESSDATA_PATH);
        appDirectory.mkdirs();
        File f = new File(appDirectory, fileName);
        OutputStream outputStream = new FileOutputStream(f);
        byte buffer[] = new byte[1024];
        int length = 0;

        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        outputStream.close();
        inputStream.close();
        return f;
    }
}
