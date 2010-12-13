package android.wordi;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

public class Board extends ViewGroup {
	public Point location = new Point();
	private View parent;
	private int pieceHeight, pieceWidth;
	private Context context;
	private final int padding = 20;
	private ScaleGestureDetector mScaleGestureDetector;
	private GestureDetector mGestureDetector;
	private float mScaleFactor = 1.f;
	private static final int INVALID_POINTER_ID = -1;
	private int originalWidth, originalHeight;
	private Scroller mScroller;

	// The ‘active pointer’ is the one currently moving our object.
	private int mActivePointerId = INVALID_POINTER_ID;
	
	/* Static Vars */
	private static int NUM_COLS = 15;
	private static int NUM_ROWS = 15;
    
	
	private Point start = new Point();
	
    public Board(Context context) {
        super(context);
        this.context = (main) context;
        this.mScroller = new Scroller(context);
        this.originalHeight = this.originalWidth = 1000;
        this.mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
        this.mGestureDetector = new GestureDetector(context, new MyGestureListener() );
        for( int i = 0; i < NUM_ROWS; i++ ){
        	for( int j = 0; j < NUM_COLS; j++ ){
        		BoardPlaceHolder temp = new BoardPlaceHolder( context );
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                temp.setLayoutParams(lp);
                temp.setPositionOnBoard( i + j );
                this.addView( temp );
                ((main) this.context).addBoardDropTarget( temp );
        	}
        }
    }

    @Override
    public void onAttachedToWindow(){
    	parent = (View) this.getParent();
//    	WindowManager.LayoutParams lp = (android.view.WindowManager.LayoutParams) this.getLayoutParams();
//    	this.originalHeight = lp.height;
//    	this.originalWidth = lp.width;
    }
    @Override
    public void onDraw(Canvas canvas) {
    }
    
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev){		
//    	return false;
//    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	this.mScaleGestureDetector.onTouchEvent(event);
    	this.mGestureDetector.onTouchEvent(event);
    	int scrollX = 0;
		int scrollY = 0;
		Point delta = new Point();
    	switch( event.getAction() & MotionEvent.ACTION_MASK ){
    		case MotionEvent.ACTION_DOWN:
				/*
				* If being flinged and user touches, stop the fling. isFinished
				* will be false if being flinged.
				*/
				if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
				}
    			start.x = (int) event.getX();
    			start.y = (int) event.getY();
    			mActivePointerId = event.getPointerId(0);
                break;
    		case MotionEvent.ACTION_MOVE:
//    			final int pointerIndex = event.findPointerIndex(mActivePointerId);
    			Point changed = new Point();
    			changed.x = (int) event.getX();
    			changed.y = (int) event.getY();
    			
				delta.x = start.x - changed.x;
				delta.y = start.y - changed.y;

				parent.scrollBy(delta.x, delta.y);
				break;
    		case MotionEvent.ACTION_UP:
//    			mActivePointerId = INVALID_POINTER_ID;
    			Log.i("BoardGroup Scroll", parent.getScrollX() + " : " + parent.getScrollY() );
				scrollX = parent.getScrollX();
				scrollY = parent.getScrollY();
				if( parent.getScrollX() < 0 - padding )
					scrollX = 0 - padding;
				if( parent.getScrollY() < 0 - padding )
					scrollY = 0 - padding;
				if( parent.getScrollX() + parent.getWidth() > this.getWidth() + padding )
					scrollX = this.getWidth() - parent.getWidth() + padding;
				if( parent.getScrollY() + parent.getHeight() > this.getHeight() + padding )
					scrollY = this.getHeight() - parent.getHeight() + padding;
				parent.scrollTo( scrollX, scrollY );
    			break;
    		case MotionEvent.ACTION_CANCEL:
    	        mActivePointerId = INVALID_POINTER_ID;
    	        break;
    	}
        return true;
    }

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
//		if( changed ){
	        int height = this.getHeight();
	        this.pieceHeight = this.getHeight() / NUM_ROWS;
	        this.pieceWidth = this.getWidth() / NUM_COLS;
			int numChildren = this.getChildCount();
			int left = 0;
			int top = 0;
			int colCount = 0;
			for( int i = 0; i < numChildren; i++ ){
				View child = this.getChildAt(i);
				child.measure(r, b);
				WindowManager.LayoutParams params = (android.view.WindowManager.LayoutParams) child.getLayoutParams();
				params.width = this.pieceWidth;
				params.height = this.pieceHeight;
				child.layout(left, top, left + this.pieceWidth, top + this.pieceHeight );
				left += this.pieceWidth;
				colCount++;
				if( colCount >= NUM_COLS ){
					left = 0;
					top += this.pieceHeight;
					colCount = 0;
				}
			}
//		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		WindowManager.LayoutParams params = (android.view.WindowManager.LayoutParams) this.getLayoutParams();
		params.width = (int) (originalWidth * this.mScaleFactor);
		params.height = (int) (originalHeight * this.mScaleFactor);
		this.setLayoutParams(params);
		this.setMeasuredDimension( params.width, params.height );
	}
	
	public float getScaleFactor(){
		return this.mScaleFactor;
	}
	
	public void setScaleFactor( float f ){
		this.mScaleFactor = f;
	}
	public void addPiece()
	{
		Tile tempG = new Tile( getContext() );
        int rand =  (int) (Math.random() * 1000);
        rand = rand % 26;
        char x = "abcdefghijklmnopqrstuvwxyz".charAt((int) rand);
        tempG.setLetter( x );
        this.addView( tempG );
	}
	
	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
	    @Override
	    public boolean onScale(ScaleGestureDetector detector) {
	    	Log.i("Scale", "Detector " + detector.getScaleFactor() );
	        mScaleFactor *= detector.getScaleFactor();
	        Log.i("Scale", "My Factor " + mScaleFactor );
	        // Don't let the object get too small or too large.
	        mScaleFactor = Math.max(0.5f, Math.min(mScaleFactor, 2.5f));
	        requestLayout();
	        return true;
	    }
	}
	
	private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
			Log.i("Fling", "Velocity " + velocityX + " : " + velocityY );
			mScroller.fling( (int) e2.getX(), (int) e2.getY(), (int) -velocityX, (int) -velocityY, 0, getWidth(), 0, getHeight() );
			int deltaX = mScroller.getFinalX() - mScroller.getStartX();
			int deltaY = mScroller.getFinalY() - mScroller.getStartY();
//			parent.scrollBy( deltaX, deltaY );
			return true;
		}
	}
}
