package android.wordi;

import java.util.ArrayList;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
 
public class main extends Activity{
	private static int mNotiBarHeight;
	private ViewGroup mRootView;
	
	public static int ID_PANEL = 1;
	
	/** Who can receive drop events */
    private ArrayList<DropView> mDropBoardTargets = new ArrayList<DropView>();/** Who can receive drop events */
    private ArrayList<DropView> mDropRackTargets = new ArrayList<DropView>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        determineNotificationBarHeight();
        
        /* Board View */
        Board mBoard = new Board(this);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        mBoard.setLayoutParams( lp );
        
        /* Board ViewGroup */
        BoardGroup mBoardGroup = new BoardGroup( this );
        mBoardGroup.addView( mBoard );
        
        /* Panel ViewGroup */
        RackGroup mPanelGroup = new RackGroup( this );
        mPanelGroup.setId(ID_PANEL);
        
        /* Game ViewGroup */
        GameGroup mGameGroup = new GameGroup( this );
        mGameGroup.addView( mBoardGroup );
        mGameGroup.addView( mPanelGroup );
        
        setContentView( mGameGroup );
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
    	super.onRestoreInstanceState(savedInstanceState);
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState){
    	super.onSaveInstanceState(outState);
    }
    
    /**
     * Add a DropTarget to the list of potential places to receive drop events.
     */
    public void addBoardDropTarget(DropView target) {
    	mDropBoardTargets.add(target);
    }

    /**
     * Don't send drop events to <em>target</em> any more.
     */
    public void removeBoardDropTarget(DropView target) {
    	mDropBoardTargets.remove(target);
    }
    
    public ArrayList<DropView> getBoardDropTargets(){
    	return this.mDropBoardTargets;
    }
    
    /**
     * Add a DropTarget to the list of potential places to receive drop events.
     */
    public void addRackDropTarget(DropView target) {
    	mDropRackTargets.add(target);
    }

    /**
     * Don't send drop events to <em>target</em> any more.
     */
    public void removeRackDropTarget(DropView target) {
    	mDropRackTargets.remove(target);
    }
    
    public ArrayList<DropView> getRackDropTargets(){
    	return this.mDropRackTargets;
    }
    
    public static int getNotificationBarHeight(){
    	return mNotiBarHeight;
    }
    private void determineNotificationBarHeight(){
    	Drawable temp = getResources().getDrawable( android.R.drawable.stat_sys_phone_call );
    	main.mNotiBarHeight = temp.getIntrinsicHeight();
    }
}