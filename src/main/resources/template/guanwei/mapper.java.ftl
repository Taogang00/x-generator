package ${table.mapperPackagePath};

import ${table.entityPackagePath}.${table.entityClassName};
import ${table.queryPackagePath}.${table.queryClassName};
import com.guanwei.mybatis.base.mapper.MBaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * ${table.tableComment} Mapper 接口
 *
 * @author ${global.author}
 * @date ${global.dateTime}
 */
@Mapper
public interface ${table.mapperClassName} extends MBaseMapper<${table.entityClassName}> {

}
