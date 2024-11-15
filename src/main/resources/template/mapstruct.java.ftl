<#--@formatter:off-->
package ${package.MapStruct};


import ${package.DTO}.${entity}DTO;
import ${package.Entity}.${entity};
import com.guanwei.mybatis.mapstruct.MybatisPageBaseConvertMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * ${entity}DTO 与 ${entity}之间通过mapStruct转化
 *
 * @author ${author}
 * @date ${date}
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ${mapper} extends MybatisPageBaseConvertMapper<${entity}DTO, ${entity}>{

}
<#--@formatter:on-->
