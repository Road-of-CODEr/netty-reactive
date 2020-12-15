package com.study.minhyungpark.chapter2.temparature;

import lombok.Getter;

@Getter
public final class Temperature {
    private final double value;

    public Temperature(double value) {
        this.value = value;
    }
}
