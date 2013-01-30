package com.symbyo.islamway.adapters;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.symbyo.islamway.R;
import com.symbyo.islamway.domain.Scholar;
import com.symbyo.islamway.persistance.Repository;
import com.symbyo.islamway.service.IWService.Section;

public class ScholarsAdapter extends BaseAdapter {
	private final Context mContext;
	private List<Scholar> mScholars;
	private final int ITEM_LAYOUT = R.layout.scholar_list_item;

	public ScholarsAdapter(@NonNull Context context, @NonNull Section section) {
		super();
		mContext = context;
		mScholars = getScholars(section);
	}

	private List<Scholar> getScholars(@NonNull Section section) {
		List<Scholar> scholars = null;
		switch (section) {
		case QURAN:
			scholars = Repository.getInstance(mContext).getQuranScholars();
			break;
		case LESSONS:
			scholars = Repository.getInstance(mContext).getLessonsScholars();
		}
		return scholars;
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
			holder.image = (ImageView) convertView
					.findViewById(R.id.scholar_image);
			holder.title = (TextView) convertView
					.findViewById(R.id.scholar_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Scholar scholar = (Scholar) getItem(position);
		holder.title.setText(scholar.getName());
		Bitmap bmp = getThumbnail(scholar.getImageFile());
		if (bmp == null) {
			Drawable placeHolder = mContext.getResources().getDrawable(
					R.drawable.scholar_placeholder);
			holder.image.setImageDrawable(placeHolder);
		} else {
			holder.image.setImageBitmap(bmp);
		}
		return convertView;
	}

	private Bitmap getThumbnail(String imageUrl) {
		Bitmap bitmap = null;
		if (imageUrl != null && !imageUrl.isEmpty()) {
			InputStream inStream = null;
			try {
				inStream = mContext.getAssets().open("scholars/" + imageUrl);
				bitmap = BitmapFactory.decodeStream(inStream);
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (inStream != null) {
						inStream.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return bitmap;
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
