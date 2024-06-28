package ${package};

<#list importPackages as pkg>
import ${pkg};
</#list>
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Po代码生成器
 * @date 2021/4/25 14:57
 */
@ApiModel(value="${entity}Po对象", description="${tableComment!}")
@Data
public class ${entity}Po extends ${entity} {

<#list fields as field>

    <#if field.comment!?length gt 0>
        <#if swagger2>
    @ApiModelProperty(value = "${field.comment}")
        <#else>
    /**
     * ${field.comment}
     */
        </#if>
    </#if>
    private ${field.propertyType} ${field.propertyName};


</#list>
}
