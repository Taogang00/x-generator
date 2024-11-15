<#--@formatter:off-->
package ${package.ServiceImpl};

import ${package.DTO}.${entity}DTO;
import ${package.Entity}.${entity};
import ${package.Mapper}.${table.mapperName};
import ${package.Query}.${entity}Query;
import ${package.Service}.${table.serviceName};
import ${superServiceImplClassPackage};
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ${table.serviceName}  服务实现类
 *
 * @author ${author}
 * @date ${date}
 */
@Service
public class ${table.serviceImplName} extends ${superServiceImplClass}<${table.mapperName}, ${entity}> implements ${table.serviceName} {

    @Override
    public List<${entity}DTO> get${entity}List(${entity}Query query) {
        return baseMapper.get${entity}List(query);
    }
}
<#--@formatter:on-->
