package com.study.huisam.chapter2.rxjava;

import com.study.huisam.chapter2.temperature.Temperature;
import lombok.Getter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import rx.Subscriber;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Getter
public class RxSeeEmitter extends SseEmitter {
    static final long SSE_SESSION_TIMEOUT = TimeUnit.MINUTES.toMillis(30);
    private final Subscriber<Temperature> subscriber;

    public RxSeeEmitter() {
        super(SSE_SESSION_TIMEOUT);

        this.subscriber = new Subscriber<>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Temperature temperature) {
                try {
                    RxSeeEmitter.this.send(temperature);
                } catch (IOException e) {
                    unsubscribe();
                }
            }
        };
        onCompletion(subscriber::unsubscribe);
        onTimeout(subscriber::unsubscribe);
    }

}
