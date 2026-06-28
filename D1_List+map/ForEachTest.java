package D1_List;

import java.util.*;

/**
 * 集合框架常见集合结构及遍历方式演示
 * 包含：
 *   List 系列：ArrayList、LinkedList
 *   Set 系列：HashSet、LinkedHashSet、TreeSet
 *   Map 系列：HashMap、LinkedHashMap、TreeMap
 * 遍历方式：
 *   1. 普通 for 循环（List 有索引）
 *   2. 增强 for 循环（所有 Collection）
 *   3. 迭代器 Iterator（所有 Collection，支持安全删除）
 *   4. Lambda forEach（JDK 8+）
 *   5. Stream 流（JDK 8+，支持链式操作）
 *   6. Map 专用遍历（keySet / values / entrySet / Lambda）
 */
public class ForEachTest {

    public static void main(String[] args) {

        // ============================================================
        // 一、List 集合 —— 有序、可重复、有索引
        // ============================================================
        System.out.println("==================== List 集合 ====================");
        testArrayList();
        testLinkedList();

        // ============================================================
        // 二、Set 集合 —— 无序/有序、不可重复、无索引
        // ============================================================
        System.out.println("\n==================== Set 集合 ====================");
        testHashSet();
        testLinkedHashSet();
        testTreeSet();

        // ============================================================
        // 三、Map 集合 —— 键值对（key-value）
        // ============================================================
        System.out.println("\n==================== Map 集合 ====================");
        testHashMap();
        testLinkedHashMap();
        testTreeMap();
    }

    // ==================== ArrayList ====================
    /**
     * ArrayList：底层基于数组，查询快，增删慢
     * 默认初始容量 10，扩容为原来的 1.5 倍
     */
    static void testArrayList() {
        System.out.println("---------- ArrayList ----------");

        // 创建集合并添加元素
        List<String> list = new ArrayList<>();
        list.add("Java");
        list.add("Python");
        list.add("C++");
        list.add("Java");   // 允许重复
        list.add("Go");
        System.out.println("原始集合: " + list);
        System.out.println("元素个数: " + list.size());

        // 1. 普通 for 循环（按索引遍历，ArrayList 随机访问 O(1)）
        System.out.print("\n1. 普通 for 循环遍历: ");
        for (int i = 0; i < list.size(); i++) {
            System.out.print("[" + i + "]" + list.get(i) + " ");
        }

        // 2. 增强 for 循环（foreach）
        System.out.print("\n2. 增强 for 循环遍历: ");
        for (String s : list) {
            System.out.print(s + " ");
        }

        // 3. 迭代器 Iterator（支持安全删除）
        System.out.print("\n3. 迭代器遍历: ");
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            System.out.print(it.next() + " ");
        }

        // 3.1 迭代器安全删除
        Iterator<String> itDel = list.iterator();
        while (itDel.hasNext()) {
            if ("Java".equals(itDel.next())) {
                itDel.remove();  // 安全删除
            }
        }
        System.out.println("\n   迭代器删除\"Java\"后: " + list);
        // 恢复元素
        list.add(0, "Java");

        // 4. Lambda forEach
        System.out.print("4. Lambda forEach 遍历: ");
        list.forEach(s -> System.out.print(s + " "));

        // 5. Stream 流遍历
        System.out.print("\n5. Stream 流遍历: ");
        list.stream().forEach(s -> System.out.print(s + " "));

        // Stream 过滤 + 遍历
        System.out.print("\n   Stream 过滤（长度>2）: ");
        list.stream()
                .filter(s -> s.length() > 2)
                .forEach(s -> System.out.print(s + " "));

        // Stream 映射 + 遍历
        System.out.print("\n   Stream 映射（转大写）: ");
        list.stream()
                .map(String::toUpperCase)
                .forEach(s -> System.out.print(s + " "));

        System.out.println();
    }

    // ==================== LinkedList ====================
    /**
     * LinkedList：底层基于双向链表，查询慢，增删快
     * 实现了 List 和 Deque 接口，可作队列/栈使用
     */
    static void testLinkedList() {
        System.out.println("\n---------- LinkedList ----------");

        // 创建集合并添加元素
        List<String> list = new LinkedList<>();
        list.add("张三");
        list.add("李四");
        list.add("王五");
        list.add("张三");   // 允许重复
        list.add("赵六");
        System.out.println("原始集合: " + list);

        // 1. 普通 for 循环（LinkedList 随机访问 O(n)，效率低）
        System.out.print("1. 普通 for 循环遍历: ");
        for (int i = 0; i < list.size(); i++) {
            System.out.print(list.get(i) + " ");
        }

        // 2. 增强 for 循环
        System.out.print("\n2. 增强 for 循环遍历: ");
        for (String s : list) {
            System.out.print(s + " ");
        }

        // 3. 迭代器 Iterator
        System.out.print("\n3. 迭代器遍历: ");
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            System.out.print(it.next() + " ");
        }

        // 3.1 迭代器安全删除
        Iterator<String> itDel = list.iterator();
        while (itDel.hasNext()) {
            if ("李四".equals(itDel.next())) {
                itDel.remove();
            }
        }
        System.out.println("\n   迭代器删除\"李四\"后: " + list);
        list.add(1, "李四");  // 恢复

        // 4. Lambda forEach
        System.out.print("4. Lambda forEach 遍历: ");
        list.forEach(s -> System.out.print(s + " "));

        // 5. Stream 流遍历（排序 + 遍历）
        System.out.print("\n5. Stream 排序后遍历: ");
        list.stream()
                .sorted()
                .forEach(s -> System.out.print(s + " "));

        System.out.println();
    }

    // ==================== HashSet ====================
    /**
     * HashSet：底层基于 HashMap（哈希表），存取无序，不可重复
     * 默认初始容量 16，加载因子 0.75
     */
    static void testHashSet() {
        System.out.println("---------- HashSet（无序，不可重复）----------");

        // 创建集合并添加元素
        Set<String> set = new HashSet<>();
        set.add("苹果");
        set.add("香蕉");
        set.add("橘子");
        set.add("苹果");   // 重复元素，不会存入
        set.add("葡萄");
        set.add("西瓜");
        System.out.println("原始集合: " + set);
        System.out.println("元素个数: " + set.size() + "（注意：重复的\"苹果\"只存了一次）");

        // Set 无索引，不能使用普通 for 循环

        // 1. 增强 for 循环
        System.out.print("1. 增强 for 循环遍历: ");
        for (String s : set) {
            System.out.print(s + " ");
        }

        // 2. 迭代器 Iterator
        System.out.print("\n2. 迭代器遍历: ");
        Iterator<String> it = set.iterator();
        while (it.hasNext()) {
            System.out.print(it.next() + " ");
        }

        // 2.1 迭代器安全删除
        Iterator<String> itDel = set.iterator();
        while (itDel.hasNext()) {
            if ("香蕉".equals(itDel.next())) {
                itDel.remove();
            }
        }
        System.out.println("\n   迭代器删除\"香蕉\"后: " + set);
        set.add("香蕉");  // 恢复

        // 3. Lambda forEach
        System.out.print("3. Lambda forEach 遍历: ");
        set.forEach(s -> System.out.print(s + " "));

        // 4. Stream 流遍历
        System.out.print("\n4. Stream 流遍历: ");
        set.stream().forEach(s -> System.out.print(s + " "));

        // Stream 过滤
        System.out.print("\n   Stream 过滤（不是\"西瓜\"）: ");
        set.stream()
                .filter(s -> !"西瓜".equals(s))
                .forEach(s -> System.out.print(s + " "));

        System.out.println();
    }

    // ==================== LinkedHashSet ====================
    /**
     * LinkedHashSet：继承 HashSet，通过双向链表维护插入顺序
     * 存取有序，不可重复
     */
    static void testLinkedHashSet() {
        System.out.println("\n---------- LinkedHashSet（有序，不可重复）----------");

        // 创建集合并添加元素
        Set<Integer> set = new LinkedHashSet<>();
        set.add(30);
        set.add(10);
        set.add(50);
        set.add(20);
        set.add(10);   // 重复元素，不会存入
        set.add(40);
        System.out.println("原始集合: " + set);
        System.out.println("（注意：输出顺序与插入顺序一致，且重复的 10 只存了一次）");

        // 1. 增强 for 循环
        System.out.print("1. 增强 for 循环遍历: ");
        for (Integer num : set) {
            System.out.print(num + " ");
        }

        // 2. 迭代器 Iterator
        System.out.print("\n2. 迭代器遍历: ");
        Iterator<Integer> it = set.iterator();
        while (it.hasNext()) {
            System.out.print(it.next() + " ");
        }

        // 3. Lambda forEach（方法引用）
        System.out.print("\n3. Lambda forEach（方法引用）: ");
        set.forEach(System.out::print);
        System.out.print(" ");  // 换行占位

        // 4. Stream 流遍历（排序）
        System.out.print("\n4. Stream 排序后遍历: ");
        set.stream()
                .sorted()
                .forEach(n -> System.out.print(n + " "));

        // Stream 映射
        System.out.print("\n   Stream 映射（元素 × 2）: ");
        set.stream()
                .map(n -> n * 2)
                .forEach(n -> System.out.print(n + " "));

        System.out.println();
    }

    // ==================== TreeSet ====================
    /**
     * TreeSet：底层基于 TreeMap（红黑树），自动排序，不可重复
     * 默认按自然顺序排序，不允许 null（取决于比较器）
     */
    static void testTreeSet() {
        System.out.println("\n---------- TreeSet（自动排序，不可重复）----------");

        // 创建集合并添加元素
        Set<Integer> set = new TreeSet<>();
        set.add(50);
        set.add(10);
        set.add(30);
        set.add(40);
        set.add(10);   // 重复元素，不会存入
        set.add(20);
        System.out.println("原始集合: " + set);
        System.out.println("（注意：自动按自然顺序排序，且重复的 10 只存了一次）");

        // TreeSet 也支持自定义排序
        Set<String> strSet = new TreeSet<>((a, b) -> b.compareTo(a));  // 降序
        strSet.add("Java");
        strSet.add("Python");
        strSet.add("Go");
        strSet.add("C++");
        System.out.println("自定义降序 TreeSet: " + strSet);

        // 1. 增强 for 循环
        System.out.print("1. 增强 for 循环遍历: ");
        for (Integer num : set) {
            System.out.print(num + " ");
        }

        // 2. 迭代器 Iterator
        System.out.print("\n2. 迭代器遍历: ");
        Iterator<Integer> it = set.iterator();
        while (it.hasNext()) {
            System.out.print(it.next() + " ");
        }

        // 3. Lambda forEach
        System.out.print("\n3. Lambda forEach 遍历: ");
        set.forEach(n -> System.out.print(n + " "));

        // 4. Stream 流遍历（过滤 > 20）
        System.out.print("\n4. Stream 过滤（> 20）: ");
        set.stream()
                .filter(n -> n > 20)
                .forEach(n -> System.out.print(n + " "));

        System.out.println();
    }

    // ==================== HashMap ====================
    /**
     * HashMap：底层基于哈希表，key 无序，不可重复
     * key 和 value 都可以为 null，默认初始容量 16，加载因子 0.75
     */
    static void testHashMap() {
        System.out.println("---------- HashMap（key 无序）----------");

        // 创建集合并添加元素
        Map<String, Integer> map = new HashMap<>();
        map.put("张三", 23);
        map.put("李四", 25);
        map.put("王五", 21);
        map.put("张三", 30);   // key 重复会覆盖旧值
        map.put("赵六", 28);
        map.put("孙七", 22);
        System.out.println("原始集合: " + map);
        System.out.println("元素个数: " + map.size() + "（注意：重复的 key\"张三\"的 value 被覆盖为 30）");

        // Map 没有实现 Iterable，不能直接用增强 for/迭代器，需要通过视图遍历

        // 1. 遍历 keySet（遍历所有键）
        System.out.print("1. keySet 遍历键: ");
        for (String key : map.keySet()) {
            System.out.print(key + "=" + map.get(key) + " ");
        }

        // 2. 遍历 values（遍历所有值）
        System.out.print("\n2. values 遍历值: ");
        for (Integer value : map.values()) {
            System.out.print(value + " ");
        }

        // 3. 遍历 entrySet（推荐，一次获取键和值）
        System.out.print("\n3. entrySet 遍历键值对: ");
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            System.out.print(entry.getKey() + "=" + entry.getValue() + " ");
        }

        // 4. 迭代器遍历 entrySet
        System.out.print("\n4. 迭代器遍历 entrySet: ");
        Iterator<Map.Entry<String, Integer>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Integer> entry = it.next();
            System.out.print(entry.getKey() + "=" + entry.getValue() + " ");
        }

        // 4.1 迭代器安全删除（删除年龄 > 25 的）
        Iterator<Map.Entry<String, Integer>> itDel = map.entrySet().iterator();
        while (itDel.hasNext()) {
            Map.Entry<String, Integer> entry = itDel.next();
            if (entry.getValue() > 25) {
                itDel.remove();
            }
        }
        System.out.println("\n   迭代器删除（年龄 > 25）后: " + map);
        // 恢复
        map.put("张三", 30);
        map.put("赵六", 28);

        // 5. Lambda forEach（最简洁）
        System.out.print("5. Lambda forEach 遍历: ");
        map.forEach((k, v) -> System.out.print(k + "=" + v + " "));

        // 6. Stream 流遍历
        System.out.print("\n6. Stream 流遍历: ");
        map.entrySet().stream()
                .forEach(e -> System.out.print(e.getKey() + "=" + e.getValue() + " "));

        // Stream 过滤 + 遍历
        System.out.print("\n   Stream 过滤（value > 23）: ");
        map.entrySet().stream()
                .filter(e -> e.getValue() > 23)
                .forEach(e -> System.out.print(e.getKey() + "=" + e.getValue() + " "));

        System.out.println();
    }

    // ==================== LinkedHashMap ====================
    /**
     * LinkedHashMap：继承 HashMap，通过双向链表维护插入顺序
     * key 有序（插入顺序），不可重复
     */
    static void testLinkedHashMap() {
        System.out.println("\n---------- LinkedHashMap（key 有序，按插入顺序）----------");

        // 创建集合并添加元素
        Map<String, String> map = new LinkedHashMap<>();
        map.put("China", "北京");
        map.put("Japan", "东京");
        map.put("Korea", "首尔");
        map.put("USA", "华盛顿");
        map.put("China", "上海");  // key 重复会覆盖旧值
        System.out.println("原始集合: " + map);
        System.out.println("（注意：输出顺序与插入顺序一致，key\"China\"的 value 被覆盖为\"上海\"）");

        // 1. keySet + 增强 for
        System.out.print("1. keySet 遍历: ");
        for (String key : map.keySet()) {
            System.out.print(key + "→" + map.get(key) + " ");
        }

        // 2. entrySet + 增强 for
        System.out.print("\n2. entrySet 遍历: ");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.print(entry.getKey() + "→" + entry.getValue() + " ");
        }

        // 3. 迭代器遍历
        System.out.print("\n3. 迭代器遍历: ");
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            System.out.print(entry.getKey() + "→" + entry.getValue() + " ");
        }

        // 4. Lambda forEach
        System.out.print("\n4. Lambda forEach 遍历: ");
        map.forEach((k, v) -> System.out.print(k + "→" + v + " "));

        // 5. Stream 流遍历
        System.out.print("\n5. Stream 流遍历: ");
        map.entrySet().stream()
                .forEach(e -> System.out.print(e.getKey() + "→" + e.getValue() + " "));

        System.out.println();
    }

    // ==================== TreeMap ====================
    /**
     * TreeMap：底层基于红黑树，key 会按自然顺序或比较器规则自动排序
     * key 不允许 null（取决于比较器），value 可以为 null
     */
    static void testTreeMap() {
        System.out.println("\n---------- TreeMap（key 自动排序）----------");

        // 创建集合并添加元素（默认按 key 的自然顺序排序）
        Map<Integer, String> map = new TreeMap<>();
        map.put(3, "张三");
        map.put(1, "李四");
        map.put(5, "王五");
        map.put(2, "赵六");
        map.put(4, "孙七");
        map.put(1, "周八");  // key 重复会覆盖旧值
        System.out.println("原始集合: " + map);
        System.out.println("（注意：自动按 key 升序排列，key=1 的 value 被覆盖为\"周八\"）");

        // 自定义降序 TreeMap
        Map<Integer, String> descMap = new TreeMap<>((a, b) -> b - a);
        descMap.putAll(map);
        System.out.println("自定义降序 TreeMap: " + descMap);

        // 1. keySet 遍历
        System.out.print("1. keySet 遍历: ");
        for (Integer key : map.keySet()) {
            System.out.print(key + "→" + map.get(key) + " ");
        }

        // 2. values 遍历
        System.out.print("\n2. values 遍历: ");
        for (String value : map.values()) {
            System.out.print(value + " ");
        }

        // 3. entrySet 遍历
        System.out.print("\n3. entrySet 遍历: ");
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            System.out.print(entry.getKey() + "→" + entry.getValue() + " ");
        }

        // 4. 迭代器遍历
        System.out.print("\n4. 迭代器遍历: ");
        Iterator<Map.Entry<Integer, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, String> entry = it.next();
            System.out.print(entry.getKey() + "→" + entry.getValue() + " ");
        }

        // 5. Lambda forEach
        System.out.print("\n5. Lambda forEach 遍历: ");
        map.forEach((k, v) -> System.out.print(k + "→" + v + " "));

        // 6. Stream 流（过滤 key > 2）
        System.out.print("\n6. Stream 过滤（key > 2）: ");
        map.entrySet().stream()
                .filter(e -> e.getKey() > 2)
                .forEach(e -> System.out.print(e.getKey() + "→" + e.getValue() + " "));

        System.out.println();
    }
}
