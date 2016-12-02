package org.gosky.rxcachedemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

import io.rx_cache.Reply;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    protected TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);
        Repository.init(getFilesDir()).getRepos("zohar-soul", true)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Reply<List<Repo>>>() {
                    @Override
                    public void call(Reply<List<Repo>> listReply) {
                        String str = "";
                        for (Repo repo : listReply.getData()) {
                            str = str + repo.getName() + "\n";
                        }
                        tv.setText(str);
                        Log.i("TAG", "result: \n " + str);

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
//        Repository.init(getFilesDir()).getRestApi().getRepos("zohar-soul")
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<List<Repo>>() {
//                    @Override
//                    public void call(List<Repo> repos) {
//                        Log.i("TAG", "call: " + repos.toString());
//
//                    }
//                });
        initView();
    }

    private void initView() {
        tv = (TextView) findViewById(R.id.tv);
    }
}
