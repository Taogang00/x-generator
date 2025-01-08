package ${global.controllerPackagePath};

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import ${table.dtoPackagePath}.${table.dtoClassName};
import ${table.queryPackagePath}.${table.queryClassName};
import ${table.entityPackagePath}.${table.entityClassName};
import ${table.mapstructPackagePath}.${table.mapstructClassName};
import ${table.servicePackagePath}.${table.serviceClassName};
import com.guanwei.core.utils.result.R;
import lombok.RequiredArgsConstructor;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.guanwei.core.utils.EmptyUtil.isNotEmpty;

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

    /**
     * 获取一条记录
     *
     * @param id 数据标识
     * @return {@link R}<{@link ${table.dtoClassName}}> 通用返回对象
     */
    @GetMapping("/{id}")
    public R<${table.dtoClassName}> get(@PathVariable String id) {
        ${table.dtoClassName} dto = ${table.mapstructClassName?uncap_first}.toTarget(${table.serviceClassName?uncap_first}.getOne(id));
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
        LambdaQueryWrapper<${table.entityClassName}> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        <#list table.tableFields?sort_by("propertyType") as field>
        <#if field.propertyType == "String">
        lambdaQueryWrapper.like(isNotEmpty(query.get${field.propertyName?cap_first}()), ${table.entityClassName}::get${field.propertyName?cap_first}, query.get${field.propertyName?cap_first}());
        <#else>
        lambdaQueryWrapper.eq(isNotEmpty(query.get${field.propertyName?cap_first}()), ${table.entityClassName}::get${field.propertyName?cap_first}, query.get${field.propertyName?cap_first}());
        </#if>
        </#list>
        List<${table.entityClassName}> list = ${table.serviceClassName?uncap_first}.selectList(query, lambdaQueryWrapper);
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
        ${table.entityClassName} entity = ${table.mapstructClassName?uncap_first}.toSource(dto);
        boolean save = ${table.serviceClassName?uncap_first}.save(entity);
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
        ${table.entityClassName} entity = ${table.mapstructClassName?uncap_first}.toSource(dto);
        ${table.serviceClassName?uncap_first}.update(entity);
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
        return R.OK(${table.serviceClassName?uncap_first}.removeById(id));
    }
}
