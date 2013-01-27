package com.symbyo.islamway;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

public class SlideMenuFragment extends SherlockFragment {
	private ListView mMenuList;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		SlideMenuItems adapter = new SlideMenuItems(getActivity());
		adapter.add(new SlideMenuItem(R.string.quran, 0));
		adapter.add(new SlideMenuItem(R.string.lessons, 0));
		adapter.add(new SlideMenuItem(R.string.playing_list, 0));
		
		ListView mMenuList = (ListView) getActivity().findViewById(R.id.slidemenu_list);
		mMenuList.setAdapter(adapter);
		
		mMenuList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				SlideMenuItem item = (SlideMenuItem) parent.getAdapter()
						.getItem(position);
				String msg = String.format("selected item: %s", item.text);
				Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.slidemenu, null);
	}
	
	private class SlideMenuItem {
		public String text = null;
		public Drawable icon = null;
		
		public SlideMenuItem(int textResource, int icon_resource) {
			this.text = getResources().getString(textResource);
			if (icon_resource != 0) {
				this.icon = getResources().getDrawable(icon_resource);
			}
		}
	}
	
	public class SlideMenuItems extends ArrayAdapter<SlideMenuItem> {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext())
						.inflate(R.layout.slidemenu_row, null);
			}
			TextView title = (TextView) convertView.findViewById(R.id.title);
			title.setText(getItem(position).text);
			Drawable icon = getItem(position).icon;
			if (icon != null) {
				title.setCompoundDrawables(icon, null, null, null);
			}
			
			return convertView;
		}

		public SlideMenuItems(Context context) {
			super(context, 0);
		}
		
		
	}

	
}
