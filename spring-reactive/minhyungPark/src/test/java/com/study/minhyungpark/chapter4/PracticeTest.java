package com.study.minhyungpark.chapter4;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import reactor.core.Disposable;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class PracticeTest {

    @Test
    void fluxPractice() {
        final List<Integer> block = Flux.range(1, 100).log()
                                        .collectList()
                                        .block();

        final Flux<String> stream1 = Flux.just("Hello", "world");
        final Flux<Integer> stream2 = Flux.fromArray(new Integer[] { 1, 2, 3 });
        final Flux<Integer> stream3 = Flux.fromIterable(Arrays.asList(9, 8, 7));
        final Flux<Integer> stream4 = Flux.range(2010, 9);

        final Mono<String> stream5 = Mono.just("One");
        final Mono<String> stream6 = Mono.justOrEmpty(null);
        final Mono<String> stream7 = Mono.justOrEmpty(Optional.empty());
    }

    @Test
    void deferTest() {
        requestUserData("defer - notsubscribe");
        requestUserData("defer - subscribe").subscribe(System.out::println);

        requestUserData1("not defer - not subscribe");
        requestUserData1("not defer - subscribe").subscribe(System.out::println);
    }

    Mono<String> requestUserData(String sessionId) {
        return Mono.defer(() -> isValidSession(sessionId) ? Mono.fromCallable(() -> requestUser(sessionId)) : Mono.error(new RuntimeException("Invalid user session")));
    }

    Mono<String> requestUserData1(String sessionId) {
        // subscribe를 하지 않아도 유효성 검사를 한다
        return isValidSession(sessionId) ? Mono.fromCallable(() -> requestUser(sessionId)) : Mono.error(new RuntimeException("Invalid user session"));
    }

    String requestUser(String sessionId) {
        System.out.println("requestUser called : sessionId=" + sessionId);
        return "sessionId";
    }


    boolean isValidSession(String sessionId) {
        System.out.println("isValidSession called : seesionId=" + sessionId);
        return true;
    }

    @Test
    void subscribeTest() {
//        Flux.just("A", "B", "C")
//            .subscribe(
//                    data -> System.out.println("onNext: " + data),
//                    err -> { },
//                    () -> System.out.println("onComplete")
//            );

        Flux.range(1, 100)
            .subscribe(
                    data -> System.out.println("onNext: " + data),
                    err -> { System.err.println("error occured"); },
                    () -> System.out.println("onComplete"),
                    subscription -> {
                        subscription.request(4);
                        subscription.cancel();
                    }
            );

    }

    @Test
    void disposableTest() throws InterruptedException {

        final Disposable disposable = Flux.interval(Duration.ofMillis(50))
                                         .subscribe(System.out::println);
        Thread.sleep(200);
        disposable.dispose(); // 구독 취소
    }

    @Test
    void customSubscriberTest() {
        Subscriber<String> subscriber = new Subscriber<String>() {
            volatile Subscription subscription;

            @Override
            public void onSubscribe(Subscription s) {
                subscription = s;
                System.out.println("initial request for 1 element");
                subscription.request(1);
            }

            @Override
            public void onNext(String s) {
                System.out.println("onNext: " + s);
                System.out.println("requesting 1 more element");

                subscription.request(1);
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("onError: " + t.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        };

        final Flux<String> stream = Flux.just("Hello", "world", "!");
        stream.subscribe(subscriber);
    }

    public static class MySubscriber<T> extends BaseSubscriber<T> {
        @Override
        protected void hookOnSubscribe(Subscription subscription) {
            System.out.println("initial request for 1 element");
            request(1);
        }

        @Override
        protected void hookOnNext(T value) {
            System.out.println("onNext: " + value);
            System.out.println("requesting 1 more element");
            request(1);
        }
    }

    @Test
    void myBaseSubscriberTest() {
        final MySubscriber<String> subscriber = new MySubscriber<>();
        final Flux<String> stream = Flux.just("Hello", "world", "!");
        stream.subscribe(subscriber);
    }


    @Test
    void reactiveSequenceElementMapping() {
        Flux.range(2018, 5)
            .timestamp()
            .index()
            .subscribe(
                    e -> System.out.println("index: " + e.getT1()
                                            + ", ts: " + Instant.ofEpochMilli(e.getT2().getT1())
                                            + ", value: " + e.getT2().getT2())
            );
    }

    @Test
    void collectReactiveSequence() {
        Flux.just(1,6,2,8,3,1,5,1)
            .collectSortedList(Comparator.reverseOrder())
            .subscribe(System.out::println);
    }

    @Test
    void fluxFunc() {
        // any
        Flux.just(3,5,7,9,11,15,16,17)
            .any(e -> e%2 == 0)
            .subscribe(e -> System.out.println("has events: " + e));

        // reduce
        Flux.range(1, 5)
            .reduce(0, (acc, elem) -> acc + elem)
            .subscribe(result -> System.out.println("Reduce Result: " + result));

        // scan
        Flux.range(1, 5)
            .scan(0, (acc, elem) -> acc + elem)
            .subscribe(result -> System.out.println("Scan Result: " + result));

    }

    @Test
    void concatTest() {
        // one by one
        Flux.concat(
                Flux.range(1, 3),
                Flux.range(4, 2),
                Flux.range(6, 5)
        ).subscribe(System.out::println);
    }

    @Test
    void bufferTest() {
        Flux.range(1, 13)
            .buffer(4)
            .subscribe(System.out::println);
    }

    @Test
    void windowsTest() {
        final Flux<Flux<Integer>> windowedFlux = Flux.range(101, 20)
                                                     .windowUntil(e -> e % 3 == 0, true);
        windowedFlux.subscribe(window -> window.collectList().subscribe(System.out::println));
    }

    @Test
    void groupByTest() {
        Flux.range(1, 7)
            .groupBy(e -> e % 2 == 0 ? "Even" : "Odd")
            .subscribe(groupFlux -> groupFlux
                       .scan(
                               new LinkedList<>(),
                               (list, elem) -> {
                                   list.add(elem);
                                   if (list.size() > 2) {
                                       list.remove(0);
                                   }
                                   return list;
                               }
                       )
                       .filter(arr -> !arr.isEmpty())
                       .subscribe(data -> System.out.println(groupFlux.key() + " : " + data))
            );
    }

    Flux<String> requestBooks(String user) {
        final Random random = new Random();
        return Flux.range(1, 3)
                .map(i -> "book-" + i)
                .delayElements(Duration.ofMillis(5));
    }

    @Test
    void flatMapTest() throws InterruptedException {
        Flux.just("user-1","user-2","user-3")
            .flatMap(u ->
                             requestBooks(u).map(b -> u + "/" + b)
            )
            .subscribe(System.out::println);

        Thread.sleep(1000);
    }

    @Test
    void 샘플링하기() throws InterruptedException {
        Flux.range(1, 100)
            .delayElements(Duration.ofMillis(1))
            .sample(Duration.ofMillis(20))
            .subscribe(System.out::println);
        Thread.sleep(1000);
    }

    @Test
    void doOnEach() {
        Flux.just(1,2,3)
            .concatWith(Flux.error(new RuntimeException("Conn error")))
            .doOnEach(System.out::println)
            .subscribe();
    }

    @Test
    void 데이터와시크널변환하기() throws InterruptedException {
        Flux.range(1,3)
            .doOnNext(System.out::println)
            .materialize()
            .doOnNext(System.out::println)
            .dematerialize()
            .collectList()
            .doOnNext(System.out::println);
        Thread.sleep(2000);
    }

    @Test
    void pushTest() {
        Flux.push(emitter -> IntStream
                .range(2000, 3000)
                .forEach(emitter::next))
            .delayElements(Duration.ofMillis(1))
            .subscribe(System.out::println);
    }
}