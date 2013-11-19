package yoga1290.bey2ollak;

import java.util.Calendar;
import java.util.TimeZone;

import org.json.JSONObject;

import com.google.android.gms.internal.ar;
import com.google.android.gms.internal.j;

import android.graphics.Color;
import android.graphics.Rect;
import android.location.Address;
import android.os.Bundle;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.PendingIntent.OnFinished;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TimepieceActivity extends FragmentActivity
{
    // When requested, this adapter returns a DemoObjectFragment,
    // representing an object in the collection.
	Timepiece_CollectionPagerAdapter mDemoCollectionPagerAdapter;
    ViewPager mViewPager;

    public static JSONObject markerJSON;
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timepiece);
        
        
        try
        {
        		markerJSON=new JSONObject(getIntent().getExtras().getString("JSON"));
        }catch(Exception e){System.err.println(e);}
        // ViewPager and its adapters use support library
        // fragments, so use getSupportFragmentManager.
        mDemoCollectionPagerAdapter =
                new Timepiece_CollectionPagerAdapter(
                        getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.activity_timepiece_pager);
        mViewPager.setAdapter(mDemoCollectionPagerAdapter);
    }
    
}

// Since this is an object collection, use a FragmentStatePagerAdapter,
// and NOT a FragmentPagerAdapter.
class Timepiece_CollectionPagerAdapter extends FragmentStatePagerAdapter 
{
	public static final String days[]=new String[]	{	"Sunday",
		"Monday",
		"Tuesday",
		"Wednesday",
		"Thursday",
		"Friday",
		"Saturday"
	};
	
	final int today=Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-1;
    public Timepiece_CollectionPagerAdapter(FragmentManager fm)
    {
        super(fm);
        
    }

    @Override
    public Fragment getItem(int i)
    {
        Fragment fragment = new Timepiece_ObjectFragment();
        Bundle args = new Bundle();
        // Our object is just an integer :-P
        args.putInt(Timepiece_ObjectFragment.ARG_OBJECT, (i+today)%7);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount()
    {
        return 7;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
//    		System.out.println("Swipping view: "+days[(position+today)%7]);
        return days[(position+today)%7];
    }
}

// Instances of this class are fragments representing a single
// object in our collection.
  class Timepiece_ObjectFragment extends Fragment
  {
	  
	  public static final String days[]=new String[]	{	"Sunday",
			"Monday",
			"Tuesday",
			"Wednesday",
			"Thursday",
			"Friday",
			"Saturday"
		};
    public static final String ARG_OBJECT = "object";

    @Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState)
    {
    		//TODO
    		
    		LayoutInflater li = (LayoutInflater) this.getActivity().getSystemService(this.getActivity().LAYOUT_INFLATER_SERVICE);
		View v=li.inflate(R.layout.view_day_timepiece, null);
		
//		ViewTreeObserver viewTreeObserver = v.getViewTreeObserver();
//		if (viewTreeObserver.isAlive()) {
//		  viewTreeObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
//		    @Override
//		    public void onGlobalLayout() {
////		      v.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//		      viewWidth = mediaGallery.getWidth();
//		      viewHeight = mediaGallery.getHeight();
//		    }
//		  });
//		}
		
		
		String txt="";
		
        // The last two arguments ensure LayoutParams are inflated
        // properly.
//        View rootView = inflater.inflate(
//                R.layout.fragment_collection_object, container, false);
        Bundle args = getArguments();
        try
        {
//        		JSONObject json=new JSONObject(getArguments().getString("JSON"));
//        		System.out.println("Marker Data= "+json.toString());
	        String avg[]=TimepieceActivity.markerJSON.getString(days[args.getInt(ARG_OBJECT)]).split(",");
	        int i,tz=(24+(TimeZone.getDefault().getOffset(System.currentTimeMillis())/3600000) )%24;
//	        for(i=0;i<9;i++)
//	        		txt+=" "+(i+1)+" AM : "+avg[(i+tz)%24]+" m/min\n";
//	        for(i=9;i<12;i++)
//	        		txt+=(i+1)+" AM : "+avg[(i+tz)%24]+" m/min\n";
//	        
//	        txt+="\n";
//	        for(i=0;i<9;i++)
//        			txt+=" "+(i+1)+" PM : "+avg[(i+12+tz)%24]+" m/min\n";
//	        for(i=9;i<12;i++)
//        			txt+=(i+1)+" PM : "+avg[(i+12+tz)%24]+" m/min\n";
//	        ((TextView)(v.findViewById(R.id.textview_day_timepiece))).setText(txt);
	        
	        
	        int tmp[]=new int[avg.length];
	        for(i=0;i<avg.length;i++)
	        		tmp[i]=(int) Double.parseDouble(avg[(i+tz)%24]);
	        ImageView graph=(ImageView) v.findViewById(R.id.imageview_day_timepiece);
	        graph.setImageBitmap(Charts.whiteGraph(500, 300, tmp));
	        RelativeLayout rv=((RelativeLayout)v.findViewById(R.id.RelativeLayout_day_timepiece));
	        
	        int IVs[]=new int[]{R.id.imageview_day_timepiece_hr1,R.id.imageview_day_timepiece_hr2,R.id.imageview_day_timepiece_hr3,R.id.imageview_day_timepiece_hr4,R.id.imageview_day_timepiece_hr5,R.id.imageview_day_timepiece_hr6,R.id.imageview_day_timepiece_hr7,R.id.imageview_day_timepiece_hr8,R.id.imageview_day_timepiece_hr9,R.id.imageview_day_timepiece_hr10,R.id.imageview_day_timepiece_hr11,R.id.imageview_day_timepiece_hr12,R.id.imageview_day_timepiece_hr13,R.id.imageview_day_timepiece_hr14,R.id.imageview_day_timepiece_hr15,R.id.imageview_day_timepiece_hr16,R.id.imageview_day_timepiece_hr17,R.id.imageview_day_timepiece_hr18,R.id.imageview_day_timepiece_hr19,R.id.imageview_day_timepiece_hr20,R.id.imageview_day_timepiece_hr21,R.id.imageview_day_timepiece_hr22,R.id.imageview_day_timepiece_hr23,R.id.imageview_day_timepiece_hr24};
	        for(i=0;i<12;i++)
	        		((ImageView) v.findViewById(IVs[i])).setImageBitmap(Charts.TimepieceHour(100, 100, i, tmp[i]+"",Color.rgb(255, 127, 0)) );
	        for(i=12;i<24;i++)
        			((ImageView) v.findViewById(IVs[i])).setImageBitmap(Charts.TimepieceHour(100, 100, i, tmp[i]+"",Color.GRAY ));
	        		
	        
        }catch(Exception e){System.err.println(e);}
        return v;
    }

}