package com.study.minhyungpark.chapter2.rxjava;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.study.minhyungpark.chapter2.temparature.Temperature;

import rx.Observable;

@Component
public class RxTemperatureSensor {
    private final Random rnd = new Random();

    private final Observable<Temperature> dataStream =
            Observable
                    .range(0, Integer.MAX_VALUE)
                    .concatMap(tick -> Observable
                            .just(tick)
                            .delay(rnd.nextInt(5000), TimeUnit.MILLISECONDS)
                            .map(tickValue -> probe()))
                    .publish()
                    .refCount();

    private Temperature probe() {
        return new Temperature(16 + rnd.nextGaussian() * 10);
    }

    public Observable<Temperature> temperatureStream() {
        return dataStream;
    }
}
