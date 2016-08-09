/*******************************************************************************
 * Copyright 2016 Manh Luong   Bui.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/


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
