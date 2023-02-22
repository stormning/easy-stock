package com.slyak.es.service.impl;

import com.google.common.collect.Lists;
import com.slyak.es.domain.Market;
import com.slyak.es.domain.OrgType;
import com.slyak.es.domain.Stock;
import com.slyak.es.domain.StockType;
import com.slyak.es.service.StockService;
import com.slyak.es.util.HttpUtil;
import com.slyak.es.util.StringUtils;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class StockServiceImpl implements StockService {

    static Pattern HINT_PATTERN = Pattern.compile(".*=\"(.*)\"");


    //https://blog.csdn.net/zchill/article/details/121303871
    //实时行情
    //简易
    //https://qt.gtimg.cn/r=0.4209775670385353&q=s_sh600741
    //详细
    //https://qt.gtimg.cn/r=0.4209775670385353&q=sh000001,sz399001,r_hkHSI
    //查询股票名称代码信息
    //https://smartbox.gtimg.cn/s3/?v=2&q=hyqc&t=all&c=1
    //v_hint="sh~600741~\u534e\u57df\u6c7d\u8f66~hyqc~GP-A"
    @Override
    public List<Stock> queryStocks(String keyword) {
        String s = HttpUtil.doGet("https://smartbox.gtimg.cn/s3/?v=2&q=" + keyword + "&t=all&c=1");
        String decoded = StringUtils.decodeUnicode(s);
        String g1 = StringUtils.find(decoded, HINT_PATTERN, 1);
        String[] stockTexts = StringUtils.split(g1, "^");
        List<Stock> stocks = Lists.newArrayList();
        if (stockTexts != null) {
            for (String stockText : stockTexts) {
                String[] stockProps = StringUtils.split(stockText, "~");
                String[] split = StringUtils.split(stockProps[4], "-");
                OrgType orgType = EnumUtils.getEnum(OrgType.class, split[0]);
                if (orgType == null) {
                    continue;
                }
                Market market = EnumUtils.getEnum(Market.class, stockProps[0]);
                if (market == null) {
                    continue;
                }
                Stock stock = new Stock();
                stock.setOrgType(orgType);
                stock.setMarket(market);
                stock.setCode(stockProps[1]);
                stock.setName(stockProps[2]);
                stock.setPinyin(stockProps[3]);
                if (split.length > 1) {
                    stock.setType(EnumUtils.getEnum(StockType.class, split[1]));
                }
                stocks.add(stock);
            }
        }
        return stocks;
    }

    @Override
    public Stock getStock(String stockCode) {
        List<Stock> stocks = queryStocks(stockCode);
        if (stocks.size() > 0) {
            return stocks.get(0);
        }
        return null;
    }

    public static void main(String[] args) {
        StockServiceImpl ss = new StockServiceImpl();
        System.out.println(ss.queryStocks("hyqc"));
    }
}
