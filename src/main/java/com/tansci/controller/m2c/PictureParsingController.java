package com.tansci.controller.m2c;

import com.alibaba.fastjson.JSON;

import com.tansci.common.reponse.ServerResponse;
import com.tansci.domain.picture.dto.PictureAnnoDto;
import com.tansci.service.picture.PictureParsingService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/pic")
public class PictureParsingController {
    @Autowired
    private PictureParsingService pictureParsingService;


    /**
     * 分页搜索待审核接口
     * @param status 	审核状态，默认0未审核，1已审核
     * @param textAnno 	检索内容
     * @param pageNo 页数
     * @param pageSize 	每页大小
     * @param queryType 检索类型（1：text检索，2：object检索）
     */
    @GetMapping("/search")
    public ServerResponse search(@RequestParam(defaultValue = "0") Integer status, String textAnno, Integer queryType, Integer pageNo, Integer pageSize){
        //查询未审核的数据
        return pictureParsingService.search(status,textAnno,queryType, pageNo, pageSize);
    }

    /**
     * 根据ID查询详情数据
     */
    @GetMapping("/searchById")
    public ServerResponse searchById(String id){
        return pictureParsingService.searchById(id);
    }


    @PostMapping("/updateTextAnno")
    public ServerResponse updateTextAnno(HttpServletRequest request) {
        String pid = request.getParameter("pid");
        String pictureAnnostr = request.getParameter("pictureAnnos");
        List<PictureAnnoDto> pictureAnnos = JSON.parseArray(pictureAnnostr, PictureAnnoDto.class);
        pictureParsingService.updateTextAnno(pid, pictureAnnos);
        return ServerResponse.success();
    }

    /**
     * 审核通过接口
     */
    @PostMapping("/audit")
    public ServerResponse audit(String id) throws IOException {
        pictureParsingService.audit(id);
        return ServerResponse.success();
    }



    @GetMapping("/test")
    public ServerResponse test() throws IOException {
        return ServerResponse.success();
    }
}