package com.karlexyan.yoj.mapper;

import com.karlexyan.yoj.model.entity.Question;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
* @author KarlexYan
* @description 针对表【question(题目)】的数据库操作Mapper
* @createDate 2024-01-21 23:23:42
* @Entity com.karlexyan.yoj.model.entity.Question
*/
public interface QuestionMapper extends BaseMapper<Question> {

    @Select("SELECT * from question where isDelete = 0 order by RAND() limit 1")
    Question selectByRandom();

}




