/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.hzyc.im.ui.activity;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import cn.em.sdk.bean.InviteMessage;
import cn.em.sdk.db.InviteMessgeDao;
import cn.hzyc.im.R;
import cn.hzyc.im.ui.BaseActivity;
import cn.hzyc.im.ui.adapter.InviteMsgAdapter;

/**
 * 申请与通知
 */
public class InviteMsgActivity extends BaseActivity {
	
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invite_msg);
		setTitle("申请与通知");

		listView = (ListView) findViewById(R.id.lv_new_friend_msg);
		InviteMessgeDao dao = new InviteMessgeDao(this);
		List<InviteMessage> msgs = dao.getMessagesList();
		
		//设置adapter
		InviteMsgAdapter adapter = new InviteMsgAdapter(this, 1, msgs); 
		listView.setAdapter(adapter);
		dao.saveUnreadMessageCount(0);
	}

	public void back(View view) {
		finish();
	}
	
	
}
