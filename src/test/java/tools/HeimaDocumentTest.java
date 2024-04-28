package tools;

import com.alibaba.fastjson.JSON;
import com.tansci.domain.picture.pojo.HeimaDoc;
import com.tansci.domain.picture.pojo.Name;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class HeimaDocumentTest {
    private RestHighLevelClient client;
    private  static final String ADD_HEIMA_1="{\n" +
            "  \"info\":\"黑马程序员导师\",\n" +
            "  \"email\":\"23424323@qq.com\",\n" +
            "  \"name\":{\n" +
            "    \"firstName\":\"c\",\n" +
            "    \"lastName\":\"y\"\n" +
            "  }\n" +
            "}";

    /**
     * 新增数据
     * @throws IOException
     */
    @Test
    void testAddDocument() throws IOException {
//        1、准备Request对象
        IndexRequest request = new IndexRequest("heima2").id("2");
        HeimaDoc heimaDoc = new HeimaDoc();
        heimaDoc.setInfo("高性能的Java JSON库,能够快捷5");
        heimaDoc.setEmail("234245354355555@qq.com");
//        heimaDoc.setName(new Name("cc","yy"));
        System.out.println(JSON.toJSONString(heimaDoc));

//        2、准备json文档
        request.source(JSON.toJSONString(heimaDoc), XContentType.JSON);
//        3、发送请求
        client.index(request, RequestOptions.DEFAULT);
    }

    /**
     * 根据ID查询
     * @throws IOException
     */
    @Test
    void testGetDocById() throws IOException {
//        1、准备Request对象
        GetRequest request = new GetRequest("heima2","2");
//        2、发送请求得到响应
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
//        3、解析响应结果
        String sourceAsString = response.getSourceAsString();
        System.out.println(sourceAsString);
        HeimaDoc heimaDoc = JSON.parseObject(sourceAsString, HeimaDoc.class);
        System.out.println(heimaDoc);
    }

    /**
     * 修改文档
     * @throws IOException
     */
    @Test
    void testUpdateDoc() throws IOException {
//        1、准备Request
        UpdateRequest request = new UpdateRequest("heima2", "1");
//        2、准备请求参数
        request.doc(
                "info","我修改你了info",
                "email","777777777777@qq.com"

        );
//        3、发送请求
        client.update(request,RequestOptions.DEFAULT);
    }

    @Test
    void updatebyqueryESdata() throws IOException {
        UpdateByQueryRequest request = new UpdateByQueryRequest("heima2");
//        QueryBuilder upd_q1 = QueryBuilders.matchPhraseQuery("info", "我修改你了info");//等量条件
//        QueryBuilder upd_q2 = QueryBuilders.wildcardQuery("title.keyword", "*财务*");//模糊条件

//        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery().must(upd_q1)
//                .must(upd_q2)
                ;
//        String setdata="ctx._source['user']='修改的user';ctx._source['sort_int']=100;ctx._source['title']='修改的title';";

//        request.setQuery(queryBuilder);
        request.setQuery(QueryBuilders.matchPhraseQuery("info", "我修改你了info"));
//        String setdata="ctx._source['user']='修改的user';ctx._source['sort_int']=100;ctx._source['title']='修改的title';";
        String setdata="ctx._source.email='修改的user66666666666'";
        request.setScript(new Script(setdata));

        BulkByScrollResponse response = client.updateByQuery(request, RequestOptions.DEFAULT);
        System.out.println("受影响行数==>"+response.getStatus().getUpdated());
//        return "";
    }

    /**
     * 删除文档
     * @throws IOException
     */
    @Test
    void testDelDoc() throws IOException {
//        1、准备Request
        DeleteRequest request = new DeleteRequest("heima2", "2");
//        2、发送请求
        client.delete(request,RequestOptions.DEFAULT);
    }

    /**
     * 批量添加
     */
    @Test
    void testBulk() throws IOException {
        HeimaDoc heimaDoc3 = new HeimaDoc();
        heimaDoc3.setInfo("高性能的Java JSON库,能够快捷3");
        heimaDoc3.setEmail("23424535435@qq.com");
        heimaDoc3.setName(new Name("cc","yy"));

        HeimaDoc heimaDoc4 = new HeimaDoc();
        heimaDoc4.setInfo("高性能的Java JSON库,能够快捷4");
        heimaDoc4.setEmail("23424535435@qq.com");
        heimaDoc4.setName(new Name("cc","yy"));

        HeimaDoc heimaDoc5 = new HeimaDoc();
        heimaDoc5.setInfo("高性能的Java JSON库,能够快捷5");
        heimaDoc5.setEmail("23424535435@qq.com");
        heimaDoc5.setName(new Name("cc","yy"));


        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.add(new IndexRequest("heima2").id("3").source(JSON.toJSONString(heimaDoc3),XContentType.JSON));
        bulkRequest.add(new IndexRequest("heima2").id("4").source(JSON.toJSONString(heimaDoc4),XContentType.JSON));
        bulkRequest.add(new IndexRequest("heima2").id("5").source(JSON.toJSONString(heimaDoc5),XContentType.JSON));
        client.bulk(bulkRequest,RequestOptions.DEFAULT);
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
