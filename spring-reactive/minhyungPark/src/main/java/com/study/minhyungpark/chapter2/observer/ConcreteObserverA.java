package com.study.minhyungpark.chapter2.observer;

public class ConcreteObserverA implements Observer<String> {

    @Override
    public void observer(String event) {
        System.out.println("Observer A: " + event);
    }
}
