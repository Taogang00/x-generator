package ${global.servicePackagePath};

import ${table.dtoPackagePath}.${table.dtoClassName};
import ${table.entityPackagePath}.${table.entityClassName};
import ${table.queryPackagePath}.${table.queryClassName};
import com.guanwei.mybatis.base.service.MBaseService;

import java.util.List;

/**
 * ${table.tableComment} 服务接口
 *
 * @author ${global.author}
 * @date ${global.dateTime}
 */
public interface ${table.serviceClassName} extends MBaseService<${table.entityClassName}> {

    /**
     * 获取记录
     *
     * @param query 查询条件对象
     * @return {@link List}<{@link ${table.entityClassName}}>
     */
    List<${table.dtoClassName}> get${table.entityClassName}List(${table.queryClassName} query);

}
