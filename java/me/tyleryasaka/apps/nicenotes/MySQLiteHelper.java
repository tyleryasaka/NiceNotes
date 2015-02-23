package me.tyleryasaka.apps.nicenotes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

public class MySQLiteHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "NiceNotes";

    // Notes table name
    private static final String TABLE_NOTES = "notes";

    // Notes Table Columns names
    static final String KEY_ID = "id";
    static final String KEY_CONTENT = "content";
    static final String KEY_ACCESSED = "accessed";

    private static final String[] COLUMNS = {KEY_CONTENT,KEY_ACCESSED};

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // SQL statement to create book table
        String CREATE_NOTES_TABLE = "CREATE TABLE notes ( " +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_CONTENT + " TEXT, " +
                KEY_ACCESSED + " INT )";

        // create books table
        db.execSQL(CREATE_NOTES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        switch(newVersion) {
            case 2:
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
                this.onCreate(db);
                break;
        }
    }

    public void addNote(Note note){

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        int time = (int) (System.currentTimeMillis());
        ContentValues values = new ContentValues();
        values.put(KEY_CONTENT, note.getContent()); // get content
        values.put(KEY_ACCESSED, time);

        // 3. insert
        db.insert(TABLE_NOTES, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        //note.setId(new_id);

        // 4. close
        db.close();
    }

    public Note getNote(long id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_NOTES, // a. table
                        COLUMNS, // b. column names
                        KEY_ID +" = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build note object
        Note note = new Note();
        note.setId(Integer.parseInt(cursor.getString(0)));
        note.setContent(cursor.getString(1));//cursor.getString(1)+" time:"

        // 5. return note
        return note;
    }

    public Cursor getAllNotes() {

        // 1. build the query
        String query = "SELECT id AS _id,* FROM " + TABLE_NOTES + " ORDER BY date(" + KEY_ACCESSED + ") ASC";

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        return db.rawQuery(query, null);
    }

    public void deleteAllNotes(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NOTES);
    }

    public int updateNote(Note note) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        int time = (int) (System.currentTimeMillis());
        ContentValues values = new ContentValues();
        values.put(KEY_CONTENT, note.getContent());
        values.put(KEY_ACCESSED, time);

        // 3. updating row
        int i = db.update(TABLE_NOTES, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(note.getId()) }); //selection args

        // 4. close
        db.close();

        return i;
    }

    public void updateNoteAccessed(long id) {
        SQLiteDatabase db = this.getWritableDatabase();

        int time = (int) (System.currentTimeMillis());
        ContentValues values = new ContentValues();
        values.put(KEY_ACCESSED, time);

        int i = db.update(TABLE_NOTES, values, KEY_ID+" = ?",
                new String[] { String.valueOf(id) });
    }

    public void deleteNote(long id) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_NOTES, //table name
                KEY_ID+" = ?",  // selections
                new String[] { String.valueOf(id) }); //selections args

        // 3. close
        db.close();
    }

}