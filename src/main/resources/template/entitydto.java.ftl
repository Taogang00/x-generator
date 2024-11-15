<#--@formatter:off-->
package ${package.DTO};

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * ${entity} 实体DTO传输对象
 *
 * @author ${author}
 * @date ${date}
 */
<#if entityLombokModel>
@Data
</#if>
public class ${entity}DTO {

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
