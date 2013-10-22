package yoga1290.bey2ollak;

import com.google.android.gms.internal.ca;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

public class help extends SurfaceView implements SurfaceHolder.Callback, OnTouchListener
{
	private SurfaceHolder holder;
	private Canvas canvas;
	public Bitmap bitmaps[];
	public help(Context context)
	{
		super(context);
		holder=getHolder();
		holder.addCallback(this);
		setOnTouchListener(this);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height)
	{
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		canvas=holder.lockCanvas();
		//BitmapFactory.decodeStream(url_value.openConnection().getInputStream());
		
		bitmaps=new Bitmap[]{
				Bitmap.createScaledBitmap
				(
						BitmapFactory.decodeResource(this.getResources(),R.drawable.p),
						canvas.getWidth(),
						canvas.getHeight(),
						true
					)};
		canvas.drawBitmap
		(
				bitmaps[0]
					, 0, 0, new Paint()
		);
		
		holder.unlockCanvasAndPost(canvas);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		// TODO Auto-generated method stub
		
	}

	
	private int initX=-1,initY=-1;
	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
//		System.out.println("TOUCH? e="+event.getActionMasked());
		if(event.getActionMasked()==MotionEvent.ACTION_DOWN)
		{
//			System.out.println("PRESS");
			initX=(int) event.getX();
			initY=(int) event.getY();
		}
		else if(event.getActionMasked()==event.ACTION_MOVE)
		{
//			System.out.println("Skewing by "+((event.getX()-initX)*canvas.getWidth()/90) );
			canvas=holder.lockCanvas();
			canvas.clipRect(0, 0, canvas.getWidth(),	 canvas.getHeight());
			canvas.skew(0,(float)Math.atan
					(
							((event.getX()-initX)*45/canvas.getWidth())
					));
			
			canvas.drawBitmap
			(
					bitmaps[0]
						, 0, 0, new Paint()
			);
			
			holder.unlockCanvasAndPost(canvas);
		}
		return true;
	}
}
