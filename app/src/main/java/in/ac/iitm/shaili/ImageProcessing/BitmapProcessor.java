package in.ac.iitm.shaili.ImageProcessing;

import android.graphics.Bitmap;
import android.util.Log;

import in.ac.iitm.shaili.Objects.RectLocation;

/**
 * Created by Awanish Raj on 20/06/15.
 */
public class BitmapProcessor {

    private static final String LOG_TAG = "BitmapProcesor";

    /**
     * Method that binarizes and crops the input image
     *
     * @param toTransform
     * @param cropLocation
     * @return
     */
    public static Bitmap process(Bitmap toTransform, RectLocation cropLocation) {

        /**
         * Getting the dimensions of the rectangle
         */
        float width = cropLocation.getAbsWidth();
        float height = cropLocation.getAbsHeight();
        float x = cropLocation.getSmallestX();
        float y = cropLocation.getSmallestY();

        /**
         * Scaling to actual bitmap dimensions
         */
        x = x * toTransform.getWidth();
        width = width * toTransform.getWidth();
        y = y * toTransform.getHeight();
        height = height * toTransform.getHeight();

        /**
         * Truncating to the bitmap dimensions
         */
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x + width > toTransform.getWidth()) width = toTransform.getWidth() - x;
        if (y + height > toTransform.getHeight()) height = toTransform.getHeight() - y;

        /**
         * Cropping the image based on the scaled dimensions
         */
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

//        /**
//         * Sharpening the image before binarization
//         */
//        long startTime1 = System.currentTimeMillis();
//        myTransformedBitmap = ImageSharpener.sharpenBitmap(myTransformedBitmap, ConvoMatrices.getConvolutionMatrix(9),context);
//        Log.e(LOG_TAG, "Time taken for sharpening = " + (System.currentTimeMillis() - startTime1) / 1000.0 + "s");

        /**
         * Binarizing the image
         */
        long startTime = System.currentTimeMillis();
        myTransformedBitmap = ImageBinarize.binarize(myTransformedBitmap);
        Log.e(LOG_TAG, "Time taken for binarization = " + (System.currentTimeMillis() - startTime) / 1000.0 + "s");


        return myTransformedBitmap;
    }
}
