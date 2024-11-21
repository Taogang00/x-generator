<#--@formatter:off-->
package ${global.serviceImplPackagePath};

import ${table.dtoPackagePath}.${table.dtoClassName};
import ${table.entityPackagePath}.${table.entityClassName};
import ${table.mapperPackagePath}.${table.mapperClassName};
import ${table.queryPackagePath}.${table.queryClassName};
import ${table.servicePackagePath}.${table.serviceClassName};
import ${global.superServiceImplClassPackagePath}.${global.superServiceImplClass};
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ${table.tableComment}  ${table.serviceClassName}实现类
 *
 * @author ${global.author}
 * @date ${global.dateTime}
 */
@Service
public class ${table.serviceImplClassName} extends ${global.superServiceImplClass}<${table.mapperClassName}, ${table.entityClassName}> implements ${table.serviceClassName} {

    @Override
    public List<${table.dtoClassName}> get${table.entityClassName}List(${table.queryClassName} query) {
        return baseMapper.get${table.entityClassName}List(query);
    }
}
<#--@formatter:on-->
