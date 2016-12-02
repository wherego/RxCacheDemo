package org.gosky.rxcachedemo;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.rx_cache.DynamicKey;
import io.rx_cache.LifeCache;
import io.rx_cache.Reply;
import rx.Observable;

/**
 * Created by guozhong on 16/12/2.
 */

public interface CacheProviders {

    @LifeCache(duration = 2,timeUnit = TimeUnit.MINUTES)
    Observable<Reply<List<Repo>>> getRepos(Observable<List<Repo>> oRepos, DynamicKey username);
}
