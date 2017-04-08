package cn.hzyc.im.ui;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;

public class BaseFragment extends Fragment {
	
	public View mRootView;
	public BaseActivity mActivity;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = (BaseActivity) getActivity();
	}

	public void showToast(String mess) {
		mActivity.showToast(mess);
	}
}
