package in.ac.iitm.shaili.Helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import org.json.JSONException;

import in.ac.iitm.shaili.Objects.RectLocation;

import static java.lang.Math.abs;

/**
 * Created by Awanish Raj on 20/06/15.
 */
public class CropTransform extends BitmapTransformation {

    RectLocation cropLocation;
    private static final String LOG_TAG = "CropTransform";
    private Context context;

    public CropTransform(Context context, RectLocation cropLocation) {
        super(context);
        this.cropLocation = cropLocation;
        this.context = context;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform,
                               int outWidth, int outHeight) {
        return toTransform;
    }

    @Override
    public String getId() {
        // Return some id that uniquely identifies your transformation.
        return "in.ac.iitm.shaili";
    }
}
