package com.yang.androiddemolog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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

import java.util.ArrayList;
import java.util.HashSet;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        initViews();
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

    private void setupSpinner() {
        loadNotesFromPreferences();

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

        Set<String> titlesSet = sharedPreferences.getStringSet(NOTE_TITLES_KEY, new HashSet<String>());
        Set<String> contentsSet = sharedPreferences.getStringSet(NOTE_CONTENTS_KEY, new HashSet<String>());

        noteTitles.clear();
        noteContents.clear();

        noteTitles.addAll(titlesSet);
        noteContents.addAll(contentsSet);
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
            Toast.makeText(this, R.string.title_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences(NOTE_PREFERENCE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // 获取现有的标题和内容集合
        Set<String> existingTitles = new HashSet<>(sharedPreferences.getStringSet(NOTE_TITLES_KEY, new HashSet<String>()));
        Set<String> existingContents = new HashSet<>(sharedPreferences.getStringSet(NOTE_CONTENTS_KEY, new HashSet<String>()));

        // 如果标题已存在，先移除旧的内容（实现更新功能）
        if (existingTitles.contains(title)) {
            int index = new ArrayList<>(existingTitles).indexOf(title);
            List<String> tempContents = new ArrayList<>(existingContents);
            if (index < tempContents.size()) {
                tempContents.set(index, content);
                existingContents = new HashSet<>(tempContents);
            }
        } else {
            // 如果是新标题，添加到集合中
            existingTitles.add(title);
            existingContents.add(content);
        }

        // 保存回 SharedPreferences
        editor.putStringSet(NOTE_TITLES_KEY, existingTitles);
        editor.putStringSet(NOTE_CONTENTS_KEY, existingContents);
        editor.apply();

        Toast.makeText(this, R.string.save_success, Toast.LENGTH_SHORT).show();
        refreshSpinner();
        clearInputFields();
    }

    private void refreshSpinner() {
        loadNotesFromPreferences();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                noteTitles
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNotes.setAdapter(adapter);
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
        // 清空输入框
        clearInputFields();

        // 将焦点设置到标题输入框
        etNoteTitle.requestFocus();

        // 显示软键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(etNoteTitle, InputMethodManager.SHOW_IMPLICIT);
        }

        // 可选：给用户一个提示
        Toast.makeText(this, "开始创建新笔记", Toast.LENGTH_SHORT).show();

        // 可选：重置Spinner选择（设置为默认位置）
        spinnerNotes.setSelection(0);
    }
}
