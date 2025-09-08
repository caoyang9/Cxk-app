package com.yang.androiddemolog.strategy;

import java.util.List;
import java.util.Map;

/**
 * 笔记应用策略接口
 */
public interface NoteStorageStrategy {

    /**
     * 获取所有笔记
     * @return
     */
    List<String> getAllNoteTitles();

    /**
     * 根据标题获取内容
     * @param title
     * @return
     */
    String getNoteContent(String title);

    /**
     * 保存笔记
     * @param title
     * @param content
     */
    void saveNote(String title, String content);

    /**
     * 获取所有笔记
     * @param title
     */
    void deleteNote(String title);

    /**
     * 获取所有笔记
     * @return
     */
    List<String> getAllNotes();
}
