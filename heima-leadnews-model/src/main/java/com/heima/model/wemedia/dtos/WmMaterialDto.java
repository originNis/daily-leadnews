package com.heima.model.wemedia.dtos;

import com.heima.model.common.dtos.PageRequestDto;
import lombok.Data;

@Data
public class WmMaterialDto extends PageRequestDto {
    /**
     * 1 代表收藏
     * 0 代表未收藏
     */
    private Short isCollection;
}
