
package com.silveroak.wifiplayer.database;

import android.database.sqlite.SQLiteException;

public class SQLiteAssetException extends SQLiteException {
	
	private static final long serialVersionUID = -4282115925733177109L;

	public SQLiteAssetException() {}

    public SQLiteAssetException(String error) {
        super(error);
    }
}
