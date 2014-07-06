package com.huiges.AndroBlip.views;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Special version of ImageView which allow enlarge width of image if android:adjustViewBounds is true.
 * 
 * <p>This simulate HTML behaviour &lt;img src="" widh="100" /&gt;</p>
 * <p><a href="http://stackoverflow.com/questions/6202000/imageview-one-dimension-to-fit-free-space-and-second-evaluate-to-keep-aspect-rati">Stackoverflow question link</p>
 * 
 * @author Tomas prochazka // tomas.prochazka@atomsoft.cz
 * @version $Revision: 0$ ($Date: 6.6.2011 18:16:52$)
 */
public class ResizeImageView extends android.widget.ImageView {

	private int mDrawableWidth;
	private int mDrawableHeight;
	private int mMaxWidth;
	private int mMaxHeight;
	
	public ResizeImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ResizeImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ResizeImageView(Context context) {
		super(context);
	}

	public void setMaxWidth(int maxWidth) {
		super.setMaxWidth(maxWidth);
		mMaxWidth = maxWidth;
	}

	public void setMaxHeight(int maxHeight) {
		super.setMaxHeight(maxHeight);
		mMaxHeight = maxHeight;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec,heightMeasureSpec);
		if(getDrawable() == null){
			return;
		}
		mDrawableWidth = getDrawable().getIntrinsicWidth();
		mDrawableHeight = getDrawable().getIntrinsicHeight();
		int maxImageWidth  = mMaxWidth;
		int maxImageHeigth =  (int) (mMaxHeight * 0.85);
		
		float viewDelta = (float) maxImageWidth / (float) maxImageHeigth;
		float imageDelta  = (float) mDrawableWidth / (float) mDrawableHeight;
		
		int finalWidth;
		int finalHeight;
		if(viewDelta < imageDelta){
			//full width
			finalWidth = maxImageWidth;
			finalHeight = (int) (finalWidth / imageDelta);
		}else{
			finalHeight = maxImageHeigth;
			finalWidth = (int) (finalHeight * imageDelta);
		}
		setMeasuredDimension(finalWidth, finalHeight);
	}

}