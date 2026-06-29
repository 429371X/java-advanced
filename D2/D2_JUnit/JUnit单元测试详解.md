# JUnit 单元测试详解

---

## 一、什么是单元测试？

单元测试是对软件中**最小可测试单元**（通常是方法/函数）进行验证的测试方式。它确保每个独立的代码单元按预期工作。

---

### 1.1 为什么要写单元测试？

| 价值 | 说明 |
|------|------|
| **尽早发现 Bug** | 在代码刚写完就验证，修复成本最低 |
| **放心重构** | 有测试覆盖，改代码后跑一遍就知道有没有破坏原有功能 |
| **即文档即规范** | 测试用例本身就说明了方法的行为和边界条件 |
| **提升设计质量** | 写出可测试的代码往往也是低耦合、高内聚的好代码 |

### 1.2 JUnit 是什么？

**JUnit** 是 Java 生态中最主流的单元测试框架，提供了一套注解、断言和运行器，让编写和运行测试变得简单。

---

## 二、JUnit 5 架构

JUnit 5 由三个子项目组成：

```
JUnit 5 = JUnit Platform + JUnit Jupiter + JUnit Vintage
```

| 子项目 | 说明 |
|--------|------|
| **JUnit Platform** | 测试引擎的基础平台，负责发现和运行测试（IDE、Maven/Gradle 都基于它启动测试） |
| **JUnit Jupiter** | JUnit 5 的核心，提供全新的注解、断言和扩展模型（我们要学的就是它） |
| **JUnit Vintage** | 兼容层，让 JUnit 3 和 JUnit 4 的老测试也能在 JUnit 5 平台上运行 |

---

## 三、快速入门

### 3.1 Maven 依赖

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.0</version>
    <scope>test</scope>
</dependency>
```

### 3.2 第一个测试

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculatorTest {

    @Test
    void testAdd() {
        Calculator calc = new Calculator();
        int result = calc.add(2, 3);
        assertEquals(5, result, "2 + 3 应该等于 5");
    }
}
```

> **约定：** 测试类名通常 = 被测类名 + `Test`，放在 `src/test/java` 下，包路径与被测类一致。

---

## 四、核心注解

---

### 4.1 @Test — 标记测试方法

最核心的注解，标记一个方法为测试方法。

```java
import org.junit.jupiter.api.Test;

class MyTest {

    @Test
    void testSomething() {
        // 测试逻辑
    }
}
```

> JUnit 5 的 `@Test` 不需要 `public`，方法可以是包级私有的（JUnit 4 必须 `public`）。

---

### 4.2 生命周期注解

| 注解 | 执行时机 | 典型用途 |
|------|----------|----------|
| `@BeforeAll` | 所有测试方法之前执行**一次**（必须是 `static`） | 初始化数据库连接池、启动嵌入服务器 |
| `@BeforeEach` | **每个**测试方法之前都执行一次 | 创建测试对象、准备测试数据 |
| `@AfterEach` | **每个**测试方法之后都执行一次 | 清理测试数据、重置 Mock |
| `@AfterAll` | 所有测试方法之后执行**一次**（必须是 `static`） | 关闭数据库连接、释放资源 |

```java
import org.junit.jupiter.api.*;

class LifecycleTest {

    @BeforeAll
    static void initAll() {
        System.out.println("【BeforeAll】只执行一次，在所有测试之前");
    }

    @BeforeEach
    void init() {
        System.out.println("【BeforeEach】每个测试前都执行");
    }

    @Test
    void test1() {
        System.out.println("  → test1");
    }

    @Test
    void test2() {
        System.out.println("  → test2");
    }

    @AfterEach
    void tearDown() {
        System.out.println("【AfterEach】每个测试后都执行");
    }

    @AfterAll
    static void tearDownAll() {
        System.out.println("【AfterAll】只执行一次，在所有测试之后");
    }
}
```

**输出：**

```
【BeforeAll】只执行一次，在所有测试之前
【BeforeEach】每个测试前都执行
  → test1
【AfterEach】每个测试后都执行
【BeforeEach】每个测试前都执行
  → test2
【AfterEach】每个测试后都执行
【AfterAll】只执行一次，在所有测试之后
```

---

### 4.3 @DisplayName — 给测试起一个可读的名字

```java
@Test
@DisplayName("两个正数相加 → 应返回正确和")
void testAddPositiveNumbers() {
    assertEquals(5, calculator.add(2, 3));
}
```

在 IDE 和测试报告中会显示中文名称，比 `testAddPositiveNumbers` 更直观。

---

### 4.4 @Disabled — 跳过测试

```java
@Test
@Disabled("暂时跳过，等该功能开发完再启用")
void testFutureFeature() {
    // ...
}
```

---

### 4.5 @RepeatedTest — 重复测试

用于测试需要多次执行验证稳定性的场景（如并发相关逻辑）：

```java
@RepeatedTest(5)  // 重复执行 5 次
@DisplayName("重复测试验证稳定性")
void repeatedTest() {
    System.out.println("第 " + (执行次数) + " 次执行");
}
```

---

### 4.6 @Timeout — 超时控制

```java
@Test
@Timeout(value = 1, unit = TimeUnit.SECONDS)  // 1 秒超时
void testShouldCompleteWithinOneSecond() {
    // 如果超过 1 秒没执行完 → 测试失败
}
```

---

## 五、断言（Assertions）

断言是测试的核心 — 判断实际结果是否符合预期。

---

### 5.1 常用断言一览

| 断言方法 | 说明 |
|----------|------|
| `assertEquals(expected, actual)` | 断言两个值**相等** |
| `assertNotEquals(unexpected, actual)` | 断言两个值**不相等** |
| `assertTrue(condition)` | 断言条件为 **true** |
| `assertFalse(condition)` | 断言条件为 **false** |
| `assertNull(object)` | 断言对象为 **null** |
| `assertNotNull(object)` | 断言对象**不为 null** |
| `assertSame(expected, actual)` | 断言是**同一个对象**（`==`） |
| `assertNotSame(unexpected, actual)` | 断言不是同一个对象 |
| `assertArrayEquals(expected, actual)` | 断言两个数组**内容相等** |
| `assertIterableEquals(expected, actual)` | 断言两个可迭代对象**内容相等** |
| `assertThrows(expectedType, executable)` | 断言**抛出指定异常** |
| `assertDoesNotThrow(executable)` | 断言**不抛异常** |
| `assertAll(...)` | **批量断言**，所有断言都会执行（不会因一个失败就中断） |

---

### 5.2 基本断言示例

```java
import static org.junit.jupiter.api.Assertions.*;

@Test
void basicAssertions() {
    assertEquals(4, 2 + 2, "加法计算错误");
    assertTrue("hello".startsWith("h"), "应该以 h 开头");
    assertNotNull("not null");
    assertFalse(3 > 5, "3 不可能大于 5");
}
```

> **最佳实践：** 断言都带第三个参数（失败消息），排查问题更快。

---

### 5.3 assertThrows — 断言异常

```java
@Test
void testDivideByZero() {
    Calculator calc = new Calculator();

    // 断言：除以零应该抛出 ArithmeticException
    ArithmeticException ex = assertThrows(
        ArithmeticException.class,
        () -> calc.divide(10, 0)  // 这个操作应该抛异常
    );

    // 还可以验证异常消息
    assertEquals("除数不能为零", ex.getMessage());
}
```

---

### 5.4 assertAll — 批量断言（推荐）

一般断言一旦失败就会中断，后面的断言不会执行。**`assertAll` 会跑完所有断言再统一报告**：

```java
@Test
void testPerson() {
    Person person = new Person("张三", 25);

    assertAll("Person 属性验证",
        () -> assertEquals("张三", person.getName(), "姓名错误"),
        () -> assertEquals(25, person.getAge(), "年龄错误"),
        () -> assertTrue(person.isAdult(), "应该是成年人"),
        () -> assertNotNull(person.getId(), "ID 不应为空")
    );
}
// 即使第一个断言失败，后面三个仍然会执行并报告
```

---

### 5.5 第三方断言库（可选）

JUnit 5 的原生断言够用但不丰富，常用的第三方选择：

| 库 | 特点 |
|----|------|
| **AssertJ** | 流式 API，链式调用，可读性极强：`assertThat(result).isEqualTo(5).isPositive()` |
| **Hamcrest** | 经典匹配器库：`assertThat(list, hasItem("apple"))` |

---

## 六、参数化测试（Parameterized Tests）

当需要对**多组数据**执行同一个测试逻辑时，参数化测试可以避免重复代码。

### 6.1 @ValueSource — 单参数

```java
@ParameterizedTest
@ValueSource(strings = {"hello", "world", "java"})
void testStringNotEmpty(String input) {
    assertFalse(input.isEmpty(), input + " 不应该为空");
}
// 这个方法会被执行 3 次，每次传入不同的参数
```

### 6.2 @CsvSource — 多参数

```java
@ParameterizedTest
@CsvSource({
    "2, 3, 5",
    "0, 0, 0",
    "-1, 1, 0",
    "100, 200, 300"
})
void testAdd(int a, int b, int expected) {
    assertEquals(expected, calculator.add(a, b),
        a + " + " + b + " 应该等于 " + expected);
}
```

### 6.3 @MethodSource — 方法提供参数

```java
@ParameterizedTest
@MethodSource("provideTestData")
void testWithMethodSource(String input, int expectedLength) {
    assertEquals(expectedLength, input.length());
}

// 参数工厂方法（必须是 static，返回 Stream）
static Stream<Arguments> provideTestData() {
    return Stream.of(
        Arguments.of("hello", 5),
        Arguments.of("", 0),
        Arguments.of("JUnit", 5)
    );
}
```

### 6.4 @DisplayName 动态显示名

```java
@ParameterizedTest(name = "测试：{0} + {1} = {2}")
@CsvSource({
    "2, 3, 5",
    "-1, 1, 0"
})
void testAdd(int a, int b, int expected) {
    assertEquals(expected, a + b);
}
// IDE 中每个用例会显示为 "测试：2 + 3 = 5 ✓"
```

---

## 七、条件执行

根据环境条件决定是否执行某个测试。

| 注解 | 条件 |
|------|------|
| `@EnabledOnOs(OS.WINDOWS)` | 只在 Windows 上执行 |
| `@DisabledOnOs(OS.LINUX)` | 跳过后 Linux 上执行 |
| `@EnabledOnJre(JRE.JAVA_17)` | 只在 Java 17 上执行 |
| `@EnabledIfEnvironmentVariable(named = "CI", matches = "true")` | 环境变量 CI=true 时才执行 |
| `@EnabledIfSystemProperty(named = "os.arch", matches = ".*64.*")` | 64 位系统才执行 |

```java
@Test
@EnabledOnOs(OS.WINDOWS)
@DisplayName("这个测试只在 Windows 上跑")
void testOnlyOnWindows() {
    // ...
}
```

---

## 八、测试命名与组织最佳实践

### 8.1 命名规范

```java
// ✅ 推荐：描述测试的"场景 + 预期行为"
@Test void shouldReturnTrueWhenInputIsValid()
@Test void shouldThrowExceptionWhenDivisorIsZero()
@Test void shouldReturnEmptyListWhenNoDataFound()

// 中文项目也可以直接用 @DisplayName
@Test
@DisplayName("除数为零时 → 抛出 ArithmeticException")
void divideByZero() { ... }
```

### 8.2 Given-When-Then 模式

把测试逻辑分成三个阶段：

```java
@Test
@DisplayName("余额充足时 → 取款成功，余额正确扣减")
void withdrawWithSufficientBalance() {
    // Given（准备）：创建一个有 100 元的账户
    Account account = new Account("张三", 100.0);

    // When（执行）：取 30 元
    boolean result = account.withdraw(30.0);

    // Then（验证）：取款成功，余额 = 70
    assertAll(
        () -> assertTrue(result, "取款应该成功"),
        () -> assertEquals(70.0, account.getBalance(), 0.01, "余额应为 70")
    );
}
```

### 8.3 测试独立性

**每个测试方法必须独立，不依赖其他测试的执行顺序。** 因为 JUnit 不保证测试方法按特定顺序执行。

```java
// ❌ 坏做法：test2 依赖 test1 的副作用
@Test void test1() { database.insert(data); }
@Test void test2() { assertEquals(data, database.find()); }  // 依赖 test1 先执行

// ✅ 好做法：每个测试自己准备数据
@BeforeEach
void setUp() {
    database.insert(data);  // 每个测试前重新插入
}
```

---

## 九、JUnit 4 vs JUnit 5 对比

| 特性 | JUnit 4 | JUnit 5 (Jupiter) |
|------|---------|-------------------|
| 包名 | `org.junit` | `org.junit.jupiter` |
| 方法可见性 | 必须 `public` | 包级私有即可 |
| `@Before` / `@BeforeClass` | `@Before` / `@BeforeClass` | `@BeforeEach` / `@BeforeAll` |
| `@After` / `@AfterClass` | `@After` / `@AfterClass` | `@AfterEach` / `@AfterAll` |
| 跳过测试 | `@Ignore` | `@Disabled` |
| 参数化测试 | 需要 `@RunWith(Parameterized.class)` | 内置 `@ParameterizedTest` |
| 异常断言 | `@Test(expected = ...)` | `assertThrows(...)` |
| 超时 | `@Test(timeout = ...)` | `@Timeout(...)` |
| 扩展机制 | Runner / Rule | Extension API |

---

## 十、关键要点速查

| 概念 | 一句话说明 |
|------|-----------|
| **@Test** | 标记一个方法为测试方法 |
| **@BeforeEach** | 每个测试前执行，用于准备测试数据 |
| **@AfterEach** | 每个测试后执行，用于清理 |
| **@BeforeAll / @AfterAll** | 所有测试前后各执行一次，必须是 `static` |
| **assertEquals(expected, actual)** | 最常用的断言：判断实际值等于期望值 |
| **assertThrows** | 断言某段代码会抛出指定异常 |
| **assertAll** | 批量断言，所有断言都执行，不会中途中断 |
| **@ParameterizedTest** | 参数化测试，一组数据驱动同一个测试逻辑 |
| **Given-When-Then** | 测试方法结构范式：准备 → 执行 → 验证 |
| **测试独立性** | 每个测试自己准备数据，不依赖其他测试的执行顺序 |
