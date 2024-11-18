<#--@formatter:off-->
package ${global.controllerPackagePath};

import com.guanwei.core.utils.result.R;
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
 * ${table.controllerName} 控制器
 *
 * @author ${global.author}
 * @date ${global.dateTime}
 */
@RestController
@RequestMapping("/v1/${table.lowerEntityName}")
@RequiredArgsConstructor
public class ${table.controllerName} {

    private final ${table.serviceClassName} ${table.serviceClassName};

    private final ${table.mapstructClassName} ${table.mapstructClassName};

    /**
    * 获取一条记录
    *
    * @param id 数据标识
    * @return {@link R}<{@link ${table.dtoClassName}}> 通用返回对象
    */
    @GetMapping("/{id}")
    public R<${table.dtoClassName}> get(@PathVariable String id) {
        ${table.dtoClassName} dto = ${table.mapstructClassName}.toTarget(${table.serviceClassName}.getOne(id));
        return R.OK(dto);
    }

	/**
	 * 获取所有记录(分页)
     *
     * @param query 请求对象
     * @return {@link R}<{@link ?}> 通用返回对象
	 */
	@GetMapping("/list")
	public R<?> list(${table.queryClassName} query) {
        List<${table.dtoClassName}> list = ${table.serviceClassName}.get${table.entityClassName}List(query);
		return R.OK(list);
	}

    /**
     * 新增一条记录
     *
     * @param dto 表单数据
     * @return {@link R}<{@link Boolean}> 通用返回对象
     */
    @PostMapping
    public R<Boolean> add(@Validated @RequestBody ${table.dtoClassName} dto) {
        ${table.entityClassName} entity = ${table.mapstructClassName}.toSource(dto);
        boolean save = ${table.serviceClassName}.save(entity);
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
    public R<?> update(@PathVariable String id, @Validated @RequestBody ${table.dtoClassName} dto) {
        Assert.notNull(id, "主键标识不能为空！");
        ${table.entityClassName} entity = ${table.mapstructClassName}.toSource(dto);
        ${table.serviceClassName}.update(entity);
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
        return R.OK(${table.serviceClassName}.removeById(id));
    }
}
</#if>
