package com.yang.androiddemolog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.gesture.GestureOverlayView;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class NoteActivity extends AppCompatActivity {

    private Spinner spinnerNotes;
    private TextInputEditText etNoteTitle;
    private TextInputEditText etNoteContent;
    private Button btnSave;
    private Button btnCancel;
    private Button btnNew;

    private static final String NOTE_PREFERENCE = "notes_preference";
    private static final String NOTE_TITLES_KEY = "note_titles";
    private static final String NOTE_CONTENTS_KEY = "note_contents";

    private List<String> noteTitles = new ArrayList<>();
    private List<String> noteContents = new ArrayList<>();

    // 添加一个标志位来区分是用户选择还是程序设置
    private boolean userSelecting = true;
    private boolean creatingNew = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        initViews();
        loadNotesFromPreferences();
        setupSpinner();
        setupButtons();
    }

    private void initViews() {
        spinnerNotes = findViewById(R.id.spinner_notes);
        etNoteTitle = findViewById(R.id.et_note_title);
        etNoteContent = findViewById(R.id.et_note_content);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
        btnNew = findViewById(R.id.btn_new);
    }

    /**
     * 下拉框
     */
    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                noteTitles
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNotes.setAdapter(adapter);

        spinnerNotes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 如果是程序设置的选中（如新建后），不加载内容
                if (!userSelecting && creatingNew) {
                    userSelecting = true; // 重置标志位
                    creatingNew = false;
                    return;
                }

                if (position >= 0 && position < noteContents.size()) {
                    String selectedTitle = noteTitles.get(position);
                    String selectedContent = noteContents.get(position);
                    etNoteTitle.setText(selectedTitle);
                    etNoteContent.setText(selectedContent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 什么都不做
            }
        });
    }

    private void loadNotesFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(NOTE_PREFERENCE, MODE_PRIVATE);

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
        SharedPreferences sharedPreferences = getSharedPreferences(NOTE_PREFERENCE, MODE_PRIVATE);
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

    private void setupButtons() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCancelConfirmation();
            }
        });
        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewNote();
            }
        });
    }

    private void saveNote() {
        String title = etNoteTitle.getText().toString().trim();
        String content = etNoteContent.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "标题不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 保存当前选中的位置
        int currentPosition = spinnerNotes.getSelectedItemPosition();
        String currentTitle = currentPosition >= 0 ? noteTitles.get(currentPosition) : null;

        // 检查标题是否已存在
        int existingIndex = noteTitles.indexOf(title);

        if (existingIndex != -1) {
            // 更新现有笔记
            noteContents.set(existingIndex, content);
        } else {
            // 添加新笔记
            noteTitles.add(title);
            noteContents.add(content);
        }

        // 保存到SharedPreferences
        saveNotesToPreferences();

        Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();

        // 只刷新数据，不清空输入框，保持当前位置
        refreshDataWithoutResetting();

        // 恢复选中位置：如果是更新现有笔记，保持原位置；如果是新建，选中新项
        if (existingIndex != -1) {
            // 更新操作，保持原位置
            spinnerNotes.setSelection(currentPosition);
        } else {
            // 新建操作，选中最新添加的项
            spinnerNotes.setSelection(noteTitles.size() - 1);
        }
    }

    /**
     * 刷新数据但不重置界面状态
     */
    private void refreshDataWithoutResetting() {
        // 保存当前输入框的内容
        String currentTitle = etNoteTitle.getText().toString();
        String currentContent = etNoteContent.getText().toString();

        // 重新加载数据
        loadNotesFromPreferences();

        // 刷新Spinner适配器
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                noteTitles
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNotes.setAdapter(adapter);

        // 恢复输入框内容（防止在刷新过程中被清空）
        etNoteTitle.setText(currentTitle);
        etNoteContent.setText(currentContent);
    }

    private void clearInputFields() {
        etNoteTitle.getText().clear();
        etNoteContent.getText().clear();
    }

    private void showCancelConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.cancel)
                .setMessage(R.string.cancel_confirmation)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
    /**
     * 创建新笔记的方法
     */
    private void createNewNote() {
        userSelecting = false;
        creatingNew = true;

        // 清空输入框
        clearInputFields();

        // 将焦点设置到标题输入框
        etNoteTitle.requestFocus();

        // 显示软键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(etNoteTitle, InputMethodManager.SHOW_IMPLICIT);
        }

        // 给用户一个提示
        Toast.makeText(this, "开始创建新笔记", Toast.LENGTH_SHORT).show();

        // 重置Spinner选择（设置为默认位置）
        spinnerNotes.setSelection(0, false);
    }
}
