package com.example.ui.ui.stretchmarket;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ui.R;

import java.util.ArrayList;
import java.util.List;

public class stretchMarketFragment extends Fragment {
    private List<StretchItem> stretchItems;
    private StretchAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stretchmarket, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.stretchRecyclerView);
        Button todoButton = view.findViewById(R.id.todoStretchButton);

        stretchItems = new ArrayList<>();
        initializeStretchItems();

        adapter = new StretchAdapter(requireContext(), stretchItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);


        todoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_stretchMarket_to_todo);
            }
        });

        return view;
    }

    private void initializeStretchItems() {
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
}