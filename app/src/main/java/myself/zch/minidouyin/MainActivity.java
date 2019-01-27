package myself.zch.minidouyin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import myself.zch.minidouyin.JavaBeans.Feed;
import myself.zch.minidouyin.JavaBeans.FeedResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "DEBUG_MAIN_ACTIVITY";

    private RecyclerView recyclerView;
    private List<Feed> mFeeds = new ArrayList<>();
    // TODO: 2019/1/27 刷新
//    private Button btn_refresh = findViewById(R.id.btn_refresh);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initRecyclerView();

        //收藏
        findViewById(R.id.btn_favorite_main).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ShowFavoriteActivity.class);
            startActivity(intent);
        });

        //拍摄
        findViewById(R.id.btn_record_main).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RecordActivity.class);
            startActivity(intent);
        });

        //发布
        findViewById(R.id.btn_post_main).setOnClickListener(v -> {
            // TODO: 2019/1/26 直接弹出对话框，准备发布
        });

        //刷新
//        btn_refresh.setOnClickListener(v -> {
//            fetchFeed();
//        });

        fetchFeed();
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.rv_main);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                ImageView imageView = new ImageView(viewGroup.getContext());
                imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                imageView.setAdjustViewBounds(true);
                return new MainActivity.MyViewHolder(imageView);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                ImageView iv = (ImageView) viewHolder.itemView;

                String url = mFeeds.get(i).getImage_url();
                Glide.with(iv.getContext()).load(url).into(iv);
                viewHolder.itemView.setOnClickListener((v -> {
                    Intent intent = new Intent(MainActivity.this, PlayActivity.class);
                    intent.putExtra("VIDEO_URL", mFeeds.get(i).getVideo_url());
                    intent.putExtra("IMAGE_URL", mFeeds.get(i).getImage_url());
                    intent.putExtra("STUDENT_ID", mFeeds.get(i).getStudent_id());
                    intent.putExtra("USER_NAME", mFeeds.get(i).getUser_name());

                    startActivity(intent);
                }));
            }

            @Override
            public int getItemCount() {
                return mFeeds.size();
            }
        });
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private void getResponseFromMiniDouyin(Callback<FeedResponse> callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.108.10.39:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofit.create(IMiniDouyinService.class).getFeed().
                enqueue(callback);
    }


    private void fetchFeed() {
//        btnRefreshDisable();

        getResponseFromMiniDouyin(new Callback<FeedResponse>() {
            @Override
            public void onResponse(Call<FeedResponse> call, Response<FeedResponse> response) {
                Toast.makeText(MainActivity.this.getApplicationContext(), "REQUEST SUCCESS", Toast.LENGTH_LONG).show();
                mFeeds = response.body().getFeeds();
                recyclerView.getAdapter().notifyDataSetChanged();
//                btnRefreshEnable();
            }

            @Override
            public void onFailure(Call<FeedResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this.getApplicationContext(), "FAILED TO REQUEST", Toast.LENGTH_LONG).show();
                Log.d(TAG, "onFailure: " + t.getMessage());
//                btnRefreshEnable();
            }
        });
    }

//    private void btnRefreshDisable() {
//        btn_refresh.setText("requesting");
//        btn_refresh.setEnabled(false);
//    }
//
//    private void btnRefreshEnable() {
//        btn_refresh.setText("refresh");
//        btn_refresh.setEnabled(true);
//    }


}