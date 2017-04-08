package cn.hzyc.im.ui.fragment;

import cn.hzyc.im.R;
import cn.hzyc.im.base.ImHelper;
import cn.hzyc.im.ui.BaseFragment;
import cn.hzyc.im.ui.activity.MyMusicActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainFragment3 extends BaseFragment {

	private ListView listView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		super.mRootView = inflater.inflate(R.layout.main_fragment_03, null, false);
		initView();
		return mRootView;
	}

	private void initView() {
		listView=(ListView)mRootView.findViewById(R.id.country_lvcountry);
		listView.setAdapter(new MyAdapter(getList()));

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				Intent intent=new Intent(mActivity, MyMusicActivity.class);
				startActivity(intent);
			}
		});

	}


	public List<Map<String,String>> getList(){
		List<Map<String,String>> list=new ArrayList<Map<String,String>>();

		Map<String,String> map=new HashMap<String,String>();
		map.put("name","我的音乐");
		list.add(map);

		return list;

	}


	class MyAdapter extends BaseAdapter{
		List<Map<String,String>> list;
		public MyAdapter(List<Map<String,String>> list){
			this.list=list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View view;
			if(convertView==null){
				view=LayoutInflater.from(mActivity).inflate(R.layout.item_lvcountry,null);
			}else {
				view=convertView;
			}

			ImageView img=(ImageView) view.findViewById(R.id.iv_icon);
			TextView tv=(TextView)view.findViewById(R.id.tv_name);

			img.setImageResource(R.drawable.music_logo);
			tv.setText(list.get(position).get("name"));

			return view;
		}
	}
}
