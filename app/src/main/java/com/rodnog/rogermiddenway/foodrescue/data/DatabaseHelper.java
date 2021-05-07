package com.rodnog.rogermiddenway.foodrescue.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.rodnog.rogermiddenway.foodrescue.model.Food;
import com.rodnog.rogermiddenway.foodrescue.model.User;
import com.rodnog.rogermiddenway.foodrescue.util.Util;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {


    public DatabaseHelper(@Nullable Context context) {
        super(context, Util.DATABASE_NAME, null, Util.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + Util.TABLE_NAME_USERS + "(" + Util.USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Util.NAME + " TEXT," + Util.EMAIL + " TEXT," + Util.PHONE + " TEXT, " + Util.ADDRESS + " TEXT, " + Util.PASSWORD + " TEXT)";
        db.execSQL(CREATE_USER_TABLE);

        String CREATE_FOOD_TABLE = "CREATE TABLE " + Util.TABLE_NAME_FOOD + "(" + Util.FOOD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Util.USER_ID + " INTEGER," + Util.IMAGE_PATH + " TEXT, " + Util.FOOD_TITLE + " TEXT, " + Util.FOOD_DESCRIPTION
                + " TEXT, " + Util.DATE +  " INTEGER, " + Util.PICKUP_TIMES + " INTEGER, " + Util.QUANTITY + " INTEGER, " + Util.LOCATION + " TEXT)";
        db.execSQL(CREATE_FOOD_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DROP_USER_TABLE = "DROP TABLE IF EXISTS ";
        db.execSQL(DROP_USER_TABLE + Util.TABLE_NAME_USERS);
        db.execSQL(DROP_USER_TABLE + Util.TABLE_NAME_FOOD);
        onCreate(db);
    }

    public long insertUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Util.NAME, user.getName());
        contentValues.put(Util.EMAIL, user.getEmail());
        contentValues.put(Util.PHONE, user.getPhone());
        contentValues.put(Util.ADDRESS, user.getAddress());
        contentValues.put(Util.PASSWORD, user.getPassword());
        long newRowId = db.insert(Util.TABLE_NAME_USERS, null, contentValues);
        db.close();
        return newRowId;
    }

    public int fetchUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Util.TABLE_NAME_USERS, new String[]{Util.USER_ID}, Util.EMAIL + "=? and " + Util.PASSWORD + "=?",
        new String[]{email, password}, null, null, null);
        if(cursor.moveToFirst()){
            db.close();
            return cursor.getInt(cursor.getColumnIndex(Util.USER_ID));
        }
        else{
            db.close();
            return -1;
        }

    }

    public long insertFood(Food food){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Util.USER_ID, food.getUser_id());
        contentValues.put(Util.IMAGE_PATH, food.getImage_path());
        contentValues.put(Util.FOOD_TITLE, food.getTitle());
        contentValues.put(Util.FOOD_DESCRIPTION, food.getDescription());
        contentValues.put(Util.DATE, food.getDate());
        contentValues.put(Util.LOCATION, food.getLocation());
        contentValues.put(Util.PICKUP_TIMES, food.getTime());
        contentValues.put(Util.QUANTITY, food.getQuantity());
        long newRowId = db.insert(Util.TABLE_NAME_FOOD, null, contentValues);
        db.close();
        return newRowId;
    }
    public List<Food> fetchAllFoodItems(){
        SQLiteDatabase db = this.getReadableDatabase();
        String selectAll = "SELECT * FROM " + Util.TABLE_NAME_FOOD;
        Cursor cursor = db.rawQuery(selectAll, null);
        //TODO orderby date
        List<Food> foodList = new ArrayList<>();
        if(cursor.moveToFirst()){
            do {
                Food food = new Food();
                food.setUser_id((cursor.getInt(cursor.getColumnIndex(Util.USER_ID))));
                food.setImage_path(cursor.getString(cursor.getColumnIndex(Util.IMAGE_PATH)));
                food.setTitle(cursor.getString(cursor.getColumnIndex(Util.FOOD_TITLE)));
                food.setDescription(cursor.getString(cursor.getColumnIndex(Util.FOOD_DESCRIPTION)));
                foodList.add(food);
            }
            while(cursor.moveToNext());
            db.close();
        }
        return foodList;
    }
    public List<Food> fetchAllFoodItems(int userId){
        SQLiteDatabase db = this.getReadableDatabase();
        String selectAll = "SELECT * FROM " + Util.TABLE_NAME_FOOD + " WHERE " + Util.USER_ID + "= ?";
        Cursor cursor = db.rawQuery(selectAll, new String[]{String.valueOf(userId)});
        //TODO orderby date
        List<Food> foodList = new ArrayList<>();
        if(cursor.moveToFirst()){
            do {
                Food food = new Food();
                food.setUser_id((cursor.getInt(cursor.getColumnIndex(Util.USER_ID))));
                food.setImage_path(cursor.getString(cursor.getColumnIndex(Util.IMAGE_PATH)));
                food.setTitle(cursor.getString(cursor.getColumnIndex(Util.FOOD_TITLE)));
                food.setDescription(cursor.getString(cursor.getColumnIndex(Util.FOOD_DESCRIPTION)));
                foodList.add(food);
            }
            while(cursor.moveToNext());
            db.close();
        }
        return foodList;
    }
}
