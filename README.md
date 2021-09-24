# Learn Java ASM

![license](https://badg.now.sh/gitee/license/lsieun/learn-java-asm)
![stars](https://badg.now.sh/gitee/stars/lsieun/learn-java-asm)
![forks](https://badg.now.sh/gitee/forks/lsieun/learn-java-asm)
![issues](https://badg.now.sh/gitee/issues/lsieun/learn-java-asm)
![commits](https://badg.now.sh/gitee/commits/lsieun/learn-java-asm)
![last commit](https://badg.now.sh/gitee/last-commit/lsieun/learn-java-asm)

:bug: Java [ASM](https://asm.ow2.io/) is an open-source java library for manipulating bytecode.

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

### 1.3. 运行代码

在`learn-java-asm`项目当中，包含`main`方法的类主要位于`run`包（`src/main/java/run`）。

## 2. 相关资源

[![51cto](https://img.shields.io/website/https/edu.51cto.com.svg?label=51cto)](https://edu.51cto.com/lecturer/9210464.html)
[![Bilibili](https://img.shields.io/website/https/bilibili.com.svg?label=bilibili&style=flat-square&logo=bilibili&logoColor=00A1D6)](https://space.bilibili.com/1321054247)
[![lsieun.cn](https://img.shields.io/website/http/lsieun.cn.svg?label=lsieun.cn)](https://lsieun.cn)
[![lsieun.github.io](https://img.shields.io/website/https/lsieun.github.io.svg?label=lsieun.github.io)](https://lsieun.github.io)

- 《Java ASM系列一：Core API》
  - 文章地址： [lsieun.cn](https://lsieun.cn/java/asm/java-asm-season-01.html) | [lsieun.github.io](https://lsieun.github.io/java/asm/java-asm-season-01.html) | [51CTO博客](https://blog.51cto.com/lsieun/2924583)
  - 视频地址： [51CTO学堂](https://edu.51cto.com/course/28517.html) | [B站](https://space.bilibili.com/1321054247/channel/detail?cid=189917)
- 《Java ASM系列二：OPCODE》
  - 文章地址：[lsieun.cn](https://lsieun.cn/java/asm/java-asm-season-02.html) | [lsieun.github.io](https://lsieun.github.io/java/asm/java-asm-season-02.html) | [51CTO博客](https://blog.51cto.com/lsieun/3273965)
  - 视频地址：[51CTO学堂](https://edu.51cto.com/course/28870.html) | [B站](https://space.bilibili.com/1321054247/channel/detail?cid=197480)
- 《Java ASM系列三：Tree API》
  - 文章地址：未开始
  - 视频地址：未开始

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

## 6. 参考资料

- [ObjectWeb ASM](https://asm.ow2.io/)
- [Gitee badge](https://badg.vercel.app/)
