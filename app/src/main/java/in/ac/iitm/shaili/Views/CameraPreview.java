package in.ac.iitm.shaili.Views;

/**
 * Created by Awanish Raj on 19/06/15.
 */

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;

/**
 * A basic Camera preview class
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private static final String LOG_TAG = "CameraPreview";
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private List<Camera.Size> mSupportedPreviewSizes;
    private Camera.Size mPreviewSize;


    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();


        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
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

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {

            if (mCamera == null)
                mCamera = getCameraInstance();


            /**
             * Querying the supported preview sizes
             */
            mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();

            /**
             * Getting the camera parameters
             */
            Camera.Parameters parameters = mCamera.getParameters();

            /**
             * Setting jpeg-quality parameter
             */
//            parameters.set("jpeg-quality", 100);
//            parameters.setJpegQuality(90);
//            parameters.setPictureFormat(ImageFormat.JPEG);
            parameters.setJpegQuality(70);
//            parameters.setPictureFormat(PixelFormat.RGB_565);
            parameters.setPictureFormat(ImageFormat.JPEG);

            parameters.setSceneMode(Camera.Parameters.SCENE_MODE_STEADYPHOTO);
//            parameters.setPictureFormat(PixelFormat.OPAQUE);

            /**
             * Setting the capture size
             */
            List<Camera.Size> abc = parameters.getSupportedPictureSizes();
            parameters.setPictureSize(abc.get(0).width, abc.get(0).height);

            Camera.Size ab = parameters.getPictureSize();
            Log.d(LOG_TAG, "Current picture size: " + ab.width + " x " + ab.height);

            /**
             * Setting the focus mode
             */
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

            /**
             * Setting the preview size
             */
            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
            parameters.setPictureSize(mPreviewSize.width, mPreviewSize.height);

            Log.e(LOG_TAG, "Preview size: " + mPreviewSize.width + " - " + mPreviewSize.height);

            mCamera.setParameters(parameters);
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
            mCamera.autoFocus(null);

        } catch (Throwable e) {
            Log.d(LOG_TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);

        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
        }

        float ratio;
        if (mPreviewSize.height >= mPreviewSize.width)
            ratio = (float) mPreviewSize.height / (float) mPreviewSize.width;
        else
            ratio = (float) mPreviewSize.width / (float) mPreviewSize.height;

        // One of these methods should be used, second method squishes preview slightly
//        setMeasuredDimension(width, (int) (width * ratio));
//            setMeasuredDimension((int) (width * ratio), height);
        setMeasuredDimension((int) (width), height);
    }


    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null)
            return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.height / size.width;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;

            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }

        return optimalSize;
    }


    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e) {
            Log.d(LOG_TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }
}