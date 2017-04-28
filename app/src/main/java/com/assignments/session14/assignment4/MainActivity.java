package com.assignments.session14.assignment4;

import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/* Problem Statement: Implement a contacts content provider to delete contact based on phone number.
 *
 * MainActivity -> Display Contacts List
 * select a contact -> Intent to DeleteContactActivity
 * DeleteContactActivity
*   -> click button 'Delete Contact'
*   ==> Toast 'Contact Deleted' and goto MainActivity
* MainActivity ->  Display Updated Contacts list
* */

public class MainActivity extends AppCompatActivity {

    public static final int INTENT_REQUEST_CODE = 20;
    MatrixCursor matrixCursor;
    SimpleCursorAdapter adapter;
    ListView listContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // on launching app -> initializeViews()
        initializeViews();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // on returning back from UpdateContactActivity -> initializeViews() again
        initializeViews();
    }

    // get and Display Contacts List
    private void initializeViews() {

        // initialize matrixCursor -> make it ready -> to store contacts from CP (Content Provider)
        matrixCursor = new MatrixCursor(new String[]{"_id", "name", "mobile"});

        // setup list_view and adapter
        adapter = new SimpleCursorAdapter(getBaseContext(),
                R.layout.layout_contact_custom_list,
                null,
                new String[]{"name", "mobile"},
                new int[]{R.id.tv_name, R.id.tv_mobile}, 0);
        listContacts = (ListView) findViewById(R.id.list_contacts);
        listContacts.setAdapter(adapter);


        // select a contact
        listContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String thisName   = ( (SimpleCursorAdapter)listContacts.getAdapter() ).getCursor().getString(1);
                String thisMobile = ( (SimpleCursorAdapter)listContacts.getAdapter() ).getCursor().getString(2);

                // Intent to DeleteContactActivity
                // putExtra name and mobile to be updated
                // start activity for Result
                Intent intent = new Intent(getApplicationContext(), DeleteContactActivity.class);
                intent.putExtra("name", thisName);
                intent.putExtra("mobile", thisMobile);
                startActivityForResult(intent, INTENT_REQUEST_CODE);
            }
        });


        // Create obj to AsyncTask class to retrieve contacts/data (Note: First create LoaderAsyncTask class)
        ContactsLoader contactsListLoader = new ContactsLoader();

        // execute Async Task class
        contactsListLoader.execute();
    }

    public class ContactsLoader extends AsyncTask<Void, Void, Cursor> {
        @Override
        protected Cursor doInBackground(Void... params) {

            // query using Contacts content Uri, to retrieve all contacts and initialize Cursor
            Cursor contactsCursor = getContentResolver()
                    .query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");

            String name, mobile;

            if (contactsCursor != null && contactsCursor.moveToFirst()) {
                do {
                    long contactId = contactsCursor.getLong(contactsCursor.getColumnIndex("_ID"));

                    // query using Contact Data Uri to retrieve a Contact's details (mobile, name etc)
                    Cursor dataCursor = getContentResolver()
                            .query(ContactsContract.Data.CONTENT_URI, null, ContactsContract.Data.CONTACT_ID + "=" + contactId, null, null);


                    if (dataCursor != null && dataCursor.moveToFirst()) {
                        name = dataCursor.getString(dataCursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));

                        do {

                            if (dataCursor.getString(dataCursor.getColumnIndex("mimetype")).equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
                                if (dataCursor.getInt(dataCursor.getColumnIndex("data2")) == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                                    mobile = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                                else
                                    mobile = "";
                            } else
                                mobile = "";

                        } while (dataCursor.moveToNext());

                        String details = "" + (mobile != null ? mobile +"\n" : "");

                        // add all info to cursor row
                        matrixCursor.addRow(new Object[]{ Long.toString(contactId), name, details });
                    }

                } while (contactsCursor.moveToNext());
            }
            return matrixCursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            adapter.swapCursor(cursor);
        }
    }

}
