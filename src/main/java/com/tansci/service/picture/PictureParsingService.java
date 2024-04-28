package com.tansci.service.picture;

import com.tansci.common.reponse.ServerResponse;
import com.tansci.domain.picture.dto.PictureAnnoDto;

import java.io.IOException;
import java.util.List;

public interface PictureParsingService {



    ServerResponse search(Integer status, String textAnno, Integer queryType, Integer pageNo, Integer pageSize);

    ServerResponse searchById(String id);

    /**
     * 根据字段对像进行查询
     * @return ServerResponse
     */
    ServerResponse searchByTable(String table,String id);


    void updateTextAnno(String id, List<PictureAnnoDto> pictureAnnos);


    void audit(String id) throws IOException;
}
