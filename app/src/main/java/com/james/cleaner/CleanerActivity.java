package com.james.cleaner;

import android.animation.Animator;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CleanerActivity extends AppCompatActivity {

    private ImageView imageView;
    private AppCompatButton button;

    private boolean isAnimating;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cleaner);

        imageView = (ImageView) findViewById(R.id.icon);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAnimating) {
                    imageView.animate().rotationBy(360).setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            isAnimating = true;
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            isAnimating = false;
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    }).start();
                }
            }
        });

        button = (AppCompatButton) findViewById(R.id.clean);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast toast = new Toast(CleanerActivity.this);

                        View snackbar = LayoutInflater.from(CleanerActivity.this).inflate(R.layout.snackbar_layout, null);
                        ViewCompat.setElevation(snackbar, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()));

                        toast.setView(snackbar);
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setMargin(0, 0);
                        toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                        toast.show();
                    }
                }, 1000);

                try {
                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                } catch ( ActivityNotFoundException e ) {
                    startActivity(new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS));
                }
            }
        });
    }
}
