package com.assignments.session14.assignment4;

import android.content.ContentProviderOperation;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DeleteContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_contact);

        // initialise Activity elements/views
        TextView tvOldName = (TextView) findViewById(R.id.tv_old_name);
        TextView tvOldMobile = (TextView) findViewById(R.id.tv_old_mobile);
        Button btnDeleteContact = (Button) findViewById(R.id.bt_delete_contact);

        // set text/values
        final String name = getIntent().getStringExtra("name");
        final String mobile = getIntent().getStringExtra("mobile");
        tvOldName.setText(name);
        tvOldMobile.setText(mobile);

        // set delete contact functionality
        if (btnDeleteContact != null) {
            btnDeleteContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // set selection string
                    String selection = ContactsContract.Data.DISPLAY_NAME + " = ? ";

                    // set selectionArgs array
                    String[] selectionArgs = new String[]{name};

                    // update Mobile
                    ArrayList<ContentProviderOperation> cpoList = new ArrayList< ContentProviderOperation > ();
                    cpoList.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI)
                            .withSelection(selection, selectionArgs)
                            .build());

                    // execute
                    try {
                        getContentResolver().applyBatch(ContactsContract.AUTHORITY, cpoList);
                        Toast.makeText(getApplicationContext(), "Contact Deleted", Toast.LENGTH_SHORT).show();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    } catch (OperationApplicationException e) {
                        e.printStackTrace();
                    }

                    finish();

                }
            });
        }

    }
}
