package com.luongbui.espressothread;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by luongbui on 09/08/16.
 */
public class LabelActivity extends AppCompatActivity {

  public static final String LABEL_KEY = "LABEL_ID";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_label);

    String label = getIntent().getStringExtra(LABEL_KEY);
    TextView labelView = (TextView)findViewById(R.id.label);
    labelView.setText(label);
  }
}
