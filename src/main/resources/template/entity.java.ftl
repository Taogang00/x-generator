package ${global.entityPackagePath};

import java.util.Date;
import java.lang.*;
import com.baomidou.mybatisplus.annotation.*;
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
    <#if field.primaryKey>
    <#if field.comment!?length gt 0>
    /**
    * ${field.comment}
    */
    </#if>
    <#if !hasProcessed>
    @TableId(type = IdType.ASSIGN_ID)
    <#assign hasProcessed = true />
    </#if>
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
    <#if field.propertyName == "creator">
    @TableField(updateStrategy = FieldStrategy.NEVER)
    </#if>
    <#if field.propertyName == "createTime">
    @TableField(updateStrategy = FieldStrategy.NEVER)
    </#if>
    private ${field.propertyType} ${field.propertyName};

    </#if>
</#list>
}
