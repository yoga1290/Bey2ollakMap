package yoga1290.bey2ollak;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.json.JSONObject;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.LocationSource.OnLocationChangedListener;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.maps.SupportMapFragment;

import android.location.Location;

import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.widget.TextView;

import android.support.v4.app.*;
import android.support.v4.*;
import android.support.v4.app.Fragment;

public class MapActivity extends Activity implements LocationListener,OnMarkerClickListener
{
	GoogleMap mMap;
	final Activity currentActivity=this;
	public HashMap<String, JSONObject> hm=new HashMap<String, JSONObject>();
	
	public boolean	viewerMode=false;
	public static final String days[]=new String[]	{	"Sunday",
		"Monday",
		"Tuesday",
		"Wednesday",
		"Thursday",
		"Friday",
		"Saturday"
	};
	
	double lastLat=-1,lastLng=-1;
	long lastTime=Calendar.getInstance().getTimeInMillis();
	
	public static int test=0;
	private URLThread_CallBack callback=new URLThread_CallBack()
	{
		@Override
		public void URLCallBack(final String response)
		{
			try
			{
				System.out.println("Response recieved:\n"+response+"\n\n");
				//TODO add Markers
				final String MarkersData[]=response.split("\n");
				
				//TODO
//				for(String markerData:MarkersData)
//				{
//					
//				}
				

//				for(final String markerData:MarkersData)
//				{
					currentActivity.runOnUiThread(new Runnable()
					{
						
						@Override
						public void run()
						{
							mMap.clear();
							for(String markerData:MarkersData)
							{
								try
								{
									System.out.println("Response Line:\n"+markerData+"\n\n");
									//TODO Timepiece graph
									JSONObject json=new JSONObject(markerData);
									
									hm.put(json.getString("lat")+","+json.getString("lng"), json);
									
									String tmp_speeds[];//json.getString(days[Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-1]).split(",");
									int speeds[]=new int[12];
									
									int tz=(24+(TimeZone.getDefault().getOffset(System.currentTimeMillis())/3600000) )%12;
									
									int offsetAM_PM=Calendar.getInstance().get(Calendar.AM_PM)*12; //AM=0, PM=1
									int i,j;
									
									int pieData[][]=new int[7][12];
									for(j=0;j<7;j++)
									{
										tmp_speeds=json.getString(days[j]).split(",");
										for(i=0;i<12;i++)
											pieData[j][i]=((int) Double.parseDouble(tmp_speeds[ (i+tz)%12+offsetAM_PM ]));
									}
									System.out.println("\n\n");
									mMap.addMarker(new MarkerOptions()
			        						.position(
			        								new LatLng(
			        										Double.parseDouble(json.getString("lat")),
			        										Double.parseDouble(json.getString("lng"))
			        										)
			        								)
			        							.icon(BitmapDescriptorFactory.fromBitmap(
			        									Charts.getBigPieChart(300, 300, pieData, 10,2, 
			        											test,//Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-1,
			        											Calendar.getInstance().get(Calendar.HOUR) )
			        																	)
			        									) // title("Hello world")
									);
									test++;
									test%=7;
									
//									mMap.moveCamera(CameraUpdateFactory.newLatLng(
//											new LatLng(	Double.parseDouble(json.getString("lat")) , Double.parseDouble(json.getString("lng")) )));
								}catch(Exception e){e.printStackTrace();}
							}
							
						}
					});
//				}
			}catch(Exception e){e.printStackTrace();}
		}
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
//		setContentView(new help(this));
		setContentView(R.layout.map);

		mMap = //((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map))
                //.getMap();
		((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
				
		Location lastKnownLocation;
		try{
			lastKnownLocation= locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}catch(Exception e){
			lastKnownLocation= locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		
		
		this.viewerMode=MainActivity.viewer;
		if(lastKnownLocation!=null)
			showMarkers(lastKnownLocation);
		// Define a listener that responds to location updates
//		LocationListener locationListener = new LocationListener(){
		  try{
			  if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
				  showSettingsAlert();
			  
			// Register the listener with the Location Manager to receive location updates
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 100, this);//locationListener);	
		  }catch(Exception e)
		  {
			  showSettingsAlert();
			  
			// Register the listener with the Location Manager to receive location updates
			  
			//locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 50, this);//locationListener);  
		  }	
	}

	
	public static int getDistance(double lat1, double lng1, double lat2, double lng2)
	{
	    double earthRadius =6371000; //km=6371; //mil=3958.75;
	    double dLat = Math.toRadians(lat2-lat1);
	    double dLng = Math.toRadians(lng2-lng1);
	    double sindLat = Math.sin(dLat / 2);
	    double sindLng = Math.sin(dLng / 2);
	    double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
	            * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double dist = earthRadius * c;

	    return (int)dist;
	}


	@Override
	public void onLocationChanged(Location location)
	{
		if(viewerMode)
			showMarkers(location);
		else
			setMarkers(location);
	}
	
	public void setMarkers(final Location location)
    {
		long d=(Calendar.getInstance().getTimeInMillis()-lastTime);
		if(d<60000)	return; //less than 1 min?
		
		if(lastLat==-1 || lastLng==-1)
		{
			lastLat=location.getLatitude();
			lastLng=location.getLongitude();
		}
		
		
      // Called when a new location is found by the network location provider.
    		final 	int x=(int)getDistance(1,1,location.getLatitude(),1) * (location.getLatitude()<0? -1:1);
		final int y=(int)getDistance(1,1,1,location.getLongitude()) * (location.getLongitude()<0? -1:1);
		
		
		final double speed=getDistance(lastLat, lastLng, location.getLatitude(), location.getLongitude())
							/
							(
									(Calendar.getInstance().getTimeInMillis()-lastTime)
									/60000
							);
		
		
		lastLat=location.getLatitude();
		lastLng=location.getLongitude();
		lastTime=Calendar.getInstance().getTimeInMillis();
		
		currentActivity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				((TextView) findViewById(R.id.debug)).setText("> x="+x+" , y= "+y+",s="+speed+","+location.getLatitude()+"/"+location.getLongitude());
				
				mMap.moveCamera(	
						CameraUpdateFactory.zoomTo(19)
					);
				mMap.animateCamera(
						CameraUpdateFactory.newLatLng(
								new LatLng(	location.getLatitude() , location.getLongitude())
						)
					);
			}
		});

		try
		{
			new URLThread("****?x="+x+"&y="+y+"&r=200", callback, 
								new JSONObject()
								.put("speed", speed)
								.put("lat", location.getLatitude()+"")
								.put("lng", location.getLongitude()+"").toString()).start();
		}catch(Exception e){e.printStackTrace();}
    }
	
	public void showMarkers(final Location location)
    {
      // Called when a new location is found by the network location provider.
		if(lastLat==-1 || lastLng==-1)
		{
			lastLat=location.getLatitude();
			lastLng=location.getLongitude();
		}
		final 	int x=(int)getDistance(1,1,location.getLatitude(),1) * (location.getLatitude()<0? -1:1);
		final int y=(int)getDistance(1,1,1,location.getLongitude()) * (location.getLongitude()<0? -1:1);
		
		
		lastLat=location.getLatitude();
		lastLng=location.getLongitude();
		lastTime=Calendar.getInstance().getTimeInMillis();
		
		currentActivity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				((TextView) findViewById(R.id.debug)).setText("> x="+x+" , y= "+y+", "+location.getLatitude()+"/"+location.getLongitude());
				
				mMap.moveCamera(	
						CameraUpdateFactory.zoomTo(19)
					);
				mMap.animateCamera(
						CameraUpdateFactory.newLatLng(
								new LatLng(	location.getLatitude() , location.getLongitude())
						)
					);
			}
		});

			new URLThread("……?x="+x+"&y="+y+"&r=100", callback,"").start();
    }

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//		
//	}
	public void showSettingsAlert()
	{
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
      
        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");
  
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
  
        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);
  
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
  
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });
  
        // Showing Alert Message
        alertDialog.show();
    }


@Override
public boolean onMarkerClick(Marker marker)
{
//	JSONObject json=hm.get(marker.getPosition().latitude+","+marker.getPosition().longitude);
	
	// TODO Auto-generated method stub
	return false;
}
}
