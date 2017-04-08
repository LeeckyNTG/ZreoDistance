package cn.hzyc.im.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * 主界面底部选项卡指示器控件，切换时会有图片颜色渐变效果
 */
public class GradientTab extends View {
	
	/** 默认字体大小 */
	private static final int DEFAULT_TEXT_SIZE = 12;
	/** 图标渐变颜色 */
	private static final int DEFAULT_GRADIENT_COLOR = 0xFF45C01A;
	/** tab普通状态时字体的颜色 */
	private static final int TEXT_COLOR_NORMAL = 0xff878787;
	/** tab选中时字体的颜色 */
	private static final int TEXT_COLOR_SELECTED = DEFAULT_GRADIENT_COLOR;
	
	/** tab显示的文本 */
	private String mTabText = "标题";
	/** tab显示的图标 */
	private Bitmap mTabIcon;
	/** tab显示的字体大小*/
	private int mTabTextSize = (int) (getResources().getDisplayMetrics().density * DEFAULT_TEXT_SIZE);
	/** 渐变颜色 */
	private int mTabGradientColor = DEFAULT_GRADIENT_COLOR;
	
	/** 滑动时图标的透明度 */
	private float mAlphaPercent;
	
	/** 渐变的bitmap对象 */
	private Bitmap mGradientBitmap;
	
	private Canvas mCanvas;
	
	/** 绘制图标的画笔 */
	private Paint mIconPaint;
	/** 绘制文本的画笔 */
	private Paint mTextPaint = new Paint();
	
	/** 图标的绘制区域 */
	private Rect mIconRect = new Rect();
	/** 文字的长宽边界范围 */
	private Rect mTextBound = new Rect();
	
	public GradientTab(Context context) {
		super(context);
		init();
	}

	public GradientTab(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public void init() {
		mTextPaint.setTextSize(mTabTextSize);
		mTextPaint.setColor(TEXT_COLOR_NORMAL);
		mTextPaint.getTextBounds(mTabText, 0, mTabText.length(), mTextBound);
		mTextPaint.setAntiAlias(true); // 去矩齿
		
		mRedDotPaint.setAntiAlias(true); // 去锯齿
		
		mUnreadCountPaint.setTextSize(dp2px(12));
		mUnreadCountPaint.setAntiAlias(true); // 去锯齿
		mUnreadCountPaint.setTextAlign(Align.CENTER); // 居中显示
		
		getUnreadCountTextHeight();
	}

	/** 获取未读条数字体显示高度 */
	public void getUnreadCountTextHeight() {
		Rect unreadCountRect = new Rect();
		mUnreadCountPaint.getTextBounds("1", 0, 1, unreadCountRect);
		mUnreadCountTextHeight = unreadCountRect.height();
	}
	
	/**
	 * 测量控件，初始化图标的绘制区域
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		// 图标宽高是一样的，取两者的较小值
		int iconWidth = Math.min(getMeasuredWidth() - getPaddingLeft()
				- getPaddingRight(), getMeasuredHeight() - getPaddingTop()
				- getPaddingBottom() - mTextBound.height());

		int left = getMeasuredWidth() / 2 - iconWidth / 2;
		int top = getMeasuredHeight() / 2 - (mTextBound.height() + iconWidth) / 2;
		
		mIconRect.left = left;
		mIconRect.top = top;
		mIconRect.right = left + iconWidth;
		mIconRect.bottom = top + iconWidth;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// 绘制红点
		drawRedDot(canvas, mShowRedDot);
		
		// 绘制未读取条数
		drawUnreadCount(canvas, mUnreadCount);
		
		// 绘制tab图标
		canvas.drawBitmap(mTabIcon, null, mIconRect, null);
		
		// 绘制tab原文本
		drawSourceText(canvas, 0);
		int alpha = (int) Math.ceil(255 * mAlphaPercent);
		
		// 内存去准备mBitmap , setAlpha , 纯色 ，xfermode
		mGradientBitmap = createGradientBitmap(alpha, mTabGradientColor);
		
		// 绘制渐变色的图标
		canvas.drawBitmap(mGradientBitmap, 0, 0, null);
		
		// 绘制渐变色的文本
		drawTargetText(canvas, alpha);
	}
	
	private int dp2px(int dp) {
		return (int) (dp * getContext().getResources().getDisplayMetrics().density);
	}

	/**
	 * 绘制变色的文本
	 * 
	 * @param canvas
	 * @param alpha
	 */
	private void drawTargetText(Canvas canvas, int alpha) {
		mTextPaint.setColor(TEXT_COLOR_SELECTED);
		mTextPaint.setAlpha(alpha);
		int x = (getMeasuredWidth() - mTextBound.width())/ 2;
		int y = mIconRect.bottom + mTextBound.height();
		canvas.drawText(mTabText, x, y, mTextPaint);
	}

	/**
	 * 绘制原文本
	 * 
	 * @param canvas
	 * @param alpha
	 */
	private void drawSourceText(Canvas canvas, int alpha) {
		mTextPaint.setColor(TEXT_COLOR_NORMAL);
		mTextPaint.setAlpha(255 - alpha);
		int x = (getMeasuredWidth()  - mTextBound.width()) / 2;
		int y = mIconRect.bottom + mTextBound.height();
		canvas.drawText(mTabText, x, y, mTextPaint);
	}

	/**
	 * 在内存中绘制可变色的Icon
	 * @param alpha 透明度
	 * @param gradientColor 渐变色
	 * @return
	 */
	private Bitmap createGradientBitmap(int alpha, int gradientColor) {
		Canvas canvas = null;
		if (mGradientBitmap != null) {
			mGradientBitmap.recycle();
		}
		
		mGradientBitmap = Bitmap.createBitmap(getMeasuredWidth(), 
				getMeasuredHeight(), Config.ARGB_8888);
		canvas = new Canvas(mGradientBitmap);
		mIconPaint = new Paint();
		mIconPaint.setColor(gradientColor);
		mIconPaint.setAntiAlias(true); // 去矩齿
		mIconPaint.setDither(true);
		mIconPaint.setAlpha(alpha); // 透明度
		canvas.drawRect(mIconRect, mIconPaint); // 画了一个矩形
		
		mIconPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		
		mIconPaint.setAlpha(255);
		// 画了图标，图标与短形是重叠的，通过setXfermode()决定出来的效果
		canvas.drawBitmap(mTabIcon, null, mIconRect, mIconPaint); 
		return mGradientBitmap;
	}

	/**
	 * 更新Tab显示的透明度
	 * @param alphaPercent 透明度 0 到 1
	 */
	public void updateTabAlpha(float alphaPercent) {
		this.mAlphaPercent = alphaPercent;
		invalidate();
	}
	
	/**
	 * 设置Tab的选中状态
	 * 
	 * @param selected true时显示为高亮, false为正常的显示状态
	 */
	public void setTabSelected(boolean selected) {
		updateTabAlpha(selected ? 1 : 0);
	}
	
	/**
	 * 设置要绘制的文本和图标
	 * 
	 * @param text tab显示的文本
	 * @param icon tab显示的图标
	 */
	public void setTextAndIcon(String text, int icon) {
		this.mTabText = text;
		this.mTabIcon = BitmapFactory.decodeResource(getResources(), icon);
		mTextPaint.getTextBounds(mTabText, 0, mTabText.length(), mTextBound);
		invalidate();
	}
	
	/** 是否显示红点 */
	private boolean mShowRedDot = false;
	
	private Paint mRedDotPaint = new Paint();
	
	/**
	 * 绘制红点提示，传false还未隐藏红点
	 * 
	 * @param canvas
	 * @param showRedDot 传true表示绘制红点，传false会清除之前绘制的红点
	 */
	private void drawRedDot(Canvas canvas, boolean showRedDot) {
		mRedDotPaint.setColor(showRedDot ? Color.RED : Color.TRANSPARENT);
		canvas.drawCircle(mIconRect.right, mIconRect.top + dp2px(5), dp2px(5), mRedDotPaint);
	}
	
	/**
	 * 显示红点
	 * 
	 * @param visible true表示显示
	 */
	public void setRedDotVisible(boolean visible) {
		mShowRedDot = visible;
		invalidate();
	}
	
	private int mUnreadCount;
	
	public void setUnreadCount(int unreadCount) {
		this.mUnreadCount = unreadCount;
		invalidate();
	}
	
	private Paint mUnreadCountPaint = new Paint();
	private int mUnreadCountTextHeight;
	
	public void drawUnreadCount(Canvas canvas, int unreadCount) {
		boolean showUnreadCount = unreadCount > 0;
		mUnreadCountPaint.setColor(showUnreadCount? Color.WHITE : Color.TRANSPARENT);
		mRedDotPaint.setColor(showUnreadCount ? Color.RED : Color.TRANSPARENT);
		
		// 未读条数超过两位数时显示三个点...
		String text = unreadCount < 100 ? String.valueOf(unreadCount) : "...";
		int left = mIconRect.right + dp2px(5);
		int top = mIconRect.top + dp2px(9);
		
		canvas.drawCircle(left, top, dp2px(9), mRedDotPaint);
		canvas.drawText(text, left,  top +  mUnreadCountTextHeight / 2, mUnreadCountPaint);
	}
}
