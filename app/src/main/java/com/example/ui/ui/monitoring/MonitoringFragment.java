package com.example.ui.ui.monitoring;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.webrtc.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.animation.ObjectAnimator;
import android.view.animation.DecelerateInterpolator;

import com.example.ui.R;

public class MonitoringFragment extends Fragment {
    private TextView predict, percent;
    private SurfaceViewRenderer remoteVideoView;
    private WebRTCService webRTCService;
    private boolean isBound = false;
    private Intent intent;
    private ServiceConnection serviceConnection;
    private ProgressBar progressBar;

//onCreateView -> onViewCreate -> onStart (프래그먼트 실행 시)
//onPause -> onDestroyView -> onDestroy (프래그먼트에서 뒤로가기, 종료버튼)


    @SuppressLint("ObsoleteSdkInt")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("onCreateView","onCreateView");
        View view = inflater.inflate(R.layout.fragment_monitoring, container, false);

        predict = view.findViewById(R.id.predict);
        percent = view.findViewById(R.id.percent);


        Log.d("remoteView Status in onCreateView","remoteView Status : " + remoteVideoView);
        Button btnStop = view.findViewById(R.id.button);
        intent = new Intent(requireActivity(), WebRTCService.class);
        if (!isServiceRunning()) {
            // 서비스가 실행 중이 아니면 startService 호출
            Intent intent = new Intent(requireActivity(), WebRTCService.class);  // requireActivity() 사용
            requireActivity().startService(intent); // startService 호출
        }
        setServiceConnection();
        requireActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        btnStop.setOnClickListener(v -> {
            if (isBound) {
                isBound = false;
            }
            requireActivity().stopService(intent);
            if (remoteVideoView != null) {
                remoteVideoView.release();
            }
            requireActivity().stopService(intent);
            requireActivity().unbindService(serviceConnection);
            NavController navController = Navigation.findNavController(requireView());
            navController.popBackStack();
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        return view;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("onDestroyView","onDestroyView");
        if (isBound) {
            requireActivity().unbindService(serviceConnection);
            isBound = false;
        }
        }
    @Override
    public void onStart() {
        super.onStart();
        Log.d("onStart","onStart");
    }

    @Override
    public void onPause() {
        super.onPause();
        //requireActivity().unbindService(serviceConnection);
        Log.d("onPause","onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("onDestroy","onDestroy");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("onViewCreate","onViewCreate");
        remoteVideoView = view.findViewById(R.id.remote_video_view);
        progressBar =  view.findViewById(R.id.progressBar);
        progressBar.setMax(100);
        progressBar.setMin(0);
        if (webRTCService != null && isBound) {
            // EglBase 컨텍스트로 다시 초기화
            remoteVideoView.init(webRTCService.getEglBaseContext(), null);
            remoteVideoView.setMirror(false);
            remoteVideoView.setEnableHardwareScaler(true);

            Log.d("remoteView Status before addSink","remoteView Status : " + remoteVideoView);

            // 기존 스트리밍 중이던 영상을 새로운 뷰에 다시 연결
            webRTCService.setRemoteVideoSink(remoteVideoView);
        }
    }

    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) requireActivity().getSystemService(Context.ACTIVITY_SERVICE);  // requireActivity() 사용
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (WebRTCService.class.getName().equals(service.service.getClassName())) {
                return true; // 서비스가 실행 중인 경우
            }
        }
        return false; // 서비스가 실행 중이지 않으면
    }

    private void setServiceConnection(){
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                WebRTCService.LocalBinder binder = (WebRTCService.LocalBinder) service;
                webRTCService = binder.getService();
                isBound = true;

                // Initialize video view with service's EglBase context
                remoteVideoView.init(webRTCService.getEglBaseContext(), null);
                remoteVideoView.setMirror(false);
                remoteVideoView.setEnableHardwareScaler(true);

                // Set video sink and data channel observer
                webRTCService.setRemoteVideoSink(remoteVideoView);
                webRTCService.setMessageListener(new WebRTCMessageListener() {
                    @SuppressLint({"SetTextI18n", "DefaultLocale"})
                    @Override
                    public void onMessageReceived(String message) {
                        if(isAdded() && getActivity() != null){
                            requireActivity().runOnUiThread(() -> {
                                try {
                                    // 메시지에서 숫자만 추출
                                    String numericValue = message.replaceAll("[^0-9.]", "");
                                    float value = Float.parseFloat(numericValue);

                                    // ProgressBar 애니메이션
                                    ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", progressBar.getProgress(), (int)(value));
                                    animation.setDuration(500); // 애니메이션 시간 조절 가능
                                    animation.setInterpolator(new DecelerateInterpolator());
                                    animation.start();

                                    // TextView에 원본 값 그대로 표시
                                    predict.setText("거북목 예측도");
                                    percent.setText(value + "%");

                                } catch (NumberFormatException e) {
                                    Log.e("MonitoringFragment", "Error parsing message: " + message, e);
                                }
                            });
                        }
                    }
                });
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                webRTCService = null;
                isBound = false;
                requireActivity().unbindService(serviceConnection);
            }
        };
    }
}
