package tools;

import com.alibaba.fastjson.JSON;
import com.tansci.domain.picture.pojo.HeimaDoc;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class HeimaSearchTest {
    private RestHighLevelClient client;

    @Test
    void testMatchAll() throws IOException {

        SearchRequest request = new SearchRequest("heima2");
        request.source().query(QueryBuilders.matchAllQuery());
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        SearchHits searchHits = response.getHits();
        long total = searchHits.getTotalHits().value;
        System.out.println("一共"+total+"条数据！");
        SearchHit[] hits = searchHits.getHits();
        for( SearchHit searchHit :hits){
            String sourceAsString = searchHit.getSourceAsString();
            HeimaDoc heimaDoc = JSON.parseObject(sourceAsString, HeimaDoc.class);
            System.out.println(heimaDoc);
        }

    }

    @Test
    void testMatch() throws IOException {

        SearchRequest request = new SearchRequest("heima2");
        request.source().query(QueryBuilders.matchQuery("info","JAVA"));
//        精确查询
//        request.source().query(QueryBuilders.termQuery("info","JAVA"));
//        范围查询
//        request.source().query(QueryBuilders.rangeQuery("info").lte(100).gte(200));


        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        SearchHits searchHits = response.getHits();
        long total = searchHits.getTotalHits().value;
        System.out.println("一共"+total+"条数据！");
        SearchHit[] hits = searchHits.getHits();
        for( SearchHit searchHit :hits){
            String sourceAsString = searchHit.getSourceAsString();
            HeimaDoc heimaDoc = JSON.parseObject(sourceAsString, HeimaDoc.class);
            System.out.println(heimaDoc);
        }

    }


    @Test
    void testBool() throws IOException {

        SearchRequest request = new SearchRequest("heima2");
        BoolQueryBuilder boolQuery= QueryBuilders.boolQuery();
        boolQuery.must(QueryBuilders.termQuery("info","java"));
//        boolQuery.filter()
        request.source().query(boolQuery);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        SearchHits searchHits = response.getHits();
        long total = searchHits.getTotalHits().value;
        System.out.println("一共"+total+"条数据！");
        SearchHit[] hits = searchHits.getHits();
        for( SearchHit searchHit :hits){
            String sourceAsString = searchHit.getSourceAsString();
            HeimaDoc heimaDoc = JSON.parseObject(sourceAsString, HeimaDoc.class);
            System.out.println(heimaDoc);
        }

    }

    @Test
    void testBool2() throws IOException {

        SearchRequest request = new SearchRequest("picannotations_index");
        BoolQueryBuilder boolQuery= QueryBuilders.boolQuery();
        boolQuery.must(QueryBuilders.matchQuery("all","BUTTONS Dance"));
//        boolQuery.filter()
        request.source().query(boolQuery);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        SearchHits searchHits = response.getHits();
        long total = searchHits.getTotalHits().value;
        System.out.println("一共"+total+"条数据！");
        SearchHit[] hits = searchHits.getHits();
        for( SearchHit searchHit :hits){
            String sourceAsString = searchHit.getSourceAsString();
//            PicAnnotationsDoc picAnnotationsDoc = JSON.parseObject(sourceAsString, PicAnnotationsDoc.class);
//            System.out.println(picAnnotationsDoc);
        }

    }

    @BeforeEach
    void setUp(){
        this.client=new RestHighLevelClient(RestClient.builder(
                HttpHost.create("http://101.43.34.244:9200")
        ));
    }

    @AfterEach
    void tearDown() throws IOException {
        this.client.close();
    }
}
