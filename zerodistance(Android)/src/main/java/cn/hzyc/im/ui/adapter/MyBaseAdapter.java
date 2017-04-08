package cn.hzyc.im.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.hzyc.im.ui.holder.BaseHolder;

/**
 * 数据适配器的基类
 */
public abstract class MyBaseAdapter<T> extends BaseAdapter {

    protected List<T> mListDatas = new ArrayList<T>();

    public MyBaseAdapter(ArrayList<T> list) {
        setDatas(list);
    }

    @Override
    public int getCount() {
        return mListDatas == null ? 0 : mListDatas.size() ;
    }

    @Override
    public T getItem(int position) {
        return mListDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setDatas(List<T> list) {
        synchronized (this.mListDatas) {
            this.mListDatas.clear();
            this.mListDatas.addAll(list);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseHolder holder = null;
        if (convertView == null) {
            // 在初始化holder的同时,已经对布局进行了加载,也给view设置了tag
            holder = getHolder(position);
        } else {
            holder = (BaseHolder) convertView.getTag();
        }

        // 刷新界面,更新数据
        holder.setData(getItem(position));
        return holder.getRootView();
    }

    // 返回BaseHolder的子类,必须实现
    public abstract BaseHolder<T> getHolder(int position);

}
