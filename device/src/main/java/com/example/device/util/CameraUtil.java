package com.example.device.util;

import java.util.List;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

public class CameraUtil {
    private final static String TAG = "CameraUtil";

    public static Point getSize(Context ctx) {
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        Point size = new Point();
        size.x = dm.widthPixels;
        size.y = dm.heightPixels;
        return size;
    }

    private static final Pattern COMMA_PATTERN = Pattern.compile(",");

    public static Point getCameraSize(Camera.Parameters params, Point screenSize) {
        String previewSizeValueString = params.get("preview-size-values");
        if (previewSizeValueString == null) {
            previewSizeValueString = params.get("preview-size-value");
        }
        Point cameraSize = null;
        if (previewSizeValueString != null) {
            Log.d(TAG, "preview-size-values parameter: " + previewSizeValueString);
            cameraSize = findBestPreviewSizeValue(previewSizeValueString, screenSize);
        }
        if (cameraSize == null) {
            cameraSize = new Point((screenSize.x >> 3) << 3, (screenSize.y >> 3) << 3);
        }
        return cameraSize;
    }

    private static Point findBestPreviewSizeValue(CharSequence previewSizeValueString, Point screenSize) {
        int bestX = 0;
        int bestY = 0;
        int diff = Integer.MAX_VALUE;
        for (String previewSize : COMMA_PATTERN.split(previewSizeValueString)) {
            previewSize = previewSize.trim();
            int dimPosition = previewSize.indexOf('x');
            if (dimPosition < 0) {
                Log.d(TAG, "Bad preview-size: " + previewSize);
                continue;
            }

            int newX;
            int newY;
            try {
                newX = Integer.parseInt(previewSize.substring(0, dimPosition));
                newY = Integer.parseInt(previewSize.substring(dimPosition + 1));
            } catch (NumberFormatException nfe) {
                Log.d(TAG, "Bad preview-size: " + previewSize);
                continue;
            }

            int newDiff = Math.abs((newX - screenSize.x) + (newY - screenSize.y));
            if (newDiff == 0) {
                bestX = newX;
                bestY = newY;
                break;
            } else if (newDiff < diff) {
                bestX = newX;
                bestY = newY;
                diff = newDiff;
            }
        }

        if (bestX > 0 && bestY > 0) {
            return new Point(bestX, bestY);
        }
        return null;
    }

    public static Camera.Size getMaxPictureSize(Camera.Parameters params) {
        List<Camera.Size> supportedSizes = params.getSupportedPictureSizes();
        Camera.Size maxSize = supportedSizes.get(0);
        for (Camera.Size size : supportedSizes) {
            if (size.width > maxSize.width) {
                maxSize = size;
            }
        }
        return maxSize;
    }

}

