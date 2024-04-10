package com.karlexyan.yoj.model.dto.examinationsubmit;

import com.karlexyan.yoj.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExaminationSubmitQueryRequest extends PageRequest implements Serializable {


    /**
     * 套题 id
     */
    private Long examinationId;

    /**
     * 套题标题
     */
    private String title;

    /**
     * 编程语言
     */
    private String submitLanguage;


    /**
     * 判题状态（0 - 待判题、1 - 判题中、2 - 成功、3 - 失败）
     */
    private Integer submitState;

    /**
     * 用户 id
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}
