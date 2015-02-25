package me.tyleryasaka.apps.nicenotes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

public class MySQLiteHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 2;
    // Database Name
    private static final String DATABASE_NAME = "NiceNotes";

    // table names
    private static final String TABLE_NOTES = "notes";
    private static final String TABLE_PREFERENCES = "preferences";

    // Notes Table Columns names
    static final String KEY_ID = "id";
    static final String KEY_CONTENT = "content";
    static final String KEY_CREATED = "created";
    static final String KEY_ACCESSED = "accessed";

    // Preferences Table Columns names
    static final String KEY_ORDERBY = "orderby";

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
                KEY_ACCESSED + " INT, " +
                KEY_CREATED + " INT)";

        String CREATE_PREFERENCES_TABLE = "CREATE TABLE preferences ( " +
                KEY_ORDERBY + " INT DEFAULT 0)";

        // create books table
        db.execSQL(CREATE_NOTES_TABLE);
        db.execSQL(CREATE_PREFERENCES_TABLE);

        ContentValues preference_values = new ContentValues();
        preference_values.put(KEY_ORDERBY,0);

        db.insert(TABLE_PREFERENCES, null, preference_values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        int upgradeTo = oldVersion + 1;
        while(upgradeTo <= newVersion) {
            switch (upgradeTo) {
                case 2:
                    String CREATE_PREFERENCES_TABLE = "CREATE TABLE preferences ( " + KEY_ORDERBY + " INT DEFAULT 0)";
                    String ALTER_NOTES_TABLE = "ALTER TABLE " + TABLE_NOTES + " ADD COLUMN " + KEY_CREATED + " INT";

                    db.execSQL(CREATE_PREFERENCES_TABLE);
                    db.execSQL(ALTER_NOTES_TABLE);

                    //Set the "created" value of all notes to present;
                    //won't be correct, but probably better than null
                    int time = (int) (System.currentTimeMillis());
                    ContentValues values = new ContentValues();
                    values.put(KEY_CREATED, time);
                    db.update(TABLE_NOTES, values, "1", null);

                    //Add default preference
                    ContentValues preference_values = new ContentValues();
                    preference_values.put(KEY_ORDERBY,0);
                    db.insert(TABLE_PREFERENCES, null, preference_values);
                    break;
            }
            upgradeTo++;
        }
    }

    public void addNote(Note note){

        //get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        //create ContentValues to add key "column"/value
        int time = (int) (System.currentTimeMillis());
        ContentValues values = new ContentValues();
        values.put(KEY_CONTENT, note.getContent()); // get content
        values.put(KEY_CREATED, time);
        values.put(KEY_ACCESSED, time);

        //insert
        db.insert(TABLE_NOTES, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        //close
        db.close();
    }

    public Note getNote(long id){

        //get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        //build query
        Cursor cursor =
                db.query(TABLE_NOTES, // a. table
                        COLUMNS, // b. column names
                        KEY_ID +" = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        //if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        //build note object
        Note note = new Note();
        note.setId(Integer.parseInt(cursor.getString(0)));
        note.setContent(cursor.getString(1));//cursor.getString(1)+" time:"

        //return note
        return note;
    }

    public Cursor getAllNotes() {

        SQLiteDatabase db = this.getWritableDatabase();

        String preference_query = "SELECT " + KEY_ORDERBY + " FROM " + TABLE_PREFERENCES + " LIMIT 1";
        Cursor preference_cursor = db.rawQuery(preference_query, null);
        if (preference_cursor != null)
            preference_cursor.moveToFirst();
        int preference = preference_cursor.getInt(0);

        String orderby_property = "";
        String orderby_direction = "";

        switch(preference){
            case 1:
                orderby_property = KEY_CREATED;
                orderby_direction = "DESC";
                break;
            case 2:
                orderby_property = KEY_CREATED;
                orderby_direction = "ASC";
                break;
            default:
                orderby_property = KEY_ACCESSED;
                orderby_direction = "DESC";
        }

        String query = "SELECT " + KEY_ID + " AS _id, " + KEY_CONTENT + " FROM " + TABLE_NOTES + " ORDER BY " + orderby_property + " " + orderby_direction;// + ", id " + orderby_direction;

        return db.rawQuery(query, null);
    }

    public void deleteAllNotes(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NOTES);
    }

    public int updateNote(Note note) {

        //get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        //create ContentValues to add key "column"/value
        int time = (int) (System.currentTimeMillis());
        ContentValues values = new ContentValues();
        values.put(KEY_CONTENT, note.getContent());
        values.put(KEY_ACCESSED, time);

        //updating row
        int i = db.update(TABLE_NOTES,values, KEY_ID+" = ?", new String[] { String.valueOf(note.getId()) });

        //close
        db.close();

        return i;
    }

    public void updateNoteAccessed(long id) {
        SQLiteDatabase db = this.getWritableDatabase();

        int time = (int) (System.currentTimeMillis());
        ContentValues values = new ContentValues();
        values.put(KEY_ACCESSED, time);

        db.update(TABLE_NOTES, values, KEY_ID+" = ?",
                new String[] { String.valueOf(id) });
    }

    public void deleteNote(long id) {

        //get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        //delete
        db.delete(TABLE_NOTES, //table name
                KEY_ID+" = ?",  // selections
                new String[] { String.valueOf(id) }); //selections args

        //close
        db.close();
    }

    public void updateOrderby(int orderby) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ORDERBY, orderby);

        db.update(TABLE_PREFERENCES, values, "1", null);
    }

    public int getOrderby(){

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + KEY_ORDERBY + " FROM " + TABLE_PREFERENCES + " LIMIT 1";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null)
            cursor.moveToFirst();

        int orderby = cursor.getInt(0);

        return orderby;
    }

}