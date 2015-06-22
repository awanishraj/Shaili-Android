package in.ac.iitm.shaili.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.googlecode.tesseract.android.TessBaseAPI;

import org.json.JSONException;

import in.ac.iitm.shaili.Helpers.CropTransform;
import in.ac.iitm.shaili.Objects.RectLocation;
import in.ac.iitm.shaili.R;
import in.ac.iitm.shaili.Utils.Constants;

/**
 * Created by Awanish Raj on 19/06/15.
 */
public class ResultActivity extends Activity {

    private static final String LOG_TAG = "ResultActivity";

    public static final String EXTRA_FILEPATH = "extra_filepath";
    public static final String EXTRA_LOCATION = "extra_location";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ImageView iv_result = (ImageView) findViewById(R.id.iv_result);
        final TextView tv_result = (TextView) findViewById(R.id.tv_result);

        /**
         * Getting the intent from the previous crossus activity. The filePath from the original image comes here.
         */
        Intent i = getIntent();
        final String filePath = i.getStringExtra(EXTRA_FILEPATH);
        RectLocation cropLocation = new RectLocation();
        try {
            cropLocation.parseString(i.getStringExtra(EXTRA_LOCATION));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            Log.d(LOG_TAG, "Crop location - " + cropLocation.getString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /**
         * Loading the image into the imageview
         */
        Glide.with(this).load(filePath)
                .transform(new CropTransform(ResultActivity.this, cropLocation))
                .into(iv_result);


        new AsyncTask<Void, Void, String>() {
            long oldtime1;

            @Override
            protected String doInBackground(Void... params) {

                oldtime1 = System.currentTimeMillis();
                Bitmap bmp = BitmapFactory.decodeFile(filePath);
                TessBaseAPI baseApi = new TessBaseAPI();
                baseApi.init(Constants.SHAILI_PATH, "eng");
                Log.e(LOG_TAG, "Time in init - " + ((System.currentTimeMillis() - oldtime1) / 1000.0) + "s");
                long oldtime = System.currentTimeMillis();
                baseApi.setImage(bmp);
                Log.e(LOG_TAG, "Time in setting image - " + ((System.currentTimeMillis() - oldtime) / 1000.0) + "s");
                oldtime = System.currentTimeMillis();

                String recognizedText = baseApi.getUTF8Text();
                Log.e(LOG_TAG, "Time in getting text - " + ((System.currentTimeMillis() - oldtime) / 1000.0) + "s");

                Log.e(LOG_TAG, "Recognized text - " + recognizedText);
                baseApi.end();

                return recognizedText;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                tv_result.setText(result);
                Toast.makeText(ResultActivity.this, "Time taken: " + (System.currentTimeMillis() - oldtime1) / 1000.0 + "s", Toast.LENGTH_SHORT).show();
            }
        }.execute();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
