package tools;


import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Lists;
import com.tansci.ZhixinApplication;
import com.tansci.domain.picture.pojo.OrderDoc;
import com.tansci.domain.picture.pojo.OrderItem;
import com.tansci.utils.ElasticSearchClient;
import com.tansci.utils.RandomUtil;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
//@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZhixinApplication.class)
public class OrderIndexServiceJunit {

    @Autowired
    private ElasticSearchClient elasticSearchClient;
//    @Autowired
//    private EsConfig esConfig;

    /**
     * 初始化索引结构
     *
     * @return
     */
    @Test
    public void initIndex(){
        String indexName = "order_index";
        // 创建请求
        boolean existsIndex = elasticSearchClient.existsIndex(indexName);
        if (!existsIndex) {
            Map<String, Object> properties = buildMapping();
            elasticSearchClient.createIndex(indexName, properties);
        }
    }
    @Test
    public void deleteIndex(){
        String indexName = "order_index";
        // 创建请求
        boolean existsIndex = elasticSearchClient.existsIndex(indexName);
        if (existsIndex) {
            Map<String, Object> properties = buildMapping();
            elasticSearchClient.deleteIndex(indexName);
        }
    }
    /**
     * 保存订单到ES中
     */
    @Test
    public void saveDocument(){
        String indexName = "order_index";
        //从数据库查询最新订单数据，并封装成对应的es订单结构
        String orderId = "202202020202";
//        OrderIndexDocDTO indexDocDTO = buildOrderIndexDocDTO(orderId);
        OrderDoc orderDoc=new OrderDoc();
        String uuid = RandomUtil.UUID32();
        orderDoc.setOrderId(uuid);
        orderDoc.setOrderNo("12345");
        orderDoc.setOrderUserName("cy");

        List<OrderItem> orderItemList= Lists.newArrayList();
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderItemId("1");
        orderItem.setOrderId(uuid);
        orderItem.setProductName("火腿肠");
        orderItem.setBrandName("双汇");
        orderItem.setSellPrice("28");

        orderItemList.add(orderItem);
        OrderItem orderItem1 = new OrderItem();
        orderItem1.setOrderItemId("12235");
        orderItem1.setOrderId(uuid);
        orderItem1.setProductName("果冻");
        orderItem1.setBrandName("汇源");
        orderItem1.setSellPrice("12");

        orderItemList.add(orderItem1);
        orderDoc.setOrderItems(orderItemList);
        //保存数据到ES中
        elasticSearchClient.addDocument(indexName, uuid, orderDoc);
    }



    /**
     * 通过商品、品牌、价格等条件，分页查询订单数据
     */
    @Test
    public void search1(){
        //查询索引，支持通配符
        String indexName = "order_index";

        String orderUserName = "cy";
        String productName = "火腿肠";
        // 条件搜索
        SearchSourceBuilder builder = new SearchSourceBuilder();

        //组合搜索
        BoolQueryBuilder mainBoolQuery = new BoolQueryBuilder();
        mainBoolQuery.must(QueryBuilders.matchQuery("orderUserName", orderUserName));

        //订单项相关信息搜索
        BoolQueryBuilder nestedBoolQuery = new BoolQueryBuilder(); nestedBoolQuery.must(QueryBuilders.matchQuery("orderItems.productName", productName));
        //内嵌对象搜索，需要指定path
        NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("orderItems",nestedBoolQuery, ScoreMode.None);
        //子表查询
        mainBoolQuery.must(nestedQueryBuilder);

        //封装查询参数
        builder.query(mainBoolQuery);

        //返回参数
        builder.fetchSource(new String[]{}, new String[]{});

        //结果集合分页，从第一页开始，返回最多四条数据
        builder.from(0).size(4);

        //排序
        builder.sort("orderId", SortOrder.DESC);
        // 执行请求
        SearchResponse response = elasticSearchClient.searchDocument(indexName, builder);
        // 当前返回的总行数
        long count = response.getHits().getTotalHits().value;
        // 返回的具体行数
        SearchHit[] searchHits = response.getHits().getHits();
        System.out.println("total="+count+",response：" +response.toString());

}


    /**
     * 构建索引结构
     *
     * @return
     */
    private Map<String, Object> buildMapping() {
        Map<String, Object> properties = new HashMap();
        //订单id  唯一键ID
        properties.put("orderId", ImmutableBiMap.of("type", "keyword"));
        //订单号
        properties.put("orderNo", ImmutableBiMap.of("type", "keyword"));
        //客户姓名
        properties.put("orderUserName", ImmutableBiMap.of("type", "text"));

        //订单项
        Map<String, Object> orderItems = new HashMap();
        //订单项ID
        orderItems.put("orderItemId", ImmutableBiMap.of("type", "keyword"));
        //产品名称
        orderItems.put("productName", ImmutableBiMap.of("type", "text"));
        //品牌名称
        orderItems.put("brandName", ImmutableBiMap.of("type", "text"));
        //销售金额,单位分*100
        orderItems.put("sellPrice", ImmutableBiMap.of("type", "integer"));
        properties.put("orderItems", ImmutableBiMap.of("type", "nested", "properties", orderItems));

        //文档结构映射
        Map<String, Object> mapping = new HashMap();
        mapping.put("properties", properties);
        return mapping;
    }



//    {
//        "orderId":"1",
//            "orderNo":"123456",
//            "orderUserName":"张三",
//            "orderItems":[
//        {
//            "orderItemId":"12234",
//                "orderId":"1",
//                "productName":"火腿肠",
//                "brandName":"双汇",
//                "sellPrice":"28"
//        },
//        {
//            "orderItemId":"12235",
//                "orderId":"1",
//                "productName":"果冻",
//                "brandName":"汇源",
//                "sellPrice":"12"
//        }
//    ]
//    }
}