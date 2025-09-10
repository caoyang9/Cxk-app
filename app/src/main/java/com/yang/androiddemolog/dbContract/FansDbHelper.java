package com.yang.androiddemolog.dbContract;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;


/**
 * 粉丝数据库表Helper
 */
public class FansDbHelper extends SQLiteOpenHelper {

    // 数据库名称和版本
    public static final String DATABASE_NAME = "fans.db";
    public static final int DATABASE_VERSION = 1;

    // 创建表的SQL语句
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FansContract.FansEntry.TABLE_NAME + " (" +
                    FansContract.FansEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    FansContract.FansEntry.COLUMN_NAME + " TEXT NOT NULL," +
                    FansContract.FansEntry.COLUMN_GENDER + " INTEGER," + // 0:未知, 1:男, 2:女
                    FansContract.FansEntry.COLUMN_AGE + " INTEGER," +
                    FansContract.FansEntry.COLUMN_BIRTHDAY + " TEXT," + // 文本存储, eg:"1990-01-01"
                    FansContract.FansEntry.COLUMN_HEIGHT + " REAL," +   // 浮点数
                    FansContract.FansEntry.COLUMN_WEIGHT + " REAL)";

    // 删除表的SQL语句
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FansContract.FansEntry.TABLE_NAME;

    public FansDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
