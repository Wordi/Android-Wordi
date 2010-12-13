package android.wordi;

import java.util.ArrayList;
import android.R.string;
import android.R.style;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

class Tile extends ImageView {
    	
    	private Point start = new Point();
    	private Point delta = new Point();
    	private String letter = "W";
    	private WindowManager.LayoutParams params;
    	private int [] loc = new int[2];
    	private GestureDetector myGestureDetector;
    	private ViewGroup originalParent;
    	private Point originalPositionInParent = new Point();
    	private Point startScreenPosition = new Point();
    	private Point finishScreenPosition = new Point();
		private int animateType;
		private Boolean isAnimating = false;
		private Context context;
		private DropView mLastDropTarget;
	    /* Temps to avoid GC */
		private Rect mRectTemp = new Rect();
	    private final int[] mCoordinatesTemp = new int[2];
	    
    	public Paint paintText = new Paint();		
		public Paint paintBackground = new Paint();
		public Rect r;
    	public Point location = new Point();
    	
    	public final int ANIM_RETURN_TO_ORIGINAL = 0;
		
		
		
		public Tile(Context context) {
			super(context);
			this.context = (main) context;
			myGestureDetector = new GestureDetector( context, new MyGestureListener() );
			animateType = -1; /* Invalid to start */
			/* Create Params */
			params = new WindowManager.LayoutParams();
	        params.height = 50;
	        params.width = 50;
	        params.x = 150;
	        params.y = 0;
	        this.setLayoutParams(params);
			
			/* Paints */
			paintText.setColor( Color.WHITE );
			paintText.setTextAlign( Align.CENTER );
			paintText.setTextSize(20);
			Typeface type = Typeface.createFromAsset(context.getAssets(), "fonts/GothamRnd-Medium.ttf");
			paintText.setTypeface( type );
			
			paintBackground.setColor( Color.BLUE );
			
			this.setBackgroundDrawable( this.getResources().getDrawable( R.drawable.tile ) );
			
		}
		
		@Override
		public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			this.setMeasuredDimension( params.width, params.height );
		}
    	
		@Override
        public void onDraw(Canvas canvas) {
//			Rect r = new Rect( 0, 0, params.width, params.height );
//			canvas.drawRect(r, paintBackground);
			canvas.drawText(this.getLetter(), this.getWidth() / 2 , this.getHeight() / 2 , paintText );
        }
		
//		@Override
//		protected void onLayout(boolean changed, int l, int t, int r, int b) {
//			super.onLayout(changed, location.x, location.y, location.x + this.getWidth(), location.y + this.getHeight() );
//		}
		
//		@Override
//		public boolean dispatchTouchEvent(MotionEvent event) {
//			switch( event.getAction() ){
//				case MotionEvent.ACTION_UP:
//					return false;
//			}
//			return true;
//		}
		@Override
        public boolean onTouchEvent(MotionEvent event) {
			this.params = (android.view.WindowManager.LayoutParams) this.getLayoutParams();
//			if( this.isAnimating )
//				return true;
			myGestureDetector.onTouchEvent(event);
			switch( event.getAction() ){
				/* Most events are handled by myGestureDetector */
				case MotionEvent.ACTION_UP:
					this.finishScreenPosition.x = (int) event.getRawX();
					this.finishScreenPosition.y = (int) event.getRawY();
					
					final int [] coords = mCoordinatesTemp;
					DropView target = this.findDropTarget( (int) event.getRawX(), (int) event.getRawY(), coords );
					if( target != null && target.acceptDrop( this ) ){
						target.onDrop( this );
					} else {
						/* Return to Original Position */
						this.returnToOriginalPosition();
					}
					
					break;
			}
            return true;
        }
        
		private DropView findDropTarget( int x, int y, int[] dropCoordinates ){
			final Rect r = mRectTemp;
			RackGroup panel = (RackGroup) this.getRootView().findViewById(main.ID_PANEL);
			if( panel != null ){
				panel.getHitRect(r);
				panel.getLocationOnScreen(dropCoordinates);
	            r.offset(dropCoordinates[0] - panel.getLeft(), dropCoordinates[1] - panel.getTop());
				if (r.contains(x, y)) {
					/* It's a panel Hit */
					final ArrayList<DropView> dropTargets = ((main) context).getRackDropTargets();
			        final int count = dropTargets.size();
			        for (int i=count-1; i>=0; i--) {
			            final DropView target = dropTargets.get(i);
			            target.getHitRect(r);
			            target.getLocationOnScreen(dropCoordinates);
			            int left = target.getLeft();
			            int top = target.getTop();
			            r.offset(dropCoordinates[0] - target.getLeft(), dropCoordinates[1] - target.getTop());
			            if (r.contains(x, y)) {
			                dropCoordinates[0] = x - dropCoordinates[0];
			                dropCoordinates[1] = y - dropCoordinates[1];
			                return target;
			            }
			        }
				} else {
					/* It's a board hit */
					final ArrayList<DropView> dropTargets = ((main) context).getBoardDropTargets();
			        final int count = dropTargets.size();
			        for (int i=count-1; i>=0; i--) {
			            final DropView target = dropTargets.get(i);
			            target.getHitRect(r);
			            target.getLocationOnScreen(dropCoordinates);
			            int left = target.getLeft();
			            int top = target.getTop();
			            r.offset(dropCoordinates[0] - target.getLeft(), dropCoordinates[1] - target.getTop());
			            if (r.contains(x, y)) {
			                dropCoordinates[0] = x - dropCoordinates[0];
			                dropCoordinates[1] = y - dropCoordinates[1];
			                return target;
			            }
			        }
				}
			}	        
	        return null;	
		}
		
		private void returnToOriginalPosition(){
			this.animateReturn();
		}
		private void returnToOriginalPositionAfterAnim(){
			((ViewGroup) this.getParent()).removeView( this );
			params.x = this.originalPositionInParent.x;
			params.y = this.originalPositionInParent.y;
			this.setLayoutParams(params);
			if( originalParent instanceof ViewGroup )
				originalParent.addView( this );
			isAnimating = false;
		}
		
		private void animateReturn(){
			this.animateType = ANIM_RETURN_TO_ORIGINAL;
			params.x = this.startScreenPosition.x;
			params.y = this.startScreenPosition.y - main.getNotificationBarHeight();
			int deltaX = this.finishScreenPosition.x - this.startScreenPosition.x - ( this.getWidth() / 2 );
			int deltaY = this.finishScreenPosition.y - this.startScreenPosition.y - ( this.getHeight() / 2 );
			requestLayout();
			Animation anim = new TranslateAnimation( deltaX, 0, deltaY, 0 );
			anim.setDuration(500);
			this.startAnimation(anim);
			isAnimating = true;
		}
		
		@Override
		public void onAnimationEnd() {
			switch( this.animateType ){
				case ANIM_RETURN_TO_ORIGINAL:
					this.returnToOriginalPositionAfterAnim();
					break;
			}
		}
		
        public Boolean hasParent( View view ){
        	return ( view.getParent() != null ) ? true : false;
        }
        
        public String getLetter(){
        	return this.letter;
        }
        
        public void setLetter( char letter ){
        	this.letter = String.valueOf( letter );
        }
        
        public void addViewToTopParent(){
        	ViewParent parent = this.getParent();
        	if( parent instanceof GameGroup ){
        		/* We Don't need to Remove/Add View b/c we're already on top of the parent */
        	} else {
        		parent = this.getTopLevelParent();
        		if( parent != null ){
    	        	((ViewGroup) this.getParent()).removeView( this );
    	        	((ViewGroup) parent).addView( this );
            	}
        	}
        }
        
        public GameGroup getTopLevelParent(){
        	ViewParent parent = this.getParent();
        	while( parent != null ){
        		if( parent instanceof GameGroup )
        			return (GameGroup) parent;
        		parent = parent.getParent();
        	}
        	return null;
        }
        
        /**
         * Change params of view's x and y position.
         * @param distX float change in x
         * @param distY float change in y
         */
        public void doScrollByChange( float distX, float distY ){
    		params.x += (int) distX - ( this.getWidth() / 2 );
			params.y += (int) distY - ( this.getHeight() / 2 );
			requestLayout();
        }
        
        public void doScrollByPosition( float posX, float posY ){
        	params.x = (int) posX - ( this.getWidth() / 2 );
        	params.y = (int) posY - ( this.getHeight() / 2 ) - main.getNotificationBarHeight();
        	requestLayout();
        }
        
        public class MyGestureListener extends SimpleOnGestureListener {
        	@Override
        	public boolean onDown( MotionEvent e ){
        		/* Record current settings in case we need to jump back to them */
        		originalPositionInParent.x = params.x;
        		originalPositionInParent.y = params.y;
        		ViewGroup parent = (ViewGroup) getParent();
        		if( parent instanceof BoardPlaceHolder || parent instanceof RackPlaceHolder )
        			originalParent = (ViewGroup) getParent();
        		/* Need to add view to window so it can move over all ViewGroups */
        		getLocationInWindow(loc);
        		params.x = (int) e.getRawX() - ( getWidth() / 2 );
        		params.y = (int) e.getRawY() - ( getHeight() / 2 ) - main.getNotificationBarHeight();
        		setLayoutParams(params);
        		Log.i("Before parent", params.x + " : " + params.y );
    			addViewToTopParent();
    			/* Set Start Screen Position */
    			startScreenPosition.x = loc[0];
    			startScreenPosition.y = loc[1];
        		return true;
        	}
        	
        	@Override
        	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY){
        		Log.i("XY", e2.getX() + " : " + e2.getY() );
        		Log.i("Raw XY", e2.getRawX() + " : " + e2.getRawY() );
        		getLocationOnScreen( mCoordinatesTemp );
        		final int [] coords = mCoordinatesTemp;
				DropView target = findDropTarget( (int) e2.getRawX(), (int) e2.getRawY(), coords );
				if( target != null ){
					if( target != mLastDropTarget ){
						/* Change Tile Size */
						params.width = target.getWidth();
						params.height = target.getHeight();
						setLayoutParams( params );
						/* Do onHoverExit */
						if( mLastDropTarget != null ){
							mLastDropTarget.onHoverExit();
						}
						/* Do onHover */
						target.onHover();
					}
					mLastDropTarget = target;
				} else {
					/* Check to see if we ended on a non DropView */
					if( mLastDropTarget != null ){
						mLastDropTarget.onHoverExit();
					}
				}
    			doScrollByPosition( e2.getRawX(), e2.getRawY() );
        		return true;
        	}
        }
    }