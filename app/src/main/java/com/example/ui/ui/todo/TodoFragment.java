package com.example.ui.ui.todo;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.ui.R;
import com.example.ui.ui.stretchmarket.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class TodoFragment extends Fragment {
    private DatabaseHelper dbHelper;
    private TodoStretchAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todostretch, container, false);

        dbHelper = new DatabaseHelper(requireContext());

        // 24시간 지난 항목 삭제
        dbHelper.deleteOldEntries();

        RecyclerView todoRecyclerView = view.findViewById(R.id.todoStretchRecyclerView);

        List<String> todoStretches = getTodoStretches();
        adapter = new TodoStretchAdapter(requireContext(), todoStretches, dbHelper);
        todoRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        todoRecyclerView.setAdapter(adapter);

        return view;
    }

    private List<String> getTodoStretches() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        long twentyFourHoursAgo = System.currentTimeMillis() - (24 * 60 * 60 * 1000);

        String[] projection = {"stretch_name"};
        String selection = "timestamp >= ?";
        String[] selectionArgs = {String.valueOf(twentyFourHoursAgo)};

        Cursor cursor = db.query("todo_stretches", projection, selection, selectionArgs, null, null, null);

        List<String> stretches = new ArrayList<>();
        while (cursor.moveToNext()) {
            stretches.add(cursor.getString(cursor.getColumnIndexOrThrow("stretch_name")));
        }
        cursor.close();

        return stretches;
    }
}