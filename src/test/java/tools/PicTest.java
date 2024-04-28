package tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tansci.ZhixinApplication;
import com.tansci.domain.picture.dto.PictureAnnoDto;
import com.tansci.domain.picture.dto.PictureDocDto;
import com.tansci.utils.AuthV3Util;
import com.tansci.utils.ElasticSearchClient;
import com.tansci.utils.HttpUtil;
import com.tansci.utils.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.apache.poi.ss.usermodel.*;
import org.elasticsearch.action.get.GetResponse;
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
import org.springframework.test.context.ActiveProfiles;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

//@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZhixinApplication.class)
@ActiveProfiles("dev")
@Slf4j
//@ActiveProfiles("pro")
public class PicTest {

    @Autowired
    private ElasticSearchClient elasticSearchClient;
//    @Autowired
//    private EsConfig esConfig;

    String indexName = "picture_index";

    @Test
    public void reCreate() throws Exception {
        deleteIndex();
        initIndex();
        batchParsingResult();
    }

    //版本3
    @Test
    void batchParsingResult() throws Exception {
        System.out.println("hello");
//        File file = new File("E:\\ideaworkspaces\\ss\\data\\jsonoutput");
        String filePath="E:\\sandong\\m2c\\src\\main\\resources\\static\\jsonoutput";
        File file = new File(filePath);
//        File file = new File("E:\\sandong\\Image-analysis-review-backend\\data\\test");

        if (!file.exists()) {
            return;
        }
        File[] files = file.listFiles();
        Map<String, List<File>> fileMap = Maps.newHashMap();
        for (File file1 : files) {
            List<File> tempListFile;
            String file1Name = file1.getName();
            file1Name = file1Name.substring(0, file1Name.length() - 5);
            String frontName = file1Name.substring(0, file1Name.length() - 5);
            String backName = file1Name.substring(file1Name.length() - 5);
//            String tempName = file1.getName().split("-")[0];
            // 定义要匹配的正则表达式
            String regex = "[a-zA-Z]";
            String splitName = backName.split(regex)[0];
            String tempName;
            if (backName.length() == splitName.length()) {
                tempName = frontName + backName;
            } else {
                tempName = frontName + splitName;
            }

            if (fileMap.containsKey(tempName)) {
                fileMap.get(tempName).add(file1);
            } else {
                tempListFile = Lists.newArrayList();
                tempListFile.add(file1);
                fileMap.put(tempName, tempListFile);
            }


//            parsingResult(file1);
        }
        fileMap.forEach((key, value) -> {
            try {
                parsingResult(value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    AtomicInteger autoId = new AtomicInteger();

    @Test
    void parsingResult(List<File> file) throws Exception {
        System.out.println("start=============");
        autoId.getAndIncrement();
//        String filePath = "E:\\ideaworkspaces\\tools\\src\\main\\resources\\jsonoutput\\Apple000000.json";
//        File file = new File("E:\\ideaworkspaces\\ss\\data\\test");
        AtomicInteger sort = new AtomicInteger();
        File file1 = file.get(0);

//        添加图片索引文档

        PictureDocDto pictureDoc = new PictureDocDto();
        List<PictureAnnoDto> pictureAnnoList = Lists.newArrayList();
        file.stream().forEach(tFile -> {
            sort.getAndIncrement();
//            String uuid = RandomUtil.UUID32();
            BufferedReader bufferedReader = null;
            try {
                bufferedReader = new BufferedReader(new FileReader(tFile));

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
//        System.out.println(sb.toString());

                JSONObject jsonObject = JSON.parseObject(sb.toString());
                String fileName = jsonObject.getString("file_name");
                if (sort.intValue() == 1) {
                    pictureDoc.setPicture(fileName);
                    pictureDoc.setId(autoId.intValue());
                    pictureDoc.setStatus(0);
                    pictureDoc.setCreateTime(new Date());
                    pictureDoc.setUpdateTime(new Date());
                }

                String  objectAnno= getObjectAnno(jsonObject);

//        解析 textAnnotations
                JSONArray textAnnotations = jsonObject.getJSONArray("textAnnotations");
                if (Objects.nonNull(textAnnotations)) {
                    JSONObject textJSONObject = (JSONObject) (textAnnotations.get(0));
                    PictureAnnoDto pictureAnno = new PictureAnnoDto();
                    pictureAnno.setId(RandomUtil.UUID32());
                    pictureAnno.setPicFileName(fileName);
                    pictureAnno.setPtype("textAnnotations");
                    pictureAnno.setTextAnno(textJSONObject.getString("description"));
                    pictureAnno.setObjectAnno(objectAnno);
                    pictureAnnoList.add(pictureAnno);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        pictureDoc.setPictureAnnos(pictureAnnoList);
        elasticSearchClient.addDocument(indexName, autoId.toString(), pictureDoc);
        log.info("添加成功");
        System.out.println("end=============");
    }

    private String getObjectAnno(JSONObject jsonObject) throws NoSuchAlgorithmException {
        StringBuilder objectAnnoBuilder = new StringBuilder();
        //        解析 faceAnnotations
        JSONArray faceAnnotations = jsonObject.getJSONArray("faceAnnotations");
        if (Objects.nonNull(faceAnnotations)) {
            for (int i = 0; i < faceAnnotations.size(); i++) {
                JSONObject jsonObject1 = faceAnnotations.getJSONObject(i);
                Set<String> sets = jsonObject1.keySet();
                sets.stream().forEach(e->{
                    if(!"bounding_poly".equalsIgnoreCase(e)){
                        objectAnnoBuilder.append(e).append(",");
                    }
                });
            }
        }
        //        解析 faceAnnotations
        JSONArray labelAnnotations = jsonObject.getJSONArray("labelAnnotations");
        if (Objects.nonNull(labelAnnotations)) {
            for (int i = 0; i < labelAnnotations.size(); i++) {
                JSONObject jsonObject1 = labelAnnotations.getJSONObject(i);
                String description = jsonObject1.getString("description");
                objectAnnoBuilder.append(description).append(",");
            }
        }
        //        解析 localizedObjectAnnotations
        JSONArray localizedObjectAnnotations = jsonObject.getJSONArray("localizedObjectAnnotations");
        if (Objects.nonNull(localizedObjectAnnotations)) {
            for (int i = 0; i < localizedObjectAnnotations.size(); i++) {
                JSONObject jsonObject1 = localizedObjectAnnotations.getJSONObject(i);
                String description = jsonObject1.getString("name");
                objectAnnoBuilder.append(description).append(",");
            }
        }
        String objectAnno = objectAnnoBuilder.toString();
//        翻译英文为中文
//        API 并发受限，不使用了
//        String translate = translate(objectAnno);

        return objectAnno;
    }

    private static final String APP_KEY = "166de3784e4fa2224d";     // 您的应用ID
    private static final String APP_SECRET = "gJvyjbceK4ap433F86BWSJYym0zSp13fdi";  // 您的应用密钥

    private String translate(String str)  {
        String translate = null;
        try {
            translate = "";
            // 添加请求参数
            Map<String, String[]> params = createRequestParams(str);
            // 添加鉴权相关参数
            AuthV3Util.addAuthParams(APP_KEY, APP_SECRET, params);
            // 请求api服务
            byte[] result = HttpUtil.doPost("https://openapi.youdao.com/api", null, params, "application/json");
            // 打印返回结果
            if (result != null) {
                String resultStr = new String(result, StandardCharsets.UTF_8);
                System.out.println(resultStr);
                JSONObject jsonObject = JSON.parseObject(resultStr);
                JSONArray jsonObject1 = jsonObject.getJSONArray("translation");
                translate = jsonObject1.getString(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return translate;
    }

    private Map<String, String[]> createRequestParams(String q) {
        /*
         * note: 将下列变量替换为需要请求的参数
         * 取值参考文档: https://ai.youdao.com/DOCSIRMA/html/%E8%87%AA%E7%84%B6%E8%AF%AD%E8%A8%80%E7%BF%BB%E8%AF%91/API%E6%96%87%E6%A1%A3/%E6%96%87%E6%9C%AC%E7%BF%BB%E8%AF%91%E6%9C%8D%E5%8A%A1/%E6%96%87%E6%9C%AC%E7%BF%BB%E8%AF%91%E6%9C%8D%E5%8A%A1-API%E6%96%87%E6%A1%A3.html
         */
//        String q = "Personal computer，Output device，Laptop";
        String from = "en";
        String to = "zh-CHS";
        String vocabId = "您的用户词表ID";

        return new HashMap<String, String[]>() {{
            put("q", new String[]{q});
            put("from", new String[]{from});
            put("to", new String[]{to});
            put("vocabId", new String[]{vocabId});
        }};
    }
    //版本2
//    @Test
//    void batchParsingResult() throws Exception {
//        System.out.println("hello");
//        File file = new File("E:\\ideaworkspaces\\ss\\src\\main\\resources\\jsonoutput");
//
//        if (!file.exists()) {
//            return;
//        }
//        File[] files = file.listFiles();
//        Map<String, List<File>> fileMap = Maps.newHashMap();
//        for (File file1 : files) {
//            List<File> tempListFile;
//            String tempName = file1.getName().split("-")[0];
//            if (fileMap.containsKey(tempName)) {
//                fileMap.get(tempName).add(file1);
//            } else {
//                tempListFile = Lists.newArrayList();
//                tempListFile.add(file1);
//                fileMap.put(tempName, tempListFile);
//            }
//
//
////            parsingResult(file1);
//        }
//        fileMap.forEach((key, value) -> {
//            try {
//                parsingResult(value);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//
//    }
//    AtomicInteger autoId = new AtomicInteger();
//
//    @Test
//    void parsingResult(List<File> file) throws Exception {
//        System.out.println("start=============");
//        autoId.getAndIncrement();
//        String filePath = "E:\\ideaworkspaces\\tools\\src\\main\\resources\\jsonoutput\\Apple000000.json";
//        AtomicInteger sort = new AtomicInteger();
//        File file1 = file.get(0);
//
////        添加图片索引文档
//
//        PictureDoc pictureDoc = new PictureDoc();
//        List<PictureAnno> pictureAnnoList = Lists.newArrayList();
//        file.stream().forEach(tFile -> {
//            sort.getAndIncrement();
////            String uuid = RandomUtil.UUID32();
//            BufferedReader bufferedReader = null;
//            try {
//                bufferedReader = new BufferedReader(new FileReader(tFile));
//
//                StringBuilder sb = new StringBuilder();
//                String line;
//                while ((line = bufferedReader.readLine()) != null) {
//                    sb.append(line);
//                }
////        System.out.println(sb.toString());
//
//                JSONObject jsonObject = JSON.parseObject(sb.toString());
//                String fileName = jsonObject.getString("file_name");
//                if (sort.intValue() == 1) {
//                    pictureDoc.setPicture(fileName);
//                    pictureDoc.setId(autoId.intValue());
//                    pictureDoc.setStatus(0);
//                    pictureDoc.setCreateTime(new Date());
//                    pictureDoc.setUpdateTime(new Date());
//                }
////        解析 textAnnotations
//                JSONArray textAnnotations = jsonObject.getJSONArray("textAnnotations");
//                if (Objects.nonNull(textAnnotations)) {
//                    JSONObject textJSONObject = (JSONObject) (textAnnotations.get(0));
//                    PictureAnno pictureAnno = new PictureAnno();
//                    pictureAnno.setId(RandomUtil.UUID32());
//                    pictureAnno.setPicFileName(fileName);
//                    pictureAnno.setPtype("textAnnotations");
//                    pictureAnno.setTextAnno(textJSONObject.getString("description"));
//                    pictureAnnoList.add(pictureAnno);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//
//        pictureDoc.setPictureAnnos(pictureAnnoList);
//        elasticSearchClient.addDocument(indexName,autoId.toString(),pictureDoc);
//        System.out.println("end=============");
//    }


    /**
     * 初始化索引结构
     *
     * @return
     */
    @Test
    public void initIndex() {

        // 创建请求
        boolean existsIndex = elasticSearchClient.existsIndex(indexName);
        if (!existsIndex) {
            Map<String, Object> properties = buildMapping();
            elasticSearchClient.createIndex(indexName, properties);
        }
    }

    @Test
    public void deleteIndex() {
        // 创建请求
        boolean existsIndex = elasticSearchClient.existsIndex(indexName);
        if (existsIndex) {
            Map<String, Object> properties = buildMapping();
            elasticSearchClient.deleteIndex(indexName);
        }
    }

    /**
     * 通过商品、品牌、价格等条件，分页查询订单数据
     */
    @Test
    public void search1() {
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
        BoolQueryBuilder nestedBoolQuery = new BoolQueryBuilder();
        nestedBoolQuery.must(QueryBuilders.matchQuery("orderItems.productName", productName));
        //内嵌对象搜索，需要指定path
        NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("orderItems", nestedBoolQuery, ScoreMode.None);
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
        System.out.println("total=" + count + ",response：" + response.toString());

    }


    /**
     * 构建索引结构
     *
     * @return
     */
    private Map<String, Object> buildMapping() {
        Map<String, Object> properties = new HashMap();
        //订单id  唯一键ID
        properties.put("id", ImmutableBiMap.of("type", "integer"));
        properties.put("picture", ImmutableBiMap.of("type", "keyword"));
        //订单号
        properties.put("status", ImmutableBiMap.of("type", "integer"));
        //客户姓名
        properties.put("createTime", ImmutableBiMap.of("type", "date"));
        properties.put("updateTime", ImmutableBiMap.of("type", "date"));

        //订单项
        Map<String, Object> subItems = new HashMap();
        //订单项ID
        subItems.put("id", ImmutableBiMap.of("type", "keyword"));
        subItems.put("picFileName", ImmutableBiMap.of("type", "keyword"));
        //产品名称
        subItems.put("ptype", ImmutableBiMap.of("type", "keyword"));
        //品牌名称
        subItems.put("textAnno", ImmutableBiMap.of("type", "text", "analyzer", "ik_max_word"));
//        类似物品
        subItems.put("objectAnno", ImmutableBiMap.of("type", "text", "analyzer", "ik_max_word"));

        properties.put("pictureAnnos", ImmutableBiMap.of("type", "nested", "properties", subItems));

        //文档结构映射
        Map<String, Object> mapping = new HashMap();
        mapping.put("properties", properties);
        return mapping;
    }
    @Test
    public void transExcel() throws IOException {
        //需要解析的Excel所在地址
        String path = "C:\\Users\\Administrator\\Desktop\\photo\\britishmuseum porcelain china 5,655 results.xlsx";
        //转换excel为list
        List<Map<String, String>> maps = transExcel(path);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(maps);
        //输出的text文件地址
        String filePath = "C:\\Users\\Administrator\\Desktop\\photo\\file.txt";
        File file = new File(filePath);
        FileWriter writer = new FileWriter(file);
        writer.write(json);
        writer.close();

        log.info("解析Excel结果：[{}]", json);

    }

    /**
     * 解析Excel
     * @param path excel地址
     * @return List<Map<String,String>>
     * @throws IOException IOException
     */
    public List<Map<String,String>> transExcel(String path) throws IOException {
        FileInputStream file = new FileInputStream(path);
        Workbook workbook = WorkbookFactory.create(file);
        Sheet sheet = workbook.getSheetAt(0); // 假设你要读取第一个Sheet
        List<Map<String, String>> dataList = new ArrayList<>(); // 存储所有行的数据

// 遍历每一行（从第二行开始，因为第一行是字段名）
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            Map<String, String> rowData = new HashMap<>(); // 存储当前行的数据

            // 遍历每一列
            for (int j = 0; j < row.getLastCellNum(); j++) {
                Cell cell = row.getCell(j);
                String fieldName = sheet.getRow(0).getCell(j).getStringCellValue(); // 第一行是字段名
                String cellValue = "";

                // 根据单元格类型获取值
                if (cell != null) {
                    switch (cell.getCellType()) {
                        case STRING:
                            cellValue = cell.getStringCellValue();
                            break;
                        case NUMERIC:
                            cellValue = String.valueOf(cell.getNumericCellValue());
                            break;
                        // 其他类型的单元格，你可以根据需要进行处理
                    }
                }

                rowData.put(fieldName, cellValue);
            }

            dataList.add(rowData);
        }
        file.close(); // 关闭文件流
        return dataList;
    }

    @Test
    public void reloadData(){
        String id ="222";
        try {
            reloadData(id);
        }catch (Exception e){
            log.error("重新导入数据失败：[{}]",e.getMessage());
        }

    }
    /**
     * 删除指定id的数据库资料并重新导入
     * @param id id
     */
    public void reloadData(String id) throws Exception {
        //索引
        String PICTURE_INDEX = "picture_index";
//        //根据id获取内容
        GetResponse getResponse = elasticSearchClient.getDocumentById(PICTURE_INDEX, id);
        String sourceAsString = getResponse.getSourceAsString();
        PictureDocDto pictureDoc = JSON.parseObject(sourceAsString, PictureDocDto.class);
        pictureDoc.setId(Integer.parseInt(getResponse.getId()));
        // 执行删除文档
        elasticSearchClient.deleteDocument(PICTURE_INDEX, id);
        //获取不带后缀名的名称
        String pictureName = pictureDoc.getPicture();
        int dotIndex = pictureName.lastIndexOf(".");
        String newPictureName = pictureName.substring(0, dotIndex);
//        String newPictureName = "Apple00001";
        //获取静态资源
        File file = new File("D:\\idea\\m2c\\src\\main\\resources\\static\\jsonoutput");
        if (!file.exists()) {
            return;
        }
        //获取相同名称前缀的文件集合
        File[] files = file.listFiles((dir, name) -> name.contains(newPictureName));
        List<File> fileList = new ArrayList<>();
        assert files != null;
        for (File fileOne : files) {
            if (fileOne.isFile()) {
                fileList.add(fileOne);
            }
        }
        //数据入库（es）
        toEs(id, fileList);
    }

    /**
     * 导入Es
     * @param id id
     * @param fileList  文件集合
     */
    private void toEs(String id, List<File> fileList) {
        log.info("start=============");
        AtomicInteger sort = new AtomicInteger();
//        添加图片索引文档
        PictureDocDto pictureDoc = new PictureDocDto();
        List<PictureAnnoDto> pictureAnnoList = Lists.newArrayList();
        fileList.forEach(tFile -> {
            sort.getAndIncrement();
            BufferedReader bufferedReader = null;
            try {
                bufferedReader = new BufferedReader(new FileReader(tFile));

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                JSONObject jsonObject = JSON.parseObject(sb.toString());
                String fileName = jsonObject.getString("file_name");
                if (sort.intValue() == 1) {
                    pictureDoc.setPicture(fileName);
                    pictureDoc.setId(Integer.valueOf(id));
                    pictureDoc.setStatus(0);
                    pictureDoc.setCreateTime(new Date());
                    pictureDoc.setUpdateTime(new Date());
                }
                String  objectAnno= getObjectAnno(jsonObject);
//        解析 textAnnotations
                JSONArray textAnnotations = jsonObject.getJSONArray("textAnnotations");
                if (Objects.nonNull(textAnnotations)) {
                    JSONObject textJSONObject = (JSONObject) (textAnnotations.get(0));
                    PictureAnnoDto pictureAnno = new PictureAnnoDto();
                    pictureAnno.setId(RandomUtil.UUID32());
                    pictureAnno.setPicFileName(fileName);
                    pictureAnno.setPtype("textAnnotations");
                    pictureAnno.setTextAnno(textJSONObject.getString("description"));
                    pictureAnno.setObjectAnno(objectAnno);
                    pictureAnnoList.add(pictureAnno);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        pictureDoc.setPictureAnnos(pictureAnnoList);
        elasticSearchClient.addDocument(indexName, id, pictureDoc);
        log.info("添加成功");
        log.info("end=============");
    }

}