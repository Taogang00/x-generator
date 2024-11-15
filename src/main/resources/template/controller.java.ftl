<#--@formatter:off-->
package ${package.Controller};

import com.guanwei.core.utils.result.R;
<#if superControllerClassPackage??>
import ${superControllerClassPackage};
</#if>
import ${package.DTO}.${entity}DTO;
import ${package.Query}.${entity}Query;
import ${package.Entity}.${entity};
import ${package.MapStruct}.${table.mapstructName};
import ${package.Service}.${table.serviceName};
import lombok.RequiredArgsConstructor;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ${table.controllerName} 控制器
 *
 * @author ${author}
 * @date ${date}
 */
<#if restControllerStyle>
@RestController
<#else>
@Controller
</#if>
@RequestMapping("/v1/${table.lowerEntityName}<#if controllerMappingHyphenStyle??>/${controllerMappingHyphen}</#if>")
<#if kotlin>
class ${table.controllerName}<#if superControllerClass??> : ${superControllerClass}()</#if>
<#else>
@RequiredArgsConstructor
<#if superControllerClass??>
public class ${table.controllerName} extends ${superControllerClass} {
<#else>
public class ${table.controllerName} {
</#if>

    private final ${table.serviceName} ${table.lowerServiceName};

    private final ${table.mapstructName} ${table.lowerMapstructName};

    /**
    * 获取一条记录
    *
    * @param id 数据标识
    * @return {@link R}<{@link ${entity}DTO}> 通用返回对象
    */
    @GetMapping("/{id}")
    public R<${entity}DTO> get(@PathVariable String id) {
        ${entity}DTO dto = ${table.lowerMapstructName}.toTarget(${table.lowerServiceName}.getOne(id));
        return R.OK(dto);
    }

	/**
	 * 获取所有记录(分页)
     *
     * @param query 请求对象
     * @return {@link R}<{@link ?}> 通用返回对象
	 */
	@GetMapping("/list")
	public R<?> list(${entity}Query query) {
        List<${entity}DTO> list = ${table.lowerServiceName}.get${entity}List(query);
		return R.OK(list);
	}

    /**
     * 新增一条记录
     *
     * @param dto 表单数据
     * @return {@link R}<{@link Boolean}> 通用返回对象
     */
    @PostMapping
    public R<Boolean> add(@Validated @RequestBody ${entity}DTO dto) {
        ${entity} entity = ${table.lowerMapstructName}.toSource(dto);
        boolean save = ${table.lowerServiceName}.save(entity);
        return R.OK(save);
    }

    /**
     * 更新一条记录
     *
     * @param id  数据标识
     * @param dto 表单数据
     * @return {@link R}<{@link ?}> 通用返回对象
     */
    @PostMapping("/edit/{id}")
    public R<?> update(@PathVariable String id, @Validated @RequestBody ${entity}DTO dto) {
        Assert.notNull(id, "主键标识不能为空！");
        ${entity} entity = ${table.lowerMapstructName}.toSource(dto);
        ${table.lowerServiceName}.update(entity);
        return R.OK();
    }

    /**
     * 删除一条记录
     *
     * @param id 数据标识
     * @return {@link R}<{@link Boolean}> 通用返回对象
     */
    @PostMapping("/delete/{id}")
    public R<Boolean> delete(@PathVariable String id) {
        return R.OK(${table.lowerServiceName}.removeById(id));
    }
}
</#if>
