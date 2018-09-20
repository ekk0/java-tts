# java-tts
一个基于Java的粤语发音TTS,文字转语音.

## 安装
1.首先安装你的Java环境
2.运行src\org\lib\speech\test\Music.java
3.部署好项目所需要的包和路径
4.部署到Tomcat

## 测试开发
你可以很简单的使用它，初次尝试的时候你可以建立一个如下的 Test.java文件来测试：

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.lib.speech.engine.Engine;
import org.lib.speech.engine.SpeechEngine;
import org.lib.speech.process.DefaultStreamProcess;
import org.lib.speech.process.ProcessCenter;

public class Test {
public static void main(String[] args) {

// 建造一个流处理器，参数设置是否重新读取字典文件
ProcessCenter pc = new DefaultStreamProcess(true);

// 建立一个语音引擎，第二个参数设置是否转换为粤语口语发音
Engine engine = new SpeechEngine(pc, true);

// 任何一个String作为你想要它发音的句子
String sentences = "你可以在这里尝试任何一个句子，看看它是如何发音的。";

// 第一种方法：直接要它发音
engine.getPronounces(sentences);

// 第二种方法：句子在一个txt文档中，你要它把txt中的内容读出来，第二个参数设置是否将文档内容输出到控制台显示
try {
	engine.getPronounces(new File("C:/a.txt"), false);
	} catch (IOException e) {
	e.printStackTrace();
}

// 第三种方法：把发音保存在一个.au的声音文件中，目前只支持保存到这种文件，当然你也可以自己扩展
try {
	engine.getPronouncesFile(sentences, new File("C:/a.au"));
	} catch (IOException e) {
	e.printStackTrace();
}

// 另外，如果你想获得初始的发音素材，可以这样显示到控制台
List<Object[]> list = engine.getPronounceElements(sentences);
Iterator<Object[]> iter = list.iterator();
while (iter.hasNext()) {
Object[] obj = iter.next();
if (obj[0] instanceof File) {
for (int i = 0; i < obj.length; i++) {
	File file = (File) obj[i];
	System.out.print(file.getName() + " ");
}
} else {
for (int i = 0; i < obj.length; i++) {
	System.out.print(obj[i] + " ");
}
}
	System.out.println();
}
}
}