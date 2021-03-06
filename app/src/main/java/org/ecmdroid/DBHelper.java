/*
 EcmDroid - Android Diagnostic Tool for Buell Motorcycles
 Copyright (C) 2012 by Michel Marti

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 3
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package org.ecmdroid;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.content.Context.MODE_PRIVATE;

/**
 * Helper for initially installing or updating the bundled SQLite database.
 */
public class DBHelper extends SQLiteOpenHelper {

	// Increase this whenever updating the database
	private static final int DB_VERSION = 201504242;

	private static final String TAG = "DBHelper";
	private static String DB_NAME = "ecmdroid";
	private Context context;


	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
	}


	public void setupDB() throws IOException {
		SQLiteDatabase db = context.openOrCreateDatabase(DBHelper.DB_NAME, MODE_PRIVATE, null);
		int version = db.getVersion();
		db.close();
		if (version != DBHelper.DB_VERSION) {
			Log.i("Main", "Database installation / upgrade required (" + version + "->" + DBHelper.DB_VERSION + ")");
			Toast.makeText(context, context.getString(R.string.update_db), Toast.LENGTH_LONG).show();
			context.deleteDatabase(DBHelper.DB_NAME);
			File dbFile = context.getDatabasePath(DB_NAME);
			long now = System.currentTimeMillis();
			Log.i(TAG, "Installing Database...");
			dbFile.getParentFile().mkdirs();
			AssetManager assets = context.getAssets();
			InputStream in = assets.open(DB_NAME + ".db");
			FileOutputStream out = new FileOutputStream(dbFile);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}
			in.close();
			out.flush();
			out.close();
			Log.d(TAG, "Database installed in " + (System.currentTimeMillis() - now) + "ms.");
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, "onCreate...");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int ov, int nv) {
		Log.w(TAG, "onUpgrade(" + ov + ", " + nv + ")");
	}
}
