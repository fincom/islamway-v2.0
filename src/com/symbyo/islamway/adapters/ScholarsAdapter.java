package com.symbyo.islamway.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.symbyo.islamway.domain.Scholar;
import com.symbyo.islamway.persistance.Repository;

public class ScholarsAdapter extends BaseAdapter {
	private final Context mContext;
	private List<Scholar> mScholars;
	private final int ITEM_LAYOUT = 0;

	public ScholarsAdapter(Context context) {
		super();
		mContext = context;
		mScholars = getScholars();
	}

	private List<Scholar> getScholars() {
		return Repository.getInstance(mContext).getQuranScholars();
	}

	@Override
	public int getCount() {
		int size = 0;
		if (mScholars != null) {
			size = mScholars.size();
		}
		return size;
	}

	@Override
	public Scholar getItem(int position) {
		Scholar scholar = null;
		if (mScholars != null) {
			scholar = mScholars.get(position);
		}
		return scholar;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext)
					.inflate(ITEM_LAYOUT, null);
		}
		ViewHolder holder = null;
		if (convertView.getTag() == null 
				|| !(convertView.getTag() instanceof ViewHolder)) {
			holder = new ViewHolder();
			// TODO populate the ViewHolder
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// TODO get the Scholar
		return null;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}
	
	private static class ViewHolder {
		public TextView title;
		public ImageView image;
	}
}
