package com.terryc.assqlite2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by terry-developer on 3/11/15.
 */
public class GridViewTable extends LinearLayout {
	protected GridView gvTable, gvPage;
	protected SimpleAdapter saTable, saPage;
	protected ArrayList<HashMap<String, String>> srcTable, srcPage;
	protected int TableRowCount = 10;
	protected int TableColCount = 0;
	protected SQLiteDatabase db;
	protected String rawSql = "";
	protected Cursor csTable;
	protected OnTableClickListener clickListener;
	protected OnPageSwitchListener switchListener;


	public GridViewTable(Context context) {
		super(context);
		this.setOrientation(VERTICAL);
		gvTable = new GridView(context);
		addView(gvTable, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		srcTable = new ArrayList<HashMap<String, String>>();
		saTable = new SimpleAdapter(
				context,
				srcTable,
				R.layout.gv_item_page,
				new String[]{"Item Text"},
				new int[]{R.id.tv_item_page}
		);
		gvTable.setAdapter(saTable);
		gvTable.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int y = position / csTable.getColumnCount() - 1;
				int x = position % csTable.getColumnCount();
				if (clickListener != null
						&& y != -1) {
					clickListener.onTableClickListener(x, y, csTable);
				}
			}

		});

		gvPage = new GridView(context);
		gvPage.setColumnWidth(40);
		gvPage.setNumColumns(GridView.AUTO_FIT);

		addView(gvPage, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		srcPage = new ArrayList<HashMap<String, String>>();
		saPage = new SimpleAdapter(
				context,
				srcPage,
				R.layout.gv_item_page,
				new String[]{"Item Text"},
				new int[]{R.id.tv_item_page}
		);
		gvPage.setAdapter(saPage);
		gvPage.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				loadTable(position);
				if (switchListener != null) {
					switchListener.onPageSwitchListener(position, srcPage.size());
				}
			}
		});
	}

	public interface OnTableClickListener {
		public void onTableClickListener(int x, int y, Cursor c);
	}

	public void setOnTableClickListener(OnTableClickListener tableClickListener) {
		this.clickListener = tableClickListener;
	}

	public interface OnPageSwitchListener {
		public void onPageSwitchListener(int page, int pageCount);
	}

	public void setOnPageSwitchListener(OnPageSwitchListener pageSwitchListener) {
		this.switchListener = pageSwitchListener;
	}

	public void clearAll() {
		if (this.csTable != null) {
			csTable.close();
		}
		srcTable.clear();
		saTable.notifyDataSetChanged();
		srcPage.clear();
		saPage.notifyDataSetChanged();
	}

	protected void loadTable(int pageId) {
		if (csTable != null) {
			csTable.close();
		}

		String sql = rawSql + " Limit " + String.valueOf(TableRowCount) + " Offset " +
				String.valueOf(pageId * TableRowCount);
		csTable = db.rawQuery(sql, null);

		gvTable.setNumColumns(csTable.getColumnCount());
		TableColCount = csTable.getColumnCount();
		srcTable.clear();

		int colCount = csTable.getColumnCount();
		for (int i = 0; i < colCount; i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("Item text", csTable.getColumnName(i));
			srcTable.add(map);
		}

		int recCount = csTable.getCount();
		for (int i = 0; i < recCount; i++) {
			csTable.moveToPosition(i);
			for (int ii = 0; ii < colCount; ii++) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("Item Text", csTable.getString(ii));
				srcTable.add(map);
			}
		}

		saTable.notifyDataSetChanged();
	}

	public void gvSetTableRowCount(int row) {
		TableRowCount = row;
	}

	public int gvGetTableRowCount() {
		return TableRowCount;
	}

	public Cursor gvGetCurrentCurosr() {
		return csTable;
	}

	public void gvReadyTable(String rawSql, SQLiteDatabase db) {
		this.rawSql = rawSql;
		this.db = db;
	}

	public void gvUpdatePageBar(String sql, SQLiteDatabase db) {
		Cursor cs = db.rawQuery(sql, null);
		cs.moveToLast();
		long size = cs.getLong(0);
		cs.close();
		int pageNumber = (int) (size / TableRowCount) + 1;

		srcPage.clear();
		for (int i = 0; i < pageNumber; i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("Item Text", String.valueOf(i));
			srcPage.add(map);
		}

		saPage.notifyDataSetChanged();
	}

}
