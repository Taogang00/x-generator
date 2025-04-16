package ${table.mapperPackagePath};

import ${table.entityPackagePath}.${table.entityClassName};
import com.guanwei.mybatis.base.mapper.MBaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * ${table.tableComment} Mapper 接口
 *
 * @author ${global.author}
 * @date ${global.dateTime}
 */
@Mapper
public interface ${table.mapperClassName} extends MBaseMapper<${table.entityClassName}> {

}
