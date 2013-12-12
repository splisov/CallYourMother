package com.callyourmother;

import java.util.ArrayList;
import java.util.List;

import com.callyourmother.data.AndroidUtility;
import com.callyourmother.data.Circle;
import com.callyourmother.data.Contact;
import com.callyourmother.data.DatabaseClient;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NotificationContactAdapter extends ArrayAdapter<Contact> {

	private final List<Contact> mContacts = new ArrayList<Contact>();
	private final Context mContext;
	int layoutResourceId;
	String contactName;
	int daysSince = 0;
	private DatabaseClient db;

	public NotificationContactAdapter(Context context, int layoutResourceId) {
		super(context, layoutResourceId);
		this.layoutResourceId = layoutResourceId;
		mContext = context;
		// this.db = new DatabaseClient(context);

	}

	public boolean includes(long contact) {
		for (Contact con : mContacts) {
			if (con.getContactId() == contact)
				return true;
		}
		return false;
	}

	@Override
	public int getCount() {
		return mContacts.size();
	}

	@Override
	public Contact getItem(int pos) {
		return mContacts.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return mContacts.get(pos).getContactId();
	}

	@Override
	public void add(Contact contact) {
		mContacts.add(contact);
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout itemLayout;
		// final Circle circle = mItems.get(position);

		itemLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(
				R.layout.notification_item, parent, false);

		final TextView nameView = (TextView) itemLayout
				.findViewById(R.id.notif_contact_name_view);
		nameView.setText(getItem(position).getDisplayName());

		final TextView numberView = (TextView) itemLayout
				.findViewById(R.id.notif_contact_number_view);
		numberView.setText(getItem(position).getAllPhoneNumbers()); // ONLY GRAB
																	// FIRST
																	// CONTACT

		ImageView photoView = (ImageView) itemLayout
				.findViewById(R.id.notif_contact_image_view);

		if (getItem(position).getPhoto() == null) {
			photoView.setImageBitmap(BitmapFactory.decodeResource(
					mContext.getResources(), R.drawable.android));
		} else {
			photoView.setImageBitmap(getItem(position).getPhoto());
		}

		photoView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

				// 2. Chain together various setter methods to set the dialog
				// characteristics
				builder.setMessage(
						"Alright! Call " + nameView.getText() + " now?")
						.setTitle("Call " + nameView.getText());

				builder.setNegativeButton("Not now",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// User clicked OK button
							}
						});
				builder.setPositiveButton("Sure thing",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// User cancelled the dialog
								String uri = "tel:" + numberView.toString();
								Intent intent = new Intent(Intent.ACTION_CALL);

								intent.setData(Uri.parse(uri));
								mContext.startActivity(intent);
							}
						});

				// 3. Get the AlertDialog from create()
				AlertDialog dialog = builder.create();
				dialog.show();
			}
		});

		TextView snoozeView = (TextView) itemLayout
				.findViewById(R.id.snooze_view);

		snoozeView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 1. Instantiate an AlertDialog.Builder with its constructor
				NotificationActivity.resetNotification(mContext,
						nameView.getText(), numberView.getText());
			}
		});
		TextView daysView = (TextView) itemLayout
				.findViewById(R.id.notif_days_view);
		daysView.setText(getItem(position).getDaysSince()
				+ " days since you've called "
				+ getItem(position).getDisplayName());

		return itemLayout;
	}
}
