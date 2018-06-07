package com.robj.deviceutils;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.text.TextUtils;
import android.util.Log;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by Rob J on 05/08/15.
 */
public class ContactUtils {

    private static final String TAG = ContactUtils.class.getSimpleName();

    public static Contact getContact(Context context, String[] people) {
        Contact contact = null;
        if (people != null && people.length == 1) { //TODO: Don't know how to handle multiple??
            int size = people.length;
            if (PermissionsUtil.hasPermission(context, Manifest.permission.READ_CONTACTS)) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < size; i++) {
                    sb.append(people[i]);
                    sb.append(", ");
                    sb.append(ContactUtils.getContactByUri(context, people[i]));
                    contact = ContactUtils.getContactByUri(context, people[i]);
                    if(contact != null)
                        Log.d(ContactUtils.class.getSimpleName(), "People: " + contact.displayName);
                }
            }
        }
        return contact;
    }

    public static Contact getContact(Context context, String number) {
        Contact contact = new Contact(-1, number);
        if(!TextUtils.isEmpty(number)) {
            if(PermissionsUtil.hasPermission(context, Manifest.permission.READ_CONTACTS)) {
                Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
                Cursor c = context.getContentResolver().query(uri,
                        new String[]{PhoneLookup.LOOKUP_KEY, PhoneLookup.DISPLAY_NAME, PhoneLookup._ID},
                        null, null, null);
                if(c != null) {
                    if (c.moveToFirst()) {
                        long id = c.getLong(c.getColumnIndexOrThrow(PhoneLookup._ID));
                        String displayName = c.getString(c.getColumnIndexOrThrow(PhoneLookup.DISPLAY_NAME));
                        contact = new Contact(id, displayName, getPhoneNumbers(context, id));
                    }
                    c.close();
                }
            } else
                Log.d(TAG, "Couldn't retrieve contact, permission READ_CONTACTS not granted..");
        }

        return contact;
    }

    public static String getNameFromNumber(Context context, String number) {
        String name = null;
        if(!TextUtils.isEmpty(number)) {
            if(PermissionsUtil.hasPermission(context, Manifest.permission.READ_CONTACTS)) {
                Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
                Cursor c = context.getContentResolver().query(uri,
                        new String[]{PhoneLookup.LOOKUP_KEY, PhoneLookup.DISPLAY_NAME, PhoneLookup._ID},
                        null, null, null);
                if(c != null) {
                    if (c.moveToFirst())
                        name = c.getString(c.getColumnIndexOrThrow(PhoneLookup.DISPLAY_NAME));
                    c.close();
                }
            } else
                Log.d(TAG, "Couldn't retrieve contact, permission READ_CONTACTS not granted..");
        }
        return name;
    }

    public static Contact getContactByUri(Context context, String uri) {
        Contact contact = null;
        String[] projection = new String[] {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.NAME_RAW_CONTACT_ID
        };
        Cursor cursor = context.getContentResolver().query (
                Uri.parse(uri),
                projection,
                null,
                null,
                null);
        if(cursor != null) {
            int nameColIndex = cursor.getColumnIndexOrThrow(PhoneLookup.DISPLAY_NAME);
            int idColIndex = cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID);
            if (cursor.moveToNext()) {
                long contactId = cursor.getLong(idColIndex);
                String displayName = cursor.getString(nameColIndex);
                contact = new Contact(contactId, displayName, getPhoneNumbers(context, contactId));
            }
            cursor.close();
        }
        return contact;
    }


    public static Contact getContactById(Context context, long id) {
        Contact contact = null;
        if(id > -1) {
            if (PermissionsUtil.hasPermission(context, Manifest.permission.READ_CONTACTS)) {
                Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                Cursor c = context.getContentResolver().query(uri,
                        new String[]{ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME},
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{String.valueOf(id)}, null);
                if (c != null) {
                    int idColIndex = c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
                    int nameColIndex = c.getColumnIndexOrThrow(PhoneLookup.DISPLAY_NAME);
                    while (c.moveToNext()) {
                        long contactId = c.getLong(idColIndex);
                        String displayName = c.getString(nameColIndex);
                        contact = new Contact(contactId, displayName, getPhoneNumbers(context, contactId));
                    }
                    c.close();
                }
            } else
                Log.d(TAG, "Couldn't retrieve contact, permission READ_CONTACTS not granted..");
        }
        return contact;
    }

    public static String getNameById(Context context, long id) {
        String name = null;
        if(id > -1) {
            if (PermissionsUtil.hasPermission(context, Manifest.permission.READ_CONTACTS)) {
                Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                Cursor c = context.getContentResolver().query(uri,
                        new String[]{ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME},
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{String.valueOf(id)}, null);
                if (c != null) {
                    int nameColIndex = c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME);
                    while (c.moveToNext())
                        name = c.getString(nameColIndex);
                    c.close();
                }
            } else
                Log.d(TAG, "Couldn't retrieve contact, permission READ_CONTACTS not granted..");
        }
        return name;
    }
    
    public static Observable<List<Contact>> getContactsObservable(Context context, boolean withNumbers) {
        return Observable.create((ObservableOnSubscribe<List<Contact>>) e -> {
            try {
                if(!PermissionsUtil.hasPermission(context, Manifest.permission.READ_CONTACTS))
                    throw new PermissionsUtil.PermissionException(Manifest.permission.READ_CONTACTS);
                List<Contact> contacts = new ArrayList();
                Cursor cur = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID, PhoneLookup.DISPLAY_NAME},
                        null, null, PhoneLookup.DISPLAY_NAME);
                if (cur != null) {
                    try {
                        int idColIndex = cur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
                        int nameColIndex = cur.getColumnIndexOrThrow(PhoneLookup.DISPLAY_NAME);
                        while (cur.moveToNext()) {
                            long contactId = cur.getLong(idColIndex);
                            String displayName = cur.getString(nameColIndex);
                            Contact tmp = withNumbers ? new Contact(contactId, displayName, getPhoneNumbers(context, contactId)) : new Contact(contactId, displayName);
                            contacts.add(tmp);
                        }
                    } finally {
                        cur.close();
                    }
                }
                e.onNext(contacts);
            } catch (Exception ex) {
                ex.printStackTrace();
                e.onError(ex);
            }
        }).subscribeOn(Schedulers.io());
    }

    public static Contact getContactByName(Context context, String name) {
        Contact contact = null;
        if(!TextUtils.isEmpty(name)) {
            if (PermissionsUtil.hasPermission(context, Manifest.permission.READ_CONTACTS)) {
                Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                Cursor c = context.getContentResolver().query(uri, new String[]{ ContactsContract.CommonDataKinds.Phone.CONTACT_ID, PhoneLookup.DISPLAY_NAME },
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = ?", new String[]{ name }, null);
                if (c != null) {
                    int idColIndex = c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
                    int nameColIndex = c.getColumnIndexOrThrow(PhoneLookup.DISPLAY_NAME);
                    while (c.moveToNext()) {
                        long contactId = c.getLong(idColIndex);
                        String displayName = c.getString(nameColIndex);
                        contact = new Contact(contactId, displayName, getPhoneNumbers(context, contactId));
                    }
                    c.close();
                }
            } else
                Log.d(TAG, "Couldn't retrieve contact, permission READ_CONTACTS not granted..");
        }
        return contact;
    }

    public static List<Number> getPhoneNumbers(Context context, long contactId) {
        Cursor c = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
        List<Number> numbers = new ArrayList();
        if (c != null) {
            int numColIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int typeColIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
            while (c.moveToNext()) {
                String number = c.getString(numColIndex);
                int type = c.getInt(typeColIndex);
                numbers.add(new Number(number, type));
            }
            c.close();
        }
        return numbers;
    }

    public static Bitmap getAvatar(Context context, long contactId, boolean preferHigherRes) {
        Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), uri, preferHigherRes);
        if (input != null)
            return ImageUtils.decodeStream(input);
        return null;
    }

    public static class Contact {
        public final long contactId;
        public final String displayName;
        public final List<Number> numbers = new ArrayList();
        public Contact(long contactId, String displayName) {
            this.contactId = contactId;
            this.displayName = displayName;
        }
        public Contact(long contactId, String displayName, List<Number> numbers) {
            this.contactId = contactId;
            this.displayName = displayName;
            this.numbers.addAll(numbers);
        }
    }
    public static class Number {
        public final String number;
        public final int type;
        public Number(String number, int type) {
            this.number = number;
            this.type = type;
        }
    }

}
