package com.example.ddvoice;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class DBHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "DDvoice.db";
	private static final String TBL_NAME = "Like";
	private static final String CREATE_TBL = " create table Like(_id integer primary key autoincrement,reaction text,like integer) ";
	
	private SQLiteDatabase db;
	public DBHelper(Context c) {
		//�������ݿ�
		super(c, DB_NAME, null, 2);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		//���ɱ�
		this.db = db;
		db.execSQL(CREATE_TBL);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		this.db = db;
		// ���ݿⱻ�ı�ʱ����ԭ�ȵı�ɾ����Ȼ�����±�
        String sql = "drop table if exists Like";
        db.execSQL(sql);
        onCreate(db);
	}
	public void insert(ContentValues values) {
		//���Ӽ�¼
		SQLiteDatabase db = getWritableDatabase();
		db.insert(TBL_NAME, null, values);
		db.close();
	}
	
	public Cursor query() {
		//�鿴��¼
		SQLiteDatabase db = getWritableDatabase();
		Cursor c = db.query(TBL_NAME, null, null, null, null, null, null);
		return c;
	}
	public void del(int id) {
		if (db == null)
			db = getWritableDatabase();
		db.delete(TBL_NAME, "_id=?", new String[] { String.valueOf(id) });
	}
	public void close() {
		if (db != null)
			db.close();
	}
	
}