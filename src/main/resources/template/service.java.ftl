<#--@formatter:off-->
package ${package.Service};

import ${package.DTO}.${entity}DTO;
import ${package.Entity}.${entity};
import ${package.Query}.${entity}Query;
import ${superServiceClassPackage};

import java.util.List;

/**
 * ${table.serviceName} 服务接口
 *
 * @author ${author}
 * @date ${date}
 */
public interface ${table.serviceName} extends ${superServiceClass}<${entity}> {

    /**
     * 获取记录
     *
     * @param query 查询条件对象
     * @return {@link List}<{@link ${entity}}>
     */
    List<${entity}DTO> get${entity}List(${entity}Query query);

}
<#--@formatter:on-->
