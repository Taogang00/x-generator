<#--@formatter:off-->
package ${global.entityPackagePath};

import java.util.Date;
import java.lang.*;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * ${table.tableComment}
 *
 * @author ${global.author}
 * @date ${global.dateTime}
 */
@Data
@TableName("${table.tableName}")
public class ${table.entityClassName} {

<#assign hasProcessed = false />
<#list table.tableFields as field>
    <#if !hasProcessed>
    <#if field.primaryKey>
    <#if field.comment!?length gt 0>
    /**
     * ${field.comment}
     */
    </#if>
    @TableId(type = IdType.ASSIGN_ID)
    private ${field.propertyType} ${field.propertyName};
    <#assign hasProcessed = true />
    </#if>
    </#if>
</#list>

<#list table.tableFields as field>
    <#if !field.primaryKey>
    <#if field.comment!?length gt 0>
    /**
     * ${field.comment}
     */
    </#if>
    private ${field.propertyType} ${field.propertyName};

    </#if>
</#list>
}
<#--@formatter:on-->