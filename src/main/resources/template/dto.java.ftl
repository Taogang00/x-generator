<#--@formatter:off-->
package ${global.dtoPackagePath};

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * ${table.entityClassName} 实体DTO传输对象
 *
 * @author ${global.author}
 * @date ${global.dateTime}
 */
@Data
public class ${table.dtoClassName} {

<#list table.tableFields as field>
    <#if field.comment!?length gt 0>
    /**
    * ${field.comment}
    */
    </#if>
    private ${field.propertyType} ${field.propertyName};

</#list>
}
<#--@formatter:off-->
