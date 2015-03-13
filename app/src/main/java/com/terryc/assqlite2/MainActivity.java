package com.terryc.assqlite2;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
	GridViewTable table;
	SQLiteDatabase db;
	int id;
	private static final String TABLE_NAME = "TESTTABLE";
	private static final String ID = "id";
	private static final String NAME = "name";
	private static final String PHONE = "phone";
	private static final String ADDRESS = "address";
	private static final String AGE = "age";

	Button btn_create;
	Button btn_insert;
	Button btn_close;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btn_create = (Button)findViewById(R.id.btn_create);
		btn_create.setOnClickListener(new PrivateOnClickListener());
		btn_insert = (Button)findViewById(R.id.btn_insert);
		btn_insert.setOnClickListener(new PrivateOnClickListener());
		btn_close = (Button)findViewById(R.id.btn_close);
		btn_close.setOnClickListener(new PrivateOnClickListener());

		table = new GridViewTable(this);
		table.gvSetTableRowCount(8);

		LinearLayout ll = (LinearLayout)findViewById(R.id.main_activity);
		table.setOnTableClickListener(new GridViewTable.OnTableClickListener(){
			@Override
			public void onTableClickListener(int x, int y, Cursor c) {
				c.moveToPosition(y);
				String str = c.getString(x) + " position: x : " + String.valueOf(x) + " y: " + String.valueOf(y);
				Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
			}
		});
		table.setOnPageSwitchListener(new GridViewTable.OnPageSwitchListener() {
			@Override
			public void onPageSwitchListener(int page, int pageCount) {
				String str = "Total Page : " + String.valueOf(pageCount) +
						"; Current Page : " + String.valueOf(page);
				Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
			}
		});
		ll.addView(table);
	}

	class PrivateOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (v == btn_create) {
				createDB();
			} else if (v == btn_insert) {
				insertRecord(16);//插入16条记录
				table.gvUpdatePageBar("select count(*) from " + TABLE_NAME,db);
				table.gvReadyTable("select * from " + TABLE_NAME,db);
			}else if (v == btn_close) {
				table.clearAll();
				db.close();

			}
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	void createDB() {
		db = SQLiteDatabase.create(null);
		Log.e("DataBase path", db.getPath());
		String amount = String.valueOf(databaseList().length);
		Log.e("DataBase amount", amount);

		String sql = "create table " + TABLE_NAME + " (" + ID + " text not null, " +
				NAME + " text not null, " +
				ADDRESS + " text not null, " +
				PHONE + " text not null, " +
				AGE + " text not null" + " );";

		try {
			db.execSQL("drop table if exists " + TABLE_NAME);
			db.execSQL(sql);
			Log.e("created", "ok");
		} catch (Exception e) {

		}
	}

	void insertRecord(int n) {
		int total = id + n;
		for (; id < total; id++) {
			String idString = String.valueOf(id);
			String sql = "insert into " + TABLE_NAME + " values('" +
					idString + "',  'name"  + idString + "', 'address" +
					idString + "', 'phone" + idString + "', 'age" +
					idString + "');";
			try{
				db.execSQL(sql);
				Log.e("insert", "ok");
			}catch (Exception e){

			}

		}

	}
}
