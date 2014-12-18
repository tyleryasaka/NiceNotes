package me.tyleryasaka.apps.nicenotes;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.content.DialogInterface;
import android.content.Intent;

public class Edit extends Activity {

    private long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        EditText note_text = (EditText) findViewById(R.id.edit_note);
        Bundle b = getIntent().getExtras();

        Boolean is_new = b.getBoolean("is_new");

        if(is_new){
            setTitle(R.string.new_note);
            note_text.requestFocus();

            Note new_note = new Note("");
            MySQLiteHelper db = new MySQLiteHelper(this);
            db.addNote(new_note);
            id = new_note.getId();
        }

        else {
            id = b.getLong("id");
            String content = b.getString("content");

            note_text.setText(content);
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
        switch(item.getItemId()){
            case R.id.delete:

                new AlertDialog.Builder(this)
                    .setTitle(R.string.delete_note)
                    .setMessage(R.string.delete_note_confirm)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            MySQLiteHelper db = new MySQLiteHelper(getApplicationContext());
                            db.deleteNote(id);
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause(){
        EditText note_text = (EditText)findViewById(R.id.edit_note);
        MySQLiteHelper db = new MySQLiteHelper(this);

        if(note_text.getText().toString().trim().length() != 0) {
            Note note = new Note();
            note.setId(id);
            note.setContent(note_text.getText().toString());
            db.updateNote(note);
        }

        else{
            db.deleteNote(id);
        }

        super.onPause();
    }

}
