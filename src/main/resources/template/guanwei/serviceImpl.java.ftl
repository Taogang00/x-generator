package ${global.serviceImplPackagePath};

import ${table.entityPackagePath}.${table.entityClassName};
import ${table.mapperPackagePath}.${table.mapperClassName};
import ${table.servicePackagePath}.${table.serviceClassName};
import com.guanwei.mybatis.base.service.MBaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ${table.tableComment}  ${table.serviceClassName}实现类
 *
 * @author ${global.author}
 * @date ${global.dateTime}
 */
@Service
public class ${table.serviceImplClassName} extends MBaseServiceImpl<${table.mapperClassName}, ${table.entityClassName}> implements ${table.serviceClassName} {

}
