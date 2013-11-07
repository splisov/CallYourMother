package com.callyourmother.data;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.callyourmother.data.Contact.ContactNotFoundException;

public class AndroidUtility {
	/*
	 * Returns a list of all contacts from Android
	 */
	public static List<Contact> getAndroidContacts(Context context) {
		ArrayList<Contact> contacts = new ArrayList<Contact>();
		
		Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				try {
					contacts.add(new Contact(cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts._ID)), context));
				} catch(Contact.ContactNotFoundException ex) {
					ex.printStackTrace();
				}
			}
		}
		cursor.close();
		
		return contacts;
	}
	
	/*
	 * Returns a list of contacts from Android for the given list of contactIds
	 */
	public static List<Contact> getAndroidContacts(List<Long> contactIds, Contact.ContactNotFoundHandler contactNotFoundHandler, Context context) {
		ArrayList<Contact> contacts = new ArrayList<Contact>();

		for(Long contactId : contactIds) {
			try {
				contacts.add(new Contact(contactId.longValue(), context));
			} catch (ContactNotFoundException e) {
				if(contactNotFoundHandler != null) {
					contactNotFoundHandler.onContactNotFound(contactId.longValue());
				}
			}
		}
		
		return contacts;
	}
}
