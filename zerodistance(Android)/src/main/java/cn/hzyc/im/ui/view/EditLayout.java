package cn.hzyc.im.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import cn.hzyc.im.R;

/**
 * 扩展的编辑框, 输入内容后，右侧有清除小图标，点击可清空编辑框输入内容
 * 
 * @author Jinquan.Wang
 * @since 1.0
 */
public class EditLayout extends RelativeLayout {

	private static final int IOCN_CLREAR = R.drawable.selector_edittext_btn_clear;
	private static final int BG_EDIT_TEXT = R.color.white;
	
	private ImageView mBtnClear;
	private ImageView mIvDrawableRight;
	
	private EditText mEditText;
	private Context context;

	private TextWatcher mTextWatcher;
	
	private boolean mUseBtnClear = true;
	
	/**
	 * 按钮焦点改变
	 */
	public OnFocusChangeListener mOnEditTextOnFocusChangeListener = new OnFocusChangeListener() {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (!mUseBtnClear)
				return ;
			
			if (hasFocus) {
				String text = mEditText.getText().toString().trim();
				if (!TextUtils.isEmpty(text)) {
					mBtnClear.setVisibility(View.VISIBLE);
				}
			} else {
				mBtnClear.setVisibility(View.GONE);
			}
		}
	};

	/** 设置编辑框内容改变监听器 */
	public void setOnTextChangedListener(TextWatcher textWatcher) {
		this.mTextWatcher = textWatcher;
	}

	public EditLayout(Context context) {
		super(context);
		this.context = context;
		init();
	}

	public EditLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	private void init() {
		setPadding(10, 5, 10, 5);
		setGravity(Gravity.CENTER_VERTICAL);

		addEditText();
		addClearBtn();
		bindListener();

		updateTheme();
	}
	
	public void disableClearBtnMode() {
		mUseBtnClear = false;
	}

	private int dp2px(int dp) {
		return (int) (dp * getContext().getResources().getDisplayMetrics().density);
	}
	
	private void addEditText() {
		mEditText = new EditText(context);
		mEditText.setPadding(0, 0, dp2px(10), 0);
		mEditText.setSingleLine();
		mEditText.setOnFocusChangeListener(mOnEditTextOnFocusChangeListener);
		// mEditText.setCursorVisible(false);

		LayoutParams param = new LayoutParams(LayoutParams.FILL_PARENT,
				dp2px(35));
		param.addRule(RelativeLayout.CENTER_VERTICAL);
		addView(mEditText, param);
	}

	private void addClearBtn() {
		mBtnClear = new ImageView(context);
		mBtnClear.setVisibility(View.INVISIBLE);
		
		LayoutParams param = new LayoutParams(
				dp2px(20), dp2px(20));
		param.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		param.addRule(RelativeLayout.CENTER_VERTICAL);
		param.rightMargin = dp2px(10);
		addView(mBtnClear, param);
	}

	/**
	 * 设置清空按钮的右边距
	 * @param marginRight
	 */
	public void setClearBtnMarginRight(int marginRight) {
		if (mBtnClear.getLayoutParams() != null) {
			LayoutParams param = (LayoutParams) mBtnClear
					.getLayoutParams();
			param.rightMargin = marginRight;
		}
	}
	
	public void addDrawableRight(int res) {
		if (mIvDrawableRight == null) {
			mIvDrawableRight = new ImageView(context);
			mIvDrawableRight.setBackgroundResource(res);

			LayoutParams param = new LayoutParams(
					dp2px(25), dp2px(25));
			param.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			param.addRule(RelativeLayout.CENTER_VERTICAL);
			param.rightMargin = 10;
			addView(mIvDrawableRight, param);
		}
	}

	public View getBtnClear() {
		return mBtnClear;
	}
	
	private void bindListener() {
		if (mUseBtnClear) {
			mBtnClear.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					mEditText.setText("");
				}
			});
		}

		mEditText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mEditText.setCursorVisible(true);
			}
		});

		mEditText.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (mTextWatcher != null)
					mTextWatcher.onTextChanged(s, start, before, count);
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				if (mTextWatcher != null)
					mTextWatcher.beforeTextChanged(s, start, count, after);
			}

			public void afterTextChanged(Editable s) {
				if (s.length() == 0) {
					if (mUseBtnClear)
						mBtnClear.setVisibility(View.GONE);
					
					if (mIvDrawableRight != null) {
						mIvDrawableRight.setVisibility(VISIBLE);
					}
					
					if (mOnDataClearListener != null)
						mOnDataClearListener.onDataClear();
					
				} else {
					if (mUseBtnClear)
						mBtnClear.setVisibility(View.VISIBLE);
					
					if (mIvDrawableRight != null) {
						mIvDrawableRight.setVisibility(GONE);
					}
				}

				if (mTextWatcher != null)
					mTextWatcher.afterTextChanged(s);
			}
		});
	}
	
	/**
	 * 编辑框数据清空监听器
	 */
	public interface OnDataClearListener {
		public void onDataClear();
	}
	
	private OnDataClearListener mOnDataClearListener;
	
	public void OnDataClearListenerset(OnDataClearListener onDataClearListener) {
		this.mOnDataClearListener = onDataClearListener;
	}
	
	public void updateTheme() {
		Drawable bg = getBackground();
		if (bg == null) {
			mEditText.setBackgroundResource(BG_EDIT_TEXT);
		} else {
			mEditText.setBackgroundColor(Color.TRANSPARENT);
		}
		mBtnClear.setBackgroundResource(IOCN_CLREAR);

		mEditText.setPadding(dp2px(10), 5, 45, 5);
		mEditText.setGravity(Gravity.CENTER_VERTICAL);
	}

	public void hideClearBtn() {
		mBtnClear.setVisibility(View.INVISIBLE);
	}
	
	public void setTextColor(int color) {
		mEditText.setTextColor(color);
	}

	public void setTextSize(int size) {
		mEditText.setTextSize(size);
	}

	public String getText() {
		return mEditText.getText().toString().trim();
	}

	public void setText(String text) {
		mEditText.setText(text);
	}

	public void setHint(String hint) {
		mEditText.setHint(hint);
	}

	public void setSelection(int index) {
		mEditText.setSelection(index);
	}

	public EditText getEditText() {
		return mEditText;
	}
	
	public void setSingleline() {
		mEditText.setSingleLine();
	}
	
	public void setPasswordStyle() {
		mEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
	}
}