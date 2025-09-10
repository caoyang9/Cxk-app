package com.yang.androiddemolog.dbContract;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * 粉丝实体契约类
 */
public class FansContract {

    // ContentProvider的Authority，包名+名称
    public static final String CONTENT_AUTHORITY = "com.yang.androiddemolog.fansprovider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // 对应数据库中的表名
    public static final String PATH_FANS = "fans";

    // 该合约类无需实例化
    private void PersonContract() {}

    /* 定义表内容的内部类 */
    public static class FansEntry implements BaseColumns {

        // 完整的CONTENT_URI
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_FANS);

        // MIME类型：返回多条记录
        public static final String CONTENT_LIST_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_URI + "/" + PATH_FANS;
        // MIME类型：返回单条记录
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_FANS;

        // 表名
        public static final String TABLE_NAME = "fans";

        // 列名
        public static final String _ID = BaseColumns._ID; // 继承自BaseColumns
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_GENDER = "gender";
        public static final String COLUMN_AGE = "age";
        public static final String COLUMN_BIRTHDAY = "birthday";
        public static final String COLUMN_HEIGHT = "height"; // 单位：厘米
        public static final String COLUMN_WEIGHT = "weight"; // 单位：公斤
    }

}
