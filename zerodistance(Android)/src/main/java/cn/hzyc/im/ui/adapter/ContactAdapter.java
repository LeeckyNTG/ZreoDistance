package cn.hzyc.im.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import cn.em.sdk.bean.EaseUser;
import cn.hzyc.im.R;
import cn.hzyc.im.base.Const;

public class ContactAdapter extends BaseAdapter implements SectionIndexer {

	private List<EaseUser> mListDatas;
	private Context mContext;

	public ContactAdapter(Context mContext, List<EaseUser> list) {
		this.mContext = mContext;
		this.mListDatas = list;
	}

	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * 
	 * @param list
	 */
	public void updateListView(List<EaseUser> list) {
		this.mListDatas = list;
		notifyDataSetChanged();//listview数据发生变化
	}

	public int getCount() {
		return this.mListDatas.size();
	}

	public Object getItem(int position) {
		return mListDatas.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		final EaseUser user = mListDatas.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.item_contact, null);
			viewHolder.tvLetter = (TextView) view.findViewById(R.id.tv_catalog);
			viewHolder.tvTitle = (TextView) view.findViewById(R.id.tv_name);
			viewHolder.ivRedDot = (ImageView) view.findViewById(R.id.iv_red_dot);
			viewHolder.ivAvatar = (ImageView) view.findViewById(R.id.iv_icon);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		if (Const.ITEM_INVITE_AND_NOTIFICATION.equals(user.getUsername())) {
			viewHolder.ivRedDot.setVisibility(mShowRedDot ? View.VISIBLE : View.GONE);
			viewHolder.ivAvatar.setBackgroundResource(R.drawable.em_new_friends_icon);
		} else if(Const.ITEM_IM_HELPER.equals(user.getUsername())){
			viewHolder.ivAvatar.setBackgroundResource(R.drawable.ease_default_avatar);
			viewHolder.ivRedDot.setVisibility(View.GONE);
		} else {
			viewHolder.ivAvatar.setBackgroundResource(R.drawable.em_default_avatar);
			viewHolder.ivRedDot.setVisibility(View.GONE);
		}
		
		// 根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);

		// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if (position == getPositionForSection(section)) {
			viewHolder.tvLetter.setVisibility(View.VISIBLE);
			viewHolder.tvLetter.setText(user.getInitialLetter());
			if (user.getUsername().equals(Const.ITEM_INVITE_AND_NOTIFICATION)) {
				viewHolder.tvLetter.setVisibility(View.GONE);
			}
			
		} else {
			viewHolder.tvLetter.setVisibility(View.GONE);
		}






		//配置显示的用户名
		if(this.mListDatas.get(position).getNick()==null&&this.mListDatas.get(position).getNick().equals("")) {
			viewHolder.tvTitle.setText(this.mListDatas.get(position).getUsername());
		}else{
			viewHolder.tvTitle.setText(this.mListDatas.get(position).getNick());
		}

		return view;
	}

	private boolean mShowRedDot = false;
	
	public void showRedDot(boolean showRedDot) {
		this.mShowRedDot = showRedDot;
		notifyDataSetChanged();
	}
	
	class ViewHolder {
		TextView tvLetter;//右侧标号A~Z
		TextView tvTitle;//编号
		ImageView ivRedDot;//新信息
		ImageView ivAvatar;//头像
	}

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return mListDatas.get(position).getInitialLetter().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = mListDatas.get(i).getInitialLetter();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * 提取英文的首字母，非英文字母用#代替。
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		String sortStr = str.trim().substring(0, 1).toUpperCase();
		// 正则表达式，判断首字母是否是英文字母
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}
}