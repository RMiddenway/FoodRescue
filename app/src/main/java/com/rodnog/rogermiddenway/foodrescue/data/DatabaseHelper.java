package com.rodnog.rogermiddenway.foodrescue.data;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.rodnog.rogermiddenway.foodrescue.MainActivity;
import com.rodnog.rogermiddenway.foodrescue.model.Food;
import com.rodnog.rogermiddenway.foodrescue.model.User;
import com.rodnog.rogermiddenway.foodrescue.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    Context mContext;

    public DatabaseHelper(@Nullable Context context) {
        super(context, Util.DATABASE_NAME, null, Util.DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + Util.TABLE_NAME_USERS + "(" + Util.USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Util.NAME + " TEXT," + Util.EMAIL + " TEXT," + Util.PHONE + " TEXT, " + Util.ADDRESS + " TEXT, " + Util.PASSWORD + " TEXT)";
        db.execSQL(CREATE_USER_TABLE);
        String CREATE_FOOD_TABLE = "CREATE TABLE " + Util.TABLE_NAME_FOOD + "(" + Util.FOOD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Util.USER_ID + " INTEGER," + Util.IMAGE_PATH + " TEXT, " + Util.FOOD_TITLE + " TEXT, " + Util.FOOD_DESCRIPTION
                + " TEXT, " + Util.START_TIME +  " INTEGER, " + Util.END_TIME + " INTEGER, " + Util.QUANTITY + " INTEGER, " + Util.LATITUDE
                + " DECIMAL, " + Util.LONGITUDE + " DECIMAL, " + Util.PRICE + " DECIMAL, " + Util.TAGS + " TEXT)";
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
        contentValues.put(Util.START_TIME, food.getStartTime());
        contentValues.put(Util.END_TIME, food.getEndTime());
        contentValues.put(Util.LATITUDE, food.getLocation().latitude);
        contentValues.put(Util.LONGITUDE, food.getLocation().longitude);
        contentValues.put(Util.QUANTITY, food.getQuantity());
        contentValues.put(Util.PRICE, food.getPrice());

        StringBuilder sb = new StringBuilder();
        if(food.getTags() != null) {
            for (String tag : food.getTags()) {
                sb.append(tag);
                sb.append("\t");
            }
        }
        else{
            sb.append("");
        }
        contentValues.put(Util.TAGS, sb.toString());

        long newRowId = db.insert(Util.TABLE_NAME_FOOD, null, contentValues);
        db.close();
        return newRowId;
    }

    public long updateFood(Food food){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Util.FOOD_ID, food.getFood_id());
        contentValues.put(Util.USER_ID, food.getUser_id());
        contentValues.put(Util.IMAGE_PATH, food.getImage_path());
        contentValues.put(Util.FOOD_TITLE, food.getTitle());
        contentValues.put(Util.FOOD_DESCRIPTION, food.getDescription());
        contentValues.put(Util.START_TIME, food.getStartTime());
        contentValues.put(Util.END_TIME, food.getEndTime());
        contentValues.put(Util.LATITUDE, food.getLocation().latitude);
        contentValues.put(Util.LONGITUDE, food.getLocation().longitude);
        contentValues.put(Util.QUANTITY, food.getQuantity());
        contentValues.put(Util.PRICE, food.getPrice());
        StringBuilder sb = new StringBuilder();
        for (String tag : food.getTags())
        {
            sb.append(tag);
            sb.append("\t");
        }
        contentValues.put(Util.TAGS, sb.toString());

        long newRowId = db.replace(Util.TABLE_NAME_FOOD, null, contentValues);
        db.close();
        return newRowId;
    }

    public Food fetchFood(int foodId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String select = "SELECT * FROM " + Util.TABLE_NAME_FOOD + " WHERE " + Util.FOOD_ID + "= ?";
        Cursor cursor = db.rawQuery(select, new String[]{String.valueOf(foodId)});

        if(cursor.moveToFirst()){
            db.close();
            Food food = new Food();
            food.setFood_id((cursor.getInt(cursor.getColumnIndex(Util.FOOD_ID))));
            food.setUser_id((cursor.getInt(cursor.getColumnIndex(Util.USER_ID))));
            food.setImage_path(cursor.getString(cursor.getColumnIndex(Util.IMAGE_PATH)));
            food.setTitle(cursor.getString(cursor.getColumnIndex(Util.FOOD_TITLE)));
            food.setDescription(cursor.getString(cursor.getColumnIndex(Util.FOOD_DESCRIPTION)));
            food.setQuantity(cursor.getInt(cursor.getColumnIndex(Util.QUANTITY)));
            food.setStartTime(cursor.getLong(cursor.getColumnIndex(Util.START_TIME)));
            food.setEndTime(cursor.getLong(cursor.getColumnIndex(Util.END_TIME)));
            food.setLocation(cursor.getDouble(cursor.getColumnIndex(Util.LATITUDE)), cursor.getDouble(cursor.getColumnIndex(Util.LONGITUDE)));
            food.setPrice(cursor.getFloat(cursor.getColumnIndex(Util.PRICE)));
            String tagString = cursor.getString(cursor.getColumnIndex(Util.TAGS));
            List<String> tagList = new ArrayList<String>(Arrays.asList(tagString.split("\t")));
            food.setTags(tagList);
            return food;
        }
        else{
            Log.d("DATABASE", "FOOD NOT FOUUND");
            db.close();
            return null;
        }

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
                food.setFood_id((cursor.getInt(cursor.getColumnIndex(Util.FOOD_ID))));
                food.setUser_id((cursor.getInt(cursor.getColumnIndex(Util.USER_ID))));
                food.setImage_path(cursor.getString(cursor.getColumnIndex(Util.IMAGE_PATH)));
                food.setTitle(cursor.getString(cursor.getColumnIndex(Util.FOOD_TITLE)));
                food.setDescription(cursor.getString(cursor.getColumnIndex(Util.FOOD_DESCRIPTION)));
                food.setQuantity(cursor.getInt(cursor.getColumnIndex(Util.QUANTITY)));
                food.setStartTime(cursor.getInt(cursor.getColumnIndex(Util.START_TIME)));
                food.setEndTime(cursor.getInt(cursor.getColumnIndex(Util.END_TIME)));
                food.setLocation(cursor.getDouble(cursor.getColumnIndex(Util.LATITUDE)), cursor.getDouble(cursor.getColumnIndex(Util.LONGITUDE)));
                food.setPrice(cursor.getFloat(cursor.getColumnIndex(Util.PRICE)));
                String tagString = cursor.getString(cursor.getColumnIndex(Util.TAGS));
                List<String> tagList = new ArrayList<String>(Arrays.asList(tagString.split("\t")));
                food.setTags(tagList);
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
                food.setFood_id((cursor.getInt(cursor.getColumnIndex(Util.FOOD_ID))));
                food.setUser_id((cursor.getInt(cursor.getColumnIndex(Util.USER_ID))));
                food.setImage_path(cursor.getString(cursor.getColumnIndex(Util.IMAGE_PATH)));
                food.setTitle(cursor.getString(cursor.getColumnIndex(Util.FOOD_TITLE)));
                food.setDescription(cursor.getString(cursor.getColumnIndex(Util.FOOD_DESCRIPTION)));
                food.setQuantity(cursor.getInt(cursor.getColumnIndex(Util.QUANTITY)));
                food.setStartTime(cursor.getInt(cursor.getColumnIndex(Util.START_TIME)));
                food.setEndTime(cursor.getInt(cursor.getColumnIndex(Util.END_TIME)));
                food.setLocation(cursor.getDouble(cursor.getColumnIndex(Util.LATITUDE)), cursor.getDouble(cursor.getColumnIndex(Util.LONGITUDE)));
                food.setPrice(cursor.getFloat(cursor.getColumnIndex(Util.PRICE)));
                String tagString = cursor.getString(cursor.getColumnIndex(Util.TAGS));
                List<String> tagList = new ArrayList<String>(Arrays.asList(tagString.split("\t")));
                food.setTags(tagList);

                foodList.add(food);
            }
            while(cursor.moveToNext());
            db.close();
        }
        return foodList;
    }
}
