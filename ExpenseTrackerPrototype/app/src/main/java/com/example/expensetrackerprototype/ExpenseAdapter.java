package com.example.expensetrackerprototype;

import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>
{

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder{
        TextView itemType;
        TextView itemAmount;
        Button deleteButton;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            itemType = itemView.findViewById(R.id.itemType);
            itemAmount = itemView.findViewById(R.id.itemAmount);
            deleteButton = itemView.findViewById(R.id.deleteButton);

        }
    }

    ArrayList<Expense> filteredExpenses;
    ArrayList<Expense> expenses;
    Runnable onExpenseDeleted;
    ExpenseDatabaseHelper dbHelper;

    public ExpenseAdapter(ArrayList<Expense> filteredExpenses,
                          ArrayList<Expense> expenses,
                          ExpenseDatabaseHelper dbHelper,
                          Runnable onExpenseDeleted) {
        this.filteredExpenses = filteredExpenses;
        this.expenses = expenses;
        this.dbHelper = dbHelper;
        this.onExpenseDeleted = onExpenseDeleted;
    }
    @NonNull
    @Override
    public ExpenseAdapter.ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.expense_item, parent, false);

        return new ExpenseAdapter.ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseAdapter.ExpenseViewHolder holder, int position) {
        Expense expense = filteredExpenses.get(position);

        holder.itemType.setText(expense.type);
        holder.itemAmount.setText("₹" + expense.amount);

        holder.deleteButton.setOnClickListener(v -> {
            int currentPosition = holder.getBindingAdapterPosition();

            if (currentPosition != RecyclerView.NO_POSITION) {
                Expense deletedExpense = filteredExpenses.get(currentPosition);

                int rowsDeleted = dbHelper.deleteExpense(deletedExpense.id);

                if (rowsDeleted > 0) {
                    filteredExpenses.remove(currentPosition);
                    expenses.remove(deletedExpense);
                    notifyItemRemoved(currentPosition);
                    onExpenseDeleted.run();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredExpenses.size();
    }
}
