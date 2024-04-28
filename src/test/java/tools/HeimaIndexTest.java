package tools;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class HeimaIndexTest {

    private RestHighLevelClient client;
    private static final String HEIMA2_MAPPING="{\n" +
            "  \"mappings\": {\n" +
            "    \"properties\": {\n" +
            "      \"info\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"ik_smart\"\n" +
            "      },\n" +
            "      \"email\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"name\": {\n" +
            "        \"type\": \"object\",\n" +
            "        \"properties\": {\n" +
            "          \"firstName\": {\n" +
            "            \"type\": \"keyword\"\n" +
            "          },\n" +
            "          \"lastName\": {\n" +
            "            \"type\": \"keyword\"\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

    @Test
    void testInit(){

        System.out.println(HEIMA2_MAPPING);
//        System.out.println(client);
    }
//    创建索引库
    @Test
    void testCreateHeima2Index() throws IOException {
        System.out.println("start");
//        1创建Request对象
        CreateIndexRequest request=new CreateIndexRequest("heima2");
//        2、请求参数，mapping dsl 字符串
        request.source(HEIMA2_MAPPING, XContentType.JSON);
//        3发起请求
        client.indices().create(request, RequestOptions.DEFAULT);
    }
//    判断索引库是否存在
    @Test
    void testExistHeima2Index() throws IOException {
        GetIndexRequest request=new GetIndexRequest("heima2");
        System.out.println(client.indices().exists(request, RequestOptions.DEFAULT));
    }
//    删除索引库
    @Test
    void testDelHeima2Index() throws IOException {
        DeleteIndexRequest request=new DeleteIndexRequest("heima2");
        client.indices().delete(request, RequestOptions.DEFAULT);
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
