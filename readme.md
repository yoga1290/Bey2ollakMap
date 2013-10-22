Android App for building statistics on how busy the roads are by generating Timepiece graphs on a Google Map.
Timepiece graphs of averages of vehicle speeds according to days & hours.


>	Please note, I'm just getting started… so, better UI & detailed code (w/out keys) later!… Feel free to contribute & issue a bug!


# Outline:

+	Android App (Client-side)
+	App Engine (Server-side)


# Android App


## [Charts.java](android/src/yoga1290/bey2ollak/Charts.java)

This class is responsible for generating the 7 Timepiece graphs inside each other…
Imagine you have 2Sx2S Canvas and want to draw a SxS Canvas in its center:
![timepiece.png](readme/timepiece.png)

…same thing goes w the drawArc: 

    canvas.drawArc(
       new RectF( S-S/2 ,S-S/2, S+S/2, S+S/2)
       ,StartAngle, 28, true, Paint);


… guess this gives the basic idea on what I'm doing [here](android/src/yoga1290/bey2ollak/Charts.java)


## [URLThread.java](android/src/yoga1290/bey2ollak/URLThread.java)


To prevent freezing the UI, URL connections are carried in separate threads:
![URLThread.png](readme/URLThread.png)

This class makes it handy to startup GET/POST connection & change the UI thread on callback in a single statement, just like this:

     final Activity currentActivity=this;
     new URLThread("URL HERE", new URLThread_CallBack() 
     { 
     	@Override 
     	public void URLCallBack(String response) 
     	{ 
    		//queue this back on in the UI 
    		currentActivity.runOnUiThread
    		( 
    			new Runnable() 
    			{ 
    				@Override 
    				public void run() 
    				{ 
    					findViewById(R.id.SOME_UI_Comp) 
    						.doSomething(); 
    				} 
    			} 
    		); 
    	}
    }, "Optional POST DATA HERE,otherwise GET is used").start();




# App Engine (Server-side)


## NoSQL datastore:


Space-wise, fixed per 100-meter radius:

     Average= ( Average * Number_of_readings + New_Record) / (Number_of_readings+1)
     Number_of_readings = Number_of_readings + 1


Speed-wise, I'm doing a Binary search for retrieving data & w a separate node telling the order of their keys, insertion should be in log(n) as well..

![Binary search](readme/nosql.png)