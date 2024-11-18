<#--@formatter:off-->

import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ${entity} 实体
 *
 * @author ${author}
 * @date ${date}
 */
<#if entityLombokModel>
@Data
<#if superEntityClass??>
@EqualsAndHashCode(callSuper = false)
</#if>
</#if>
<#if table.convert>
@TableName("${table.name}")
</#if>
<#if superEntityClass??>
public class ${entity} extends ${superEntityClass}<#if activeRecord><${entity}></#if> {
<#elseif activeRecord>
public class ${entity} extends Model<${entity}> {
<#else>
public class ${entity}{
</#if>

<#-- ----------  BEGIN 字段循环遍历  ---------->
<#list table.fields as field>
<#if field.comment!?length gt 0>
    /**
    * ${field.comment}
    */
</#if>
<#-- 主键 -->
<#if field.keyFlag>
<#if field.keyIdentityFlag>
    @TableId(type = IdType.AUTO)
<#else>
    @TableId(type = IdType.ASSIGN_ID)
</#if>
</#if>
<#-- 乐观锁注解 -->
<#if (versionFieldName!"") == field.name>
    @Version
</#if>
<#-- 逻辑删除注解 -->
<#if (logicDeleteFieldName!"") == field.name>
    @TableLogic
</#if>
    private ${field.propertyType} ${field.propertyName};

</#list>
<#------------  END 字段循环遍历  ---------->
}
<#--@formatter:on-->
