package overlay;

/**
 * 
 */

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import android.graphics.Bitmap;

import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.core.LatLonPoint;

class AMapServicesUtil {
	public static int BUFFER_SIZE = 2048;

	public static byte[] inputStreamToByte(InputStream in) throws IOException {

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] data = new byte[BUFFER_SIZE];
		int count = -1;
		while ((count = in.read(data, 0, BUFFER_SIZE)) != -1)
			outStream.write(data, 0, count);

		data = null;
		return outStream.toByteArray();
	}
	public static LatLonPoint convertToLatLonPoint(LatLng latlon) {
		return new LatLonPoint(latlon.latitude, latlon.longitude);
	}
	public static LatLng convertToLatLng(LatLonPoint latLonPoint) {
		return new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
	}
	public static ArrayList<LatLng> convertArrList(List<LatLonPoint> shapes) {
		ArrayList<LatLng> lineShapes = new ArrayList<LatLng>();
		for (LatLonPoint point : shapes) {
			LatLng latLngTemp = AMapServicesUtil.convertToLatLng(point);
			lineShapes.add(latLngTemp);
		}
		return lineShapes;
	}
	public static Bitmap zoomBitmap(Bitmap bitmap, float res) {
		if (bitmap == null) {
			return null;
		}
		int width, height;
		width = (int) (bitmap.getWidth() * res);
		height = (int) (bitmap.getHeight() * res);
		Bitmap newbmp = Bitmap.createScaledBitmap(bitmap, width, height, true);
		return newbmp;
	}

}
