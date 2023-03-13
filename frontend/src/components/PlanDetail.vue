<template>
  <div class="stock-holder" v-if="plan!=null">
    <div class="flex-container mb5">
      <div class="flex stock-title">{{plan.stock.name}}</div>
      <div class="flex stock-code">{{plan.stock.code}}</div>
    </div>
    <div class="flex-container mb10">
      <div class="flex flex-end text-minor">总盈亏</div>
      <div class="flex result win">
        <div class="flex percent">{{plan.winPercent}} %</div>
        <div class="flex flex-end total">{{plan.win}}</div>
      </div>
    </div>
    <div class="flex-container no-padding">
      <div class="flex-container detail-item mr20">市价
        <div class="flex-auto detail-text">{{ plan.stock.info.price }}</div>
      </div>
      <div class="flex-container detail-item">持仓数
        <div class="flex-auto detail-text">{{ plan.amount }}</div>
      </div>
    </div>
    <div class="flex-container no-padding">
      <div class="flex-container detail-item mr20">买入成本
        <div class="flex-auto detail-text">{{ plan.avgPrice }}</div>
      </div>
      <div class="flex-container detail-item">市值
        <div class="flex-auto detail-text">{{ plan.marketVal }}</div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "PlanDetail",
  props : ['plan']
}
</script>

<style scoped lang="scss">
@import '../assets/css/core.scss';

.stock-title {
  font-weight: 500;
  font-size: 16px;
}

.detail-item {
  padding: 5px 0;
  width: 110px;
  color: $text-minor;
  //border-bottom: 1px solid $border-minor;
}

.detail-text {
  color: $text-main;
}

.stock-code {
  color: $text-minor;
  align-self: flex-end;
  margin-left: 10px;
}

.stock-holder {
  font-size: 12px;
}

.result {
  font-size: 16px;
  margin-left: 10px;

  .percent {
    margin-right: 10px;
  }

  .total {
    font-size: 12px;
  }

  &.win {
    color: $red;
  }

  &.lose {
    color: $green;
  }
}
</style>