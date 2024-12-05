<#--@formatter:off-->
package ${global.dtoPackagePath};

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

/**
 * ${table.tableComment} 实体DTO传输对象
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
    <#if field.propertyClass =="java.lang.String">
    @Length(max = ${field.dataLength}, message = "${field.comment}长度不能超过${field.dataLength}位")
    </#if>
    private ${field.propertyType} ${field.propertyName};

</#list>
}
<#--@formatter:on-->
