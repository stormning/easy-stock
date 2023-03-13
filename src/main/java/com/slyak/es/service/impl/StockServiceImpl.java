package com.slyak.es.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.slyak.es.domain.*;
import com.slyak.es.repo.StockRepo;
import com.slyak.es.service.StockService;
import com.slyak.es.util.HttpUtil;
import com.slyak.es.util.StringUtils;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class StockServiceImpl implements StockService {

    private final StockRepo stockRepo;

    static Pattern HINT_PATTERN = Pattern.compile(".*=\"(.*)\"");

    @Autowired
    public StockServiceImpl(StockRepo stockRepo) {
        this.stockRepo = stockRepo;
    }
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
                if (stockProps.length == 1) {
                    continue;
                }
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
    @Transactional
    public Stock getStock(String stockCode) {
        Stock stock = stockRepo.findByCode(stockCode);
        if (stock == null) {
            List<Stock> stocks = queryStocks(stockCode);
            if (stocks.size() > 0) {
                stock = stocks.get(0);
                stock = stockRepo.save(stock);
            }
        }
        return stock;
    }

    @Override
    public StockInfo getStockInfo(String stockCode) {
        return getStockInfos(Collections.singleton(stockCode)).get(0);
    }

    @Override
    public Map<String, StockInfo> mgetStockInfos(Collection<String> stockCodes) {
        List<StockInfo> stockInfos = getStockInfos(stockCodes);
        if (CollectionUtils.isEmpty(stockCodes)){
            return Collections.emptyMap();
        } else {
            Map<String,StockInfo> infoMap = Maps.newHashMap();
            for (StockInfo stockInfo : stockInfos) {
                infoMap.put(stockInfo.getCode(), stockInfo);
            }
            return infoMap;
        }
    }

    @Override
    //https://qt.gtimg.cn/r=0.4209775670385353&q=s_sh600741,s_sh600956
    public List<StockInfo> getStockInfos(Collection<String> stockCodes) {
        StringBuilder sb = new StringBuilder("https://qt.gtimg.cn/r=");
        sb.append(Math.random());
        sb.append("&q=");
        for (String stockCode : stockCodes) {
            sb.append(stockCode).append(",");
        }
        String infosText = HttpUtil.doGet(sb.toString());
        if (infosText.contains("v_pv_none_match")) {
            return Collections.emptyList();
        }
        String[] split = StringUtils.split(infosText, ";");
        if (split != null && split.length > 0) {
            List<StockInfo> stockInfos = Lists.newArrayList();
            for (String s : split) {
                stockInfos.add(new StockInfo(s));
            }
            return stockInfos;
        } else {
            return Collections.emptyList();
        }
    }

    public static void main(String[] args) {
        StockServiceImpl ss = new StockServiceImpl(null);
        System.out.println(ss.mgetStockInfos(Sets.newHashSet("sh600956")));
    }
}
