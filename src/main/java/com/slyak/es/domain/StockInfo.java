package com.slyak.es.domain;

import com.google.common.collect.Range;
import com.slyak.es.util.DateUtils;
import com.slyak.es.util.StringUtils;
import lombok.Data;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

@Data
//https://qt.gtimg.cn/r=0.4209775670385353&q=sh600741,sh600956
//v_sh600741="1~华域汽车~600741~19.06~18.90~18.90~91344~48307~43037~19.06~321~19.05~376~19.04~234~19.03~320~19.02~360~19.07~237~19.08~259~19.09~223~19.10~830~19.11~296~~20230301155928~0.16~0.85~19.17~18.86~19.06/91344/173901870~91344~17390~0.29~9.04~~19.17~18.86~1.64~600.91~600.91~1.19~20.79~17.01~0.54~-234~19.04~9.24~9.29~~~1.18~17390.1870~0.0000~0~ ~GP-A~9.98~-1.29~4.46~13.15~4.57~24.92~15.22~3.87~2.47~5.60~3152723984~3152723984~-6.77~-14.53~3152723984~~~-22.46~0.05~~CNY";
public class StockInfo {

    /**
     * 编号, 2
     */
    private String code;

    /**
     * 当前价格, 3
     */
    private BigDecimal price;

    /**
     * 昨收, 4
     */
    private BigDecimal yesPrice;

    /**
     * 今开, 5
     */
    private BigDecimal toPriceStart;


    /**
     * 获取最新的收盘价，用于计算可执行计划项
     */
    public BigDecimal getFixedPrice() {
        //收盘后则取当前价格作为收盘价，否则取昨日收盘价
        if (DateUtils.getFragmentInHours(new Date(), Calendar.DATE) >= 15){
            return price;
        } else {
            return yesPrice;
        }
    }


    private static final Pattern P = Pattern.compile("v_(.*)=\"(.*)\"");

    public StockInfo(String text) {
        List<String> gs = StringUtils.findGroups(text, P);
        this.code = gs.get(0);
        String infoText = gs.get(1);
        String[] split = StringUtils.split(infoText, "~");
        Assert.isTrue(split != null && split.length > 3, "无效的股票状态");
        this.price = new BigDecimal(split[3]);
        this.yesPrice = new BigDecimal(split[4]);
        this.toPriceStart = new BigDecimal(split[5]);
    }

}
