package ${global.queryPackagePath};

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.guanwei.mybatis.model.PageQuery;

import java.util.Date;

/**
 * ${table.tableComment} 查询对象
 *
 * @author ${global.author}
 * @date ${global.dateTime}
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ${table.queryClassName} extends PageQuery {

<#list table.tableFields as field>
<#if field.comment!?length gt 0>
    /**
     * ${field.comment}
     */
</#if>
    private ${field.propertyType} ${field.propertyName};

</#list>
}
