package backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;


public class GeoCalculator {

	public static void main(String[] args) {
		/*	GeoLocation alsace = new GeoLocation(50.0, 0);
		GeoLocation bordeux = new GeoLocation(45.0, 0);*/

		GeoLocation alsace = getLangLat("Alsace");
		GeoLocation bordeux = getLangLat("Bordeux");

		System.out.println("Udaljenost : " + distance(alsace,bordeux));
		//System.out.println("Geografska sirina je: "+latitude+ "\nGeografska duzina "+longitude);
	}

	public static GeoLocation getLangLat(String regija)
	{
		GeoLocation povratna = new GeoLocation();
		StringBuilder result = new StringBuilder();	
		URL oracle;
		try {
			oracle = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=" + regija);
			URLConnection yc = oracle.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) 
				result.append(inputLine);
			in.close();
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		try {
			JSONObject jsonReply = new JSONObject(result.toString());
			JSONArray jsonArr =  new JSONArray(jsonReply.get("results").toString());
			if (jsonArr.length() > 0)
			{
				jsonReply = new JSONObject(jsonArr.getJSONObject(0).get("geometry").toString());
				jsonReply = new JSONObject(jsonReply.get("location").toString());
				double longitude = jsonReply.getDouble("lng");
				double latitude = jsonReply.getDouble("lat");
				povratna.setLatitude(latitude);
				povratna.setLongitude(longitude);
				System.out.println(jsonReply);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return povratna;         

	}

	public static double distance(GeoLocation prva, GeoLocation druga) {

		double lat1 = prva.getLatitude();
		double lon1 = prva.getLongitude();

		double lat2 = druga.getLatitude();
		double lon2 = druga.getLongitude();
		double theta = lon1 - lon2;

		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

		dist = Math.acos(dist);

		dist = rad2deg(dist);

		dist = dist * 60 * 1.1515;

		dist = dist * 1.609344;

		return (dist);

	}

	private static double deg2rad(double deg) {

		return (deg * Math.PI / 180.0);

	}

	private static double rad2deg(double rad) {

		return (rad * 180 / Math.PI);

	}


}
