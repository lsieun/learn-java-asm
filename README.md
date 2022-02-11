# Learn Java ASM

[![Gitee](https://img.shields.io/badge/Gitee-white.svg?style=flat-square&logo=gitee&logoColor=C71D23)](https://gitee.com/lsieun/learn-java-asm)
![license](https://badg.now.sh/gitee/license/lsieun/learn-java-asm)
![Gitee stars](https://badg.now.sh/gitee/stars/lsieun/learn-java-asm)
![Gitee forks](https://badg.now.sh/gitee/forks/lsieun/learn-java-asm)
![Gitee issues](https://badg.now.sh/gitee/issues/lsieun/learn-java-asm)
![Gitee last commit](https://badg.now.sh/gitee/last-commit/lsieun/learn-java-asm)

[![Github](https://img.shields.io/badge/GitHub-white.svg?style=flat-square&logo=github&logoColor=181717)](https://github.com/lsieun/learn-java-asm)
![GitHub](https://img.shields.io/github/license/lsieun/learn-java-asm)
![GitHub stars](https://img.shields.io/github/stars/lsieun/learn-java-asm.svg)
![GitHub forks](https://img.shields.io/github/forks/lsieun/learn-java-asm.svg)
![GitHub issues](https://img.shields.io/github/issues-raw/lsieun/learn-java-asm?label=issues)
![GitHub last commit](https://img.shields.io/github/last-commit/lsieun/learn-java-asm.svg)

:maple_leaf: Java [ASM](https://asm.ow2.io/) is an open-source java library for manipulating bytecode.

本项目旨在系统地介绍如何学习Java ASM的知识，主要涉及Core API、OPCODE和Tree API等内容。至于学习的预期目标就是，用一个形象的说法来讲，让字节码在你的手中“跳舞”：看看你的左手，一个完整的ClassFile拆解成不同粒度的字节码内容；看看你的右手，不同粒度的字节码内容又重新组织成一个ClassFile结构。

如果你觉得代码还不错（ :sparkling_heart: ），欢迎加星（ :star: ）支持！

```text
           _______                                 ,        _        _        
          (,     /'                              /'/      /' `\     ' )     _)
               /'                              /' /     /'   ._)    //  _/~/' 
             /'____ .     ,   ____          ,/'  /     (____      /'/_/~ /'   
   _       /'/'    )|    /  /'    )        /`--,/           )   /' /~  /'     
 /' `    /'/'    /' |  /' /'    /'       /'    /          /'  /'     /'       
(_____,/' (___,/(___|/(__(___,/(__   (,/'     (_,(_____,/'(,/'      (_,       
```

---

如果我们学会了Java ASM之后，可能还是需要一个具体的应用场景来进行使用，这个场景就是由 [Java Agent](https://lsieun.github.io/java-agent/java-agent-01.html) 开启的。

那么，Java ASM和Java Agent这两者之间是什么关系呢？
Java ASM是一个操作字节码的工具（tool），而Java Agent提供了修改字节码的机会（opportunity）。
想像这样一个场景：
有一个JVM正在运行，突然Java Agent在JVM上打开一扇大门，Java ASM通过大门冲进JVM里面，就要开始修改字节码了。

```text
.class --- Java ASM --- Java Agent --- JVM
```

再打个比方，Java ASM就是“一匹千里马”，而Java Agent就是“伯乐”。
如果遇不到“伯乐”，可能“千里马”的才能就埋没了；正因为有了“伯乐”，“千里马”就有了施展才能的机会。

```text
世有伯乐，然后有千里马。
千里马常有，而伯乐不常有。
故虽有名马，祗辱于奴隶人之手，骈死于槽枥之间，不以千里称也。
```

---

## 1. 如何使用

### 1.1 代码下载

[![Gitee](https://img.shields.io/badge/Gitee-white.svg?style=flat-square&logo=gitee&logoColor=C71D23)](https://gitee.com/lsieun/learn-java-asm)
[![Github](https://img.shields.io/badge/GitHub-white.svg?style=flat-square&logo=github&logoColor=181717)](https://github.com/lsieun/learn-java-asm)

从[Gitee](https://gitee.com/lsieun/learn-java-asm) 仓库下载代码，使用如下命令：

```text
git clone https://gitee.com/lsieun/learn-java-asm
```

从[GitHub](https://github.com/lsieun/learn-java-asm) 仓库下载代码，使用如下命令：

```text
git clone https://github.com/lsieun/learn-java-asm
```

### 1.2 开发环境

[![Licence](https://img.shields.io/github/license/lsieun/learn-java-asm?style=social)](./LICENSE)
![Git](https://img.shields.io/badge/Git-white.svg?style=flat-square&logo=git&logoColor=F05032)
![Java](https://img.shields.io/badge/-Java-white.svg?style=flat-square&logo=java&logoColor=007396)
![Apache Maven](https://img.shields.io/badge/Maven-white.svg?style=flat-square&logo=Apache%20Maven&logoColor=C71A36)
![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ_IDEA-white.svg?style=flat-square&logo=intellij-idea&logoColor=000000)

- [Git](https://git-scm.com/)
- [Java 8](https://www.oracle.com/java/technologies/javase/javase8-archive-downloads.html)
- [Apache Maven](https://maven.apache.org/)
- [IntelliJ IDEA](https://www.jetbrains.com/idea/download/other.html) (Ultimate or Community Edition)

在`learn-java-asm`项目当中，使用的ASM版本为`9.0`。如果想使用最新![Maven Central](https://img.shields.io/maven-central/v/org.ow2.asm/asm.svg?color=25a162&label=ASM) 版本，可以修改`pom.xml`文件中的`asm.version`属性：

```text
<asm.version>9.0</asm.version>
```

### 1.3. 运行代码

在`learn-java-asm`项目当中，包含`main`方法的类主要位于`run`包（`src/main/java/run`）。

## 2. 课程资料

[![51cto](https://img.shields.io/website/https/edu.51cto.com.svg?label=51cto)](https://edu.51cto.com/lecturer/9210464.html)
[![Bilibili](https://img.shields.io/website/https/bilibili.com.svg?label=bilibili&style=flat-square&logo=bilibili&logoColor=00A1D6)](https://space.bilibili.com/1321054247)
[![lsieun.github.io](https://img.shields.io/website/https/lsieun.github.io.svg?label=lsieun.github.io)](https://lsieun.github.io)

- 《Java ASM系列一：Core API》
  - 文章地址： [lsieun.github.io](https://lsieun.github.io/java/asm/java-asm-season-01.html) | [51CTO](https://blog.51cto.com/lsieun/2924583)
  - 视频地址： [51CTO](https://edu.51cto.com/course/28517.html) | [Bilibili](https://space.bilibili.com/1321054247/channel/seriesdetail?sid=381716)
- 《Java ASM系列二：OPCODE》
  - 文章地址：[lsieun.github.io](https://lsieun.github.io/java/asm/java-asm-season-02.html) | [51CTO](https://blog.51cto.com/lsieun/3273965)
  - 视频地址：[51CTO](https://edu.51cto.com/course/28870.html) | [Bilibili](https://space.bilibili.com/1321054247/channel/seriesdetail?sid=381716)
- 《Java ASM系列三：Tree API》
  - 文章地址：[lsieun.github.io](https://lsieun.github.io/java/asm/java-asm-season-03.html) | [51CTO](https://blog.51cto.com/lsieun/4034588)
  - 视频地址：[51CTO](https://edu.51cto.com/course/29459.html) | [Bilibili](https://space.bilibili.com/1321054247/channel/seriesdetail?sid=381716)

### 2.1. ASM的组成部分

从组成结构上来说，Java ASM有Core API和Tree API两部分组成。

```text
                                   ┌─── asm.jar
                                   │
            ┌─── Core API ─────────┼─── asm-util.jar
            │                      │
            │                      └─── asm-commons.jar
Java ASM ───┤
            │
            │                      ┌─── asm-tree.jar
            └─── Tree API ─────────┤
                                   └─── asm-analysis.jar
```

从依赖关系角度上说，Java ASM当中的各个`.jar`之间的依赖关系如下：

```text
┌────────────────────────────┬─────────────────────────────┐
│                    ┌───────┴────────┐                    │
│     util           │    analysis    │         commons    │
│             ┌──────┴────────────────┴──────┐             │
│             │             tree             │             │
├─────────────┴──────────────────────────────┴─────────────┤
│                           core                           │
└──────────────────────────────────────────────────────────┘
```

### 2.2. ASM能够做什么

从应用的角度来说，Java ASM可以进行Class Generation、Class Transformation和Class Analysis三个类型的操作。

```text
                                   ┌─── find potential bugs
                                   │
            ┌─── analysis ─────────┼─── detect unused code
            │                      │
            │                      └─── reverse engineer code
            │
Java ASM ───┼─── generation
            │
            │                      ┌─── optimize programs
            │                      │
            └─── transformation ───┼─── obfuscate programs
                                   │
                                   └─── insert performance monitoring code
```

### 2.3. top, null和void

在下表当中，top、null和void三者相对应的转换值：

```text
┌─────────────┬────────────────────────────┬────────────────────────────────┐
│   .class    │          ASM Type          │      ASM Value in Frame        │
├─────────────┼────────────────────────────┼────────────────────────────────┤
│     top     │            null            │ BasicValue.UNINITIALIZED_VALUE │
├─────────────┼────────────────────────────┼────────────────────────────────┤
│ aconst_null │ BasicInterpreter.NULL_TYPE │   BasicValue.REFERENCE_VALUE   │
├─────────────┼────────────────────────────┼────────────────────────────────┤
│    void     │       Type.VOID_TYPE       │              null              │
└─────────────┴────────────────────────────┴────────────────────────────────┘
```

## 3. 注意事项

### 3.1. 添加typo字典

在编写代码的过程中，会遇到一些Typo提示，原因是`insn`等内容不是合法的单词。

解决方法：借助于IntelliJ IDEA的[Spellchecking](https://www.jetbrains.com/help/idea/spellchecking.html) 的功能。

操作步骤：

- 第一步，在`Settings/Preferences`当中，找到`Editor | Natural Languages | Spelling`位置。
- 第二步，在右侧的Custom dictionaries位置，添加**custom dictionary**，在`learn-java-asm`项目根目录下，有一个`accepted-words.dic`文件，添加该文件即可。

配置完成之后，需要**重新启动IntelliJ IDEA**才能生效。

### 3.2. 查看笔记

在编写代码的过程中，为了方便理解代码，我添加了一些笔记，格式如下：

```text
NOTE: 希望这是一条有用的笔记
```

但是，在默认情况下，它并不会高亮显示，因此不容易被察觉到。

解决方法：借助于IntelliJ IDEA的[TODO comments](https://www.jetbrains.com/help/idea/using-todo.html) 功能。

操作步骤：

- 第一步，在`Settings/Preferences`当中，找到`Editor | TODO`位置。
- 第二步，在右侧的Patterns位置，添加以下内容：

```text
\bnote\b.*
```

配置完成之后，需要**重新启动IntelliJ IDEA**才能生效。

### 3.3. 关闭调试信息

在默认情况下，运行任何类，都会输出调试信息。在调试信息中，会带有`[DEBUG]`标识。

如果想关闭调试信息，可以修改`lsieun.cst.Const`类的`DEBUG`字段值为`false`（默认值为`true`）：

```java
public class Const {
    public static final boolean DEBUG = false;
}
```

然后，执行`mvn clean compile`对类进行重新编译：

```text
mvn clean compile
```

等待编译完成之后，再次运行程序。

## 4. 交流反馈

- 如果您有好的想法，可以提issues
- 如果您想贡献代码，可以进行fork
- 如果您有其它问题，可以添加QQ群（参考联系方式）

## 5. 联系方式

[![wechat](https://img.shields.io/badge/-lsieun-white.svg?style=flat-square&logo=wechat&logoColor=07C160)](https://lsieun.github.io/assets/images/contact/we-chat.jpg)
[![Tencent QQ](https://img.shields.io/badge/515882294-white.svg?style=flat-square&logo=tencentqq&logoColor=EB1923)](https://lsieun.github.io/assets/images/contact/qq.png)
[![QQ Group](https://img.shields.io/badge/584642776-white.svg?style=flat-square&logo=tencentqq&logoColor=1DA1F2&label=QQ%20Group)](https://lsieun.github.io/assets/images/contact/qq-group.jpg)
[![Java字节码交流QQ群](https://pub.idqqimg.com/wpa/images/group.png)](https://jq.qq.com/?_wv=1027&k=yOBiOaJV)

## 6. License

This project is licensed under the MIT License.
See the [LICENSE](./LICENSE) file for the full license text.
