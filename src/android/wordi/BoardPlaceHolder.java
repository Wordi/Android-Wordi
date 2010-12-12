package test.image;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

public class BoardPlaceHolder extends DropView {
	private int positionOnBoard;
	private Drawable mBackgroundDrawable;
	private int mHoverColor;

	public BoardPlaceHolder(Context context) {
		super(context);
		this.mBackgroundDrawable = this.getResources().getDrawable( R.drawable.tile_gradient );
		this.setBackgroundDrawable( this.mBackgroundDrawable );
		this.mHoverColor = Color.argb(90, 0, 0, 200 );
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int numChildren = this.getChildCount();
		for( int i = 0; i < numChildren; i++ ){
			View child = this.getChildAt(i);
			WindowManager.LayoutParams params = (android.view.WindowManager.LayoutParams) child.getLayoutParams();
			child.layout( 0, 0, this.getWidth(), this.getHeight() );
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
		this.setBackgroundDrawable(mBackgroundDrawable);
		this.addView( v );
	}
	
	@Override
	public Boolean acceptDrop(View v) {
		if( v instanceof Tile )
			return true;
		return false;
	}

	@Override
	public void onHover() {
		this.setBackgroundColor( this.mHoverColor );
		
	}
	
	@Override
	public void onHoverExit() {
		this.setBackgroundDrawable( this.mBackgroundDrawable );
	}
	
	public void setPositionOnBoard( int pos ){
		this.positionOnBoard = pos;
	}
	public int getPositionOnBoard(){
		return this.positionOnBoard;
	}

	

}
