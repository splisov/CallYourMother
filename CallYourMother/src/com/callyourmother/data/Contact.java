package com.callyourmother.data;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;

public class Contact {
	private int contactId;
	private String androidContactId;
	private String displayName;
	private ArrayList<Phone> phones;
	private ArrayList<Email> emails;
	private Bitmap photo;
	
	public static List<Contact> getPhoneContacts(Context context) {
		ArrayList<Contact> contacts = new ArrayList<Contact>();
		
		Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null); 
		while (cursor.moveToNext()) { 
		   contacts.add(new Contact(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)), context));
		}
		cursor.close(); 
		
		return contacts;
	}
	
	public Contact(String androidContactId, Context context) {
		
		boolean hasPhoneNumber = false;
		Uri photoUri = null;
		
		//get general info
		Cursor contact = context.getContentResolver().query( ContactsContract.Contacts.CONTENT_URI, null, ContactsContract.Contacts._ID +" = "+ androidContactId, null, null);
		while(contact.moveToFirst()) {
			displayName = contact.getString(contact.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			hasPhoneNumber = Boolean.parseBoolean(contact.getString(contact.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
			photoUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(androidContactId));
		}
		
		//get phones
		this.phones = new ArrayList<Phone>();
		if(hasPhoneNumber) {
			Cursor phones = context.getContentResolver().query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ androidContactId, null, null); 
			while (phones.moveToNext()) {
				  this.phones.add(new Phone(phones));
			} 
			phones.close();
		}
		
		//get emails
		Cursor emails = context.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null); 
		this.emails = new ArrayList<Email>();
		while (emails.moveToNext()) { 
			this.emails.add(new Email(emails));
		} 
		emails.close();
		
		//get photo
		if (photoUri != null) {
	        InputStream photoStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), photoUri);
	        if (photoStream != null) {
	            photo = BitmapFactory.decodeStream(photoStream);
	        }
	    }
	}

	public int getContactId() {
		return contactId;
	}
	public String getAndroidContactId() {
		return androidContactId;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public List<Phone> getPhones() {
		return phones;
	}
	
	public List<Email> getEmails() {
		return emails;
	}
	
	public Bitmap getPhoto() {
		return photo;
	}
	
	public class Phone {
		private String number;
		//type is a constant of type android.provider.ContactsContract.CommonDataKinds.Phone
		private int type;
		private String customLabel;
		
		public Phone(Cursor cursor) {
	         number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
	         type = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
	         if(type == CommonDataKinds.Phone.TYPE_CUSTOM) {
	        	 customLabel = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL));
	         } else {
	        	 customLabel = null;
	         }
		}
		
		public int getType() {
			return type;
		}
		
		public String getNumber() {
			return number;
		}
		
		public String getLabel() {
			switch(type) {
			case CommonDataKinds.Phone.TYPE_ASSISTANT:
				return "ASSISTANT";
			case CommonDataKinds.Phone.TYPE_CALLBACK:
				return "CALLBACK";
			case CommonDataKinds.Phone.TYPE_CAR:
				return "CAR";
			case CommonDataKinds.Phone.TYPE_COMPANY_MAIN:
				return "COMPANY_MAIN";
			case CommonDataKinds.Phone.TYPE_FAX_HOME:
				return "FAX_HOME";
			case CommonDataKinds.Phone.TYPE_FAX_WORK:
				return "FAX_WORK";
			case CommonDataKinds.Phone.TYPE_HOME:
				return "HOME";
			case CommonDataKinds.Phone.TYPE_ISDN:
				return "ISDN";
			case CommonDataKinds.Phone.TYPE_MAIN:
				return "MAIN";
			case CommonDataKinds.Phone.TYPE_MMS:
				return "MMS";
			case CommonDataKinds.Phone.TYPE_MOBILE:
				return "MOBILE";
			case CommonDataKinds.Phone.TYPE_OTHER:
				return "OTHER";
			case CommonDataKinds.Phone.TYPE_OTHER_FAX:
				return "OTHER_FAX";
			case CommonDataKinds.Phone.TYPE_PAGER:
				return "PAGER";
			case CommonDataKinds.Phone.TYPE_RADIO:
				return "RADIO";
			case CommonDataKinds.Phone.TYPE_TELEX:
				return "TELEX";
			case CommonDataKinds.Phone.TYPE_TTY_TDD:
				return "TTY_TDD";
			case CommonDataKinds.Phone.TYPE_WORK:
				return "WORK";
			case CommonDataKinds.Phone.TYPE_WORK_MOBILE:
				return "WORK_MOBILE";
			case CommonDataKinds.Phone.TYPE_WORK_PAGER:
				return "WORK_PAGER";
			case CommonDataKinds.Phone.TYPE_CUSTOM:
			default:
				return customLabel;
			}
		}
	}

	public class Email {
		private String address;
		private int type;
		private String customLabel;
		
		public Email(Cursor cursor) {
			this.address = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
			this.type = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
			if(this.type == CommonDataKinds.Email.TYPE_CUSTOM) {
				this.customLabel = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.LABEL));
			} else {
				this.customLabel = null;
			}
			
		}
		
		public int getType() {
			return type;
		}
		
		public String getAddress() {
			return address;
		}
		
		public String getLabel() {
			switch(type) {
			case CommonDataKinds.Email.TYPE_HOME:
				return "HOME";
			case CommonDataKinds.Email.TYPE_MOBILE:
				return "MOBILE";
			case CommonDataKinds.Email.TYPE_OTHER:
				return "OTHER";
			case CommonDataKinds.Email.TYPE_WORK:
				return "WORK";
			case CommonDataKinds.Email.TYPE_CUSTOM:
			default:
				return customLabel;
			}
		}
	}
}


