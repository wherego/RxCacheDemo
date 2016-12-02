package org.gosky.rxcachedemo;

import java.io.File;
import java.util.List;

import io.rx_cache.DynamicKey;
import io.rx_cache.Reply;
import io.rx_cache.internal.RxCache;
import io.victoralbertos.jolyglot.GsonSpeaker;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

public class Repository {
    public static final int USERS_PER_PAGE = 25;

    public static Repository init(File cacheDir) {
        return new Repository(cacheDir);
    }

    private final CacheProviders cacheProviders;
    private final RestApi restApi;

    public Repository(File cacheDir) {
        //persistence设置为缓存文件路径cacheDir,using设置成你所定义的接口类class
        cacheProviders = new RxCache.Builder()
                .persistence(cacheDir, new GsonSpeaker())
                .using(CacheProviders.class);

        restApi = new Retrofit.Builder()
                .baseUrl(RestApi.URL_BASE)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(RestApi.class);
    }

//    /**
//     * @param update 是否更新,如果设置为true，缓存数据将被清理，并且向服务器请求数据
//     * @return
//     */
//    public Observable<Reply<List<User>>> getUsers(int idLastUserQueried, final boolean update) {
//        //这里设置idLastUserQueried为DynamicKey,
//        return cacheProviders.getUsers(restApi.getUsers(idLastUserQueried, USERS_PER_PAGE), new DynamicKey(idLastUserQueried), new EvictDynamicKey(update));
//    }

    //对应每个不同的userName，配置缓存
    public Observable<Reply<List<Repo>>> getRepos(final String userName, final boolean update) {
        //以userName为DynamicKey,如果update为true,将会重新获取数据并清理缓存。
        return cacheProviders.getRepos(restApi.getRepos(userName), new DynamicKey(userName));
    }

//    public Observable<Reply<User>> loginUser(final String userName) {
//        return restApi.getUser(userName).map(new Func1<Response<User>, Observable<Reply<User>>>() {
//            @Override
//            public Observable<Reply<User>> call(Response<User> userResponse) {
//
//                if (!userResponse.isSuccess()) {
//                    try {
//                        ResponseError responseError = new Gson().fromJson(userResponse.errorBody().string(), ResponseError.class);
//                        throw new RuntimeException(responseError.getMessage());
//                    } catch (JsonParseException | IOException exception) {
//                        throw new RuntimeException(exception.getMessage());
//                    }
//                }
//                //用户登陆，这里设置 new EvictProvider(true),表示登陆不缓存，为实时登陆
//                return cacheProviders.getCurrentUser(Observable.just(userResponse.body()), new EvictProvider(true));
//            }
//        }).flatMap(new Func1<Observable<Reply<User>>, Observable<Reply<User>>>() {
//            @Override
//            public Observable<Reply<User>> call(Observable<Reply<User>> replyObservable) {
//                return replyObservable;
//            }
//        }).map(new Func1<Reply<User>, Reply<User>>() {
//            @Override
//            public Reply<User> call(Reply<User> userReply) {
//                return userReply;
//            }
//        });
//    }
//
//    public Observable<String> logoutUser() {
//        return cacheProviders.getCurrentUser(Observable.<User>just(null), new EvictProvider(true))
//                .map(new Func1<Reply<User>, String>() {
//                    @Override
//                    public String call(Reply<User> user) {
//                        return "Logout";
//                    }
//                })
//                .onErrorReturn(new Func1<Throwable, String>() {
//                    @Override
//                    public String call(Throwable throwable) {
//                        return "Logout";
//                    }
//                });
//    }
//
//    public Observable<Reply<User>> getLoggedUser(boolean invalidate) {
//        Observable<Reply<User>> cachedUser = cacheProviders.getCurrentUser(Observable.<User>just(null), new EvictProvider(false));
//
//        Observable<Reply<User>> freshUser = cachedUser.flatMap(new Func1<Reply<User>, Observable<Reply<User>>>() {
//            @Override
//            public Observable<Reply<User>> call(Reply<User> userReply) {
//                return loginUser(userReply.getData().getLogin());
//            }
//        });
//
//        if (invalidate) return freshUser;
//        else return cachedUser;
//    }

    public RestApi getRestApi() {
        return restApi;
    }

    private static class ResponseError {
        private final String message;

        public ResponseError(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}          