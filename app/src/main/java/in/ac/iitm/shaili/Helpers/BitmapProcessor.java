package in.ac.iitm.shaili.Helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import in.ac.iitm.shaili.Objects.RectLocation;

import static java.lang.Math.abs;

/**
 * Created by Awanish Raj on 20/06/15.
 */
public class BitmapProcessor {
    private static final String LOG_TAG = "BitmapProcesor";

    public static Bitmap process(Context context, Bitmap toTransform, RectLocation cropLocation){
        float width = abs(cropLocation.getLeft_top_x() - cropLocation.getRight_bottom_x());
        float height = abs(cropLocation.getLeft_top_y() - cropLocation.getRight_bottom_y());

        float x = 0, y = 0;
        if (cropLocation.getLeft_top_x() < cropLocation.getRight_bottom_x()) {
            x = cropLocation.getLeft_top_x();
        } else {
            x = cropLocation.getRight_bottom_x();
        }

        if (cropLocation.getLeft_top_y() < cropLocation.getRight_bottom_y()) {
            y = cropLocation.getLeft_top_y();
        } else {
            y = cropLocation.getRight_bottom_y();
        }

        x = x * toTransform.getWidth();
        width = width * toTransform.getWidth();

        y = y * toTransform.getHeight();
        height = height * toTransform.getHeight();

        Bitmap myTransformedBitmap = null;
        try {
            myTransformedBitmap = Bitmap.createBitmap(toTransform,
                    (int) x,
                    (int) y,
                    (int) width,
                    (int) height);


        } catch (Throwable e) {
            e.printStackTrace();
        }

        long startTime1 = System.currentTimeMillis();
//        myTransformedBitmap = ImageSharpener.sharpenBitmap(myTransformedBitmap, ConvoMatrices.getConvolutionMatrix(9),context);
        Log.e(LOG_TAG, "Time taken for sharpening = " + (System.currentTimeMillis() - startTime1) / 1000.0 + "s");

        long startTime = System.currentTimeMillis();
        myTransformedBitmap = ImageBinarize.binarize(myTransformedBitmap);
        Log.e(LOG_TAG, "Time taken for binarization = " + (System.currentTimeMillis() - startTime) / 1000.0 + "s");
        return myTransformedBitmap;
    }
}
