package com.study.minhyungpark.chapter2.observer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConcreteSubjectTest {

    @Test
    @DisplayName("observer handling event test")
    void observersHandleEventsFromSubject() {
        // given
        final Subject<String> subject = new ConcreteSubject();
        final Observer<String> observerA = Mockito.spy(new ConcreteObserverA());
        final Observer<String> observerB = Mockito.spy(new ConcreteObserverB());

        // when
        subject.notifyObservers("No listeners");

        subject.registerObserver(observerA);
        subject.notifyObservers("Message for A");

        subject.registerObserver(observerB);
        subject.notifyObservers("Message for A & B");

        subject.unregisterObserver(observerA);
        subject.notifyObservers("Message for B");

        subject.unregisterObserver(observerB);
        subject.notifyObservers("No listeners");

        // then
        Mockito.verify(observerA, Mockito.times(1)).observer("Message for A");
        Mockito.verify(observerA, Mockito.times(1)).observer("Message for A & B");
        Mockito.verifyNoMoreInteractions(observerA);

        Mockito.verify(observerB, Mockito.times(1)).observer("Message for A & B");
        Mockito.verify(observerB, Mockito.times(1)).observer("Message for B");
        Mockito.verifyNoMoreInteractions(observerB);
    }

    @Test
    @DisplayName("observer handling event using lambdas ")
    void subjectLeveragesLambdas() {
        final Subject<String> subject = new ConcreteSubject();

        subject.registerObserver(e -> System.out.println("A: " + e));
        subject.registerObserver(e -> System.out.println("B: " + e));
        subject.notifyObservers("This message will receive A & B");
    }
}