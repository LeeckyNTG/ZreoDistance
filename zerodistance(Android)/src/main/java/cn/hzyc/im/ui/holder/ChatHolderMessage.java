package cn.hzyc.im.ui.holder;

import java.util.Date;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.hzyc.im.R;
import cn.hzyc.im.base.Global;
import cn.hzyc.im.util.EmojiUtil;

import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.EMMessage.Type;
import com.easemob.util.DateUtils;

public class ChatHolderMessage extends ChatBaseHolder<EMMessage>{

	private TextView timestamp;
	private ImageView ivuserhead;
	private TextView tvchatcontent;
	private RelativeLayout bubble;
	private ImageView msgstatus;
	private TextView tvack;
	private TextView tvdelivered;
	private ProgressBar progressbar;

	public ChatHolderMessage(EMMessage.Direct direct) {
		super(direct);
	}

	@Override
	public View initView() {
		int layoutResId = direct == EMMessage.Direct.RECEIVE
				? R.layout.item_received_message
				: R.layout.item_sent_message;

		View root = View.inflate(Global.mContext, layoutResId, null);

		this.progressbar = (ProgressBar) root.findViewById(R.id.progress_bar);
		this.tvdelivered = (TextView) root.findViewById(R.id.tv_delivered);
		this.tvack = (TextView) root.findViewById(R.id.tv_ack);
		this.msgstatus = (ImageView) root.findViewById(R.id.msg_status);
		this.bubble = (RelativeLayout) root.findViewById(R.id.bubble);
		this.tvchatcontent = (TextView) root.findViewById(R.id.tv_chatcontent);
		this.ivuserhead = (ImageView) root.findViewById(R.id.iv_userhead);
		this.timestamp = (TextView) root.findViewById(R.id.timestamp);
		return root;
	}
	

	@Override
	public void refreshView(Object data) {
		final EMMessage message = (EMMessage)data;
		this.tvchatcontent.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (message.getType() == Type.TXT) {
					TextMessageBody txtBody = (TextMessageBody) message.getBody();
					String text= txtBody.getMessage();
					Toast.makeText(Global.mContext, "" + text, Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(Global.mContext, "" + message.getMsgTime(), Toast.LENGTH_SHORT).show();
				}
			}
		});

		timestamp.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
		timestamp.setVisibility(View.VISIBLE);

		TextMessageBody txtBody = (TextMessageBody) message.getBody();
		if (txtBody != null)
			EmojiUtil.setTextWithEmoji(Global.mContext,this.tvchatcontent, txtBody.getMessage());

		if (this.progressbar != null)
			this.progressbar.setVisibility(View.GONE);
	}
}