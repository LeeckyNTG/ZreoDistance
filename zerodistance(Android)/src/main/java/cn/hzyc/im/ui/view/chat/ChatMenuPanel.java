package cn.hzyc.im.ui.view.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import cn.hzyc.im.R;
import cn.hzyc.im.base.Global;
import cn.hzyc.im.ui.activity.ChatActivity;
import cn.hzyc.im.util.EmojiUtil;
import cn.hzyc.im.util.EmojiUtil.Emoji;

/**
 * 聊天界面底部聊天菜单面板
 * 
 * @auther jq
 * @date 2015/11/4
 */
public class ChatMenuPanel extends LinearLayout {

    private Button btnemoji;
    private Button btnsend;
    private EditText etchattinginput;
    private RelativeLayout llmainbar;
    private FrameLayout flemojilist;
    private EmojiPanel emojiPanel;

    public ChatMenuPanel(Context context) {
        super(context);
        init();
    }
    
    public ChatActivity mActivity;

    public void setActivity(ChatActivity activity) {
        this.mActivity = activity;
    }

    public ChatMenuPanel(Context context, AttributeSet attrs) {
    	super(context, attrs);
    	init();
    }

    private void init() {
    	// 把布局添加到当前控件里
        View.inflate(Global.mContext, R.layout.chat_menu_panel, this);
        this.flemojilist = (FrameLayout) findViewById(R.id.fl_chat_layout);
        this.llmainbar = (RelativeLayout) findViewById(R.id.ll_main_bar);
        this.etchattinginput = (EditText) findViewById(R.id.et_chatting_input);
        this.btnsend = (Button) findViewById(R.id.btn_send);
        this.btnemoji = (Button) findViewById(R.id.btn_emoji);
        this.emojiPanel = (EmojiPanel) findViewById(R.id.ep_emoji);
        this.etchattinginput.setCursorVisible(true);
        
        etchattinginput.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                	etchattinginput.setBackgroundResource(R.drawable.ease_input_bar_bg_active);
                } else {
                	etchattinginput.setBackgroundResource(R.drawable.ease_input_bar_bg_normal);
                }
            }
        });
        
        // 点击编辑框时隐藏表情面板
        etchattinginput.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				flemojilist.setVisibility(View.GONE);
				Global.showInputMethod(etchattinginput);
			}
		});
        
        btnsend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnMessageSendListener != null) {
                    mOnMessageSendListener.onMsgSend(getUserInput());
                    clearUserInput();
                }
            }
        });

        btnemoji.setOnClickListener(new OnClickListener() {
        	
            @Override
            public void onClick(View view) {
            	// 表情与文本输入切换：
            	// 当前显示表情面板，点击时弹出输入法，并隐藏表情面板
                if (flemojilist.getVisibility() == View.VISIBLE) {
                    hideEmojiPanel();
                    Global.showInputMethod(etchattinginput);
                } else {
                    Global.getMainHandler().postDelayed(new Runnable() {
						
						@Override
						public void run() {
							 showEmojiPanel();
						}
					}, 100);
                    Global.hideInputMethod(etchattinginput);
                }
            }
        });

        emojiPanel.setDatas(EmojiUtil.mEmojiDatas);
        emojiPanel.setOnEmojiItemClickListener(mOnEmojiItemClickListener);
    }

    private EmojiPanel.OnEmojiItemClickListener mOnEmojiItemClickListener
            = new EmojiPanel.OnEmojiItemClickListener() {
    	
        @Override
        public void onEmojiItemClick(boolean isDeleteIcon, Emoji clickIcon) {
        	if (isDeleteIcon) { // viewpager每一页最后一个表情为删除符
        		deleteEmoji();
        	} else {
                appendEmoji(clickIcon);
        	}
        }
    };

    /**
     * 在编辑框上输入表情
     */
    public void appendEmoji(EmojiUtil.Emoji bean) {
        Bitmap phiz = BitmapFactory.decodeResource(getResources(), bean.resId);
        if (phiz != null) {
            int phizSize = Global.dp2px(20);
            int rawHeigh = phiz.getHeight();
            int rawWidth = phiz.getWidth();
            int newHeight = phizSize;
            int newWidth = phizSize;
            float heightScale = ((float) newHeight) / rawHeigh;
            float widthScale = ((float) newWidth) / rawWidth;
            Matrix matrix = new Matrix();
            matrix.postScale(widthScale, heightScale);
            Bitmap newBitmap = Bitmap.createBitmap(phiz, 0, 0, rawWidth, rawHeigh, matrix, true);
            ImageSpan imageSpan = new ImageSpan(mActivity, newBitmap);

            String emojiStr = bean.desc;
            SpannableString spannableString = new SpannableString(emojiStr);
            spannableString.setSpan(imageSpan,
                    emojiStr.indexOf('['), emojiStr.indexOf(']') + 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            etchattinginput.getText().insert(etchattinginput.getSelectionStart(), spannableString);
        }
    }
    
	/**
	 * 点击表情符删除键，删除表情或已输入的文字
	 * @author Jinquan.Wang
	 */
	public void deleteEmoji() {
		int selection = etchattinginput.getSelectionStart();
		String text = etchattinginput.getText().toString();
		if (selection > 0) {
			String text2 = text.substring(0, selection);
			if (text2.endsWith("]")) {
				int start = text2.lastIndexOf("[");
				int end = selection;
				etchattinginput.getText().delete(start, end);
				return;
			}
			etchattinginput.getText().delete(selection - 1, selection);
		}
	}

    private OnMessageSendListener mOnMessageSendListener;

    public void setOnMessageSendListener(OnMessageSendListener onMessageSendListener) {
        this.mOnMessageSendListener = onMessageSendListener;
    }

    private String getUserInput() {
        return etchattinginput.getText().toString().trim();
    }

    public void clearUserInput() {
        etchattinginput.setText("");
    }

    public interface OnMessageSendListener {
        public void onMsgSend(String msg);
    }

    /**
     * 隐藏表情面板
     */
    public void hideEmojiPanel() {
        flemojilist.setVisibility(View.GONE);
        btnemoji.setBackgroundResource(R.drawable.ease_chatting_biaoqing_btn_normal);
    }

    /**
     * 显示表情面板
     */
    public void showEmojiPanel() {
        flemojilist.setVisibility(View.VISIBLE);
        btnemoji.setBackgroundResource(R.drawable.ease_chatting_biaoqing_btn_enable);
    }

    /**
     * 系统返回键被按时调用此方法
     *
     * @return 返回false表示返回键时扩展菜单栏时打开状态，true则表示按返回键时扩展栏是关闭状态<br/>
     *         如果返回时打开状态状态，会先关闭扩展栏再返回值
     */
    public boolean onBackPressed() {
        if (flemojilist.getVisibility() == View.VISIBLE) {
            hideEmojiPanel();
            return true;
        } else {
            return false;
        }
    }
}
