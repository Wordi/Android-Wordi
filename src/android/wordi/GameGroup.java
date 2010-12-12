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
public class GameGroup extends ViewGroup {
	
	public GameGroup(Context context ) {
		super(context);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int numChildren = this.getChildCount();
		for( int i = 0; i < numChildren; i++ ){
			View child = this.getChildAt(i);
			WindowManager.LayoutParams params = (android.view.WindowManager.LayoutParams) child.getLayoutParams();
			if( child instanceof Tile ){
				@SuppressWarnings("unused")
				String temp = "hi";
			}
			child.measure(r, b);
			int width = child.getMeasuredWidth();
			int height = child.getMeasuredHeight();
			child.layout(params.x, params.y, params.x + width, params.y + height );
		}
	}
}