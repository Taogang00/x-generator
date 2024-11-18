<#--@formatter:off-->
package ${global.entityPackagePath};

import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* ${table.entityClassName} 实体
*
* @author ${global.author}
* @date ${global.dateTime}
*/
@Data
@TableName("${table.tableName}")
public class ${table.entityClassName}{

<#list table.tableFields as field>
    <#if field.comment!?length gt 0>
    /**
    * ${field.comment}
    */
    </#if>
    <#if field.primaryKey>
    @TableId(type = IdType.AUTO)
    </#if>
    private ${field.propertyType} ${field.propertyName};

</#list>
}
<#--@formatter:on-->
