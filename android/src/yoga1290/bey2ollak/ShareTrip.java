package yoga1290.bey2ollak;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.internal.ar;

import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class ShareTrip extends Activity
{
	private ShareTrip currentActivity=this;
	private Bitmap bm;
	private String tripURL="",message="";
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);
		
		Bundle args = getIntent().getExtras();
		tripURL=args.getString("tripURL");
		message=args.getString("message");
		
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				bm=loadBitmap(tripURL);
				currentActivity.runOnUiThread(new Runnable()
				{					
					@Override
					public void run()
					{
						((ImageView) findViewById(R.id.imageView_trip)).setImageBitmap(bm);
						((EditText) findViewById(R.id.editText_trip)).setText(message);
					}
				});
			}
		}).start();
		
		// Post Trip to Facebook
		((Button) findViewById(R.id.button_trip)).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				try
				{
					AccountManager mAccountManager = AccountManager.get(currentActivity);
				    Account[] accounts = mAccountManager.getAccountsByType(
				            GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
				    
				    String key = Secure.getString(currentActivity.getContentResolver(),
			                Secure.ANDROID_ID);
				    for (int i = 0; i < accounts.length; i++)
				    		if(accounts[i].type.equals("com.google"))
				    			key+="/"+accounts[i].name;
//				    System.out.println("User state:"+key);
				    
				    // GET Saved Access Token
					new URLThread(“http://…/ANDROID_ID+GOOGLE_ACCOUNT=“+key, new URLThread_CallBack()
					{
						@Override
						public void URLCallBack(String access_token)
						{
							try
							{
								URL url = new URL("https://graph.facebook.com/me/photos?access_token="+access_token);
						        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				                        
						        connection.setDoOutput(true);
						        connection.setRequestMethod("POST");
						        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
						        OutputStream ops=connection.getOutputStream();
						        ops.write("message=".getBytes());
						        ops.write(((EditText) findViewById(R.id.editText_trip)).getText().toString().getBytes("UTF-8"));
				                        ops.write("&url=".getBytes());
	//			                        System.out.println(Uri.);
						        ops.write(tripURL.replaceAll("=", "%3D").replaceAll("&", "%26").getBytes());
						        ops.close();
						        InputStream in=connection.getInputStream();
						        byte buff[]=new byte[in.available()];
				                            int ch;
				                            String res="";
				                            while((ch=in.read(buff))!=-1)
				                                        res+=new String(buff,0,ch);
				                 //TODO show alert : done sharing or failed
				                    System.out.println(res);
				                    
				                    currentActivity.finish();
							}catch(Exception e)
							{
								e.printStackTrace();
								Intent i = new Intent(Intent.ACTION_VIEW);
								AccountManager mAccountManager = AccountManager.get(currentActivity);
							    Account[] accounts = mAccountManager.getAccountsByType(
							            GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
							    
							    String key = Secure.getString(currentActivity.getContentResolver(),
						                Secure.ANDROID_ID);
							    for (int j = 0; j < accounts.length; j++)
							    		if(accounts[j].type.equals("com.google"))
							    			key+="/"+accounts[j].name;
								i.setData(Uri.parse("https://www.facebook.com/dialog/oauth?client_id=326905200782417&redirect_uri=http://yogash1290.appspot.com/bey2ollak/oauth/facebook/&scope=publish_stream,email&state="+key));
								currentActivity.startActivity(i);
							}
			        
						}
					}, "").start();
				}catch(Exception e){e.printStackTrace();}
			}
		});
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.share_trip, menu);
		return true;
	}

	
	
	public static Bitmap loadBitmap(String url)
	{
        Bitmap bitmap = null;
        InputStream in = null;
        BufferedOutputStream out = null;
        int i;
        try {
        	
            in = new BufferedInputStream(new URL(url).openStream(),200);
            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            out = new BufferedOutputStream(dataStream, 200);
//            copy(in, out);
            byte buff[]=new byte[100];
            while((i=in.read(buff))>0)
                            out.write(buff, 0, i);
            out.flush();
            out.close();
            in.close();

            final byte[] data = dataStream.toByteArray();
            BitmapFactory.Options options = new BitmapFactory.Options();
            //options.inSampleSize = 1;

            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,options);
            
        } catch (Exception e) {
        		e.printStackTrace();
//            Log.e(TAG, "Could not load Bitmap from: " + url);
        } finally {
//            closeStream(in);
//            closeStream(out);
        }

        return bitmap;
    }
}
