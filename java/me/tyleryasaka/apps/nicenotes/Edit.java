package me.tyleryasaka.apps.nicenotes;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

public class Edit extends Activity {

    private long id;
    private boolean discard_note = false;//flag to turn on when the user clicks "discard note"
    private boolean is_new;
    private boolean saved_note = false;//to prevent weird glitch where sometimes note saves multiple times

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        EditText note_text = (EditText) findViewById(R.id.edit_note);
        Bundle b = getIntent().getExtras();

        is_new = b.getBoolean("is_new");

        //If this is a new note
        if(is_new){
            setTitle(R.string.new_note);
            note_text.requestFocus();
        }

        //If this is editing a previous note
        else {
            id = b.getLong("id");
            String content = b.getString("content");

            note_text.setText(content);

            //Update note to automatically update last accessed time in database
            MySQLiteHelper db = new MySQLiteHelper(this);
            db.updateNoteAccessed(id);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
            case R.id.delete:

                new AlertDialog.Builder(this)
                    .setTitle(R.string.delete_note)
                    .setMessage(R.string.delete_note_confirm)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            MySQLiteHelper db = new MySQLiteHelper(getApplicationContext());
                            db.deleteNote(id);
                            Toast.makeText(getApplicationContext(),R.string.toast_deleted,Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .show();
                break;
            case R.id.email:
                EditText note_text = (EditText)findViewById(R.id.edit_note);

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_note_subject));
                intent.putExtra(Intent.EXTRA_TEXT   , note_text.getText());
                startActivity(Intent.createChooser(intent, getApplicationContext().getString(R.string.email_note)));
                break;
            case R.id.save:
                saveNote();
                finish();
                break;
            case R.id.discard:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.discard_note)
                        .setMessage(R.string.discard_note_confirm)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                discard_note = true;
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause(){
        super.onPause();

        EditText note_text = (EditText)findViewById(R.id.edit_note);

        //if the user clicked "discard" or note is empty, will not save
        if(!discard_note && note_text.getText().length() > 0) saveNote();
    }

    public void saveNote() {
        EditText note_text = (EditText)findViewById(R.id.edit_note);
        MySQLiteHelper db = new MySQLiteHelper(this);

        Note note = new Note();
        note.setContent(note_text.getText().toString());

        /*if(is_new && saved_note) {
            //wrote this code for debugging. Finally managed to prevent the weird duplicate save errors.
            Note gotcha = new Note();
            gotcha.setContent("Gotcha!! :P");
            db.addNote(gotcha);
        }*/

        //Add the note if it's a new note
        if(is_new && !saved_note){
            db.addNote(note);
            saved_note = true;
        }

        //Otherwise update the previous note
        else {
            note.setId(id);
            db.updateNote(note);
        }

        Toast.makeText(getApplicationContext(),R.string.toast_saved,Toast.LENGTH_SHORT).show();
    }

}
