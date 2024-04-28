
package com.tansci.service.impl.picture;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alipay.service.schema.util.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tansci.common.constant.StringConstant;
import com.tansci.common.enums.QueryTypeEnum;
import com.tansci.common.reponse.ServerResponse;
import com.tansci.domain.picture.dto.PictureAnnoDto;
import com.tansci.domain.picture.dto.PictureDocDto;
import com.tansci.service.picture.PictureParsingService;
import com.tansci.utils.ElasticSearchClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.jacoco.agent.rt.internal_035b120.core.internal.flow.IFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;


@Service
@Slf4j
public class PictureParsingServiceImpl implements PictureParsingService {
    @Autowired
    private ElasticSearchClient elasticSearchClient;
    String PICTURE_INDEX = "picture_index";
    String TEXTANNOTATIONS = "textAnnotations";

    @Override
    public ServerResponse search(Integer status, String textAnno, Integer queryType, Integer pageNo, Integer pageSize){
        List<PictureDocDto> pictureVoList = Lists.newArrayList();
        // 条件搜索
        SearchSourceBuilder builder = new SearchSourceBuilder();
        //组合搜索
        BoolQueryBuilder mainBoolQuery = new BoolQueryBuilder();
        mainBoolQuery.must(QueryBuilders.termQuery("status", status));
        if (!StringUtil.isEmpty(textAnno)) {
            //订单项相关信息搜索

            BoolQueryBuilder nestedBoolQuery = new BoolQueryBuilder();
            if (ObjectUtil.equal(queryType, QueryTypeEnum.QUERY_TYPE_TEXT.getCode())){
                nestedBoolQuery.must(QueryBuilders.matchQuery("pictureAnnos.textAnno", textAnno));
            }
            if (ObjectUtil.equal(queryType, QueryTypeEnum.QUERY_TYPE_OBJECT.getCode())){
                nestedBoolQuery.must(QueryBuilders.matchQuery("pictureAnnos.objectAnno", textAnno));
            }
            //内嵌对象搜索，需要指定path
            NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("pictureAnnos", nestedBoolQuery, ScoreMode.None);
            //子表查询
            mainBoolQuery.must(nestedQueryBuilder);
            //指定高亮
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("pictureAnnos.textAnno", -1)
                    .preTags("<span style=\"color: #ff5252;\">")
                    .postTags("</span>");
            builder.highlighter(highlightBuilder);
        }
        //封装查询参数
        builder.query(mainBoolQuery);

        //返回参数
        builder.fetchSource(new String[]{}, new String[]{});

        //结果集合分页，从第一页开始，返回最多四条数据
        builder.from((pageNo - 1) * pageSize).size(pageSize);

        //排序
        builder.sort("id", SortOrder.ASC);
        // 执行请求
        SearchResponse response = elasticSearchClient.searchDocument(PICTURE_INDEX, builder);

        SearchHits searchHits = response.getHits();
        long total = searchHits.getTotalHits().value;
        log.info("一共:[{}]条数据！",total);
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit searchHit : hits) {
            String sourceAsString = searchHit.getSourceAsString();
            PictureDocDto pictureDoc1 = JSON.parseObject(sourceAsString, PictureDocDto.class);
            //获取高亮文本
            Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
            HighlightField highlightField = highlightFields.get("pictureAnnos.textAnno");
            Text[] fragments = null;
            if (ObjectUtil.isNotEmpty(highlightField)){
                fragments = highlightField.fragments();
            }

                        //根据分隔符拆分数组
            if (CollUtil.isNotEmpty(pictureDoc1.getPictureAnnos())&&ObjectUtil.equal(queryType,QueryTypeEnum.QUERY_TYPE_TEXT.getCode())){
                List<PictureAnnoDto> pictureAnnoList = getPictureAnnos(textAnno, pictureDoc1,fragments);
                pictureDoc1.setPictureAnnos(pictureAnnoList);
            }
            pictureVoList.add(pictureDoc1);
        }
        return ServerResponse.success().setPage(pictureVoList, pageNo, pageSize, total);
    }

    /**
     * 行对齐处理text内容
     * @param textAnno 检索内容
     * @param pictureDoc1 检索结果
     * @return
     */
    private static List<PictureAnnoDto> getPictureAnnos(String textAnno, PictureDocDto pictureDoc1,Text[] fragments) {
        List<PictureAnnoDto> pictureAnnoList = pictureDoc1.getPictureAnnos();
        pictureAnnoList.forEach(pictureAnno -> {
            if (ObjectUtil.isNotEmpty(fragments)){
                for (Text fragment : fragments) {
                    String[] array = fragment.toString().split(StringConstant.NEWLINE);
                    //找到检索内容所在的句子以及内容
                    String stringBuilder = "";
                    if (pictureAnno.getTextAnno().toLowerCase().contains(textAnno.toLowerCase())){
                         stringBuilder = getStringBuilder(textAnno.toLowerCase(), array);
                    }
                    pictureAnno.setTextAnno(stringBuilder);
                }
            }else {
                if(ObjectUtil.isNotEmpty(pictureAnno.getTextAnno())){
                    if (ObjectUtil.isEmpty(textAnno)){
                        return;
                    }
                    String[] array = pictureAnno.getTextAnno().split(StringConstant.NEWLINE);
                    //找到检索内容所在的句子以及内容
                    String stringBuilder = getStringBuilder(textAnno, array);
                    pictureAnno.setTextAnno(stringBuilder);
                }
            }

        });
        return pictureAnnoList;
    }
    /**
     * 找到检索内容所在的句子以及内容并拼接
     * @param textAnno
     * @param array
     * @return
     */
    private static String getStringBuilder(String textAnno, String[] array) {
        String previousSentence ="";
        String currentSentence = "";
        String nextSentence = "";
        for (int i = 0; i < array.length; i++) {
            if (array[i].toLowerCase().contains(textAnno.toLowerCase())) {
                previousSentence = (i > 0) ? array[i - 1] : "";
                log.info("上一句:[{}]",previousSentence);
                currentSentence = array[i];
                log.info("本一句:[{}]",currentSentence);
                nextSentence = (i < array.length - 1) ? array[i + 1] : "";
                log.info("下一句:[{}]！",nextSentence);
                // 找到第一个匹配后结束循环
                break;
            }
        }
        StringJoiner stringJoiner = new StringJoiner(" ");
        stringJoiner.add(previousSentence);
        stringJoiner.add(currentSentence);
        stringJoiner.add(nextSentence);
        //拼接输出语句
        return stringJoiner.toString();
    }

    @Override
    public ServerResponse searchById(String id){
        GetResponse getResponse = elasticSearchClient.getDocumentById(PICTURE_INDEX, id);
        String sourceAsString = getResponse.getSourceAsString();
        PictureDocDto pictureDoc1 = JSON.parseObject(sourceAsString, PictureDocDto.class);
        pictureDoc1.setId(Integer.parseInt(getResponse.getId()));
        return ServerResponse.success().setData(pictureDoc1);
    }

    @Override
    public ServerResponse searchByTable(String table, String id) {
        return null;
    }

    @Override
    public void updateTextAnno(String id, List<PictureAnnoDto> pictureAnnos){
        Map<String, String> textMap = Maps.newHashMap();
        pictureAnnos.forEach(e -> textMap.put(e.getId(), e.getTextAnno()));


        GetResponse getResponse = elasticSearchClient.getDocumentById(PICTURE_INDEX, id);
        String sourceAsString = getResponse.getSourceAsString();
        PictureDocDto pictureDoc = JSON.parseObject(sourceAsString, PictureDocDto.class);

        pictureDoc.setStatus(1);

        pictureDoc.getPictureAnnos().forEach(p -> p.setTextAnno(textMap.get(p.getId())));

        elasticSearchClient.updateDocument(PICTURE_INDEX, id, pictureDoc);


    }

    @Override
    public void audit(String id) throws IOException {

        Map<String, Object> objectMap = Maps.newHashMap();
        objectMap.put("status", 1);
        elasticSearchClient.updateDocument(PICTURE_INDEX, id, objectMap);

//        //        1、准备Request
//        UpdateRequest request = new UpdateRequest(PICTURE_INDEX, id);
////        2、准备请求参数
//        request.doc(
//                "status", 1
//        );
////        3、发送请求
//        restHighLevelClient.update(request, RequestOptions.DEFAULT);
    }
}
//
//    @Autowired
//    private RestHighLevelClient restHighLevelClient;
//
////    @Override
////    public ServerResponse search(Picture picture) {
////        List<Picture> pictures = pictureParsingMapper.selectAll();
////        return ServerResponse.success(pictures);
////    }
//
//

//    String FACEANNOTATIONS = "faceAnnotations";
//    String LABELANNOTATIONS = "labelAnnotations";
//
//    @Override
//    public ServerResponse search(PictureDoc pictureDoc, Integer pageNo, Integer pageSize) throws IOException {
//        List<PictureVo> pictureVoList = Lists.newArrayList();
//
//        SearchRequest request = new SearchRequest(PICTURE_INDEX);
//        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
//        boolQuery.must(QueryBuilders.termQuery("status", pictureDoc.getStatus()));
////        boolQuery.filter()
//        request.source().query(boolQuery);
////        分页
//        request.source().from((pageNo - 1) * pageSize).size(pageSize);
//        SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
//        SearchHits searchHits = response.getHits();
//        long total = searchHits.getTotalHits().value;
//        System.out.println("一共" + total + "条数据！");
//        SearchHit[] hits = searchHits.getHits();
//        for (SearchHit searchHit : hits) {
//            String sourceAsString = searchHit.getSourceAsString();
//            PictureVo pictureVo = JSON.parseObject(sourceAsString, PictureVo.class);
//            pictureVo.setId(searchHit.getId());
////            pictureVo.setBas64(Base64Util.convertImageToBase64(Base64Util.PICTURE_PARENT_PATH + pictureVo.getPicture()));
//            pictureVoList.add(pictureVo);
//        }
//        return ServerResponse.success().setPage(pictureVoList, pageNo, pageSize, total);
//    }
//
//    @Override
//    public ServerResponse searchById(String id) throws IOException {
//
//        SearchRequest request = new SearchRequest(PICANNOTATIONS_INDEX);
//        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
//        boolQuery.must(QueryBuilders.termQuery("pid", id));
////        boolQuery.filter()
//        request.source().query(boolQuery).size(100);
////        分页
////        request.source().from((pageNo-1)*pageSize).size(pageSize);
//        SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
//        SearchHits searchHits = response.getHits();
//        long total = searchHits.getTotalHits().value;
//        System.out.println("一共" + total + "条数据！");
//        SearchHit[] hits = searchHits.getHits();
//        Map map = Maps.newHashMap();
//        List<PicAnnotationsDoc> textList = Lists.newArrayList();
//        List<PicAnnotationsDoc> faceList = Lists.newArrayList();
//        List<PicAnnotationsDoc> labelList = Lists.newArrayList();
//        List<PicAnnotationsDoc> objectList = Lists.newArrayList();
//        for (SearchHit searchHit : hits) {
//            String sourceAsString = searchHit.getSourceAsString();
//            PicAnnotationsDoc picAnnotationsDoc = JSON.parseObject(sourceAsString, PicAnnotationsDoc.class);
//            if (TEXTANNOTATIONS.equalsIgnoreCase(picAnnotationsDoc.getPtype())) {
//                textList.add(picAnnotationsDoc);
////            } else if (FACEANNOTATIONS.equalsIgnoreCase(picAnnotationsDoc.getPtype())) {
////                faceList.add(picAnnotationsDoc);
////            } else if (LABELANNOTATIONS.equalsIgnoreCase(picAnnotationsDoc.getPtype())) {
////                labelList.add(picAnnotationsDoc);
////            } else {
////                objectList.add(picAnnotationsDoc);
//            }
//        }
//
//
//////        1、准备Request对象
////        GetRequest request2 = new GetRequest(PICTURE_INDEX, id);
//////        2、发送请求得到响应
////        GetResponse response2 = restHighLevelClient.get(request2, RequestOptions.DEFAULT);
//////        3、解析响应结果
////        String sourceAsString = response2.getSourceAsString();
////        PictureVo pictureVo = JSON.parseObject(sourceAsString, PictureVo.class);
//        map.put("file", null);
//        map.put("text", textList);
//        map.put("face", faceList);
//        map.put("label", labelList);
//        map.put("object", objectList);
//        ServerResponse success = ServerResponse.success();
//        success.setMap(map);
//        return success;
//    }
//
//    @Override
//    public void updateTextAnno(String id, String textAnno) throws IOException {
//        //        1、准备Request PICANNOTATIONS_INDEX
//        UpdateByQueryRequest request = new UpdateByQueryRequest(PICANNOTATIONS_INDEX);
//// 设置查询条件
////        request.setQuery(QueryBuilders.termQuery("pid", id));
////        request.setQuery(QueryBuilders.termQuery("ptype", TEXTANNOTATIONS));
////        request.setQuery( QueryBuilders.matchPhraseQuery("pid", id));
////        request.setQuery( QueryBuilders.matchPhraseQuery("ptype", TEXTANNOTATIONS));
////        request.setQuery( QueryBuilders.matchPhraseQuery("ptype", "faceAnnotations"));
//
//// 设置更新脚本
////        request.setScript(new Script("ctx._source.textAnno = '测试收拾收拾';"));
////        request.setScript(new Script("ctx._source.objectAnno = '测试收拾收拾';"));
//
//        QueryBuilder upd_q1 = QueryBuilders.matchPhraseQuery("pid", id);//等量条件
//        QueryBuilder upd_q3 = QueryBuilders.matchPhraseQuery("ptype", TEXTANNOTATIONS);//等量条件
//
//        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery().must(upd_q1).must(upd_q3);
//        request.setQuery(queryBuilder);
//
//        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("textAnno", textAnno);
//        Script script = new Script(Script.DEFAULT_SCRIPT_TYPE, Script.DEFAULT_SCRIPT_LANG, "ctx._source.textAnno = params.textAnno", params);
//        request.setScript(script);
//
//
//        BulkByScrollResponse response = restHighLevelClient.updateByQuery(request, RequestOptions.DEFAULT);
//        System.out.println("受影响行数==>" + response.getStatus().getUpdated());
//
//    }
//
//
//
//    @Override
//    public void audit(String id) throws IOException {
//        //        1、准备Request
//        UpdateRequest request = new UpdateRequest(PICTURE_INDEX, id);
////        2、准备请求参数
//        request.doc(
//                "status", 1
//        );
////        3、发送请求
//        restHighLevelClient.update(request, RequestOptions.DEFAULT);
//    }
//
//
//}
