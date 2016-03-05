package com.uppoint.android.pro.login.endpoint;

import com.appspot.uppoint_api.uppointApi.UppointApi;
import com.appspot.uppoint_api.uppointApi.model.ProUserPayload;
import com.uppoint.android.pro.core.EndpointCommand;
import com.uppoint.android.pro.core.util.Preconditions;

import java.io.IOException;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 */
public class GetUser extends EndpointCommand<ProUserPayload, GetUser> {

    private String mEmail;

    public GetUser withEmail(String email) {
        Preconditions.nonNull(email, "Required parameter email is null");

        mEmail = email;
        return this;
    }

    @Override
    protected void validate() {
        super.validate();

        Preconditions.stateNonNull(mEmail, "Email is not set. Did you call withEmail(String)?");
    }

    @Override
    public Observable<ProUserPayload> toObservable() {
        return super.toObservable().flatMap(
                new Func1<ProUserPayload, Observable<ProUserPayload>>() {
                    @Override
                    public Observable<ProUserPayload> call(ProUserPayload proUserPayload) {
                        if (proUserPayload == null) {
                            return insertUser();
                        } else {
                            return Observable.just(proUserPayload);
                        }
                    }
                });
    }

    private Observable<ProUserPayload> insertUser() {
        return Observable.create(new Observable.OnSubscribe<ProUserPayload>() {
            @Override
            public void call(Subscriber<? super ProUserPayload> subscriber) {
                try {
                    final ProUserPayload proUserPayload = getApi().users().insert(mEmail).execute();
                    subscriber.onNext(proUserPayload);
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).cache();
    }

    @Override
    protected ProUserPayload execute(UppointApi api) throws IOException {
        return api.users().getByEmail(mEmail).execute();
    }
}
