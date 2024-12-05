<#--@formatter:off-->
package ${global.controllerPackagePath};

import ${table.dtoPackagePath}.${table.dtoClassName};
import ${table.queryPackagePath}.${table.queryClassName};
import ${table.entityPackagePath}.${table.entityClassName};
import ${table.mapstructPackagePath}.${table.mapstructClassName};
import ${table.servicePackagePath}.${table.serviceClassName};
import lombok.RequiredArgsConstructor;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ${table.tableComment} 控制器
 *
 * @author ${global.author}
 * @date ${global.dateTime}
 */
@RestController
@RequestMapping("/v1/${table.controllerMapping}")
@RequiredArgsConstructor
public class ${table.controllerClassName} {

    private final ${table.serviceClassName} ${table.serviceClassName?uncap_first};

    private final ${table.mapstructClassName} ${table.mapstructClassName?uncap_first};

}
<#--@formatter:on-->
