package com.example.ui.ui.todo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ui.R;
import com.example.ui.ui.stretchmarket.DatabaseHelper;
import com.example.ui.ui.stretchmarket.StepAdapter;
import com.example.ui.ui.stretchmarket.StretchItem;

import java.util.ArrayList;
import java.util.List;

public class TodoStretchAdapter extends RecyclerView.Adapter<TodoStretchAdapter.TodoStretchViewHolder> {
    private Context context;
    private List<String> todoStretches;
    private DatabaseHelper dbHelper;
    private List<StretchItem> stretchItems;

    public TodoStretchAdapter(Context context, List<String> todoStretches, DatabaseHelper dbHelper) {
        this.context = context;
        this.todoStretches = todoStretches;
        this.dbHelper = dbHelper;
        initializeStretchItems();
    }

    @NonNull
    @Override
    public TodoStretchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_todo_stretch, parent, false);
        return new TodoStretchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoStretchViewHolder holder, int position) {
        String stretchName = todoStretches.get(position);
        holder.todoStretchNameTextView.setText(stretchName);

        StretchItem currentStretch = null;
        for (StretchItem item : stretchItems) {
            if (item.getName().equals(stretchName)) {
                currentStretch = item;
                break;
            }
        }

        if (currentStretch != null) {
            StepAdapter stepAdapter = new StepAdapter(context, currentStretch.getSteps());
            holder.stepsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            holder.stepsRecyclerView.setAdapter(stepAdapter);
        }

        holder.todoStretchNameTextView.setOnClickListener(v -> {
            if (holder.stepsRecyclerView.getVisibility() == View.GONE) {
                holder.stepsRecyclerView.setVisibility(View.VISIBLE);
            } else {
                holder.stepsRecyclerView.setVisibility(View.GONE);
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            dbHelper.deleteStretch(stretchName);
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                todoStretches.remove(currentPosition);
                notifyItemRemoved(currentPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return todoStretches.size();
    }

    private void initializeStretchItems() {
        stretchItems = new ArrayList<>();
        stretchItems.add(new StretchItem("턱 당기기 스트레칭", "Cervical Retraction", createCervicalRetractionSteps()));
        stretchItems.add(new StretchItem("상부 승모근 스트레칭", "Upper Trap Stretch", createUpperTrapStretchSteps()));
        stretchItems.add(new StretchItem("가슴 열기 스트레칭", "Pec Stretch", createPecStretchSteps()));
        stretchItems.add(new StretchItem("목 좌우 스트레칭", "Neck Side Stretch", createNeckSideStretchSteps()));
        stretchItems.add(new StretchItem("흉추 스트레칭", "Thoracic Extension Stretch", createThoracicExtensionSteps()));
    }

    private List<String> createCervicalRetractionSteps() {
        List<String> steps = new ArrayList<>();
        steps.add("벽에 등을 붙이고 바르게 섭니다.");
        steps.add("턱을 살짝 아래로 내린 상태에서 목을 뒤로 밀어 벽에 닿게 합니다.");
        steps.add("5초간 유지한 후 원래 자세로 돌아옵니다.");
        steps.add("10회 반복.");
        return steps;
    }

    private List<String> createUpperTrapStretchSteps() {
        List<String> steps = new ArrayList<>();
        steps.add("바르게 앉아 오른손으로 머리 왼쪽을 잡습니다.");
        steps.add("머리를 오른쪽으로 천천히 당겨 귀가 어깨에 가까워지게 합니다.");
        steps.add("15초 유지 후 반대쪽도 동일하게 진행.");
        steps.add("양쪽 각각 3회 반복.");
        return steps;
    }

    private List<String> createPecStretchSteps() {
        List<String> steps = new ArrayList<>();
        steps.add("문틀 앞에 서서 양손을 문틀 위쪽에 올립니다.");
        steps.add("한 발을 앞으로 내딛고 상체를 천천히 앞으로 밀어 가슴을 엽니다.");
        steps.add("15초간 유지 후 천천히 돌아옵니다.");
        steps.add("3회 반복.");
        return steps;
    }

    private List<String> createNeckSideStretchSteps() {
        List<String> steps = new ArrayList<>();
        steps.add("바르게 앉거나 선 상태에서 어깨를 편안히 둡니다.");
        steps.add("오른손으로 머리 왼쪽을 잡고 천천히 오른쪽으로 당겨 목 옆쪽이 늘어나도록 합니다.");
        steps.add("15초간 유지한 뒤 반대쪽도 동일하게 반복.");
        steps.add("양쪽 각각 3회.");
        return steps;
    }

    private List<String> createThoracicExtensionSteps() {
        List<String> steps = new ArrayList<>();
        steps.add("의자에 앉아 손을 머리 뒤에 깍지 낍니다.");
        steps.add("등을 의자 등받이에 기대며 상체를 천천히 뒤로 젖힙니다.");
        steps.add("10초간 유지하며 가슴을 열고 깊게 호흡.");
        steps.add("3회 반복.");
        return steps;
    }

    class TodoStretchViewHolder extends RecyclerView.ViewHolder {
        TextView todoStretchNameTextView;
        ImageButton deleteButton;
        RecyclerView stepsRecyclerView;

        TodoStretchViewHolder(@NonNull View itemView) {
            super(itemView);
            todoStretchNameTextView = itemView.findViewById(R.id.todoStretchNameTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            stepsRecyclerView = itemView.findViewById(R.id.todoStepsRecyclerView);
        }
    }
}