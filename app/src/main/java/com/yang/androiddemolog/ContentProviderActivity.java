package com.yang.androiddemolog;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.yang.androiddemolog.dbContract.FansContract;

/**
 * ContentProvider操作SQLite
 */
public class ContentProviderActivity extends AppCompatActivity {

    private EditText etName, etGender, etAge, etBirthday, etHeight, etWeight, etQueryId;
    private TextView tvDisplay;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contentprovider);

        // 初始化视图
        initViews();

        // 设置按钮点击监听器
        setupButtonListeners();
    }

    private void initViews() {
        etName = findViewById(R.id.et_name);
        etGender = findViewById(R.id.et_gender);
        etAge = findViewById(R.id.et_age);
        etBirthday = findViewById(R.id.et_birthday);
        etHeight = findViewById(R.id.et_height);
        etWeight = findViewById(R.id.et_weight);
        etQueryId = findViewById(R.id.et_query_id);
        tvDisplay = findViewById(R.id.tv_display);
    }

    private void setupButtonListeners() {
        Button btnInsert = findViewById(R.id.btn_insert);
        Button btnQueryAll = findViewById(R.id.btn_query_all);
        Button btnQuery = findViewById(R.id.btn_query);
        Button btnUpdate = findViewById(R.id.btn_update);
        Button btnDelete = findViewById(R.id.btn_delete);

        btnInsert.setOnClickListener(v -> insertPerson());
        btnQueryAll.setOnClickListener(v -> queryAllPersons());
        btnQuery.setOnClickListener(v -> queryPersonById());
        btnUpdate.setOnClickListener(v -> updatePerson());
        btnDelete.setOnClickListener(v -> deletePerson());
    }

    private void insertPerson() {
        ContentValues values = new ContentValues();
        values.put(FansContract.FansEntry.COLUMN_NAME, etName.getText().toString().trim());

        // 数据验证
        try {
            values.put(FansContract.FansEntry.COLUMN_GENDER, Integer.parseInt(etGender.getText().toString()));
            values.put(FansContract.FansEntry.COLUMN_AGE, Integer.parseInt(etAge.getText().toString()));
            values.put(FansContract.FansEntry.COLUMN_BIRTHDAY, etBirthday.getText().toString().trim());
            values.put(FansContract.FansEntry.COLUMN_HEIGHT, Float.parseFloat(etHeight.getText().toString()));
            values.put(FansContract.FansEntry.COLUMN_WEIGHT, Float.parseFloat(etWeight.getText().toString()));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "请输入有效的数字", Toast.LENGTH_SHORT).show();
            return;
        }

        // getContentResolver
        Uri newUri = getContentResolver().insert(FansContract.FansEntry.CONTENT_URI, values);

        if (newUri != null) {
            Toast.makeText(this, "添加成功, ID: " + newUri.getLastPathSegment(), Toast.LENGTH_SHORT).show();
            clearInputs();
        } else {
            Toast.makeText(this, "添加失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void queryAllPersons() {
        // getContentResolver
        Cursor cursor = getContentResolver().query(
                FansContract.FansEntry.CONTENT_URI,
                null, // 查询所有列
                null,
                null,
                null
        );

        displayCursor(cursor);
    }

    private void queryPersonById() {
        String idStr = etQueryId.getText().toString().trim();
        if (idStr.isEmpty()) {
            Toast.makeText(this, "请输入ID", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri uri = Uri.withAppendedPath(FansContract.FansEntry.CONTENT_URI, idStr);
        // getContentResolver
        Cursor cursor = getContentResolver().query(
                uri,
                null,
                null,
                null,
                null
        );

        displayCursor(cursor);
    }

    private void updatePerson() {
        String idStr = etQueryId.getText().toString().trim();
        if (idStr.isEmpty()) {
            Toast.makeText(this, "请输入要更新的ID", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        // 只更新用户填写了的字段
        if (!etName.getText().toString().isEmpty()) values.put(FansContract.FansEntry.COLUMN_NAME, etName.getText().toString());
        if (!etGender.getText().toString().isEmpty()) values.put(FansContract.FansEntry.COLUMN_GENDER, Integer.parseInt(etGender.getText().toString()));
        if (!etAge.getText().toString().isEmpty()) values.put(FansContract.FansEntry.COLUMN_AGE, Integer.parseInt(etAge.getText().toString()));
        if (!etBirthday.getText().toString().isEmpty()) values.put(FansContract.FansEntry.COLUMN_BIRTHDAY, etBirthday.getText().toString());
        if (!etHeight.getText().toString().isEmpty()) values.put(FansContract.FansEntry.COLUMN_HEIGHT, Float.parseFloat(etHeight.getText().toString()));
        if (!etWeight.getText().toString().isEmpty()) values.put(FansContract.FansEntry.COLUMN_WEIGHT, Float.parseFloat(etWeight.getText().toString()));

        if (values.size() == 0) {
            Toast.makeText(this, "请至少填写一个要更新的字段", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri uri = Uri.withAppendedPath(FansContract.FansEntry.CONTENT_URI, idStr);

        // getContentResolver
        int rowsUpdated = getContentResolver().update(uri, values, null, null);

        if (rowsUpdated > 0) {
            Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show();
            clearInputs();
        } else {
            Toast.makeText(this, "更新失败，请检查ID是否存在", Toast.LENGTH_SHORT).show();
        }
    }

    private void deletePerson() {
        String idStr = etQueryId.getText().toString().trim();
        if (idStr.isEmpty()) {
            Toast.makeText(this, "请输入要删除的ID", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri uri = Uri.withAppendedPath(FansContract.FansEntry.CONTENT_URI, idStr);
        // getContentResolver
        int rowsDeleted = getContentResolver().delete(uri, null, null);

        if (rowsDeleted > 0) {
            Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "删除失败，请检查ID是否存在", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayCursor(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) {
            tvDisplay.setText("没有找到数据");
            if (cursor != null) cursor.close();
            return;
        }

        StringBuilder builder = new StringBuilder();
        builder.append("共找到 ").append(cursor.getCount()).append(" 条记录:\n\n");

        while (cursor.moveToNext()) {
            int idIndex = cursor.getColumnIndex(FansContract.FansEntry._ID);
            int nameIndex = cursor.getColumnIndex(FansContract.FansEntry.COLUMN_NAME);
            int genderIndex = cursor.getColumnIndex(FansContract.FansEntry.COLUMN_GENDER);
            int ageIndex = cursor.getColumnIndex(FansContract.FansEntry.COLUMN_AGE);
            int birthdayIndex = cursor.getColumnIndex(FansContract.FansEntry.COLUMN_BIRTHDAY);
            int heightIndex = cursor.getColumnIndex(FansContract.FansEntry.COLUMN_HEIGHT);
            int weightIndex = cursor.getColumnIndex(FansContract.FansEntry.COLUMN_WEIGHT);

            builder.append("ID: ").append(cursor.getInt(idIndex)).append("\n");
            builder.append("姓名: ").append(cursor.getString(nameIndex)).append("\n");
            builder.append("性别: ").append(cursor.getInt(genderIndex)).append("\n");
            builder.append("年龄: ").append(cursor.getInt(ageIndex)).append("\n");
            builder.append("生日: ").append(cursor.getString(birthdayIndex)).append("\n");
            builder.append("身高: ").append(cursor.getFloat(heightIndex)).append("cm\n");
            builder.append("体重: ").append(cursor.getFloat(weightIndex)).append("kg\n");
            builder.append("----------------------------\n");
        }
        cursor.close();
        tvDisplay.setText(builder.toString());
    }

    private void clearInputs() {
        etName.setText("");
        etGender.setText("");
        etAge.setText("");
        etBirthday.setText("");
        etHeight.setText("");
        etWeight.setText("");
    }
}
