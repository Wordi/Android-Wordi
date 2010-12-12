package test.image;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

public abstract class DropView extends ViewGroup {
	private Boolean mIsFilled;

	public DropView(Context context) {
		super(context);
		mIsFilled = false;
	}

	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		WindowManager.LayoutParams params = (android.view.WindowManager.LayoutParams) this.getLayoutParams();
		this.setMeasuredDimension( params.width, params.height );
	}	
	
	public Boolean isFilled(){
		return this.mIsFilled;
	}
	public void setIsFilled( Boolean b ){
		this.mIsFilled = b;
	}
	
	public abstract void onDrop( View v );
	public abstract Boolean acceptDrop( View v );
	public abstract void onHover();
	public abstract void onHoverExit();

}
