<template>
  <div class="stock-holder">
    <div class="flex-container mb5">
      <div class="flex stock-title">中国电影</div>
      <div class="flex stock-code">600977</div>
    </div>
    <div class="flex-container mb10">
      <div class="flex flex-end text-minor">总盈亏</div>
      <div class="flex result win">
        <div class="flex percent">+5.00 %</div>
        <div class="flex flex-end total">+1235.33</div>
      </div>
    </div>
    <div class="flex-container no-padding">
      <div class="flex-container detail-item mr20">市价
        <div class="flex-auto detail-text">12.3</div>
      </div>
      <div class="flex-container detail-item">持仓数
        <div class="flex-auto detail-text">12.3</div>
      </div>
    </div>
    <div class="flex-container no-padding">
      <div class="flex-container detail-item mr20">成本
        <div class="flex-auto detail-text">12.3</div>
      </div>
      <div class="flex-container detail-item">市值
        <div class="flex-auto detail-text">12.3</div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "StockDetail"
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