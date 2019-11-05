package com.example.contentproviderdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    private boolean firstTimeLoaded = false;
    private ProgressBar progressBar;
    private TextView textView;
    private static final String TAG = "MainActivity";
    private String[] columnProjectino = {
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.Contacts.CONTACT_STATUS,
            ContactsContract.Contacts.HAS_PHONE_NUMBER,
            ContactsContract.Contacts.NAME_RAW_CONTACT_ID
    };
    private String mSelectionClause = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " = 'Harshu'";
    private String mSelectionArgument[] = {"Harshu"};
    private String orderBy = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        findViewById(R.id.btnContacts).setOnClickListener(this);
        progressBar = findViewById(R.id.progresBar);

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        if (id==1){
            progressBar.setVisibility(View.VISIBLE);
            return new CursorLoader(this, ContactsContract.Contacts.CONTENT_URI,columnProjectino,null,null,orderBy);
        }

        if (id==2){

        }
        return null;
    }
    String phoneNumber ;

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            while (cursor.moveToNext()) {
                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.NAME_RAW_CONTACT_ID));

                String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
//
                if (Integer.parseInt(hasPhone)>0) {

                // You know it has a number so now query it like this
                Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                if (phones != null) {
                    while (phones.moveToNext()) {
                        phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                }
                phones.close();


                stringBuilder.append(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))).append("\n").append(hasPhone).append("\n\n");

                } else {
                    textView.setText("No contacts saved.");
                }

            }
            progressBar.setVisibility(GONE);
            textView.setText(stringBuilder.toString());
        } else {
            progressBar.setVisibility(GONE);
            textView.setText("No contacts saved.");
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnContacts:
                if (!firstTimeLoaded) {
                    getSupportLoaderManager().initLoader(1, null, this);
                    firstTimeLoaded = true;
                }else{
                    getSupportLoaderManager().restartLoader(1,null,this);
                }

                break;
        }
    }
}
