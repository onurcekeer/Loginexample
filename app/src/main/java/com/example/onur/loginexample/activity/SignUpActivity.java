package com.example.onur.loginexample.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import com.example.onur.loginexample.R;

import java.util.Locale;

public class SignUpActivity extends AppCompatActivity {


    private static final String KEY_USER = "KEY_USER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);



        // Login clickable
        if(Locale.getDefault().getLanguage().equals("tr"))
        {
            SpannableString ss2 = new SpannableString("Zaten hesabın var mı? Giriş Yap.");
            ClickableSpan clickableSpan2 = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    Intent intent = LoginActivity.newIntent(SignUpActivity.this, 1);
                    startActivity(intent);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                }
            };
            ss2.setSpan(clickableSpan2, 22, 32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            TextView textView = (TextView) findViewById(R.id.already_have_an_account);
            textView.setText(ss2);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setHighlightColor(Color.TRANSPARENT);
        }
        else{
            SpannableString ss = new SpannableString("Already have an account? Login.");
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    Intent intent = LoginActivity.newIntent(SignUpActivity.this, 1);
                    startActivity(intent);
                }
                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                }
            };

            ss.setSpan(clickableSpan, 25, 31, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            TextView textView = (TextView) findViewById(R.id.already_have_an_account);
            textView.setText(ss);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setHighlightColor(Color.TRANSPARENT);
        }

    }

    public static Intent newIntent(Activity callerActivity, int parameter){
        Intent intent=new Intent(callerActivity, SignUpActivity.class);
        intent.putExtra(KEY_USER,parameter);
        return intent;
    }


}
