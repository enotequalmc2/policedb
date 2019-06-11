package police.db;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.textfield.TextInputEditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.objectbox.Box;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import police.db.adapter.PoliceListAdapter;
import police.db.box.ObjectBox;
import police.db.entity.Police;
import police.db.entity.Police_;
import police.db.event.PoliceListEvent;
import police.db.network.Client;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.searchbox)
    TextInputEditText searchbox;
    @BindView(R.id.status_text)
    TextView status;
    @BindView(R.id.police_list)
    RecyclerView list;

    private Box<Police> db;
    private Handler uiHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        db = ObjectBox.get().boxFor(Police.class);

        uiHandler = new Handler();

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(RecyclerView.VERTICAL);
        list.setLayoutManager(llm);

        searchbox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_NULL
                        && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    Log.e("Key", "ENTER");

                    String pid = searchbox.getText().toString().trim();

                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            if (db.query().equal(Police_.pid, pid).build().count() > 0) {
                                EventBus.getDefault().post(new PoliceListEvent(db.query().equal(Police_.pid, pid).build().find()));
                            }
                        }
                    });
                }
                return false;
            }
        });

        downloadData();
    }

    public void downloadData() {

        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                status.setText("Downloading data");
            }
        });

        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                status.setText("Press item to view source");
            }
        }, 3000);

        Request request = new Request.Builder()
                .url("https://github.com/police-github/police-github.github.io/raw/master/data.json")
                .build();

        Client.get().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        new MaterialDialog.Builder(MainActivity.this)
                                .content("Network error. Try again later.")
                                .negativeText("Close")
                                .build().show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    updateDB(new JSONObject(response.body().string()));
                } catch (JSONException e) {
                    e.printStackTrace();
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            new MaterialDialog.Builder(MainActivity.this)
                                    .content("Data format error")
                                    .negativeText("Close")
                                    .build().show();
                        }
                    });
                }
            }
        });
    }

    public void updateDB(JSONObject json) {
        try {
            Iterator<String> temp = json.keys();
            while (temp.hasNext()) {
                String key = temp.next();
                JSONArray value = (JSONArray) json.get(key);
                for (int i = 0; i < value.length(); i++) {
                    JSONObject object = value.getJSONObject(i);

                    if (db.query().equal(Police_.source, (String) object.get("source")).build().count() == 0) {
                        Police police = new Police();
                        police.pid = key;
                        police.name = (String) object.get("name");
                        police.position = (String) object.get("position");
                        police.auxiliary = (boolean) object.get("auxiliary");
                        police.source = (String) object.get("source");

                        db.put(police);

                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                status.setText("Adding: " + police.pid + " " + police.name);
                            }
                        });
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    status.setText("Update finished");
                }
            });

//            EventBus.getDefault().post(new PoliceListEvent(db.getAll()));

            uiHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    status.setText("Press item to view source");
                }
            }, 3000);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPCListEvent(PoliceListEvent event) {
        list.setAdapter(new PoliceListAdapter(MainActivity.this, event.getPolices()));
    }
}
