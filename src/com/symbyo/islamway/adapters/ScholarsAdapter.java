package com.symbyo.islamway.adapters;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.symbyo.islamway.R;
import com.symbyo.islamway.domain.Scholar;
import com.symbyo.islamway.persistance.Repository;
import com.symbyo.islamway.service.IWService.Section;

public class ScholarsAdapter extends BaseAdapter implements Filterable {
	private final Context mContext;
	
	/**
	 * List of scholars returned by this adapter.
	 */
	private List<Scholar> mScholars;
	
	/**
	 * when filtered, this holds the original list while mScholars will hold 
	 * only the filtered list.
	 */
	private List<Scholar> mOriginalValues;
	private ArrayFilter mFilter;
	
	private final Object mLock = new Object();
	
	private final int ITEM_LAYOUT =  R.layout.scholar_list_item;

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
	
    private class ArrayFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                synchronized (mLock) {
                    mOriginalValues = new ArrayList<Scholar>(mScholars);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                ArrayList<Scholar> list;
                synchronized (mLock) {
                    list = new ArrayList<Scholar>(mOriginalValues);
                }
                results.values = list;
                results.count = list.size();
            } else {
                String prefixString = prefix.toString();

                ArrayList<Scholar> values;
                synchronized (mLock) {
                    values = new ArrayList<Scholar>(mOriginalValues);
                }

                final int count = values.size();
                final ArrayList<Scholar> newValues = new ArrayList<Scholar>();

                for (int i = 0; i < count; i++) {
                    final Scholar scholar = values.get(i);
                    final String valueText = scholar.getName();

                    // First match against the whole, non-splitted value
                    if (valueText.startsWith(prefixString)) {
                        newValues.add(scholar);
                    } else {
                        final String[] words = valueText.split(" ");
                        final int wordCount = words.length;

                        // Start at index 0, in case valueText starts with space(s)
                        for (int k = 0; k < wordCount; k++) {
                            if (words[k].startsWith(prefixString)) {
                                newValues.add(scholar);
                                break;
                            }
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }
        
		@Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //type unchecked warning. it's safe in this case!
            mScholars = (List<Scholar>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

	@Override
	public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }
}
