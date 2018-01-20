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
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public class Music extends HttpServlet {


	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 设置响应内容类型

		 //response.setHeader("Content-type", "text/html; charset=UTF-8");
		 request.setCharacterEncoding("UTF-8");//解决乱码
	     response.setContentType("text/html;charset=UTF-8");//解决乱码
		 String sentences = request.getParameter("text");

		try {
			sayPlay(sentences,request,response);
		}catch (Exception e){
			System.out.printf(e.getMessage());
		}

	}

	public static void sayPlay (String sentences,HttpServletRequest request,HttpServletResponse response) throws Exception{
        //获取tomcat 路径
		String ROOT = System.getProperty("catalina.home")+"/webapps/JavaWeb";

		int num = RandomNum();
		Calendar now = Calendar.getInstance();
		String rootDir = ROOT+"/music/";
		//String timeDir = ""+(now.get(Calendar.YEAR))  + (now.get(Calendar.MONTH) + 1) +(now.get(Calendar.DAY_OF_MONTH)) + "/" + (now.get(Calendar.HOUR_OF_DAY)) +"/";
		//System.out.println(rootDir+timeDir);
		//File file_mk = new File(rootDir+timeDir+num+"/");

		List<String> listArr = new ArrayList<String>();
		String dir = ROOT+"/data/jyutping-wong-44100-v7/"; //读取wav文件路径
		//String data_dir = timeDir+num+"/"; //生成文件路径
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
				 	  	System.out.print(file.getName() + " ");
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

		//定义最终保存的文件名

		try {
			//以当前的时间命名录音的名字
			//将录音的文件存放到F盘下语音文件夹下

			//将录音产生的wav文件转换为容量较小的mp3格式
			//定义产生后文件名

			Runtime run = null;

			try {
				run = Runtime.getRuntime();

				//调用解码器来将wav文件转换为mp3文件
				Process p=run.exec("lame -b 16 "+localhost_dir + " "+ rootDir+num+".mp3"); //16为码率，可自行修改

				p.waitFor();
				//释放进程
				p.getOutputStream().close();
				p.getInputStream().close();
				p.getErrorStream().close();

				localhost_dir = rootDir+num+".mp3";
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				//最后都要执行的语句
				//run调用lame解码器最后释放内存
				run.freeMemory();
			}

		} catch (Exception e) {
			e.printStackTrace();
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
		//删除文件夹,只能按路径删除
		deleteFile(rootDir+num+".wav");
		deleteFile(rootDir+num+".mp3");

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

	public static void main (String args[]) throws Exception{

			//获取tomcat 路径
		    String ROOT = "E:\\java\\apache-tomcat-7.0.77";
            String sentences = "阿法第三方";
			int num = RandomNum();
			Calendar now = Calendar.getInstance();
			String rootDir = ROOT+"/webapps/JavaWeb/music/";
			//String timeDir = ""+(now.get(Calendar.YEAR))  + (now.get(Calendar.MONTH) + 1) +(now.get(Calendar.DAY_OF_MONTH)) + "/" + (now.get(Calendar.HOUR_OF_DAY)) +"/";
			//System.out.println(rootDir+timeDir);
			//File file_mk = new File(rootDir+timeDir+num+"/");


			List<String> listArr = new ArrayList<String>();
			String dir = ROOT+"/webapps/JavaWeb/data/jyutping-wong-44100-v7/"; //读取wav文件路径
			//String data_dir = timeDir+num+"/"; //生成文件路径
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



		System.out.println(localhost_dir);

//			//输出 wav IO流
//			try{
//				response.setHeader("Content-Type", "audio/mpeg");
//				File file = new File(localhost_dir);
//				int len_l = (int) file.length();
//				byte[] buf = new byte[2048];
//				FileInputStream fis = new FileInputStream(file);
//				OutputStream out = response.getOutputStream();
//				len_l = fis.read(buf);
//				while (len_l != -1) {
//					out.write(buf, 0, len_l);
//					len_l = fis.read(buf);
//				}
//				out.flush();
//				out.close();
//				fis.close();
//			}catch (Exception e){
//				System.out.println(e);
//			}
			//response.setHeader("Content-Length",len_l+"");
			//删除文件夹,只能按路径删除
		//	deleteFile(rootDir+num+".wav");

		}

	//保存录音
//	public  void save(String localhost_dir)
//	{
//
//		//取得录音输入流
//		af = getAudioFormat();
//
//		byte audioData[] = baos.toByteArray();
//		bais = new ByteArrayInputStream(audioData);
//		ais = new AudioInputStream(bais,af, audioData.length / af.getFrameSize());
//		//定义最终保存的文件名
//		File file = null;
//		//写入文件
//		try {
//			//以当前的时间命名录音的名字
//			//将录音的文件存放到F盘下语音文件夹下
//			File filePath = new File("F:/语音文件");
//			if(!filePath.exists())
//			{//如果文件不存在，则创建该目录
//				filePath.mkdir();
//			}
//			long time = System.currentTimeMillis();
//			file = new File(filePath+"/"+time+".wav");
//			AudioSystem.write(ais, AudioFileFormat.Type.WAVE, file);
//			//将录音产生的wav文件转换为容量较小的mp3格式
//			//定义产生后文件名
//			String tarFileName = time+".mp3";
//			Runtime run = null;
//
//			try {
//				run = Runtime.getRuntime();
//				long start=System.currentTimeMillis();
//				//调用解码器来将wav文件转换为mp3文件
//				Process p=run.exec(filePath+"/"+"lame -b 16 "+filePath+"/"+file.getName()+" "+filePath+"/"+tarFileName); //16为码率，可自行修改
//				//释放进程
//				p.getOutputStream().close();
//				p.getInputStream().close();
//				p.getErrorStream().close();
//				p.waitFor();
//				long end=System.currentTimeMillis();
//				System.out.println("convert need costs:"+(end-start)+"ms");
//				//删除无用的wav文件
//				if(file.exists())
//				{
//					file.delete();
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}finally{
//				//最后都要执行的语句
//				//run调用lame解码器最后释放内存
//				run.freeMemory();
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally{
//			//关闭流
//			try {
//
//				if(bais != null)
//				{
//					bais.close();
//				}
//				if(ais != null)
//				{
//					ais.close();
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}




	public AudioFormat getAudioFormat()
	{
		//下面注释部分是另外一种音频格式，两者都可以
		AudioFormat.Encoding encoding = AudioFormat.Encoding.
				PCM_SIGNED ;
		float rate = 8000f;
		int sampleSize = 16;
		String signedString = "signed";
		boolean bigEndian = true;
		int channels = 1;
		return new AudioFormat(encoding, rate, sampleSize, channels,
				(sampleSize / 8) * channels, rate, bigEndian);
//		//采样率是每秒播放和录制的样本数
//		float sampleRate = 16000.0F;
//		// 采样率8000,11025,16000,22050,44100
//		//sampleSizeInBits表示每个具有此格式的声音样本中的位数
//		int sampleSizeInBits = 16;
//		// 8,16
//		int channels = 1;
//		// 单声道为1，立体声为2
//		boolean signed = true;
//		// true,false
//		boolean bigEndian = true;
//		// true,false
//		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,bigEndian);
	}


}




