package tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tansci.ZhixinApplication;
import com.tansci.domain.picture.dto.PictureAnnoDto;
import com.tansci.domain.picture.dto.PictureDocDto;
import com.tansci.utils.ElasticSearchClient;
import com.tansci.utils.RandomUtil;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
@SpringBootTest(classes = ZhixinApplication.class)
public class PictureParsingTest {
    @Autowired
    private ElasticSearchClient elasticSearchClient;
    private RestHighLevelClient client;
    String indexName = "picture_index";


    @Test
    void batchParsingResult() throws Exception {
        System.out.println("hello");
        File file = new File("D:\\idea\\\\ss\\src\\main\\resources\\jsonoutput");

        if (!file.exists()) {
            return;
        }
        File[] files = file.listFiles();
//        List<List<File>> listFile=Lists.newArrayList();
        Map<String, List<File>> fileMap = Maps.newHashMap();
        for (File file1 : files) {
            List<File> tempListFile;
            String tempName = file1.getName().split("-")[0];
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
        String filePath = "E:\\ideaworkspaces\\tools\\src\\main\\resources\\jsonoutput\\Apple000000.json";
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
//        解析 textAnnotations
                JSONArray textAnnotations = jsonObject.getJSONArray("textAnnotations");
                if (Objects.nonNull(textAnnotations)) {
                    JSONObject textJSONObject = (JSONObject) (textAnnotations.get(0));
                    PictureAnnoDto pictureAnno = new PictureAnnoDto();
                    pictureAnno.setId(RandomUtil.UUID32());
                    pictureAnno.setPicFileName(fileName);
                    pictureAnno.setPtype("textAnnotations");
                    pictureAnno.setTextAnno(textJSONObject.getString("description"));
                    pictureAnnoList.add(pictureAnno);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        pictureDoc.setPictureAnnos(pictureAnnoList);
        elasticSearchClient.addDocument(indexName,autoId.toString(),pictureDoc);
//        System.out.println(description);

//        //        1、准备Request对象
//        IndexRequest request = new IndexRequest("picture_index");
//        System.out.println(JSON.toJSONString(pictureDoc));
////        2、准备json文档
//        request.source(JSON.toJSONString(pictureDoc), XContentType.JSON);
////        3、发送请求
//        client.index(request, RequestOptions.DEFAULT);
//
////        批量入库
//        BulkRequest bulkRequest = new BulkRequest();
//        for (PicAnnotationsDoc picAnnotationsDoc1 : picAnnotationsDocList) {
//            bulkRequest.add(new IndexRequest("picannotations_index").source(JSON.toJSONString(picAnnotationsDoc1), XContentType.JSON));
//        }
////        bulkRequest.add(new IndexRequest("picannotations_index").id("3").source(JSON.toJSONString(heimaDoc3),XContentType.JSON));
////        bulkRequest.add(new IndexRequest("heima2").id("4").source(JSON.toJSONString(heimaDoc4),XContentType.JSON));
////        bulkRequest.add(new IndexRequest("heima2").id("5").source(JSON.toJSONString(heimaDoc5),XContentType.JSON));
//        client.bulk(bulkRequest, RequestOptions.DEFAULT);


        System.out.println("end=============");
    }

    @BeforeEach
    void setUp() {
        this.client = new RestHighLevelClient(RestClient.builder(
                HttpHost.create("http://101.43.34.244:9200")
        ));
    }

    @AfterEach
    void tearDown() throws IOException {
        this.client.close();
    }

}
