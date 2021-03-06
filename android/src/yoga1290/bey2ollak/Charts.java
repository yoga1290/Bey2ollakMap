package yoga1290.bey2ollak;

import java.util.Calendar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;

public class Charts 
{
	public static int gcd(int a,int b)
	{
		if(b==0)
			return a;
		return gcd(b,a%b);
	}
	public static Bitmap getBigPieChart(int width,int height,int data[][],int Green,int Red,int today,int hour)
	{
		
		Bitmap bitmap=Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		int S=Math.min(width, height)>>1;
		Canvas canvas=new Canvas(bitmap);
		Paint paint=new Paint();
		int i,j,angle=0;
		
		paint.setColor(Color.TRANSPARENT);
		canvas.drawRect(0, 0, width,height, paint);
//		int rainbow[]=new int[]{Color.RED,Color.rgb(255, 127, 0),Color.YELLOW,Color.GREEN,Color.BLUE,Color.rgb(75, 0, 130),Color.rgb(143, 0, 255)};

		int f=4,st,ed;
		
		boolean extended=true;
		for(i=7;i>0;i--)
		{
			if(today==i)//day+1
				f=2;
			
			st=S- (S*i*f/28);
			ed=S+ (S*i*f/28);
//			st=S- (S*i*f/42);
//			ed=S+ (S*i*f/42);
//			System.out.println(i+" : "+(S*i*f/28));
			
			angle=270;
			for(j=0;j<12;j++)
			{
//				System.out.println("data["+i+"]["+j+"] = "+data[i-1][j]);
				
				
				paint.setColor(Color.WHITE);
//				if(today==i || today==i-1)
//					if(hour==j%12)
//						paint.setColor(Color.BLACK);
				
//				canvas.drawArc(
//						new RectF(S- (S*i*2/14),
//								S- (S*i*2/14),
//								S+ (S*i*2/14),
//								S+ (S*i*2/14) ) , angle, 28, true, paint);
				canvas.drawArc(
						new RectF(st,
								st,
								ed,
								ed ) , angle, 28, true, paint);
							
				//7+6=13
//				paint.setColor(rainbow[i-1]);
				paint.setColor(Color.GREEN);
				if(data[i-1][j]<Green)
					paint.setColor(Color.YELLOW);
				if(data[i-1][j]<Red)
					paint.setColor(Color.RED);
				
				
//				canvas.drawArc(
//						new RectF(S- (S*i*f/28-2),
//								S- (S*i*f/28-2),
//								S+ (S*i*f/28-2),
//								S+ (S*i*f/28-2) ) , angle, 28, true, paint);
				canvas.drawArc(
						new RectF(st+2,
								st+2,
								ed-2,
								ed-2 ) , angle, 28, true, paint);
				
				angle+=30;
			}
		}
		return bitmap;
	}
	public static Bitmap getTimepiece(int width,int height,int ar[])//[0:8~9..11:7~8]=value
	{
		//TODO try ARGB
		Bitmap bitmap=Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		
		Canvas canvas=new Canvas(bitmap);
		
		int i,l=Math.min(ar.length, 12),angle=270,max=1,gcd=ar[0],lpad,tpad;
		for(i=0;i<ar.length;i++)
		{
			System.out.println(ar[i]);
			gcd=gcd(ar[i],gcd);
			max=Math.max(max,ar[i]);
		}
		if(gcd>1)
		{
			for(i=0;i<ar.length;i++)
				ar[i]=ar[i]/gcd;
		}
		System.out.println("GCD="+gcd);
		
		Paint paint=new Paint();
		paint.setColor(Color.WHITE);
		canvas.drawRect(0, 0, width,height, paint);
		int colors[]=new int[]{Color.BLUE,Color.CYAN,Color.GRAY,Color.GREEN};
		int r,R=Math.min(width, height)>>1;
		for(i=0;i<l;i++)
		{
			r=(ar[i]*R/max);
			
			paint.setColor(colors[i%colors.length]);
			canvas.drawArc(
							new RectF(R-r,
									R-r,
									R+r,
									R+r), angle, 30, true, paint);
			angle+=30;
		}
		return bitmap;
	}
	
	
	public static Bitmap getClassTimetable(int width,int height,int ar[])
	{
		Bitmap bitmap=Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		Canvas canvas=new Canvas(bitmap);
		int i,j,l=Math.min(ar.length, 6),angle=0,max=0,gcd=ar[0],lpad,tpad;
		for(i=0;i<ar.length;i++)
			max=Math.max(max,ar[i]);
		Paint paint=new Paint();
		paint.setColor(Color.GRAY);
		canvas.drawRect(0, 0, width,height, paint);
		int colors[]=new int[]{	Color.YELLOW, //Sunday
								Color.GRAY,  //Monday
								Color.RED,   //Tuesday
								Color.MAGENTA, //Wednesday
								Color.GREEN,   //Thursday
								Color.BLUE,    //Friday
								Color.WHITE};  //Saturday
		int dayR[]=new int[]{1,2,3,4,5,6,7};//{7,6,5,4,3,2,1};
		String days[]=new String[]{"S","M","T","W","T","F","Saturday"};		
		//Sunday w the biggest radius; Saturday w shortest 1
		for(j=0;j<7;j++)//j-th day of week
		{
			angle=30;
			for(i=0;i<l;i++)// i:0..6; periods
			{
				tpad=lpad=0;
//				//ar: array of flags[0:8~10..6:6~8], where each in form of 0bSMTWtFs
					if((ar[	(l-1-i	+4)%l ]&(1<<j))>0) //0 deg starts at i+4 anti-clockwise
					{
						lpad=Math.min(width, height)*dayR[j]/8;
						lpad>>=1;
					}

				if(lpad>0)
					tpad=Math.min(width, height)-lpad;
				paint.setColor(colors[j]);
				canvas.drawArc(new RectF(lpad,lpad, tpad, tpad), angle, 58, true, paint);
				angle+=60;
			}
		}
		for(i=0;i<7;i++)
		{
			paint.setColor(colors[i]);
			paint.setTextSize(20);
			canvas.drawText(days[i], i*20, height>>1, paint);
		}
		return bitmap;
	}
	
	public static Bitmap whiteGraph(int width,int height,int ar[])
	{
		Bitmap bitmap=Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		height-=2;
		width-=2;
		Canvas canvas=new Canvas(bitmap);
		int i,max=1,center=height>>1;
		
		for(i=0;i<ar.length;i++)
			max=Math.max(max,ar[i]);
		
		int x,y,ox=0,oy=height;
		Paint paint=new Paint();
		paint.setColor(Color.TRANSPARENT);
		canvas.drawRect(0, 0, width,height, paint);
		paint.setColor(Color.BLACK);
		paint.setTextSize(14);
		
		for(i=0;i<12;i++)
		{
			x=i*width/ar.length;
			y=height-(ar[i]*height/max);
			
			paint.setColor(Color.rgb(255, 127, 0));
			canvas.drawLine(ox, oy, x, y, paint);
			
//			paint.setColor(Color.GRAY);
//			canvas.drawLine(x, 0, x, height, paint);
			if(ar[i]>0)
			{
				paint.setColor(Color.GRAY);
				canvas.drawText(ar[i]+"", x, y, paint);
			}
			
			ox=x;
			oy=y;
		}
		for(i=12;i<ar.length;i++)
		{
			x=i*width/ar.length;
			y=height-(ar[i]*height/max);
			
			paint.setColor(Color.BLACK);
			canvas.drawLine(ox, oy, x, y, paint);
			
//			paint.setColor(Color.GRAY);
//			canvas.drawLine(x, 0, x, height, paint);
			if(ar[i]>0)
			{
				paint.setColor(Color.BLACK);
				canvas.drawText(ar[i]+"", x, y, paint);
			}
			
			ox=x;
			oy=y;
		}
		return bitmap;
	}
	
	//TODO
	public static Bitmap TimepieceHour(int width,int height,int hour, String txt,int color)
	{
		Bitmap bitmap=Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas=new Canvas(bitmap);
		
		int S=Math.min(width, height)>>1,s=S-(S>>1);
		Paint paint=new Paint();
		
		paint.setColor(Color.TRANSPARENT);
		canvas.drawRect(0, 0, width,height, paint);
		
		paint.setColor(color);//Color.rgb(255, 127, 0));
		
		canvas.drawArc(
				new RectF(0,
						0,
						S<<1,
						S<<1 ) , 0, 360, true, paint);
		paint.setColor(Color.WHITE);
		canvas.drawArc(
				new RectF(2,
						2,
						(S<<1)-2,
						(S<<1)-2 ) , 0, 360, true, paint);
		
		 
		paint.setColor(color);//Color.rgb(255, 127, 0));
		canvas.drawArc(
				new RectF(0,
						0,
						S<<1,
						S<<1 ) , hour*360/12+270, 28, true, paint);
		paint.setColor(Color.WHITE);
		canvas.drawArc(
				new RectF(S-(S>>1),
						S-(S>>1),
						S+(S>>1),
						S+(S>>1)) , hour*360/12+270, 28, true, paint);
		paint.setColor(Color.BLACK);
		paint.setTextSize(30);
		canvas.drawText(txt, 0, S, paint);
		return bitmap;
	}
	
	public static Bitmap getGPAGraph(int width,int height,int ar[])//ar:GPA*100 across terms
	{
		Bitmap bitmap=Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		Canvas canvas=new Canvas(bitmap);
		int gcd=ar[0],i,max=0,last=ar[ar.length-1];
		//get ratio by dividing by Greatest-Common Divisor
		for(i=0;i<ar.length;i++)
			gcd=gcd(ar[i],gcd);
		for(i=0;i<ar.length;i++)
			ar[i]=ar[i]/gcd;
		
		for(i=0;i<ar.length;i++)
			max=Math.max(max,ar[i]);
		int x,y,ox=0,oy=height;
		Paint paint=new Paint();
		paint.setColor(Color.WHITE);
		canvas.drawRect(0, 0, width,height, paint);
		paint.setColor(Color.BLACK);
		for(i=0;i<ar.length;i++)
		{
			x=i*width/ar.length;
			y=height-(ar[i]*height/max);
			
			paint.setColor(Color.BLACK);
			canvas.drawLine(ox, oy, x, y, paint);
			
			paint.setColor(Color.GRAY);
			canvas.drawLine(x, 0, x, height, paint);
			ox=x;
			oy=y;
		}
		paint.setColor(Color.GRAY);
		paint.setTextSize(32);
		canvas.drawText((last/100.0)+"", width>>1, height>>1, paint);
		return bitmap;
	}
}
