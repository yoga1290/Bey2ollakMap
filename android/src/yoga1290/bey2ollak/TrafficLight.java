package yoga1290.bey2ollak;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

public class TrafficLight extends SurfaceView implements SurfaceHolder.Callback, OnTouchListener
{

	private SurfaceHolder holder;
	private Canvas canvas;
	private int R,G;
	
	public TrafficLight(Context context)
	{
		super(context);
		holder=getHolder();
		holder.addCallback(this);
		setOnTouchListener(this);
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent event)
	{
		if(event.getActionMasked()==MotionEvent.ACTION_DOWN)
		{
			//TODO set
		}
		return false;
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		canvas=holder.lockCanvas();
		R=canvas.getWidth()/3;
		G=R<<1;
		Paint paint=new Paint();
		
		paint.setColor(Color.RED);
		canvas.drawRect(new RectF(0, 0, R, canvas.getHeight()), paint);
		
		paint.setColor(Color.YELLOW);
		canvas.drawRect(new RectF(R, 0, G, canvas.getHeight()), paint);
		
		paint.setColor(Color.GREEN);
		canvas.drawRect(new RectF(G, 0, canvas.getWidth(), canvas.getHeight()), paint);
		
		//TODO draw pins
		holder.unlockCanvasAndPost(canvas);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		
	}

}
