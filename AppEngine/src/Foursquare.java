import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class Foursquare 
{
	/**
	 * @see 	https://developer.foursquare.com/overview/auth
	 * @param CLIENT_ID
	 * @param CLIENT_SECRET
	 * @param code
	 * @return access_token
	 * @throws Exception
	 */
	public static String getAccessToken(String CLIENT_ID,String CLIENT_SECRET,String redirectURI,String code) throws Exception
	{
		String res="";
		/*
		https://foursquare.com/oauth2/authenticate
		    ?client_id=YOUR_CLIENT_ID
		    	    &response_type=code
		    	    &redirect_uri=YOUR_REGISTERED_REDIRECT_URI
	    	*/
			URL url = new URL("https://foursquare.com/oauth2/access_token?client_id="+CLIENT_ID
					+"&client_secret="+CLIENT_SECRET
					+"&grant_type=authorization_code"
					+"&redirect_uri="+redirectURI
					+"&code="+code);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream in=connection.getInputStream();
            byte buff[]=new byte[in.available()];
            int ch;
            while((ch=in.read(buff))!=-1)
            		res+=new String(buff,0,ch);
            in.close();
            //Extract the access token
            return new JSONObject(res).getString("access_token");
	}
	
	
	/**
	 * @see https://developer.foursquare.com/docs/venues/search
	 * @param access_token
	 * @param Latitude
	 * @param longitude
	 * @return
	 * @throws Exception
	 */
	public static JSONObject search(String access_token,double Latitude ,double longitude) throws Exception
	{
		String res="";
		URL url = new URL("https://api.foursquare.com/v2/venues/search?ll="+Latitude+","+longitude+"&oauth_token="+access_token);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        InputStream in=connection.getInputStream();
        byte buff[]=new byte[in.available()];
        int ch;
        while((ch=in.read(buff))!=-1)
        		res+=new String(buff,0,ch);
        in.close();
        return new JSONObject(res);
	}
	
	
	/**
	 * @see	https://developer.foursquare.com/docs/checkins/add
	 * @param access_token
	 * @param venueId
	 * @return
	 * @throws Exception
	 */
	public static JSONObject checkin(String access_token,String venueId) throws Exception
	{
		String res="";
		URL url = new URL("https://api.foursquare.com/v2/checkins/add?venueId="+venueId+"&oauth_token="+access_token);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        InputStream in=connection.getInputStream();
        byte buff[]=new byte[in.available()];
        int ch;
        while((ch=in.read(buff))!=-1)
        		res+=new String(buff,0,ch);
        in.close();
        return new JSONObject(res);
	}
	
	/**
	 * 
	 * @see https://developer.foursquare.com/docs/users/checkins
	 * @param access_token 
	 * @param YYYYMMDD
	 * @return JSONObject of Checkins
	 * @throws Exception
	 * e.g:
	 * lat/lng: JSONObject(response).getJSONArray("items").get(i).getJSONObject("venue").get("location").getDouble("lat"/"lng")
	 */
	public static JSONObject getCheckins(String access_token,String YYYYMMDD) throws Exception
	{
		String res="";
		URL url = new URL("https://api.foursquare.com/v2/users/self/checkins?oauth_token="+access_token+"&v="+YYYYMMDD);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        InputStream in=connection.getInputStream();
        byte buff[]=new byte[in.available()];
        int ch;
        while((ch=in.read(buff))!=-1)
        		res+=new String(buff,0,ch);
        in.close();
        return new JSONObject(res);
	}
	public static JSONObject getCheckins(String access_token,String YYYYMMDD,int offset) throws Exception
	{
		String res="";
		URL url = new URL("https://api.foursquare.com/v2/users/self/checkins?oauth_token="+access_token+"&v="+YYYYMMDD+"&offset="+offset);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        InputStream in=connection.getInputStream();
        byte buff[]=new byte[in.available()];
        int ch;
        while((ch=in.read(buff))!=-1)
        		res+=new String(buff,0,ch);
        in.close();
        return new JSONObject(res);
	}
	
	/**
	 * 
	 * @see https://developer.foursquare.com/docs/explore#req=users/self
	 * @param access_token
	 * @return
	 * @throws Exception
	 */
	public static JSONObject getUserInfo(String access_token) throws Exception
	{
		String res="";
		URL url = new URL("https://api.foursquare.com/v2/users/self?oauth_token="+access_token);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        InputStream in=connection.getInputStream();
        byte buff[]=new byte[in.available()];
        int ch;
        while((ch=in.read(buff))!=-1)
        		res+=new String(buff,0,ch);
        in.close();
        return new JSONObject(res);
	}
	
}
