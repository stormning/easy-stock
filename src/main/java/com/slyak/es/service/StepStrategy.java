package com.slyak.es.service;

import java.util.List;

public interface StepStrategy<G, T extends StepStrategy<G, T>> {
    T init(StrategyArgs args);

    List<G> generate();
}
