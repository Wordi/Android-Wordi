package android.wordi;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.MeasureSpec;

/* Container to hold Board and board pieces */
public class RackGroup extends ViewGroup {
	private int squareSize = 0;;
	private int padding = 2;
	private main context;
	
	public RackGroup(Context context ) {
		super(context);
		this.context = (main) context;
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		this.setLayoutParams(lp);
		this.setBackgroundDrawable( this.getResources().getDrawable( R.drawable.bottom ) );
		for( int i = 0; i < this.getNumPiecesOnRack(); i++ ){
			RackPlaceHolder temp = new RackPlaceHolder( context );
            lp = new WindowManager.LayoutParams();
            temp.setLayoutParams(lp);
            temp.setPositionOnRack( i );
            /* Create a tile for each holder too */
            Tile temp2 = new Tile( context );
            temp.addView( temp2 );
            /* Add them */
            this.addView( temp );
            ((main) this.context).addRackDropTarget( temp );
		}
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int numChildren = this.getChildCount();
		int left = 0;
		int top = 0;
		for( int i = 0; i < numChildren; i++ ){
			squareSize = this.getSquareSize( r-l, b-t );
			View child = this.getChildAt(i);
			WindowManager.LayoutParams params = (android.view.WindowManager.LayoutParams) child.getLayoutParams();
			params.width = squareSize;
			params.height = squareSize;
			child.setLayoutParams( params );
			child.measure(r, b);
			child.layout(left , top , left + squareSize , top + squareSize  );
			left += squareSize;
			if( left + squareSize > r - l ){
				left = 0;
				top += squareSize;
			}
		}
	}
	
	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        assert(MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.UNSPECIFIED);

        final int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();
        
        ( (WindowManager.LayoutParams) this.getLayoutParams() ).y = ( height / 3 ) * 2;
        height = ( height / 3 );
        setMeasuredDimension(width, height);
    }
	
	public double optimalSize (double W, double H, int N)
	{
	    int i,j ;
	    for (i = (int) Math.round(Math.sqrt(N*W/H)) ; i*Math.floor(H*i/W) < N ; i++){}
	    for (j = (int) Math.round(Math.sqrt(N*H/W)) ; Math.floor(W*j/H)*j < N ; j++){}
	    return Math.max(W/i, H/j) ;
	}
	
	public int getNumPiecesOnRack(){
		return 8;
	}
	private int getSquareSize( int width, int height ){
		if( squareSize > 0 ) return squareSize;
		return (int) this.optimalSize( width , height, this.getNumPiecesOnRack() );
	}
}