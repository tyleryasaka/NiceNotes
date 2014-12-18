package me.tyleryasaka.apps.nicenotes;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.widget.SimpleCursorAdapter;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.database.Cursor;

public class Notes extends Activity {

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_notes);

        MySQLiteHelper db = new MySQLiteHelper(this);

        Cursor notes_list = db.getAllNotes();

        // The desired columns to be bound
        String[] columns = new String[] {
                MySQLiteHelper.KEY_CONTENT
        };

        // the XML defined views which the data will be bound to
        int[] to = new int[] {
                R.id.list_item
        };

        // create the adapter using the cursor pointing to the desired data
        //as well as the layout information
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this, R.layout.activity_listview,
                notes_list,
                columns,
                to,
                0);

        ListView listView = (ListView) findViewById(R.id.notes_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(getApplicationContext(),Edit.class);
                intent.putExtra("id",id);
                intent.putExtra("content",((TextView) view).getText());
                intent.putExtra("is_new",false);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.notes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                Intent settings = new Intent(this, Settings.class);
                startActivity(settings);
                break;
            // action with ID action_settings was selected
            case R.id.new_note:
                Intent edit = new Intent(this, Edit.class);
                edit.putExtra("is_new",true);
                startActivity(edit);
        }
        return super.onOptionsItemSelected(item);
    }
}
