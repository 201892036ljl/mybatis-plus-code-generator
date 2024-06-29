package ${package.Mapper};

import ${package.Entity}.${entity};
<#if (cfg.poXmlData['${entity}Po:poXmlDataFlag'])!false>
import ${cfg.poPackage}.${entity}Po;
import senrui.mybatisplus.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
<#else>
@Mapper
import senrui.mybatisplus.CoreMapper;
</#if>
/**
 * <p>
 * ${table.comment!} Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
<#if kotlin>
interface ${table.mapperName} : ${superMapperClass}<${entity}>
<#else>
<#if (cfg.poXmlData['${entity}Po:poXmlDataFlag'])!false>
public interface ${table.mapperName} extends BaseMapper<${entity}, ${entity}Po> {
<#else>
public interface ${table.mapperName} extends CoreMapper<${entity}> {
</#if>
}
</#if>
