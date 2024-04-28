package com.tansci.domain.picture.dto;

import com.tansci.domain.picture.dto.PictureAnnoDto;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PictureDocDto {
    private Integer id;
    /**
     * 图片地址
     */
    private String picture;
    /**
     * 图片状态： 默认未审核0，审核1
     */
    private Integer status=0;

    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改时间
     */
    private Date updateTime;
    /**
     * 图片注释子类
     */
    private List<PictureAnnoDto> pictureAnnos;
}
