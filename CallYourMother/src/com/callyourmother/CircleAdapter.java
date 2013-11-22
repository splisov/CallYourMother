package com.callyourmother;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.callyourmother.data.Circle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CircleAdapter extends ArrayAdapter<Circle> {

	private final List<Circle> mItems = new ArrayList<Circle>();
	private final Context mContext;
	int layoutResourceId;    
	Circle data[] = null;

	/*public CircleAdapter(Context context) {
		super(context, 0);

		mContext = context;
	}*/
	
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
		nameView.setText("Test contact!");/*

		CheckBox statusView = (CheckBox) itemLayout
				.findViewById(R.id.statusCheckBox);
		statusView.setChecked(item.getStatus().equals(TodoItem.Status.DONE));
		statusView.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				item.setStatus(isChecked ? Status.DONE : Status.NOTDONE);
			}
		});

		TextView priorityView = (TextView) itemLayout
				.findViewById(R.id.priorityView);
		priorityView.setText(item.getPriority().toString());

		TextView dateView = (TextView) itemLayout.findViewById(R.id.dateView);
		dateView.setText(TodoItem.FORMAT.format(item.getDate()));

		if (item.getDate().before(new Date())
				&& item.getStatus() == Status.NOTDONE) {
			itemLayout.setBackgroundColor(Color.parseColor("#9E3C5D"));
		}

		
		ImageView imageView = (ImageView) itemLayout.findViewById(R.id.play);

		
		if (item.getFile() != ""){
			imageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
		        	Log.i("lol", "Path in Adapter is: " + item.getFile());

					TodoManagerActivity.play(mContext, item.getFile());
					
				}
			});
			
		}*/
		
		return itemLayout;
	}
}
