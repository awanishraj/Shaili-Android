package in.ac.iitm.shaili.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import in.ac.iitm.shaili.Helpers.BitmapWriter;
import in.ac.iitm.shaili.ImageProcessing.BitmapProcessor;
import in.ac.iitm.shaili.Objects.RectLocation;
import in.ac.iitm.shaili.R;
import in.ac.iitm.shaili.Views.CameraPreview;

public class CameraActivity extends Activity {

    private Camera mCamera;
    private CameraPreview mPreview;
    private SurfaceView transparentView;
    private SurfaceHolder holderTransparent;
    RectLocation cropLocation = new RectLocation();
    FrameLayout preview;


    private static final String LOG_TAG = "CameraActivity";

    ProgressDialog pd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * Removing title bar
         */
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        /**
         * Setting layout for the activity
         */
        setContentView(R.layout.activity_main);

        /**
         * A transparent surface view for showing the crop rectangle
         */
        transparentView = (SurfaceView) findViewById(R.id.TransparentView);
        transparentView.setZOrderOnTop(true);
        holderTransparent = transparentView.getHolder();
        holderTransparent.setFormat(PixelFormat.TRANSPARENT);
        holderTransparent.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        /**
         * Setting up the Camera preview
         */
        mCamera = getCameraInstance();
        mPreview = new CameraPreview(this, mCamera);
        preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        preview.setOnTouchListener(onTouchListener);

        /**
         * Splash animation of the Shaili logo
         */
        ImageView iv = (ImageView) findViewById(R.id.iv_splash);
        AlphaAnimation animation1 = new AlphaAnimation(1.0f, 0.0f);
        animation1.setDuration(500);
        animation1.setStartOffset(1000);
        animation1.setFillAfter(true);
        iv.startAnimation(animation1);
    }

    /**
     * Method to draw a rectangle on the transparent view.
     *
     * @param location
     */
    private void DrawFocusRect(RectLocation location) {
        try {
            /**
             * Locking the canvas
             */
            Canvas canvas = holderTransparent.lockCanvas();
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            /**
             * Preparing the paint brush for the rectangle
             */
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(getResources().getColor(R.color.crop_rect));
            paint.setStrokeWidth(3);

            /**
             * Drawing the rectangle
             */
            canvas.drawRect(location.getLeft_top_x()
                    , location.getLeft_top_y()
                    , location.getRight_bottom_x()
                    , location.getRight_bottom_y()
                    , paint);

            /**
             * Unlocking the canvas
             */
            holderTransparent.unlockCanvasAndPost(canvas);
        } catch (Throwable e) {
            Log.e(LOG_TAG, "Error in drawing");
        }
    }


    /**
     * On touch listener for the camera preview. This gives coordinates for drawing the rectangle.
     */
    View.OnTouchListener onTouchListener = new View.OnTouchListener() {

//        float RectLeft = 0, RectTop = 0, RectRight = 0, RectBottom = 0;


        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    /**
                     * Setting the coordinates of the rectangle on initial touch
                     */
                    cropLocation.setLeft_top_x(event.getX());
                    cropLocation.setLeft_top_y(event.getY());
                    cropLocation.setRight_bottom_x(event.getX());
                    cropLocation.setRight_bottom_y(event.getY());

                    /**
                     * Attempting to focus the camera on initial touch
                     */
                    try {
                        mCamera.autoFocus(null);
                    } catch (Throwable e) {
                        Log.e(LOG_TAG, "Auto focus failed");
                    }

                    break;
                case MotionEvent.ACTION_UP:
                    /**
                     * Attempt to capture image when touch is released
                     */
                    try {
                        preview.setOnTouchListener(null);
                        mCamera.takePicture(null, null, mPicture);
                    } catch (Throwable e) {
                        Log.e(LOG_TAG, "Capture failed");
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    /**
                     * Set the new right-bottom-x and right-bottom-y locations when touch is dragged
                     */
                    cropLocation.setRight_bottom_x(event.getX());
                    cropLocation.setRight_bottom_y(event.getY());
                    break;
            }
            /**
             * Call the drawing method for the rectangle
             */
            DrawFocusRect(cropLocation);
            return true;
        }
    };


    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    /**
     * Callback after the image capture
     */
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(final byte[] data, Camera camera) {

            try {
                pd = ProgressDialog.show(CameraActivity.this, "Processing image", "Please wait...", true);
            } catch (Throwable ignored) {
            }
            new AsyncTask<Void, Void, Boolean>() {

                private RectLocation normLoc;
                private File pictureFile;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    normLoc = cropLocation.normalize(transparentView.getWidth(), transparentView.getHeight());
                }

                @Override
                protected Boolean doInBackground(Void... params) {
                    /**
                     * Decoding byte array to bitmap
                     */
                    Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);

                    /**
                     * Processing bitmaps and writing to file
                     */
                    BitmapWriter.write(BitmapProcessor.process(bmp, normLoc, BitmapProcessor.TYPE_NONE), "NONE");    //TODO ONLY FOR COMPARISON SAKE
                    BitmapWriter.write(BitmapProcessor.process(bmp, normLoc, BitmapProcessor.TYPE_OTSU), "OTSU");    //TODO ONLY FOR COMPARISON SAKE
                    pictureFile = BitmapWriter.write(BitmapProcessor.process(bmp, normLoc, BitmapProcessor.TYPE_ADAPTIVE), "ADAP");

                    bmp.recycle();

                    return pictureFile != null;
                }

                @Override
                protected void onPostExecute(Boolean success) {
                    super.onPostExecute(success);
                    try {
                        pd.dismiss();
                    } catch (Throwable ignored) {
                    }

                    if (success) {
                        /**
                         * Passing on the cropped image path to the Result Activity
                         */
                        Intent i = new Intent(CameraActivity.this, ResultActivity.class);
                        i.putExtra(ResultActivity.EXTRA_FILEPATH, pictureFile.getAbsolutePath());
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(CameraActivity.this, "Failed to capture. Please try again!", Toast.LENGTH_SHORT).show();
                    }
                }
            }.execute();

        }
    };




}
