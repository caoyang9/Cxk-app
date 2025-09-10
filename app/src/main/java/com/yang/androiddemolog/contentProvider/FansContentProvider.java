package com.yang.androiddemolog.contentProvider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yang.androiddemolog.dbContract.FansContract;
import com.yang.androiddemolog.dbContract.FansDbHelper;

public class FansContentProvider extends ContentProvider {

    // 区分不同的URI请求
    public static final int FANS = 100;       // 操作整张表
    public static final int FANS_ID = 101;     // 操作单个ID的记录

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(FansContract.CONTENT_AUTHORITY, FansContract.PATH_FANS, FANS);
        sUriMatcher.addURI(FansContract.CONTENT_AUTHORITY, FansContract.PATH_FANS + "/#", FANS_ID);
    }

    /**
     * 数据库帮助类对象
     */
    private FansDbHelper fansDbHelper;

    @Override
    public boolean onCreate() {
        fansDbHelper = new FansDbHelper(getContext());
        return true;
    }

    /**
     * @param uri 要查询的URI。这将是客户端发送的完整URI；如果客户端请求特定记录，URI将以一个记录号结尾，
     *            实现应解析该记录号并将其添加到WHERE或HAVING子句中，指定_id值。
     * @param projection The list of columns to put into the cursor. If
     *      {@code null} all columns are included. 列
     * @param selection A selection criteria to apply when filtering rows.
     *      If {@code null} then all rows are included.
     * @param selectionArgs You may include ?s in selection, which will be replaced by
     *      the values from selectionArgs, in order that they appear in the selection.
     *      The values will be bound as Strings.
     * @param sortOrder How the rows in the cursor should be sorted.
     *      If {@code null} then the provider is free to define the sort order.
     * @return
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase readableDatabase = fansDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);

        if(match == FANS){
            // 查询整个表
            cursor = readableDatabase.query(FansContract.FansEntry.TABLE_NAME, projection,
                    selection, selectionArgs, null, null, sortOrder);
        }else if(match == FANS_ID){
            // 查询单条记录，从uri从提取ID
            selection = FansContract.FansEntry._ID + "=?";
            selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
            cursor = readableDatabase.query(FansContract.FansEntry.TABLE_NAME, projection,
                    selection, selectionArgs, null, null, sortOrder);
        }else{
            throw new IllegalArgumentException("Cannot query unknown URI: " + uri);
        }

        // 设置通知URI，当数据改变时，Cursor知道需要更新
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        if (match == FANS) {
            return FansContract.FansEntry.CONTENT_LIST_TYPE;
        }else if(match == FANS_ID){
            return FansContract.FansEntry.CONTENT_ITEM_TYPE;
        }else {
            throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int match = sUriMatcher.match(uri);
        if(match != FANS){
            throw new IllegalArgumentException("Insert is only support for" + FansContract.FansEntry.CONTENT_URI);
        }

        // 参数校验：name不能为空
        if(values == null || !values.containsKey(FansContract.FansEntry.COLUMN_NAME)){
            throw new IllegalArgumentException("Name is required");
        }

        SQLiteDatabase writableDatabase = fansDbHelper.getWritableDatabase();
        long id = writableDatabase.insert(FansContract.FansEntry.TABLE_NAME, null, values);

        if(id == -1){
            Log.e("FansContentProvider", "Fail to insert row for" + uri);
        }
        // 通知所有监听此Uri的观察者数据已改变
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        SQLiteDatabase writableDatabase = fansDbHelper.getWritableDatabase();
        int rowsDeleted = 0;

        if(match == FANS){
            // 删除记录
            rowsDeleted = writableDatabase.delete(FansContract.FansEntry.TABLE_NAME, selection, selectionArgs);
        } else if (match == FANS_ID) {
            // 删除单条记录
            selection = FansContract.FansEntry._ID + "=?";
            selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
            rowsDeleted = writableDatabase.delete(FansContract.FansEntry.TABLE_NAME, selection, selectionArgs);
        }else {
            throw new IllegalArgumentException("Delete is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        SQLiteDatabase writableDatabase = fansDbHelper.getWritableDatabase();
        int rowsUpdate = 0;

        if (match == FANS){
            // 更新记录
            writableDatabase.update(FansContract.FansEntry.TABLE_NAME, values, selection, selectionArgs);
        }else if(match == FANS_ID){
            // 更新单条记录
            selection = FansContract.FansEntry._ID + "=?";
            selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
            rowsUpdate = writableDatabase.update(FansContract.FansEntry.TABLE_NAME, values, selection, selectionArgs);
        }else {
            throw new IllegalArgumentException("Update is not supported for " + uri);
        }

        if(rowsUpdate != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdate;
    }
}
