package com.study.huisam.chapter4;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@Slf4j
public class SubscriberPractice {
    public static String requestUserData(String sessionId) {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return sessionId + ": complete";
    }

    public static class MySubscriber<T> extends BaseSubscriber<T> {
        @Override
        public void hookOnSubscribe(Subscription subscription) {
            log.info("subscribe start");
            subscription.request(Integer.MAX_VALUE);
        }

        @Override
        protected void hookOnNext(T value) {
            log.info("value : {}", value);
            request(5);
            // 리액티브 스트림은 순차 진행을 보장하기 때문에 1만 출력하고 멈추게됨
            cancel();
        }

        @Override
        protected void hookOnCancel() {
            log.info("canceled");
        }
    }

    public static void main(String[] args) {
        final MySubscriber<Integer> mySubscriber = new MySubscriber<>();
        Flux.range(1, 100)
                .subscribeWith(mySubscriber);
    }
}
