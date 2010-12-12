package android.wordi;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

/* Container to hold Board and board pieces */
public class BoardGroup extends ViewGroup {
	
	public BoardGroup(Context context) {
		super(context);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.x = 0;
		lp.y = 0;
		this.setLayoutParams(lp);
		this.setBackgroundDrawable( this.getResources().getDrawable( R.drawable.board_background ) );
		//this.setBackgroundColor( Color.RED );
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int numChildren = this.getChildCount();
		for( int i = 0; i < numChildren; i++ ){
			View child = this.getChildAt(i);
			WindowManager.LayoutParams params = (android.view.WindowManager.LayoutParams) child.getLayoutParams();
			child.measure(r, b);
			int height = child.getMeasuredHeight();
			int width = child.getMeasuredWidth();
			child.layout(params.x, params.y, params.x + width, params.y + height );
		}
	}
	
	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        assert(MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.UNSPECIFIED);

        final int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();
        
        height = ( height / 3 ) * 2;
        setMeasuredDimension(width, height);
    }
}