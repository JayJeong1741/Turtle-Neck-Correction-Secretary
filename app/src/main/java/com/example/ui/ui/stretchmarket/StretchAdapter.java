package com.example.ui.ui.stretchmarket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ui.R;

import java.util.List;


public class StretchAdapter extends RecyclerView.Adapter<StretchAdapter.StretchViewHolder> {
    private Context context;
    private List<StretchItem> stretchItems;
    private DatabaseHelper dbHelper;

    public StretchAdapter(Context context, List<StretchItem> stretchItems) {
        this.context = context;
        this.stretchItems = stretchItems;
        this.dbHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public StretchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_stretch, parent, false);
        return new StretchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StretchViewHolder holder, int position) {
        StretchItem item = stretchItems.get(position);
        holder.nameTextView.setText(item.getName());

        StepAdapter stepAdapter = new StepAdapter(context, item.getSteps());
        holder.stepsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.stepsRecyclerView.setAdapter(stepAdapter);

        holder.nameTextView.setOnClickListener(v -> {
            if (holder.stepsRecyclerView.getVisibility() == View.GONE) {
                holder.stepsRecyclerView.setVisibility(View.VISIBLE);
            } else {
                holder.stepsRecyclerView.setVisibility(View.GONE);
            }
        });

        // 데이터베이스에 이미 해당 스트레치가 존재하는지 확인
        boolean isAlreadyInTodo = dbHelper.isTodoStretchExists(item.getName());

        // 체크박스 상태 초기화 항상 false로 시작
        holder.todoCheckBox.setChecked(false);

        // 체크박스 리스너 수정
        holder.todoCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // 체크된 경우에만 데이터베이스에 추가
                dbHelper.insertTodoStretch(item.getName());
            } else {
                // 체크 해제 시 데이터베이스에서 삭제
                dbHelper.deleteStretch(item.getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return stretchItems.size();
    }

    class StretchViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        RecyclerView stepsRecyclerView;
        CheckBox todoCheckBox;

        StretchViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.stretchNameTextView);
            stepsRecyclerView = itemView.findViewById(R.id.stepsRecyclerView);
            todoCheckBox = itemView.findViewById(R.id.todoCheckBox);
        }
    }
}