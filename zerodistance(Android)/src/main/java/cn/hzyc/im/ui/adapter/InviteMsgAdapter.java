package cn.hzyc.im.ui.adapter;

import java.util.List;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.em.sdk.bean.InviteMessage;
import cn.em.sdk.bean.InviteMessage.InviteMesageStatus;
import cn.em.sdk.db.InviteMessgeDao;
import cn.hzyc.im.R;
import cn.hzyc.im.base.Global;

import com.easemob.chat.EMChatManager;

public class InviteMsgAdapter extends ArrayAdapter<InviteMessage> {

	private Context context;
	private InviteMessgeDao messgeDao;

	public InviteMsgAdapter(Context context, int textViewResourceId,
			List<InviteMessage> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		messgeDao = new InviteMessgeDao(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.item_invite_msg, null);
			holder.avator = (ImageView) convertView.findViewById(R.id.avatar);
			holder.reason = (TextView) convertView.findViewById(R.id.message);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.status = (Button) convertView.findViewById(R.id.user_state);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String str1 = context.getResources().getString(
				R.string.Has_agreed_to_your_friend_request);
		String str2 = context.getResources().getString(R.string.agree);

		String str3 = context.getResources().getString(
				R.string.Request_to_add_you_as_a_friend);
		String str4 = context.getResources().getString(
				R.string.Apply_to_the_group_of);
		String str5 = context.getResources().getString(R.string.Has_agreed_to);
		String str6 = context.getResources().getString(R.string.Has_refused_to);

		final InviteMessage msg = getItem(position);
		if (msg != null) {
			holder.reason.setText(msg.getReason());
			holder.name.setText(msg.getFrom());
			if (msg.getStatus() == InviteMesageStatus.BEAGREED) {
				holder.status.setVisibility(View.INVISIBLE);
				holder.reason.setText(str1);
			} else if (msg.getStatus() == InviteMesageStatus.BEINVITEED
					|| msg.getStatus() == InviteMesageStatus.BEAPPLYED) {
				holder.status.setVisibility(View.VISIBLE);
				holder.status.setEnabled(true);
				holder.status.setText(str2);
				// 设置点击事件
				holder.status.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 同意别人发的好友请求
						acceptInvitation(holder.status, msg);
					}
				});
			} else if (msg.getStatus() == InviteMesageStatus.AGREED) {
				holder.status.setText(str5);
				holder.status.setBackgroundColor(Color.TRANSPARENT);
				holder.status.setTextColor(Color.BLACK);
				holder.status.setEnabled(false);
			} else if (msg.getStatus() == InviteMesageStatus.REFUSED) {
				holder.status.setTextColor(Color.BLACK);
				holder.status.setText(str6);
				holder.status.setBackgroundColor(Color.TRANSPARENT);
				holder.status.setEnabled(false);
			}
			// 设置用户头像
		}
		return convertView;
	}

	/**
	 * 同意好友请求或者群申请
	 * 
	 * @param button
	 */
	private void acceptInvitation(final Button button, final InviteMessage msg) {
		final ProgressDialog pd = new ProgressDialog(context);
		String str1 = context.getResources().getString(R.string.Are_agree_with);
		final String str2 = context.getResources().getString(
				R.string.Has_agreed_to);
		final String str3 = context.getResources().getString(
				R.string.Agree_with_failure);
		pd.setMessage(str1);
		pd.setCanceledOnTouchOutside(false);
		pd.show();

		new Thread(new Runnable() {
			public void run() {
				// 调用sdk的同意方法
				try {
					EMChatManager.getInstance().acceptInvitation(msg.getFrom());
					msg.setStatus(InviteMesageStatus.AGREED);
					// 更新db
					ContentValues values = new ContentValues();
					values.put(InviteMessgeDao.COLUMN_NAME_STATUS, msg
							.getStatus().ordinal());
					messgeDao.updateMessage(msg.getId(), values);
					Global.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							pd.dismiss();
							button.setText(str2);
							button.setBackgroundDrawable(null);
							button.setEnabled(false);
						}
					});
				} catch (final Exception e) {
					Global.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							pd.dismiss();
							Toast.makeText(context, str3 + e.getMessage(), Toast.LENGTH_SHORT)
									.show();
						}
					});
				}
			}
		}).start();
	}

	private static class ViewHolder {
		ImageView avator;
		TextView name;
		TextView reason;
		Button status;
	}

}
