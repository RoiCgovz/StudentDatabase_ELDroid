package com.eldroid.studentdatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class StudentDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "student.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "student";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_COURSE = "course";
    public static final String COL_IMAGE = "image";

    public StudentDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_COURSE + " TEXT NOT NULL, " +
                COL_IMAGE + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Insert
    public long insertStudent(String name, String course, String imageUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_COURSE, course);
        values.put(COL_IMAGE, imageUri);
        return db.insert(TABLE_NAME, null, values);
    }

    // Update
    public int updateStudent(int id, String name, String course, String imageUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_COURSE, course);
        values.put(COL_IMAGE, imageUri);
        return db.update(TABLE_NAME, values, COL_ID + "=?", new String[]{String.valueOf(id)});
    }

    // Delete
    public int deleteStudent(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, COL_ID + "=?", new String[]{String.valueOf(id)});
    }

    public List<Student> getAllStudentsList() {
        List<Student> studentList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME));
                String course = cursor.getString(cursor.getColumnIndexOrThrow(COL_COURSE));
                String image = cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE));

                studentList.add(new Student(id, name, course, image));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return studentList;
    }

    public Student getStudentById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, COL_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Student s = new Student(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_COURSE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE))
            );
            cursor.close();
            return s;
        }
        return null;
    }
}
