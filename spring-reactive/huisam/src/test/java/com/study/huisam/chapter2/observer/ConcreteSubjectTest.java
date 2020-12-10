package com.study.huisam.chapter2.observer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ConcreteSubjectTest {

    @Test
    @DisplayName("observer handling event test")
    void test_observer_event_handling() {
        /* given */
        final Subject<String> subject = new ConcreteSubject();
        final Observer<String> observerA = Mockito.spy(new ConcreteObserverA());
        final Observer<String> observerB = Mockito.spy(new ConcreteObserverB());

        /* when */
        subject.notifyObservers("No listeners");

        subject.registerObserver(observerA);
        subject.notifyObservers("Message for A");

        subject.registerObserver(observerB);
        subject.notifyObservers("Message for A & B");

        subject.unregisterObserver(observerA);
        subject.notifyObservers("Message for B");

        subject.unregisterObserver(observerB);
        subject.notifyObservers("No listeners");

        /* then */
        Mockito.verify(observerA, times(1)).observe("Message for A");
        Mockito.verify(observerA, times(1)).observe("Message for A & B");
        Mockito.verifyNoMoreInteractions(observerA);

        Mockito.verify(observerB, times(1)).observe("Message for B");
        Mockito.verify(observerB, times(1)).observe("Message for A & B");
        Mockito.verifyNoMoreInteractions(observerB);
    }
}