package com.uppoint.android.pro.core;

import com.appspot.uppoint_api.uppointApi.UppointApi;
import com.uppoint.android.pro.core.util.Preconditions;

import android.content.Context;
import android.support.annotation.CallSuper;

import java.io.IOException;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 */
public abstract class EndpointCommand<T, EC extends EndpointCommand> {

    private UppointApi mApi;

    public EC using(Context context) {
        Preconditions.nonNull(context, "Required parameter context is null");

        mApi = ApiFactory.getApi(context);
        return (EC) this;
    }

    public Observable<T> toObservable() {
        validate();

        return Observable
                .create(new Observable.OnSubscribe<T>() {
                    @Override
                    public void call(Subscriber<? super T> subscriber) {
                        try {
                            final T result = execute(mApi);
                            subscriber.onNext(result);
                            subscriber.onCompleted();
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache();
    }

    protected UppointApi getApi() {
        return mApi;
    };

    @CallSuper
    protected void validate() {
        Preconditions.stateNonNull(mApi, "Api not instantiated. Did you call using(Context)?");
    }

    protected abstract T execute(UppointApi api) throws IOException;
}
