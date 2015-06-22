package in.ac.iitm.shaili.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Awanish Raj on 20/06/15.
 */
public class SPUtil {

    private static final String spUtils = "ShailiSP";
    private static final String spVersion = "pack_version";

    public static boolean isPackLatest(Context context) {
        SharedPreferences sp = context.getSharedPreferences(spUtils, Context.MODE_PRIVATE);
        int currVersion = sp.getInt(spVersion, 0);
        return Constants.PACK_VERSION > currVersion ? false : true;
    }

    public static void updatePackVersion(Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(spUtils, Context.MODE_PRIVATE).edit();
        editor.putInt(spVersion, Constants.PACK_VERSION);
        editor.commit();
    }
}
