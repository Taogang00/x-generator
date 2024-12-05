<#--@formatter:off-->
package ${table.mapstructPackagePath};

import ${table.dtoPackagePath}.${table.dtoClassName};
import ${table.entityPackagePath}.${table.entityClassName};
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

/**
 * ${table.dtoClassName} 与 ${table.entityClassName}之间通过mapStruct转化
 *
 * @author ${global.author}
 * @date ${global.dateTime}
 */
@Mapper(componentModel = SPRING, nullValuePropertyMappingStrategy = IGNORE)
public interface ${table.mapstructClassName}{

    ${table.dtoClassName} toTarget(${table.entityClassName} s);

    ${table.entityClassName} toSource(${table.dtoClassName} t);

    List<${table.dtoClassName}> toTargetList(List<${table.entityClassName}> list);

    List<${table.entityClassName}> toSourceList(List<${table.dtoClassName}> list);

    void updateToSource(${table.dtoClassName} t, @MappingTarget ${table.entityClassName} s);

    void updateToTarget(${table.entityClassName} s, @MappingTarget ${table.dtoClassName} t);

    void updateSource(${table.entityClassName} source, @MappingTarget ${table.entityClassName} target);

    void updateTarget(${table.entityClassName} source, @MappingTarget ${table.entityClassName} target);
}
<#--@formatter:on-->
