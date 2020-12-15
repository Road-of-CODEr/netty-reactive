package com.study.minhyungpark.chapter2.rxjava;

import java.io.IOException;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.study.minhyungpark.chapter2.temparature.Temperature;

import rx.Subscriber;

public class RxSseEmitter extends SseEmitter {
    private static final long SSE_SESSION_TIMEOUT = 30 * 60 * 1000L;
    private final Subscriber<Temperature> subscriber;

    public RxSseEmitter() {
        super(SSE_SESSION_TIMEOUT);

        subscriber = new Subscriber<Temperature>() {
            @Override
            public void onCompleted() { }

            @Override
            public void onError(Throwable e) { }

            @Override
            public void onNext(Temperature temperature) {
                try {
                    send(temperature);
                } catch (IOException e) {
                    unsubscribe();
                }
            }
        };

        onCompletion(subscriber::unsubscribe);
        onTimeout(subscriber::unsubscribe);
    }

    public Subscriber<Temperature> getSubscriber() {
        return subscriber;
    }
}
