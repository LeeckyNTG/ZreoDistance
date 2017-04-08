package cn.hzyc.im.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * 右侧字母快速导航条
 * 
 * @author Jinquan.Wang
 */
public class LetterBar extends View {

	/** 字母正常状态时的颜色 */
	private static final int TEXT_COLOR = 0xff595959;
	/** 字母按下时的颜色 */
	private static final int TEXT_COLOR_PRESSED = 0xff595959;
	/** 按下时的背景颜色 */ 
	private static final int COLOR_PRESSED_BG = 0xffB0B0B0;
	
	/** 26个字母 */ 
	public static final String[] LETTERS = {"↑", // 
			"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", 
			"K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", 
			"U", "V", "W", "X", "Y", "Z", "#" };
	
	/** 字母触摸监听器 */
	private OnPressedLetterChangedListener mOnPressedLetterChangedListener;

	private int mCheckedIndex = -1;// 选中
	
	private Paint paint = new Paint();
	
	/** 按下字母时，在屏幕中间显示的字母，松开手时会隐藏 */
	private TextView mTextDialog;

	public LetterBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LetterBar(Context context) {
		super(context);
	}

	public void setTextView(TextView mTextDialog) {
		this.mTextDialog = mTextDialog;
		mTextDialog.setBackgroundDrawable(createRoundCornerDrawable());
	}
	
	public Drawable createRoundCornerDrawable() {
	    int roundRadius = 10; // 8dp
	    int fillColor = Color.parseColor("#ff808080");
	    GradientDrawable gd = new GradientDrawable();
	    gd.setColor(fillColor);
	    gd.setCornerRadius(roundRadius);
	    return gd;
	}
	
	private int dp2px(int dp) {
		return (int) (dp * getContext().getResources().getDisplayMetrics().density + 0.5f);
	}
	
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		// 获取焦点改变背景颜色.
		int height = getHeight();// 获取对应高度
		int width = getWidth(); // 获取对应宽度
		
		// 这里使用浮点型，使用整形出来的效果可能会有偏差
		float singleHeight = (Float.valueOf(height) / LETTERS.length);// 获取每一个字母的高度

		paint.setAntiAlias(true);
		paint.setTextSize(dp2px(14));
		
		for (int i = 0; i < LETTERS.length; i++) {
			if (i != mCheckedIndex) {// 非选中
				paint.setColor(TEXT_COLOR);
			} else {
				paint.setColor(TEXT_COLOR_PRESSED);// 选中的状态
			}
			
			// x坐标等于中间-字符串宽度的一半.
			float xPos = width / 2 - paint.measureText(LETTERS[i]) / 2;
			float yPos = singleHeight * i + singleHeight;
			canvas.drawText(LETTERS[i], xPos, yPos, paint);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		final float y = event.getY();// 点击y坐标
		final int oldChoose = mCheckedIndex;
		
		final OnPressedLetterChangedListener listener = mOnPressedLetterChangedListener;
		
		// 点击y坐标所占总高度的比例*b数组的长度就等于点击b中的个数.
		final int c = (int) (y / getHeight() * LETTERS.length);

		switch (action) {
		case MotionEvent.ACTION_UP:
			setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			mCheckedIndex = -1;//
			invalidate();
			if (mTextDialog != null) {
				mTextDialog.setVisibility(View.INVISIBLE);
			}
			break;

		default:
			setBackgroundDrawable(new ColorDrawable(COLOR_PRESSED_BG));
			
			if (oldChoose != c) {
				if (c >= 0 && c < LETTERS.length) {
					if (listener != null) {
						listener.onTouchingLetterChanged(LETTERS[c]);
					}
					if (mTextDialog != null) {
						mTextDialog.setText(LETTERS[c]);
						mTextDialog.setVisibility(View.VISIBLE);
					}
					
					mCheckedIndex = c;
					invalidate();
				}
			}
			break;
		}
		return true;
	}

	/**
	 * 向外公开的方法
	 * 
	 * @param onPressedLetterChangedListener
	 */
	public void setOnTouchingLetterChangedListener(
			OnPressedLetterChangedListener onPressedLetterChangedListener) {
		this.mOnPressedLetterChangedListener = onPressedLetterChangedListener;
	}

	public interface OnPressedLetterChangedListener {
		public void onTouchingLetterChanged(String s);
	}

}