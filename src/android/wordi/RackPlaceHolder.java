package android.wordi;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

public class RackPlaceHolder extends DropView {
	private int positionOnRack;
	private Drawable mBackgroundDrawable;
	private int mHoverColor;
	private int mPadding = 5;

	public RackPlaceHolder(Context context) {
		super(context);
//		this.mBackgroundDrawable = this.getResources().getDrawable( R.drawable.tile_gradient );
//		this.setBackgroundDrawable( this.mBackgroundDrawable );
		this.mHoverColor = Color.argb(90, 0, 0, 200 );
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int numChildren = this.getChildCount();
		for( int i = 0; i < numChildren; i++ ){
			View child = this.getChildAt(i);
			WindowManager.LayoutParams params = (android.view.WindowManager.LayoutParams) child.getLayoutParams();
			params.width = this.getWidth() - this.mPadding;
			params.height = this.getHeight() - this.mPadding;
			child.setLayoutParams(params);
			child.layout( this.mPadding, this.mPadding, params.width, params.height );
		}
	}
	
	/* Called by a piece on ACTION_UP
	 * when it gets dropped onto this view
	 */
	public void onDrop( View v ){
		((ViewGroup) v.getParent()).removeView( v );
		WindowManager.LayoutParams lp = (android.view.WindowManager.LayoutParams) v.getLayoutParams();
		lp.x = 0;
		lp.y = 0;
		lp.width = this.getWidth();
		lp.height = this.getHeight();
		v.setLayoutParams(lp);
		this.setBackgroundColor(Color.TRANSPARENT);
		this.addView( v );
	}
	
	@Override
	public Boolean acceptDrop(View v) {
		if( v instanceof Tile && ! isFilled() && getChildCount() == 0 )
			return true;
		return false;
	}

	@Override
	public void onHover() {
		this.setBackgroundColor( this.mHoverColor );
		
	}
	
	@Override
	public void onHoverExit() {
		this.setBackgroundColor(Color.TRANSPARENT);
	}
	
	public void setPositionOnRack( int pos ){
		this.positionOnRack = pos;
	}
	public int getPositionOnRack(){
		return this.positionOnRack;
	}

	

}
