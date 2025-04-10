package com.chensoul.ip.utils;

import com.chensoul.core.util.ResourceUtils;
import com.chensoul.ip.Area;
import com.chensoul.ip.enums.AreaTypeEnum;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 区域工具类
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 */
@Slf4j
public class AreaUtils {

	private static final Map<Integer, Area> areas;

	static {
		long now = System.currentTimeMillis();
		areas = new HashMap<>();
		areas.put(Area.ID_GLOBAL, new Area(Area.ID_GLOBAL, "全球", 0, null, new ArrayList<>()));
		// 从 csv 中加载数据
		List<String[]> all = new ArrayList<>();

		try {
			Files.readAllLines(Paths.get(ResourceUtils.getResource("area.csv").toURI())).forEach(line -> {
				String[] items = line.split(",");
				if (items.length < 4) {
					return;
				}
				if (items[0].equals("id")) {
					return;
				}
				all.add(items);
				Area area = new Area(Integer.valueOf(items[0]), items[1], Integer.valueOf(items[2]), null,
					new ArrayList<>());
				// 添加到 areas 中
				areas.put(area.getId(), area);
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		// 构建父子关系：因为 Area 中没有 parentId 字段，所以需要重复读取
		for (String[] row : all) {
			Area area = areas.get(Integer.valueOf(row[0])); // 自己
			Area parent = areas.get(Integer.valueOf(row[3])); // 父
			if (area == parent) {
				throw new RuntimeException(area.getName() + "父子节点相同");
			}
			area.setParent(parent);
			parent.getChildren().add(area);
		}
		log.info("启动加载 AreaUtils 成功，耗时 ({}) 毫秒", System.currentTimeMillis() - now);
	}

	/**
	 * 获得指定编号对应的区域
	 *
	 * @param id 区域编号
	 * @return 区域
	 */
	public static Area getArea(Integer id) {
		return areas.get(id);
	}

	/**
	 * 获得指定区域对应的编号
	 *
	 * @param pathStr 区域路径，例如说：河南省/石家庄市/新华区
	 * @return 区域
	 */
	public static Area parseArea(String pathStr) {
		String[] paths = pathStr.split("/");
		Area area = null;
		for (String path : paths) {
			if (area == null) {
				area = areas.values().stream().filter(item -> item.getName().equals(path)).findFirst().orElse(null);
			} else {
				area = area.getChildren().stream().filter(item -> item.getName().equals(path)).findFirst().orElse(null);
			}
		}
		return area;
	}

	/**
	 * 获取所有节点的全路径名称如：河南省/石家庄市/新华区
	 *
	 * @param areas 地区树
	 * @return 所有节点的全路径名称
	 */
	public static List<String> getAreaNodePathList(List<Area> areas) {
		List<String> paths = new ArrayList<>();
		areas.forEach(area -> getAreaNodePathList(area, "", paths));
		return paths;
	}

	/**
	 * 构建一棵树的所有节点的全路径名称，并将其存储为 "祖先/父级/子级" 的形式
	 *
	 * @param node  父节点
	 * @param path  全路径名称
	 * @param paths 全路径名称列表，省份/城市/地区
	 */
	private static void getAreaNodePathList(Area node, String path, List<String> paths) {
		if (node == null) {
			return;
		}
		// 构建当前节点的路径
		String currentPath = path.isEmpty() ? node.getName() : path + "/" + node.getName();
		paths.add(currentPath);
		// 递归遍历子节点
		for (Area child : node.getChildren()) {
			getAreaNodePathList(child, currentPath, paths);
		}
	}

	/**
	 * 格式化区域
	 *
	 * @param id 区域编号
	 * @return 格式化后的区域
	 */
	public static String format(Integer id) {
		return format(id, " ");
	}

	/**
	 * 格式化区域
	 * <p>
	 * 例如说： 1. id = “静安区”时：上海 上海市 静安区 2. id = “上海市”时：上海 上海市 3. id = “上海”时：上海 4. id =
	 * “美国”时：美国 当区域在中国时，默认不显示中国
	 *
	 * @param id        区域编号
	 * @param separator 分隔符
	 * @return 格式化后的区域
	 */
	public static String format(Integer id, String separator) {
		// 获得区域
		Area area = areas.get(id);
		if (area == null) {
			return null;
		}

		// 格式化
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < AreaTypeEnum.values().length; i++) { // 避免死循环
			sb.insert(0, area.getName());
			// “递归”父节点
			area = area.getParent();
			if (area == null || ObjectUtils.equals(area.getId(), Area.ID_GLOBAL)
				|| ObjectUtils.equals(area.getId(), Area.ID_CHINA)) { // 跳过父节点为中国的情况
				break;
			}
			sb.insert(0, separator);
		}
		return sb.toString();
	}

	/**
	 * 获取指定类型的区域列表
	 *
	 * @param type 区域类型
	 * @param func 转换函数
	 * @param <T>  结果类型
	 * @return 区域列表
	 */
	public static <T> List<T> getByType(AreaTypeEnum type, Function<Area, T> func) {
		return areas.values()
			.stream()
			.filter(area -> type.getCode() == area.getType())
			.map(func)
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}

	/**
	 * 根据区域编号、上级区域类型，获取上级区域编号
	 *
	 * @param id   区域编号
	 * @param type 区域类型
	 * @return 上级区域编号
	 */
	public static Integer getParentIdByType(Integer id, @NonNull AreaTypeEnum type) {
		for (int i = 0; i < Byte.MAX_VALUE; i++) {
			Area area = AreaUtils.getArea(id);
			if (area == null) {
				return null;
			}
			// 情况一：匹配到，返回它
			if (type.getCode() == area.getType()) {
				return area.getId();
			}
			// 情况二：找到根节点，返回空
			if (area.getParent() == null || area.getParent().getId() == null) {
				return null;
			}
			// 其它：继续向上查找
			id = area.getParent().getId();
		}
		return null;
	}

}
