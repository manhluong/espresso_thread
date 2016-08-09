package com.luongbui.espressothread;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

  private static final String ASYNCTASK_LABEL = "AsyncTask";
  private static final String THREAD_LABEL = "Thread";
  private static final long WAIT_TIME = 1000;

  private ExecutorService executor;

  private Button asyncTaskButton;
  private Button threadButton;

  private static ActivityTask task;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    executor = Executors.newCachedThreadPool();

    asyncTaskButton = (Button) findViewById(R.id.async_task_button);
    asyncTaskButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        task = new ActivityTask(MainActivity.this);
        task.execute();
      }
    });

    threadButton = (Button) findViewById(R.id.thread_button);
    threadButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        executor.execute(new Runnable() {
          @Override public void run() {
            try {
              Thread.sleep(WAIT_TIME);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            new Handler(Looper.getMainLooper()).post(new Runnable() {
              @Override
              public void run() {
                launchActivity(THREAD_LABEL);
              }
            });
          }
        });
      }
    });
  }

  public void launchActivity(String label) {
    Intent intent = new Intent(this, LabelActivity.class);
    intent.putExtra(LabelActivity.LABEL_KEY, label);
    startActivity(intent);
  }

  private static class ActivityTask extends AsyncTask<Void, Void, String> {

    private WeakReference<MainActivity> activityRef;

    public ActivityTask(MainActivity activity) {
      activityRef = new WeakReference<MainActivity>(activity);
    }

    @Override protected String doInBackground(Void... params) {
      try {
        Thread.sleep(WAIT_TIME);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      return ASYNCTASK_LABEL;
    }

    @Override protected void onPostExecute(String result) {
      super.onPostExecute(result);

      MainActivity activity = activityRef.get();

      if (activity != null) {
        activity.launchActivity(result);
      }
    }
  }
}
