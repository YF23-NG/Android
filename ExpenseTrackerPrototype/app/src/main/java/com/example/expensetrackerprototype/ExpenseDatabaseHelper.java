package com.example.expensetrackerprototype;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import java.util.ArrayList;

public class ExpenseDatabaseHelper extends SQLiteOpenHelper
{
    public ExpenseDatabaseHelper(Context context){
        super(context, "ExpenseTracker.db", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        String createTable= "CREATE TABLE expenses(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "day INTEGER, "+
                "type TEXT, "+
                "amount INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }

    public long insertExpense(Expense expenses){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("day", expenses.day);
        values.put("type", expenses.type);
        values.put("amount", expenses.amount);

        return db.insert("expenses", null, values);
    }

    public ArrayList<Expense> getAllExpenses() {
        ArrayList<Expense> expenseList = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM expenses", null);

        while (cursor.moveToNext()) {
            int day = cursor.getInt(cursor.getColumnIndexOrThrow("day"));
            String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
            int amount = cursor.getInt(cursor.getColumnIndexOrThrow("amount"));
            long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));

            Expense expense = new Expense(day, type, amount);
            expense.id = id;
            expenseList.add(expense);
        }

        cursor.close();

        return expenseList;
    }

    public int deleteExpense(long id) {
        SQLiteDatabase db = getWritableDatabase();

        return db.delete(
                "expenses",
                "id = ?",
                new String[]{String.valueOf(id)}
        );
    }

}
