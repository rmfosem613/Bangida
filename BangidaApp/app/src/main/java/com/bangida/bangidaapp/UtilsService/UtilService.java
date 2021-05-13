package com.bangida.bangidaapp.UtilsService;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.snackbar.Snackbar;

public class UtilService {

    // EditText에 내용을 쓰고 버튼을 누르는 등 액션을 할때, 하단의 키보드가 자동으로 사라지게 하는 것이 사용
    public void hideKeyboard(View view, Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 사용자에게 간단한 문자열 메시지를 보여줄 목적으로 사용(Toast 역할)
    public void showSnackBar(View view, String msg) {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show();
    }

}
