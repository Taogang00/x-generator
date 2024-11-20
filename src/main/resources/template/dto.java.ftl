<#--@formatter:off-->
package ${global.dtoPackagePath};

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

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
    <#if field.nullOption?has_content && field.nullOption == true>
    @NotNull(message = "${field.comment}不能为空！")
    </#if>
    @Length(max = ${field.dataLength}, message = "${field.comment}长度不能超过${field.dataLength}位")
    private ${field.propertyType} ${field.propertyName};

</#list>
}
<#--@formatter:off-->
