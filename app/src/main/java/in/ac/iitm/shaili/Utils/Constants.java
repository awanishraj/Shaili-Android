package in.ac.iitm.shaili.Utils;

import android.os.Environment;

/**
 * Created by Awanish Raj on 20/06/15.
 */
public class Constants {

    public static final int PACK_VERSION = 1;

    public static final String SHAILI_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Shaili Data";

    public static final String SHAILI_TESSDATA_PATH = SHAILI_PATH+"/tessdata";
    public static String[] dataPacks = {
            "eng.traineddata",
            "hin.traineddata"
    };

    public static final String ENG_PACK_PATH = SHAILI_PATH+"/"+dataPacks[0];
    public static final String HIN_PACK_PATH = SHAILI_PATH+"/"+dataPacks[1];
}
