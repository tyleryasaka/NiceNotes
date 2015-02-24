package me.tyleryasaka.apps.nicenotes;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.content.Context;

public class orderbySelector implements OnItemSelectedListener {

    Context app_context;
    public orderbySelector(Context context)
    {
        app_context = context;
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
        MySQLiteHelper db = new MySQLiteHelper(app_context);
        db.updateOrderby(pos);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

}