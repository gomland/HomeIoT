package my.home.mobileiot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

import my.home.mobileiot.data.Constants;
import my.home.mobileiot.data.Prefs;
import my.home.mobileiot.net.HttpListener;
import my.home.mobileiot.net.MyNetManager;
import my.home.mobileiot.tools.DeviceTool;
import my.home.mobileiot.view.Popup;

/**
 * Created by Supuro on 2016-11-11.
 */

public class LoginActivity extends Activity{
    private boolean showLogin = false;
    private EditText loginIdText;
    private EditText loginPwText;
    private EditText regId;
    private EditText regPw;
    private TextView regResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );

        String autoLogin = Prefs.get(getApplicationContext(), Constants.KEY_AUTO_LOGIN);
        if(autoLogin.equals("Y")) {
            CheckBox autoCheck = (CheckBox) findViewById(R.id.l_auto_login);
            autoCheck.setChecked(true);
            String id = Prefs.get(getApplicationContext(), Constants.KEY_LOGIN_ID);
            String pw = Prefs.get(getApplicationContext(), Constants.KEY_PASSWORD);
            loginAction(id, pw);
        }
        else
            initView();
    }

    void initView(){
        loginIdText = (EditText) findViewById(R.id.l_login_id);
        loginPwText = (EditText) findViewById(R.id.l_login_pw);
        regId = (EditText) findViewById(R.id.l_reg_id);
        regPw = (EditText) findViewById(R.id.l_reg_pw);
        regResult = (TextView) findViewById(R.id.l_reg_result);

        Button loginBtn = (Button) findViewById(R.id.l_login_btn);
        Button regSendBtn = (Button) findViewById(R.id.l_reg_send_btn);
        Button regCloseBtn = (Button) findViewById(R.id.l_reg_close_btn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(showLogin){
                    String id = loginIdText.getText().toString();
                    String pw = loginPwText.getText().toString();
                    loginAction(id, pw);
                }
                else {
                    setRegisterView(true);
                }
            }
        });

        regSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String id = regId.getText().toString();
                final String pw = regPw.getText().toString();

                if(id.length() == 0)
                    regResult.setText("아이디를 입력하세요.");
                else if(pw.length() == 0)
                    regResult.setText("비밀번호를 입력하세요.");
                else {
                    Popup.startProgress(LoginActivity.this, "가입 중...");
                    MyNetManager.addUser(MyNetManager.ip, "Home", id, pw, DeviceTool.getDeviceUUID(LoginActivity.this),
                            new HttpListener() {
                                @Override
                                public void httpRequestListener(int code, String message) {
                                    Popup.stop();

                                    if (code == HttpURLConnection.HTTP_OK) {
                                        JSONObject resJson = null;
                                        try {
                                            resJson = new JSONObject(message);
                                            if (resJson.has("result") && resJson.get("result").equals("success")) {
                                                loginIdText.setText(id);
                                                loginPwText.setText(pw);
                                                setRegisterView(false);
                                            }
                                            else {
                                               regResult.setText(resJson.get("message").toString());
                                            }
                                        } catch (JSONException e) {
                                            regResult.setText("JSON 파싱 에러...");
                                        }
                                    } else {
                                        regResult.setText("통신실패...");
                                    }
                                }
                            });
                }
            }
        });

        regCloseBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                setRegisterView(false);
            }
        });

        loginIdText.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() > 0)
                    showLoginBtn(true);
                else
                    showLoginBtn(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    void loginAction(final String id, final String pw){
        if(id.length() == 0)
            Popup.startResultPopup(LoginActivity.this, "아이디를 입력해주세요.");
        else if(pw.length() == 0)
            Popup.startResultPopup(LoginActivity.this, "패스워드를 입력해주세요.");
        else {
            Popup.startProgress(LoginActivity.this, "인증 중...");
            MyNetManager.authorize(MyNetManager.ip, id, pw, "PAD",
                    new HttpListener() {
                        @Override
                        public void httpRequestListener(int code, String message) {
                            Popup.stop();

                            if (code == HttpURLConnection.HTTP_OK) {
                                JSONObject resJson = null;
                                try {
                                    resJson = new JSONObject(message);
                                    if (resJson.has("result") && resJson.get("result").equals("success")) {
                                        Prefs.put(getApplicationContext(), Constants.KEY_AUTH_DEVICES, resJson.get("devices").toString());
                                        Prefs.put(getApplicationContext(), Constants.KEY_GUUID, resJson.get("guuid").toString());

                                        CheckBox autoCheck = (CheckBox) findViewById(R.id.l_auto_login);
                                        String autoLogin = "N";
                                        if(autoCheck.isChecked())
                                            autoLogin = "Y";
                                        Prefs.put(getApplicationContext(), Constants.KEY_AUTO_LOGIN, autoLogin);
                                        Prefs.put(getApplicationContext(), Constants.KEY_LOGIN_ID, id);
                                        Prefs.put(getApplicationContext(), Constants.KEY_PASSWORD, pw);

                                        Intent intent =  new Intent(getApplicationContext(), HomeActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else {
                                        Popup.startResultPopup(LoginActivity.this, resJson.get("message").toString());
                                    }
                                } catch (JSONException e) {
                                    Popup.startResultPopup(LoginActivity.this, "Json 파싱에러...");
                                }
                            } else {
                                Popup.startResultPopup(LoginActivity.this, "통신실패");
                            }
                        }
                    });
        }
    }

    void showLoginBtn(boolean show){
        Button loginBtn = (Button) findViewById(R.id.l_login_btn);

        if(show)
            loginBtn.setBackgroundResource(R.drawable.login_btn);
        else
            loginBtn.setBackgroundResource(R.drawable.regi_btn);
        showLogin = show;
    }

    void setRegisterView(boolean on){
        LinearLayout dimmer = (LinearLayout) findViewById(R.id.l_dimmer);
        LinearLayout regLayout = (LinearLayout) findViewById(R.id.l_sign_in_layout);
        TextView regResult = (TextView) findViewById(R.id.l_reg_result);

        if(on){
            dimmer.setVisibility(View.VISIBLE);
            regLayout.setVisibility(View.VISIBLE);
            regLayout.setAnimation(AnimationUtils.loadAnimation(this, R.anim.left_in));
            regResult.setText("");
        }
        else{
            dimmer.setVisibility(View.GONE);
            regLayout.setVisibility(View.GONE);
            regLayout.setAnimation(AnimationUtils.loadAnimation(this, R.anim.left_out));
        }
    }
}
