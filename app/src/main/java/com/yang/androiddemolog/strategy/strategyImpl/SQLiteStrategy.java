package com.yang.androiddemolog.strategy.strategyImpl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yang.androiddemolog.strategy.NoteStorageStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * SQLite实现笔记应用的存储策实现类
 */
public class SQLiteStrategy implements NoteStorageStrategy {

    private NotesDbHelper dbHelper;

    public SQLiteStrategy(NotesDbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public List<String> getAllNoteTitles() {
        List<String> titles = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                NotesDbHelper.TABLE_NOTES,
                new String[]{NotesDbHelper.COLUMN_TITLE},
                null, null, null, null,
                NotesDbHelper.COLUMN_CREATED_AT + " DESC" // 按创建时间降序
        );

        while (cursor.moveToNext()) {
            titles.add(cursor.getString(cursor.getColumnIndexOrThrow(NotesDbHelper.COLUMN_TITLE)));
        }

        cursor.close();
        db.close();
        return titles;
    }

    @Override
    public String getNoteContent(String title) {
        return null;
    }

    @Override
    public void saveNote(String title, String content) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NotesDbHelper.COLUMN_TITLE, title);
        values.put(NotesDbHelper.COLUMN_CONTENT, content);

        // 使用insertWithOnConflict实现插入或更新
        db.insertWithOnConflict(
                NotesDbHelper.TABLE_NOTES,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE
        );

        db.close();
    }

    @Override
    public void deleteNote(String title) {

    }

    @Override
    public List<String> getAllNotes() {
        List<String> notesContent = new ArrayList<>();
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        Cursor cursor = database.query(
                NotesDbHelper.TABLE_NOTES,
                new String[]{NotesDbHelper.COLUMN_CONTENT},
                null, null, null, null,
                NotesDbHelper.COLUMN_CREATED_AT + " DESC"
        );

        while (cursor.moveToNext()) {
            String content = cursor.getString(cursor.getColumnIndexOrThrow(NotesDbHelper.COLUMN_CONTENT));
            notesContent.add(content);
        }
        cursor.close();
        database.close();
        return notesContent;
    }
}
