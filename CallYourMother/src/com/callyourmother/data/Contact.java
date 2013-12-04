package com.callyourmother.data;

import java.io.ByteArrayInputStream;
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
import android.provider.ContactsContract.Contacts;


public class Contact {
	private long contactId;
	private String displayName;
	private ArrayList<Phone> phones = new ArrayList<Phone>();
	private ArrayList<Email> emails = new ArrayList<Email>();
	private ArrayList<NotificationRule> notificationRules = new ArrayList<NotificationRule>();
	private Bitmap photo;

	public interface ContactNotFoundHandler {
		public void onContactNotFound(long contactId);
	}
	
	/*
	 * Creates a dummy contact with sample data
	 */
	public static Contact createDummyContact() {
		Contact c = new Contact();
		c.contactId = 0;
		c.displayName = "Dummy Contact";
		Email email = c.new Email();
		email.address = "dummy@email.com";
		email.type = CommonDataKinds.Email.TYPE_HOME;
		c.emails.add(email);
		Phone phone = c.new Phone();
		phone.number = "5555555555";
		phone.type = CommonDataKinds.Phone.TYPE_MOBILE;
		c.phones.add(phone);
		return c;
	}
	
	private static final String[] CONTACT_PROJECTION = new String[] {
		ContactsContract.Contacts.DISPLAY_NAME,
		ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
	};
	
	private Contact() {}
	
	public Contact(long contactId, Context context) throws ContactNotFoundException {
		
		this.contactId = contactId;
		
		Uri photoUri = null;
		
		//get general info
		
		Cursor contact = context.getContentResolver().query                                     //This query now working
				(android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI, CONTACT_PROJECTION, 
						android.provider.ContactsContract.CommonDataKinds.Phone._ID + "=?", 
						new String[]{String.valueOf(contactId)}, null);
        
		
		//Cursor contact = context.getContentResolver().query( ContactsContract.Contacts.CONTENT_URI, null, ContactsContract.Contacts._ID +" = ?", new String[] { String.valueOf(contactId) }, null);
		if(contact.moveToFirst()) {
			displayName = contact.getString(contact.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			if(!contact.isNull(contact.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI))) {
				photoUri = Uri.parse(contact.getString(contact.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI)));
			}
			contact.close();
			
			//get phones
			Cursor phones = context.getContentResolver().query( Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, String.valueOf(contactId)), null, null, null, null);
			while (phones.moveToNext()) {
				  this.phones.add(new Phone(phones));
			} 
			phones.close();
			
			//get emails
			Cursor emails = context.getContentResolver().query( Uri.withAppendedPath(ContactsContract.CommonDataKinds.Email.CONTENT_URI, String.valueOf(contactId)), null, null, null, null);
			while (emails.moveToNext()) { 
				this.emails.add(new Email(emails));
			}
			emails.close();
			
			//get photo
			if (photoUri != null) {
				Cursor photoCursor = context.getContentResolver().query(photoUri, new String[] { Contacts.Photo.PHOTO }, null, null, null);
				if(photoCursor != null && photoCursor.moveToFirst()) {
					byte[] photoData = photoCursor.getBlob(0);
					if(photoData != null) {
						photo = BitmapFactory.decodeStream(new ByteArrayInputStream(photoData));
					}
				}
				photoCursor.close();
		    }
		} else {
			contact.close();
			throw new ContactNotFoundException(contactId);
		}
	}
	
	public String getAllPhoneNumbers() {
		StringBuilder sb = new StringBuilder();
		int count = 0;
		String newline = System.getProperty("line.separator");
		for(Phone p : this.phones) {
			if(count > 0) {
				sb.append(newline);
			}
			sb.append(p.getNumber()+" ("+p.getLabel()+")");
			count++;
		}
		return sb.toString();
	}

	public String getAllEmailsNumbers() {
		StringBuilder sb = new StringBuilder();
		int count = 0;
		String newline = System.getProperty("line.separator");
		for(Email e : this.emails) {
			if(count > 0) {
				sb.append(newline);
			}
			sb.append(e.getAddress()+" ("+e.getLabel()+")");
			count++;
		}
		return sb.toString();
	}	
	public class ContactNotFoundException extends Exception {

		private static final long serialVersionUID = 1L;
		private final long contactId;
		
		public ContactNotFoundException(long contactId) {
			this.contactId = contactId;
		}
		
		public long getcontactId() {
			return contactId;
		}
	}

	public long getContactId() {
		return contactId;
	}
	
	/*
	 * returns a list of notification rules assigned specifically to this contact
	 */
	public List<NotificationRule> getNotificationRules() {
		ArrayList<NotificationRule> list = new ArrayList<NotificationRule>();
		list.addAll(notificationRules);
		return list;
	}
	
	public boolean hasNotificationRules() {
		return notificationRules.size() > 0;
	}
	
	/*
	 * adds a notification rule for this specific contact
	 */
	public boolean addNotificationRule(NotificationRule notificationRule) {
		if(!notificationRules.contains(notificationRule)) {
			return notificationRules.add(notificationRule);
		} else {
			return false;
		}
	}

	/*
	 * removes a notification rule assigned specifically to this contact
	 */
	public boolean removeNotificationRule(NotificationRule notificationRule) {
		return notificationRules.remove(notificationRule);
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
		
		private Phone() {}
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
				return "Assistant";
			case CommonDataKinds.Phone.TYPE_CALLBACK:
				return "Callback";
			case CommonDataKinds.Phone.TYPE_CAR:
				return "Car";
			case CommonDataKinds.Phone.TYPE_COMPANY_MAIN:
				return "Company Main";
			case CommonDataKinds.Phone.TYPE_FAX_HOME:
				return "Fax Home";
			case CommonDataKinds.Phone.TYPE_FAX_WORK:
				return "Fax Work";
			case CommonDataKinds.Phone.TYPE_HOME:
				return "Home";
			case CommonDataKinds.Phone.TYPE_ISDN:
				return "ISDN";
			case CommonDataKinds.Phone.TYPE_MAIN:
				return "Main";
			case CommonDataKinds.Phone.TYPE_MMS:
				return "MMS";
			case CommonDataKinds.Phone.TYPE_MOBILE:
				return "Mobile";
			case CommonDataKinds.Phone.TYPE_OTHER:
				return "Other";
			case CommonDataKinds.Phone.TYPE_OTHER_FAX:
				return "Other Fax";
			case CommonDataKinds.Phone.TYPE_PAGER:
				return "Pager";
			case CommonDataKinds.Phone.TYPE_RADIO:
				return "Radio";
			case CommonDataKinds.Phone.TYPE_TELEX:
				return "TELEX";
			case CommonDataKinds.Phone.TYPE_TTY_TDD:
				return "TTY-TDD";
			case CommonDataKinds.Phone.TYPE_WORK:
				return "Work";
			case CommonDataKinds.Phone.TYPE_WORK_MOBILE:
				return "Work Mobile";
			case CommonDataKinds.Phone.TYPE_WORK_PAGER:
				return "Work Pager";
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
		
		private Email() {}

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
				return "Home";
			case CommonDataKinds.Email.TYPE_MOBILE:
				return "Mobile";
			case CommonDataKinds.Email.TYPE_OTHER:
				return "Other";
			case CommonDataKinds.Email.TYPE_WORK:
				return "Work";
			case CommonDataKinds.Email.TYPE_CUSTOM:
			default:
				return customLabel;
			}
		}
	}
}