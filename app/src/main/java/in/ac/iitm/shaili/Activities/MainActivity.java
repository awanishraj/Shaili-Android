package in.ac.iitm.shaili.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import in.ac.iitm.shaili.Helpers.BitmapProcessor;
import in.ac.iitm.shaili.Helpers.MediaHelper;
import in.ac.iitm.shaili.Objects.RectLocation;
import in.ac.iitm.shaili.R;
import in.ac.iitm.shaili.Views.CameraPreview;

public class MainActivity extends Activity {

    private Camera mCamera;
    private CameraPreview mPreview;
    private SurfaceView transparentView;
    private SurfaceHolder holderTransparent;

    private static final String LOG_TAG = "MainActivity";

    ProgressDialog pd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);


        // Create second surface with another holder (holderTransparent)
        transparentView = (SurfaceView) findViewById(R.id.TransparentView);
        transparentView.setZOrderOnTop(true);
        holderTransparent = transparentView.getHolder();
        holderTransparent.setFormat(PixelFormat.TRANSPARENT);
//        holderTransparent.addCallback(callBack);
        holderTransparent.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


        // Create an instance of Camera
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        preview.setOnTouchListener(onTouchListener);

        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.autoFocus(null);
            }
        });


        ImageView iv = (ImageView) findViewById(R.id.iv_splash);
        AlphaAnimation animation1 = new AlphaAnimation(1.0f, 0.0f);
        animation1.setDuration(800);
        animation1.setStartOffset(1200);
        animation1.setFillAfter(true);
        iv.startAnimation(animation1);
        // Add a listener to the Capture button
        // get an image from the camera
    }

    private void DrawFocusRect(RectLocation location, int color) {

        try {
            Canvas canvas = holderTransparent.lockCanvas();
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            //border's properties
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(color);
            paint.setStrokeWidth(3);
            canvas.drawRect(location.getLeft_top_x()
                    , location.getLeft_top_y()
                    , location.getRight_bottom_x()
                    , location.getRight_bottom_y()
                    , paint);

            holderTransparent.unlockCanvasAndPost(canvas);
        } catch (Throwable e) {
            Log.e(LOG_TAG, "Error in drawing");
        }
    }

    RectLocation cropLocation = new RectLocation();
    View.OnTouchListener onTouchListener = new View.OnTouchListener() {

//        float RectLeft = 0, RectTop = 0, RectRight = 0, RectBottom = 0;


        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    cropLocation.setLeft_top_x(event.getX());
                    cropLocation.setLeft_top_y(event.getY());
                    try {
                        mCamera.autoFocus(null);
                    } catch (Throwable e) {
                        Log.e(LOG_TAG, "Auto focus failed");
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    try {
                        pd = ProgressDialog.show(MainActivity.this, "Processing image", "Please wait...", true);
                        mCamera.takePicture(null, null, mPicture);
                    } catch (Throwable e) {
                        Log.e(LOG_TAG, "Capture failed");
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    cropLocation.setRight_bottom_x(event.getX());
                    cropLocation.setRight_bottom_y(event.getY());
                    break;
            }
            DrawFocusRect(cropLocation, Color.GREEN);
            return true;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);

            long oldTime = System.currentTimeMillis();
            Bitmap transformed = BitmapProcessor.process(MainActivity.this, bmp, cropLocation.normalize(transparentView.getWidth(), transparentView.getHeight()));
            Log.d(LOG_TAG, "TIme taken for the processing : " + (System.currentTimeMillis() - oldTime) / 1000.0 + "s");

            File pictureFile = MediaHelper.getOutputMediaFile(MediaHelper.MEDIA_TYPE_IMAGE);
            if (pictureFile == null) {
                Log.d(LOG_TAG, "Error creating media file, check storage permissions: ");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                transformed.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();


                Intent i = new Intent(MainActivity.this, ResultActivity.class);
                i.putExtra(ResultActivity.EXTRA_FILEPATH, pictureFile.getAbsolutePath());
//                i.putExtra(ResultActivity.EXTRA_LOCATION, cropLocation.getString());
                i.putExtra(ResultActivity.EXTRA_LOCATION, cropLocation.normalize(transparentView.getWidth(), transparentView.getHeight()).getString());
                pd.dismiss();
                startActivity(i);
                finish();

            } catch (FileNotFoundException e) {
                Log.d(LOG_TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(LOG_TAG, "Error accessing file: " + e.getMessage());
            } catch (JSONException e) {
                Log.d(LOG_TAG, "Error parsing JSON");
            }
        }
    };


}
