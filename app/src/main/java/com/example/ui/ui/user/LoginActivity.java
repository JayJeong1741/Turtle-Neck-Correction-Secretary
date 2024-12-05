package com.example.ui.ui.user; // 패키지 이름에 맞게 조정하세요.
import static androidx.core.content.ContentProviderCompat.requireContext;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.ui.MainActivity;
import com.example.ui.R;
import com.example.ui.database.FirebaseManager;
import com.example.ui.database.UserDataManager;
import com.example.ui.ui.notification.NotificationWorker;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextId;
    private EditText editTextPassword;
    private Button buttonLogin;
    private Button buttonSignup;
    private UserDataManager userDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login); // 로그인 XML 레이아웃 설정

        editTextId = findViewById(R.id.login_id);
        editTextPassword = findViewById(R.id.login_pw);
        buttonLogin = findViewById(R.id.btn_login);
        buttonSignup = findViewById(R.id.btn_signup); // 회원가입 버튼 초기화

        userDataManager = new UserDataManager();

        buttonLogin.setOnClickListener(v -> {
            String id = editTextId.getText().toString();
            String password = editTextPassword.getText().toString();

            if (id.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "아이디와 비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                return;
            }
            // 서버에서 사용자 데이터 확인
            userDataManager.getUserData(id, new FirebaseManager.FirebaseCallback() {
                @Override
                public void onSuccess(Object data) {
                    if (data == null) {
                        // ID가 존재하지 않는 경우
                        Toast.makeText(LoginActivity.this, "아이디 또는 비밀번호를 확인해 주세요.", Toast.LENGTH_SHORT).show();
                    } else {
                        Map<String, Object> userData = (Map<String, Object>) data;
                        String storedPassword = (String) userData.get("password");

                        // 비밀번호 확인
                        if (password.equals(storedPassword)) {

                            // 로그인 성공 시 SharedPreferences에 사용자 정보 저장
                            SharedPreferences sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("USER_ID", id); // 사용자 ID 저장
                            editor.putBoolean("IS_LOGGED_IN", true); // 로그인 상태 저장
                            editor.apply();
                            // 로그인 성공 시 메인 액티비티로 이동
                            Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // 비밀번호가 틀림
                            Toast.makeText(LoginActivity.this, "아이디 또는 비밀번호를 확인해 주세요", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    // Firebase에서 데이터 읽기 실패
                    Toast.makeText(LoginActivity.this, "로그인 오류: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });


        buttonSignup.setOnClickListener(v -> {
            // 회원가입 화면으로 이동
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });



    }

}