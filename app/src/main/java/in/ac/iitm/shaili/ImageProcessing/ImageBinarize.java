/*******************************************************************************
 * Copyright 2015 Sridhar Ananthakrishnan
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package in.ac.iitm.shaili.ImageProcessing;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

public class ImageBinarize {

    /**
     * Luminance method for converting image to grayscale
     * @param mOrginalImage
     * @return
     */
    public static Bitmap toGray(Bitmap mOrginalImage) {

        final int imgWidth = mOrginalImage.getWidth();
        final int imgHeight = mOrginalImage.getHeight();

        final Bitmap lum = Bitmap.createBitmap(imgWidth, imgHeight, Bitmap.Config.ARGB_8888);
        final Canvas mBitmapCanvas = new Canvas(lum);
        final Paint mPaint = new Paint();
        final ColorMatrix mColorMatrix = new ColorMatrix();
        mColorMatrix.setSaturation(0);
        final ColorMatrixColorFilter mColorMatrixColorFilter = new ColorMatrixColorFilter(mColorMatrix);
        mPaint.setColorFilter(mColorMatrixColorFilter);
        mBitmapCanvas.drawBitmap(mOrginalImage, 0, 0, mPaint);
        return lum;
    }

    /**
     * Method to convert RGB bitmap to grayscale
     * @param bmpOriginal
     * @return
     */
    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
        final int height = bmpOriginal.getHeight();
        final int width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    /**
     * Return a histogram of the pixels in the image based for 256 levels.
     * @param mImage
     * @return
     */
    public static int[] mImageHistogram(Bitmap mImage) {

        int[] histogram = new int[256];

        int mPixel;
        int redValue;


        for (int i = 0; i < histogram.length; i++) {
            histogram[i] = 0;
        }

        for (int i = 0; i < mImage.getWidth(); i++) {
            for (int j = 0; j < mImage.getHeight(); j++) {
                mPixel = mImage.getPixel(i, j);
                redValue = Color.red(mPixel);
                histogram[redValue]++;
            }

        }

        return histogram;
    }

    /**
     * Method to obtain threshold value as per Otsu thresholding algorithm
     * @param Original
     * @return
     */
    private static int mOtsuTreshold(Bitmap Original) {
        int[] histogram = mImageHistogram(Original);
        int total = Original.getHeight() * Original.getWidth();

        float sum = 0;
        for (int i = 0; i < 256; i++) {
            sum += i * histogram[i];
        }

        float sumB = 0;
        int wB = 0;
        int wF = 0;

        float varMax = 0;
        int threshold = 0;

        for (int i = 0; i < 256; i++) {
            wB += histogram[i];
            if (wB == 0) continue;
            wF = total - wB;

            if (wF == 0) break;

            sumB += (float) (i * histogram[i]);
            float mB = sumB / wB;
            float mF = (sum - sumB) / wF;

            float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);

            if (varBetween > varMax) {
                varMax = varBetween;
                threshold = i;
            }
        }

        return threshold;
    }

    /**
     * Binarizing the given image.
     * @param original
     * @return
     */
    public static Bitmap binarize(Bitmap original) {

        int red, alpha;
        int newPixel, mPixel;

        int threshold = mOtsuTreshold(original);

        final Bitmap binarized = Bitmap.createBitmap(original);

        for (int i = 0; i < original.getWidth(); i++) {
            for (int j = 0; j < original.getHeight(); j++) {
                //Get Pixels
                mPixel = original.getPixel(i, j);
                red = Color.red(mPixel);
                alpha = Color.alpha(mPixel);

                if (red > threshold) {
                    newPixel = 255;
                } else {
                    newPixel = 0;
                }

                newPixel = colorToRGB(alpha, newPixel, newPixel, newPixel);
                binarized.setPixel(i, j, newPixel);
            }
        }

        return binarized;

    }

    /**
     * Converting ARGB values to pixel value
     * @param alpha
     * @param red
     * @param green
     * @param blue
     * @return
     */
    private static int colorToRGB(int alpha, int red, int green, int blue) {

        int newPixel = 0;
        newPixel += alpha;
        newPixel = newPixel << 8;
        newPixel += red;
        newPixel = newPixel << 8;
        newPixel += green;
        newPixel = newPixel << 8;
        newPixel += blue;

        return newPixel;

    }

}
