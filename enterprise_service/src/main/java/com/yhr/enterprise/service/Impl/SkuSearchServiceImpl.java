package com.yhr.enterprise.service.Impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.yhr.enterprise.service.SkuSearchService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class SkuSearchServiceImpl implements SkuSearchService {
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public Map search(Map<String, String> searchMap) {

        //封装查询请求
        SearchRequest searchRequest =new SearchRequest("sku");
        searchRequest.types("doc");//设置查询类型，可以不设置查询全部
        SearchSourceBuilder searchSourceBuilder =new SearchSourceBuilder();//相当于query
        //关键字搜索
        BoolQueryBuilder boolQueryBuilder =QueryBuilders.boolQuery();//bool查询构建器
        MatchQueryBuilder queryBuilder = QueryBuilders.matchQuery("name",searchMap.get("keywords"));
        boolQueryBuilder.must(queryBuilder);

        searchSourceBuilder.query(queryBuilder);
        searchRequest.source(searchSourceBuilder);

        //获取查询结果
        Map resultMap = new HashMap();
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits searchHits =searchResponse.getHits();
            long totalHits = searchHits.getTotalHits();
            System.out.println("记录数"+totalHits);
            SearchHit[] hits = searchHits.getHits();

            //商品列表
            List<Map<String,Object>>  resultList = new ArrayList<>();
            for (SearchHit hit:hits) {
                Map<String,Object> skuMap = hit.getSourceAsMap();
                resultList.add(skuMap);
            }
            resultMap.put("rows",resultList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultMap;
    }
}
