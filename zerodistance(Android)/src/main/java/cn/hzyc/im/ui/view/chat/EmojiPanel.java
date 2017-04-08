package cn.hzyc.im.ui.view.chat;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import cn.hzyc.im.R;
import cn.hzyc.im.base.Global;
import cn.hzyc.im.util.EmojiUtil;
import cn.hzyc.im.util.EmojiUtil.Emoji;

/**
 * 表情显示面板的实现
 * 
 * @auther jq
 * @date 2015/11/4
 */
public class EmojiPanel extends ViewPager {

	private static final int collumnCount = 8;
	private static final int rowCount = 3;

	// 为什么要减一？
	// viewpager每一页表情的最后一个是用来作删除的,
	// 点击可以删除编辑框上的输入
	public static final int pageSize = collumnCount * rowCount - 1;

	private ArrayList<ArrayList<EmojiUtil.Emoji>> mPanelDatas = new ArrayList<ArrayList<EmojiUtil.Emoji>>();

	public EmojiPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setDatas(ArrayList<EmojiUtil.Emoji> datas) {
		// 表情总个数
		int totalCount = datas.size();

		int pno = totalCount / (pageSize);
		// 总页数
		int pageCount = totalCount % (pageSize) == 0 ? pno : pno + 1;

		ArrayList<EmojiUtil.Emoji> onePageEmoji = null;
		for (int i = 0; i < totalCount; i++) {
			if (i % pageSize == 0) { // viewpager显示的每一页都对应一个集合，该集合即为:onePageEmoji
				onePageEmoji = new ArrayList<EmojiUtil.Emoji>();
				mPanelDatas.add(onePageEmoji);
			}
			onePageEmoji.add(datas.get(i));
		}
		
		// 给每一页的表情集合最后一个位置添加删除符
		for (ArrayList<EmojiUtil.Emoji> page : mPanelDatas) {
			Emoji deleteIcon = new Emoji(R.drawable.delete_emoji, "删除");
			page.add(deleteIcon);
		}

		refreshUI();
	}

	private ArrayList<GridView> mChildsView = new ArrayList<GridView>();

	private void refreshUI() {
		mChildsView = new ArrayList<GridView>();
		for (int i = 0; i < mPanelDatas.size(); i++) {
			mChildsView.add(createGridView(i, mPanelDatas.get(i)));
		}

		setAdapter(new PagerAdapter() {
			@Override
			public int getCount() {
				return mPanelDatas.size();
			}

			@Override
			public boolean isViewFromObject(View view, Object o) {
				return view == o;
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				GridView child = mChildsView.get(position);
				container.addView(child);
				return child;
			}

			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				container.removeView(mChildsView.get(position));
			}
		});
	}

	private GridView createGridView(int pageNo,
			final ArrayList<EmojiUtil.Emoji> onePageEmoji) {
		
		GridView gridView = new GridView(getContext());
		gridView.setTag(pageNo);
		gridView.setNumColumns(collumnCount);
		gridView.setVerticalSpacing(Global.dp2px(10));

		gridView.setAdapter(new ArrayAdapter<EmojiUtil.Emoji>(getContext(), 0,
				onePageEmoji) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View item = Global.inflate(R.layout.item_emoji);
				ImageView ivIcon = (ImageView) item.findViewById(R.id.iv_icon);
				// ivIcon.setBackgroundResource(getItem(position).resId);
				ivIcon.setImageResource(getItem(position).resId);
				return item;
			}
		});

		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int pos, long l) {
				
				boolean isDeleteIcon = false;// 删除图标
				if (adapterView.getCount() - 1 == pos) { 
					isDeleteIcon = true;
				} else {
					isDeleteIcon = false;
				}
				
				int pageNo = (int) adapterView.getTag();
				Emoji clickEmoji = mPanelDatas.get(pageNo).get(pos);
				
				if (mOnEmojiItemClickListener != null)
					mOnEmojiItemClickListener.onEmojiItemClick(isDeleteIcon, clickEmoji);
			}
		});
		return gridView;
	}

	private OnEmojiItemClickListener mOnEmojiItemClickListener;

	public void setOnEmojiItemClickListener(
			OnEmojiItemClickListener onEmojiItemClickListener) {
		this.mOnEmojiItemClickListener = onEmojiItemClickListener;
	}

	public interface OnEmojiItemClickListener {
		public void onEmojiItemClick(boolean isDeleteIcon, Emoji clickIcon);
	}
}
