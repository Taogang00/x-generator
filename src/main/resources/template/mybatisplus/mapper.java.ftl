<#--@formatter:off-->
package ${table.mapperPackagePath};

import ${table.dtoPackagePath}.${table.dtoClassName};
import ${table.entityPackagePath}.${table.entityClassName};
import ${table.queryPackagePath}.${table.queryClassName};
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * ${table.tableComment} Mapper 接口
 *
 * @author ${global.author}
 * @date ${global.dateTime}
 */
@Mapper
public interface ${table.mapperClassName} extends BaseMapper<${table.entityClassName}> {

}
<#--@formatter:on-->