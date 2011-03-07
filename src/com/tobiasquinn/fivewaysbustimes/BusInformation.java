package com.tobiasquinn.fivewaysbustimes;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BusInformation {
	// This is used to load the sqlite database from an external source to
	// internal memory for access. The database is for now generated offline
	private static final String BUSTABLE = "busdata";
	private static final String DB_PATH = "/data/data/com.tobiasquinn.fivewaysbustimes/databases/";
	private static final String DB_NAME = "buses.db";
	private static final String LOG_TAG_DB = "DB";
	private BusDatabaseHelper busdbhelper;
	private SQLiteDatabase businfo;

	public BusInformation(Context context) {
		this.busdbhelper = new BusDatabaseHelper(context);
		this.businfo = this.busdbhelper.getReadableDatabase();
	}

	public List<String> getBusNumbers(String start, String destination) {
		Log.v(LOG_TAG_DB, "Get Bus Numbers for: "+start+":"+destination);
		List<String> numlist = new ArrayList<String>();
		
		// do a select to find unique bus stops
		Cursor cursor = businfo.rawQuery("SELECT number FROM busdata WHERE stop=?", new String[] {start});
		cursor.moveToFirst();
		while (cursor.isAfterLast() == false) {
			numlist.add(cursor.getString(0));
			Log.v(LOG_TAG_DB, cursor.getString(0));
			cursor.moveToNext();
		}
		cursor.close();
		return numlist;
	}
	
	private class BusDatabaseHelper extends SQLiteOpenHelper {
		private final Context myContext;
		private SQLiteDatabase myDatabase;

		public BusDatabaseHelper(Context context) {
			super(context, DB_NAME, null, 1);
			this.myContext = context;
			try {
				createDatabase();
			} catch (Exception e) {
				Log.v(LOG_TAG_DB, "Create database failed");
				e.printStackTrace();
			}
			try {
				openDatabase();
			} catch (SQLException sqle) {
				throw sqle;
			}
		}

		// create an empty database and populate it
		private void createDatabase() {
			boolean dbExist = checkDatabase();
			if (dbExist) {
				Log.v(LOG_TAG_DB, "Database exists");
			} else {
				Log.v(LOG_TAG_DB, "Copy database");
				this.getReadableDatabase(); // creates the database
				try {
					copyDatabase();
				} catch (IOException e) {
					throw new Error("Error copying database");
				}
			}
		}

		private boolean checkDatabase() {
			// true if the database exists
			SQLiteDatabase checkDB = null;
			try {
				String myPath = DB_PATH + DB_NAME;
				checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
			} catch (SQLiteException e) {
				// database doesn't exist yet
			}

			if (checkDB != null) {
				checkDB.close();
			}

			return checkDB == null ? false : true;
		}

		private void openDatabase() throws SQLException {
			String myPath = DB_PATH + DB_NAME;
			myDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
		}

		@Override
		public synchronized void close() {
			if (myDatabase != null)
				myDatabase.close();
			super.close();
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.v(LOG_TAG_DB, "onCreate");
		}

		private void copyDatabase() throws IOException {
			System.out.println("Copy Database");
			InputStream myInput = myContext.getAssets().open(DB_NAME);
			String outFileName = DB_PATH + DB_NAME;
			OutputStream myOutput = new FileOutputStream(outFileName);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = myInput.read(buffer)) > 0) {
				myOutput.write(buffer, 0, length);
			}
			myOutput.flush();
			myOutput.close();
			myInput.close();
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.v(LOG_TAG_DB, "onUpgrade");
		}
	}
}
