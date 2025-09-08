package com.yang.androiddemolog.strategy.strategyImpl;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yang.androiddemolog.strategy.NoteStorageStrategy;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * SharedPreferences实现笔记应用的存储策实现类
 */
public class SharedPreferencesStrategy implements NoteStorageStrategy {

    private SharedPreferences sharedPreferences;
    private static final String NOTE_TITLES_KEY = "note_titles";
    private static final String NOTE_CONTENTS_KEY = "note_contents";

    private List<String> noteTitles = new ArrayList<>();
    private List<String> noteContents = new ArrayList<>();

    public SharedPreferencesStrategy(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        loadNotesFromPreferences();
    }

    @Override
    public List<String> getAllNoteTitles() {
        return new ArrayList<>(noteTitles);
    }

    @Override
    public String getNoteContent(String title) {
        int index = noteContents.indexOf(title);
        if(index != -1 && index < noteContents.size()){
            return noteContents.get(index);
        }
        return "";
    }

    @Override
    public void saveNote(String title, String content) {
        int existingIndex = noteTitles.indexOf(title);
        if(existingIndex != -1){
            // 更新笔记
            noteContents.set(existingIndex, content);
        }else {
            // 新增笔记
            noteTitles.add(title);
            noteContents.add(content);
        }
        saveNotesToPreferences();
    }

    @Override
    public void deleteNote(String title) {

    }

    @Override
    public List<String> getAllNotes() {
        return new ArrayList<>(noteContents);
    }

    private void loadNotesFromPreferences() {
        Gson gson = new Gson();

        // 加载标题列表
        String titlesJson = sharedPreferences.getString(NOTE_TITLES_KEY, "[]");
        Type titlesType = new TypeToken<LinkedHashSet<String>>() {}.getType();
        Set<String> titlesSet = gson.fromJson(titlesJson, titlesType);

        // 加载内容列表
        String contentsJson = sharedPreferences.getString(NOTE_CONTENTS_KEY, "[]");
        Type contentsType = new TypeToken<LinkedHashSet<String>>() {}.getType();
        Set<String> contentsSet = gson.fromJson(contentsJson, contentsType);

        noteTitles.clear();
        noteContents.clear();

        if (titlesSet != null) noteTitles.addAll(titlesSet);
        if (contentsSet != null) noteContents.addAll(contentsSet);
    }

    private void saveNotesToPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();

        // 保存标题列表（使用LinkedHashSet保持顺序）
        LinkedHashSet<String> titlesSet = new LinkedHashSet<>(noteTitles);
        String titlesJson = gson.toJson(titlesSet);
        editor.putString(NOTE_TITLES_KEY, titlesJson);

        // 保存内容列表（使用LinkedHashSet保持顺序）
        LinkedHashSet<String> contentsSet = new LinkedHashSet<>(noteContents);
        String contentsJson = gson.toJson(contentsSet);
        editor.putString(NOTE_CONTENTS_KEY, contentsJson);

        editor.apply();
    }
}
