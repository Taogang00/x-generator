<#--@formatter:off-->
package ${global.servicePackagePath};

import ${table.dtoPackagePath}.${table.dtoClassName};
import ${table.entityPackagePath}.${table.entityClassName};
import ${table.queryPackagePath}.${table.queryClassName};
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * ${table.tableComment} 服务接口
 *
 * @author ${global.author}
 * @date ${global.dateTime}
 */
public interface ${table.serviceClassName} extends IService<${table.entityClassName}> {

}
<#--@formatter:on-->
