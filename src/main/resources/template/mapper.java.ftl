<#--@formatter:off-->
package ${package.Mapper};

import ${package.DTO}.${entity}DTO;
import ${package.Entity}.${entity};
import ${package.Query}.${entity}Query;
import ${superMapperClassPackage};
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * ${table.mapperName} Mapper 接口
 * @author ${author}
 * @date ${date}
 */
@Mapper
public interface ${table.mapperName} extends ${superMapperClass}<${entity}> {

  /**
   * 获取记录
   *
   * @param query 查询条件对象
   * @return {@link List}<{@link ${entity}}>
   */
   List<${entity}DTO> get${entity}List(${entity}Query query);
}
<#--@formatter:off-->
