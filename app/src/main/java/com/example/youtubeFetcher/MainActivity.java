package com.example.youtubeFetcher;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    EditText video_id_input;
    TextView title;
    TextView views;
    TextView channel;
    TextView subscribers;
    OkHttpClient client = new OkHttpClient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        video_id_input=(EditText)findViewById(R.id.editText);
        title = (TextView) findViewById(R.id.title);
        views = (TextView) findViewById(R.id.views);
        channel = (TextView) findViewById(R.id.channel);
        subscribers = (TextView) findViewById(R.id.subscribers);
    }


    public void onButtonClick(View view){
        Grab(video_id_input.getText().toString());
    }

    public JSONObject Grab(String video_id) {
        JSONObject item = new JSONObject();
        String youTube_link="https://www.youtube.com/watch?v="+video_id;

        Handler handler = new Handler();
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    Request request = new Request.Builder()
                            .url(youTube_link)
                            .build();
                    try {
                        Response response = client.newCall(request).execute();
                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                call.cancel();
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {

                                final String myResponse = response.body().string();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        // to fetch video title
                                        Pattern title_pattern = Pattern.compile("<title>(.*?)</title>");
                                        Matcher title_matcher = title_pattern.matcher(myResponse);
                                        if (title_matcher.find()) {
                                            String result = title_matcher.group(1).replace("&#39;","'");
                                            try {
                                                item.put("title",result);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        // to fetch views
                                        Pattern view_pattern = Pattern.compile("\"viewCount\":\"(.*?)\",\"author\"");
                                        Matcher view_matcher = view_pattern.matcher(myResponse);
                                        if (view_matcher.find()) {
                                            String result = view_matcher.group(1);
                                            try {
                                                item.put("views",result);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        // to fetch channel name
                                        Pattern channel_pattern = Pattern.compile("\"ownerChannelName\":\"(.*?)\",");
                                        Matcher channel_matcher = channel_pattern.matcher(myResponse);
                                        if (channel_matcher.find()) {
                                            String result = channel_matcher.group(1);
                                            try {
                                                item.put("channel_name",result);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        // to fetch subscribers
                                        Pattern subscriber_pattern = Pattern.compile("\"subscriberCountText\":\\{\"accessibility\":\\{\"accessibilityData\":\\{\"label\":\"(.*?)subscribers\"\\}\\}");
                                        Matcher subscriber_matcher = subscriber_pattern.matcher(myResponse);
                                        if (subscriber_matcher.find()) {
                                            String result = subscriber_matcher.group(1);
                                            try {
                                                item.put("channel_subscribers",result);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }


                                        //to update the fetched data on UI
                                                try {
                                                    title.setText("Title: "+item.getString("title"));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                try {
                                                    views.setText("Views: "+item.getString("views"));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                try {
                                                    channel.setText("Channel: "+item.getString("channel_name"));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                try {
                                                    subscribers.setText("Subscribers: "+item.getString("channel_subscribers"));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }


                                    }
                                });
                            }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        return item;
    }
}
