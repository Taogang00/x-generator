<#--@formatter:off-->
package ${package.Query};

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.guanwei.mybatis.model.PageQuery;

import java.math.BigDecimal;
import java.util.Date;

/**
 * ${entity} 查询对象
 *
 * @author ${author}
 * @date ${date}
 */
<#if entityLombokModel>
@EqualsAndHashCode(callSuper = true)
@Data
</#if>
public class ${entity}Query extends PageQuery {

<#list table.fields as field>
    <#if field.comment!?length gt 0>
        /**
        * ${field.comment}
        */
    </#if>
    private ${field.propertyType} ${field.propertyName};

</#list>
}
<#--@formatter:off-->
