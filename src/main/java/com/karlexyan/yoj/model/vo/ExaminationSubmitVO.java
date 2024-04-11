package com.karlexyan.yoj.model.vo;

import com.karlexyan.yoj.model.entity.ExaminationSubmit;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;

@Data
public class ExaminationSubmitVO {
    /**
     * id
     */
    private Long id;

    /**
     * 套题ID
     */
    private Long examinationId;


    /**
     * 创建套题用户 id
     */
    private Long userId;


    /**
     * 编程语言
     */
    private String submitLanguage;

    /**
     * 判题状态（0 - 待判题、1 - 判题中、2 - 成功、3 - 失败）
     */
    private Integer submitState;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 用户封装类
     */
    private UserVO userVO;


    /**
     * 包装类转对象
     *
     * @param examinationSubmitVo
     * @return
     */
    public static ExaminationSubmit voToObj(ExaminationSubmitVO examinationSubmitVo) {
        if (examinationSubmitVo == null) {
            return null;
        }
        ExaminationSubmit examinationSubmit = new ExaminationSubmit();
        BeanUtils.copyProperties(examinationSubmitVo, examinationSubmit);
        return examinationSubmit;
    }

    /**
     * 对象转包装类
     *
     * @param examinationSubmit
     * @return
     */
    public static ExaminationSubmitVO objToVo(ExaminationSubmit examinationSubmit) {
        if (examinationSubmit == null) {
            return null;
        }
        ExaminationSubmitVO examinationSubmitVo = new ExaminationSubmitVO();
        BeanUtils.copyProperties(examinationSubmit, examinationSubmitVo);
        // 转换成包装类
        return examinationSubmitVo;
    }

    private static final long serialVersionUID = 1L;
}
