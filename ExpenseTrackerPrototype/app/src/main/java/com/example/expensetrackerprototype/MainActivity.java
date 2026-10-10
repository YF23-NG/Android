package com.example.expensetrackerprototype;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import android.text.Editable;
import android.text.TextWatcher;

public class MainActivity extends AppCompatActivity {

    EditText dayInput;
    EditText typeInput;
    EditText amountInput;
    Button addButton;

    ArrayList<Expense> expenses = new ArrayList<Expense>();
    ArrayList<Expense> filteredExpenses = new ArrayList<>();
    ExpenseAdapter adapter;
    ExpenseDatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        dbHelper = new ExpenseDatabaseHelper(this);

        dayInput= findViewById(R.id.dayInput);
        typeInput= findViewById(R.id.typeInput);
        amountInput= findViewById(R.id.amountInput);
        addButton= findViewById(R.id.addButton);

        RecyclerView expenseRecyclerView = findViewById(R.id.expenseRecyclerView);

        expenseRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ExpenseAdapter(filteredExpenses, expenses, dbHelper, () -> updateTotals());

        expenseRecyclerView.setAdapter(adapter);
        expenses.addAll(dbHelper.getAllExpenses());

        String initialDayText = dayInput.getText().toString();

        if (!initialDayText.isEmpty()) {
            int selectedDay = Integer.parseInt(initialDayText);

            if (selectedDay >= 1 && selectedDay <= 31) {
                filterExpensesByDay(selectedDay);
                updateTotals();
            }
        }


        dayInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String dayText = s.toString();

                if (!dayText.isEmpty()) {
                    int selectedDay = Integer.parseInt(dayText);

                    if (selectedDay >= 1 && selectedDay <= 31) {
                        filterExpensesByDay(selectedDay);
                        updateTotals();
                    }
                }
            }
        });

        addButton.setOnClickListener(v-> {
            String dayText= dayInput.getText().toString();
            String type= typeInput.getText().toString();
            String amountText= amountInput.getText().toString();

            if(dayText.isEmpty()||type.isEmpty()||amountText.isEmpty()){
                return;
            }

            int day= Integer.parseInt(dayInput.getText().toString());
            int amount= Integer.parseInt(amountInput.getText().toString());

            if(day<1||day>31||amount<=0){
                return;
            }

            Expense expense = new Expense(day, type, amount);

            long result = dbHelper.insertExpense(expense);

            if (result != -1) {
                expense.id = result;
                expenses.add(expense);

                int selectedDay = Integer.parseInt(dayInput.getText().toString());

                if (day == selectedDay) {
                    filteredExpenses.add(expense);
                    adapter.notifyItemInserted(filteredExpenses.size() - 1);
                }

                updateTotals();
            }

        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    int calculateTotalExpenses() {
        int total = 0;

        for (Expense expense : expenses) {
            total += expense.amount;
        }

        return total;
    }
    int calculateDailyTotal(int selectedDay) {
        int total = 0;

        for (Expense expense : expenses) {
            if (expense.day == selectedDay) {
                total += expense.amount;
            }
        }

        return total;
    }
    void updateTotals() {
        int selectedDay = Integer.parseInt(dayInput.getText().toString());

        int dailyTotal = calculateDailyTotal(selectedDay);
        int monthlyTotal = calculateTotalExpenses();
        int remaining = 5000 - monthlyTotal;

        TextView totalText = findViewById(R.id.totalText);
        TextView remainingText = findViewById(R.id.remainingText);

        totalText.setText("Day " + selectedDay + " Total: ₹" + dailyTotal);
        remainingText.setText("Monthly Remaining: ₹" + remaining);
    }
    void filterExpensesByDay(int selectedDay) {
        filteredExpenses.clear();

        for (Expense expense : expenses) {
            if (expense.day == selectedDay) {
                filteredExpenses.add(expense);
            }
        }

        adapter.notifyDataSetChanged();
    }

}