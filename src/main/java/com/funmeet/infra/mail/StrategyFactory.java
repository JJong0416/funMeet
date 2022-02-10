package com.funmeet.infra.mail;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class StrategyFactory {
    private Map<StrategyName, SendStrategy> strategies;

    public StrategyFactory(Set<SendStrategy> strategySet) {
        createStrategy(strategySet);
    }

    public SendStrategy findStrategy(StrategyName strategyName) {
        return strategies.get(strategyName);
    }

    private void createStrategy(Set<SendStrategy> strategySet) {
        strategies = new HashMap<StrategyName, SendStrategy>();
        strategySet.forEach(
                strategy -> strategies.put(strategy.getStrategyName(), strategy));
    }
}