package com.slyak.es.service.impl;

import com.slyak.es.domain.Plan;
import com.slyak.es.domain.Trade;
import com.slyak.es.domain.TradeType;
import com.slyak.es.repo.PlanRepo;
import com.slyak.es.repo.TradeRepo;
import com.slyak.es.service.PlanService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class PlanServiceImpl implements PlanService {

    private PlanRepo planRepo;

    private TradeRepo tradeRepo;

    public PlanServiceImpl(PlanRepo planRepo) {
        this.planRepo = planRepo;
    }

    @Override
    public Plan getById(Long id) {
        return planRepo.getById(id);
    }

    public List<Trade> getTrades(Long planId) {
        Plan plan = getById(planId);
        List<Trade> trades = tradeRepo.findByPlanId(planId, Sort.by(Sort.Order.asc("id")));
        //成本计算
//        BigDecimal totalPrice =
        for (Trade trade : trades) {
            TradeType type = trade.getType();
            BigDecimal price = trade.getPrice();

        }

        /*BigDecimal price = plan.getFirstPrice();
        BigDecimal marginCall = plan.getMarginCall();
        int scale = price.scale();
        BigDecimal nextPrice = getNextPrice(price, marginCall, scale);
        buys.stream().filter(new Predicate<Buy>() {
            @Override
            public boolean test(Buy buy) {
                BigDecimal buyPrice = buy.getPrice();
                //
                buyPrice.compareTo(price) <= 0 && nextPrice.compareTo(price) > 0;
                return buyPrice1.compareTo(price) < 0;
            }
        });*/
        return null;
    }

    private BigDecimal getNextPrice(BigDecimal priceNow, BigDecimal marginCall, int scale) {
        BigDecimal percent = BigDecimal.valueOf(100).subtract(marginCall);
        return priceNow.multiply(percent).divide(BigDecimal.valueOf(100), scale, RoundingMode.FLOOR);
    }
}
