package in.ac.iitm.shaili.Objects;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Awanish Raj on 20/06/15.
 */
public class RectLocation {

    private float left_top_x;
    private float left_top_y;
    private float right_bottom_x;
    private float right_bottom_y;

    private static final String LEFT_TOP_X = "left-top-x";
    private static final String LEFT_TOP_Y = "left-top-y";
    private static final String RIGHT_BOTTOM_X = "right-bottom-x";
    private static final String RIGHT_BOTTOM_Y = "right-bottom-y";

    public float getLeft_top_x() {
        return left_top_x;
    }

    public float getLeft_top_y() {
        return left_top_y;
    }

    public float getRight_bottom_x() {
        return right_bottom_x;
    }

    public float getRight_bottom_y() {
        return right_bottom_y;
    }

    public void setLeft_top_x(float left_top_x) {
        this.left_top_x = left_top_x;
    }

    public void setLeft_top_y(float left_top_y) {
        this.left_top_y = left_top_y;
    }

    public void setRight_bottom_x(float right_bottom_x) {
        this.right_bottom_x = right_bottom_x;
    }

    public void setRight_bottom_y(float right_bottom_y) {
        this.right_bottom_y = right_bottom_y;
    }

    public JSONObject getJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(LEFT_TOP_X, this.left_top_x);
        jsonObject.put(LEFT_TOP_Y, this.left_top_y);
        jsonObject.put(RIGHT_BOTTOM_X, this.right_bottom_x);
        jsonObject.put(RIGHT_BOTTOM_Y, this.right_bottom_y);
        return jsonObject;
    }

    public void parseJson(JSONObject jsonObject) throws JSONException {
        this.left_top_x = (float) jsonObject.getDouble(LEFT_TOP_X);
        this.left_top_y = (float) jsonObject.getDouble(LEFT_TOP_Y);
        this.right_bottom_x = (float) jsonObject.getDouble(RIGHT_BOTTOM_X);
        this.right_bottom_y = (float) jsonObject.getDouble(RIGHT_BOTTOM_Y);
    }


    public String getString() throws JSONException {
        return getJson().toString();
    }

    public void parseString(String source) throws JSONException {
        this.parseJson(new JSONObject(source));
    }

    public RectLocation normalize(float width, float height) {
        RectLocation rectLocation = new RectLocation();
        rectLocation.left_top_x = this.left_top_x / width;
        rectLocation.right_bottom_x = this.right_bottom_x / width;
        rectLocation.left_top_y = this.left_top_y / height;
        rectLocation.right_bottom_y = this.right_bottom_y / height;
        return rectLocation;
    }
}
