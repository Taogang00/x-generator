<#--@formatter:off-->
package ${global.entityPackagePath};

import java.util.Date;
import java.math.BigDecimal;
import java.lang.*;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * ${table.entityClassName} 实体
 *
 * @author ${global.author}
 * @date ${global.dateTime}
 */
@Data
@TableName("${table.tableName}")
public class ${table.entityClassName} {

<#list table.tableFields as field>
    <#if field.primaryKey>
    <#if field.comment!?length gt 0>
    /**
     * ${field.comment}
     */
    </#if>
    @TableId(type = IdType.AUTO)
    private ${field.propertyType} ${field.propertyName};
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
