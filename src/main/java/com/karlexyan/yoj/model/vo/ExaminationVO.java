package com.karlexyan.yoj.model.vo;

import cn.hutool.json.JSONUtil;
import com.karlexyan.yoj.model.entity.Examination;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;

@Data
public class ExaminationVO {

    /**
     * id
     */
    private Long id;

    /**
     * 创建套题用户 id
     */
    private Long userid;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表（json 数组）
     */
    private List<String> tags;

    /**
     * 套题提交数
     */
    private Integer submitNum;

    /**
     * 套题通过数
     */
    private Integer acceptedNum;

    /**
     * 点赞数
     */
    private Integer thumbNum;

    /**
     * 收藏数
     */
    private Integer favourNum;

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
     * @param examinationVo
     * @return
     */
    public static Examination voToObj(ExaminationVO examinationVo) {
        if (examinationVo == null) {
            return null;
        }
        Examination examination = new Examination();
        BeanUtils.copyProperties(examinationVo, examination);
        List<String> tagList = examinationVo.getTags();
        if (tagList != null) {
            examination.setTags(JSONUtil.toJsonStr(tagList));
        }
        return examination;
    }

    /**
     * 对象转包装类
     *
     * @param examination
     * @return
     */
    public static ExaminationVO objToVo(Examination examination) {
        if (examination == null) {
            return null;
        }
        ExaminationVO examinationVo = new ExaminationVO();
        BeanUtils.copyProperties(examination, examinationVo);
        // 转换成包装类
        List<String> tagList = JSONUtil.toList(examination.getTags(), String.class);
        examinationVo.setTags(tagList);
        return examinationVo;
    }

    private static final long serialVersionUID = 1L;
}
