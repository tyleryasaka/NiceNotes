package me.tyleryasaka.apps.nicenotes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.Toast;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

public class Settings extends Activity {

    private static final int DIALOG_ALERT = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button delete_all = (Button) findViewById(R.id.delete_all);
        Button email_all = (Button) findViewById(R.id.email_all);

        final Context app_context = this;

        delete_all.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                AlertDialog confirm = new AlertDialog.Builder(app_context)
                        .setTitle(R.string.delete_all)
                        .setMessage(R.string.delete_all_confirm)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                MySQLiteHelper db = new MySQLiteHelper(getApplicationContext());
                                db.deleteAllNotes();
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .create();
                confirm.show();
            }

        });

        email_all.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                MySQLiteHelper db = new MySQLiteHelper(app_context);
                Cursor cursor = db.getAllNotes();
                int notes_count = cursor.getCount();
                String all_notes = "";
                int i = 0;
                if (notes_count > 0) {
                    while (cursor.moveToNext()) {
                        String note = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.KEY_CONTENT));
                        all_notes += (note+"\n\n------------------------------\n\n");
                        i++;
                    }
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("message/rfc822");
                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_all_subject));
                    intent.putExtra(Intent.EXTRA_TEXT, all_notes);
                    startActivity(Intent.createChooser(intent, getApplicationContext().getString(R.string.email_all)));
                }
                else {
                    Toast.makeText(getApplicationContext(), "There are no notes to email.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        MySQLiteHelper db = new MySQLiteHelper(app_context);

        Spinner ordering_selector = (Spinner) findViewById(R.id.ordering_selector);
        ArrayAdapter<CharSequence> ordering_adapter = ArrayAdapter.createFromResource(this,
                R.array.ordering_options, android.R.layout.simple_spinner_item);
        ordering_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ordering_selector.setAdapter(ordering_adapter);
        int current_orderby = db.getOrderby();
        ordering_selector.setSelection(current_orderby);
        ordering_selector.setOnItemSelectedListener(new orderbySelector(this));
    }
}