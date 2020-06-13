package org.lib.speech.test;
import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.EncodingAttributes;
import org.lib.speech.engine.Engine;
import org.lib.speech.engine.SpeechEngine;
import org.lib.speech.process.DefaultStreamProcess;
import org.lib.speech.process.ProcessCenter;
import it.sauronsoftware.jave.Encoder;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import static java.lang.Thread.sleep;


public class Music extends HttpServlet {
        public int num;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 设置响应内容类型

		 //response.setHeader("Content-type", "text/html; charset=UTF-8");
		 request.setCharacterEncoding("UTF-8");//解决乱码
	     response.setContentType("text/html;charset=UTF-8");//解决乱码
		 String text=request.getParameter("text");
		 String sentences = text;
		try {
			sayPlay(text,request,response);
		}catch (Exception e){

		}

	}

	public static void sayPlay (String sentences,HttpServletRequest request,HttpServletResponse response) throws Exception{

		//System.out.println(System.getProperty("user.dir"));
		String ROOT = "E:\\java\\apache-tomcat-7.0.77\\webapps\\JavaWeb";

		int num = RandomNum();
		Calendar now = Calendar.getInstance();
		String rootDir = ROOT+"/music/";
		String timeDir = ""+(now.get(Calendar.YEAR))  + (now.get(Calendar.MONTH) + 1)
				+(now.get(Calendar.DAY_OF_MONTH)) + "/" + (now.get(Calendar.HOUR_OF_DAY)) +"/";
		//System.out.println(rootDir+timeDir);
		File file_mk = new File(rootDir+timeDir+num+"/") ;

		List<String> listArr = new ArrayList<String>();
		String dir = ROOT+"\\data\\jyutping-wong-44100-v7\\"; //读取wav文件路径
		String data_dir = timeDir+num+"/"; //生成文件路径
		//createDir(rootDir+timeDir+num);
		String localhost_dir = rootDir+num+".wav"; //生成的wav语音包
		ProcessCenter pc = new DefaultStreamProcess(true);
		// 建立一个语音引擎，第二个参数设置是否转换为粤语口语发音
		Engine engine = new SpeechEngine(pc, true);

		List<Object[]> list = engine.getPronounceElements(sentences);
		Iterator<Object[]> iter = list.iterator();
		//获取需要播放的文件名
		while (iter.hasNext()) {
			Object[] obj = iter.next();
			if (obj[0] instanceof File) {
				for (int i = 0; i < obj.length; i++) {
					File file = (File) obj[i];
				//	  	System.out.print(file.getName() + " ");
					listArr.add(file.getName());

				}
			}
		}

		//如果这个语音大于 2 个
		if (listArr.size() >= 2){
			AudioInputStream audio1 = AudioSystem.getAudioInputStream(new File(dir+listArr.get(0)));

			AudioInputStream audio2 = AudioSystem.getAudioInputStream(new File(dir+listArr.get(1)));
			AudioInputStream audioBuild = new AudioInputStream(
					new SequenceInputStream(audio1, audio2),
					audio1.getFormat(),
					audio1.getFrameLength() +
							audio2.getFrameLength()
			);
			AudioInputStream audio3;
			//大于两个时继续合并
			for(int i = 2; i<listArr.size();i++){
				audio3 = AudioSystem.getAudioInputStream(new File(dir+listArr.get(i)));
				audioBuild = new AudioInputStream(
						new SequenceInputStream(audioBuild, audio3),
						audioBuild.getFormat(), audioBuild.getFrameLength() +
						audio3.getFrameLength()
				);
			}
			//生成语音
			AudioSystem.write(audioBuild, AudioFileFormat.Type.WAVE, new File(localhost_dir));

		} else {
			//TODO::否则只有一个,直接返回语音路径
			localhost_dir = dir+listArr.get(0);
		}

		//输出 wav IO流
		try{
			response.setHeader("Content-Type", "audio/mpeg");
			File file = new File(localhost_dir);
			int len_l = (int) file.length();
			byte[] buf = new byte[2048];
			FileInputStream fis = new FileInputStream(file);
			OutputStream out = response.getOutputStream();
			len_l = fis.read(buf);
			while (len_l != -1) {
				out.write(buf, 0, len_l);
				len_l = fis.read(buf);
			}
			out.flush();
			out.close();
			fis.close();
		}catch (Exception e){
			System.out.println(e);
		}
		//response.setHeader("Content-Length",len_l+"");
		deleteFile(rootDir+num+".wav");

	}

	public static boolean deleteFile(String fileName) {
		File file = new File(fileName);
		// 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
		if (file.exists() && file.isFile()) {
			if (file.delete()) {
				//System.out.println("删除单个文件" + fileName + "成功！");
				return true;
			} else {
			//	System.out.println("删除单个文件" + fileName + "失败！");
				return false;
			}
		} else {
			//System.out.println("删除单个文件失败：" + fileName + "不存在！");
			return false;
		}
	}

	public static void sayPlay_old (String sentences,HttpServletRequest request,HttpServletResponse response) throws Exception{


		//System.out.println(System.getProperty("user.dir"));
		String ROOT = "E:\\java\\apache-tomcat-7.0.77\\webapps\\JavaWeb";

		int num = RandomNum();
		Calendar now = Calendar.getInstance();
		String rootDir = ROOT+"/music/";
		String timeDir = ""+(now.get(Calendar.YEAR))  + (now.get(Calendar.MONTH) + 1)
				+(now.get(Calendar.DAY_OF_MONTH)) + "/" + (now.get(Calendar.HOUR_OF_DAY)) +"/";
		//System.out.println(rootDir+timeDir);
		File file_mk = new File(rootDir+timeDir+num+"/") ;
		mkDir(file_mk);

		List<String> listArr = new ArrayList<String>();
		String dir = ROOT+"\\data\\jyutping-wong-44100-v7\\"; //读取wav文件路径
		String data_dir = timeDir+num+"/"; //生成文件路径
		createDir(rootDir+timeDir+num);

		String localhost_dir = rootDir+timeDir+num+"/"+num+".mp3";
		ProcessCenter pc = new DefaultStreamProcess(true);
		// 建立一个语音引擎，第二个参数设置是否转换为粤语口语发音
		Engine engine = new SpeechEngine(pc, true);

		List<Object[]> list = engine.getPronounceElements(sentences);
		Iterator<Object[]> iter = list.iterator();
		while (iter.hasNext()) {
			Object[] obj = iter.next();
			if (obj[0] instanceof File) {
				for (int i = 0; i < obj.length; i++) {
					File file = (File) obj[i];
				// 	System.out.print(file.getName() + " ");
					listArr.add(file.getName());
				}
			}
		}

		OutputStream osv = new FileOutputStream(localhost_dir) ;

		for(int i = 0; i<listArr.size();i++){
			//System.out.println(dir + listArr.get(i));
			execute(new File(dir + listArr.get(i)),  rootDir+data_dir+i+".mp3");
			InputStream is  = new FileInputStream(new File(rootDir+data_dir+i+".mp3"));
			byte[]bytes=new byte[128];
			int lens =0;
			int indexs = 0;
			while((lens = is.read(bytes))!=-1){
				indexs++;
				if(indexs==1){
					continue ;
				}
				osv.write(bytes, 0, lens ) ;
			}
			is.close();

		}
        try{
			response.setHeader("Content-Type", "audio/mpeg");
			File file = new File(localhost_dir);
			//File file = new File("F:\\www\\A.wav");
			int len_l = (int) file.length();
			byte[] buf = new byte[2048];
			FileInputStream fis = new FileInputStream(file);
			OutputStream out = response.getOutputStream();
			len_l = fis.read(buf);
			while (len_l != -1) {

				out.write(buf, 0, len_l);
				len_l = fis.read(buf);
			}

			out.flush();
			out.close();
			fis.close();
		}catch (Exception e){
			System.out.println(e);
		}

		//response.setHeader("Content-Length",len_l+"");

	}

	public static void mkDir(File file_mk) {

		if (file_mk.getParentFile().exists()) {
			file_mk.mkdir();
		} else {
			//	mkDir(file_mk.getParentFile());
			//	file_mk.mkdir();
		}
	}

	/**
	 * 执行转化过程
	 *
	 * @param source
	 *            源文件
	 * @param desFileName
	 *            目标文件名
	 * @return 转化后的文件
	 */
	public static File execute(File source, String desFileName)
			throws Exception {
		File target = new File(desFileName);
		AudioAttributes audio = new AudioAttributes();
		audio.setCodec("libmp3lame");
		audio.setBitRate(new Integer(36000)); //音频比率 MP3默认是1280000
		audio.setChannels(new Integer(2));
		audio.setSamplingRate(new Integer(44100));
		EncodingAttributes attrs = new EncodingAttributes();
		attrs.setFormat("mp3");
		attrs.setAudioAttributes(audio);
		Encoder encoder = new Encoder();
		encoder.encode(source, target, attrs);
		return target;
	}

	public static int RandomNum() throws Exception{
		int max=999999999;
		int min=10;
		Random random = new Random();
		return  random.nextInt(max-min) + min;

	}
	public static boolean createDir(String destDirName) {
		File dir = new File(destDirName);
		if (dir.exists()) {
			//System.out.println("创建目录" + destDirName + "失败，目标目录已经存在");
			return false;
		}
		if (!destDirName.endsWith(File.separator)) {
			destDirName = destDirName + File.separator;
		}
		//创建目录
		if (dir.mkdirs()) {
			//System.out.println("创建目录" + destDirName + "成功！");
			return true;
		} else {
			//System.out.println("创建目录" + destDirName + "失败！");
			return false;
		}
	}
	public static boolean createFile(String destFileName) {
		File file = new File(destFileName);
		if(file.exists()) {
			//System.out.println("创建单个文件" + destFileName + "失败，目标文件已存在！");
			return false;
		}
		if (destFileName.endsWith(File.separator)) {
			//System.out.println("创建单个文件" + destFileName + "失败，目标文件不能为目录！");
			return false;
		}
		//判断目标文件所在的目录是否存在
		if(!file.getParentFile().exists()) {
			//如果目标文件所在的目录不存在，则创建父目录
			//System.out.println("目标文件所在目录不存在，准备创建它！");
			if(!file.getParentFile().mkdirs()) {
				//	System.out.println("创建目标文件所在目录失败！");
				return false;
			}
		}
		//创建目标文件
		try {
			if (file.createNewFile()) {
				//	System.out.println("创建单个文件" + destFileName + "成功！");
				return true;
			} else {
				//System.out.println("创建单个文件" + destFileName + "失败！");
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			//	System.out.println("创建单个文件" + destFileName + "失败！" + e.getMessage());
			return false;
		}
	}

	public static void main1 (String args[]) throws Exception{

		String sentences = "1";

		//System.out.println(System.getProperty("user.dir"));
		String ROOT = "E:\\java\\apache-tomcat-7.0.77\\webapps\\JavaWeb";

		int num = RandomNum();
		Calendar now = Calendar.getInstance();
		String rootDir = ROOT+"/music/";
		String timeDir = ""+(now.get(Calendar.YEAR))  + (now.get(Calendar.MONTH) + 1)
				+(now.get(Calendar.DAY_OF_MONTH)) + "/" + (now.get(Calendar.HOUR_OF_DAY)) +"/";
		//System.out.println(rootDir+timeDir);
		File file_mk = new File(rootDir+timeDir+num+"/") ;
		mkDir(file_mk);

		List<String> listArr = new ArrayList<String>();
		String dir = ROOT+"\\data\\jyutping-wong-44100-v7\\"; //读取wav文件路径
		String data_dir = timeDir+num+"/"; //生成文件路径
		createDir(rootDir+timeDir+num);

		String localhost_dir = rootDir+timeDir+num+"/"+num+".mp3";
		ProcessCenter pc = new DefaultStreamProcess(true);
		// 建立一个语音引擎，第二个参数设置是否转换为粤语口语发音
		Engine engine = new SpeechEngine(pc, true);

		List<Object[]> list = engine.getPronounceElements(sentences);
		Iterator<Object[]> iter = list.iterator();
		while (iter.hasNext()) {
			Object[] obj = iter.next();
			if (obj[0] instanceof File) {
				for (int i = 0; i < obj.length; i++) {
					File file = (File) obj[i];
					//	System.out.print(file.getName() + " ");
					listArr.add(file.getName());
				}
			}
		}

		OutputStream osv = new FileOutputStream(localhost_dir) ;

		for(int i = 0; i<listArr.size();i++){
			//System.out.println(dir + listArr.get(i));
			execute(new File(dir + listArr.get(i)),  rootDir+data_dir+i+".mp3");
			InputStream is  = new FileInputStream(new File(rootDir+data_dir+i+".mp3"));
			byte[]bytes=new byte[128];
			int lens =0;
			int indexs = 0;
			while((lens = is.read(bytes))!=-1){
				indexs++;
				if(indexs==1){
					continue ;
				}
				osv.write(bytes, 0, lens ) ;
			}
			is.close();

		}

		File file = new File(localhost_dir);
		FileInputStream in = new FileInputStream(file);

		byte[] buf = new byte[128];
		int len = in.read(buf);

		System.out.println(buf);
		 System.out.println(new String(buf, 0, len).length());
		//	response.addHeader("Content-Length", lens + "");


		//2.获取要下载的文件名
		//         String fileName = realPath.substring(realPath.lastIndexOf("\\")+1);
		//3.设置content-disposition响应头控制浏览器以下载的形式打开文件
		//        response.setHeader("content-disposition", "attachment;filename="+fileName);

		//4.获取要下载的文件输入流
//		InputStream in = new FileInputStream(realPath);
//		int len = 0;
//		//5.创建数据缓冲区
//		byte[] buffer = new byte[1024];
//		//6.通过response对象获取OutputStream流
//		OutputStream out = response.getOutputStream();
//		//7.将FileInputStream流写入到buffer缓冲区
//		while ((len = in.read(buffer)) > 0) {
//		//8.使用OutputStream将缓冲区的数据输出到客户端浏览器
//		   out.write(buffer,0,len);
//		}


		//in.close();
		osv.flush();
		osv.close();
		//System.out.println(lens);


//			is1.close();
//			is2.close();
//			os.flush();
//			os.close() ;
//			try {
//				// 1.wav 文件放在java project 下面
//				for(int i = 0 ;i<2;i++){
//
//					String dir = "F:\\Java\\data\\jyutping-wong-44100-v7\\";
//					FileInputStream fileau = new FileInputStream(
//							dir + "cing2.wav");
//					AudioStream as = new AudioStream(fileau);
//					AudioPlayer.player.start(as);
//
//				}
//
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
	}


}




