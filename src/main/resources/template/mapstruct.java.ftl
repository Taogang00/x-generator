package ${table.mapstructPackagePath};

import com.guanwei.mybatis.mapstruct.MybatisPageBaseConvertMapper;
import ${table.dtoPackagePath}.${table.dtoClassName};
import ${table.entityPackagePath}.${table.entityClassName};
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

/**
 * ${table.dtoClassName} 与 ${table.entityClassName}之间通过mapStruct转化
 *
 * @author ${global.author}
 * @date ${global.dateTime}
 */
@Mapper(componentModel = SPRING, nullValuePropertyMappingStrategy = IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ${table.mapstructClassName} extends MybatisPageBaseConvertMapper<${table.dtoClassName}, ${table.entityClassName}>{

}
