package ${table.mapperPackagePath};

import ${table.dtoPackagePath}.${table.dtoClassName};
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

  /**
   * 获取记录
   *
   * @param query 查询条件对象
   * @return {@link List}<{@link ${table.entityClassName}}>
   */
   List<${table.dtoClassName}> get${table.entityClassName}List(${table.queryClassName} query);
}
