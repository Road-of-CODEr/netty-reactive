package com.study.huisam.chapter2.rxjava;

import com.study.huisam.chapter2.temperature.Temperature;
import org.springframework.stereotype.Component;
import rx.Observable;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Component
public class RxTemperatureSensor {
    private final ThreadLocalRandom random = ThreadLocalRandom.current();

    private final Observable<Temperature> dataStream = Observable
            .range(0, Integer.MAX_VALUE)
            .concatMap(tick -> Observable
                    .just(tick)
                    .delay(random.nextInt(5000), TimeUnit.MILLISECONDS)
                    .map(tickValue -> this.probe())
            )
            .publish()
            .refCount();

    private Temperature probe() {
        return new Temperature(16 + random.nextGaussian() * 10);
    }

    public Observable<Temperature> temperatureStream() {
        return dataStream;
    }
}
