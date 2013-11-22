package com.callyourmother;

import java.util.ArrayList;
import java.util.List;

import com.callyourmother.data.Circle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CircleAdapter extends ArrayAdapter<Circle> {

	private final List<Circle> mItems = new ArrayList<Circle>();
	private final Context mContext;
	int layoutResourceId;    
	Circle data[] = null;

	
    public CircleAdapter(Context context, int layoutResourceId, Circle[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        mContext = context;
        this.data = data;
    }

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Circle getItem(int pos) {
		return mItems.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public void add(Circle circle) {
		mItems.add(circle);
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout itemLayout;
		//final Circle circle = mItems.get(position);

		itemLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(
				R.layout.circle_item, parent, false);
	

		TextView nameView = (TextView) itemLayout.findViewById(R.id.circle_name);
		nameView.setText("Test contact!");
		
		return itemLayout;
	}
}
