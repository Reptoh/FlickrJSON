package com.reptoh.flickrjson;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomAdapter extends BaseAdapter {

	private ArrayList<Post> postsList;
	private LayoutInflater mInflater;

	public CustomAdapter(Context c) {
		mInflater = LayoutInflater.from(c);
	}

	public void setData(ArrayList<Post> poolList) {
		postsList = poolList;
	}

	@Override
	public int getCount() {
		return postsList.size();
	}

	@Override
	public Object getItem(int position) {
		return postsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.post, null);

			holder = new ViewHolder();

			holder.mImg = (ImageView) convertView.findViewById(R.id.image);
			holder.mTitleTxt = (TextView) convertView.findViewById(R.id.title);
			holder.mDateTxt = (TextView) convertView.findViewById(R.id.date);
			holder.mDescription = (TextView) convertView
					.findViewById(R.id.description);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Post post = postsList.get(position);

		holder.mTitleTxt.setText(post.getTitle());
		holder.mDateTxt.setText(post.getDate());
		holder.mDescription.setText(post.getDescription());

		ImageManager.grabPic(post.getMedia(), holder.mImg);

		return convertView;
	}

	static class ViewHolder {
		ImageView mImg;
		TextView mTitleTxt;
		TextView mDateTxt;
		TextView mDescription;
	}

}
