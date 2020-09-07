package com.chinalife.clerk.utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import javax.imageio.ImageIO;

//此类用于生成验证码

public class ImageCode {
	//w为宽度，h为长度
	private int w = 80;
	private int h = 50;
 	private Random r = new Random();
 	//将字体名称存储在字符串数组中
	private String[] fontNames  = {"宋体", "华文楷体", "黑体", "华文新魏", "华文隶书", "微软雅黑", "楷体_GB2312"};
	//将验证码可能出现的内容存放在字符串中，排除了较为相似的数字和字母，如：0和O
	private String codes  = "23456789abcdefghijkmnpqrstuvwxyzABCDEFGHIJKLMNPQRSTUVWXYZ";
	//设置背景颜色（白色）和文字内容
	private Color bgColor  = new Color(255, 255, 255);
	private String text ;
	
	//此方法用于随机生成颜色，数值在0~255之间
	private Color randomColor () {
		int red = r.nextInt(256);
		int green = r.nextInt(256);
		int blue = r.nextInt(256);
		return new Color(red, green, blue);
	}
	
	/*
	 * 此方法用于随机设置字体，通过获取一个随机数来表示数组的下标
	 * 通过随机数设置字体样式和字体大小（字体风格实际都是通过数字来选取）
	 * 0（无样式），1（粗体），2（斜体），3（粗体+斜体）
	 */
	private Font randomFont () {
		int index = r.nextInt(fontNames.length);
		String fontName = fontNames[index];
		int style = r.nextInt(4);
		int size = r.nextInt(5) + 24; 
		return new Font(fontName, style, size);
	}
	
	/*
	 * 此方法用于添加干扰线，总共添加四条 
	 * x1,x2,y1,y2分别为起始坐标和终点坐标
	 */
	private void drawLine (BufferedImage image) {
		int num  = 5;
		Graphics2D g2 = (Graphics2D)image.getGraphics();
		for(int i = 0; i < num; i++) {
			int x1 = r.nextInt(w);
			int y1 = r.nextInt(h);
			int x2 = r.nextInt(w);
			int y2 = r.nextInt(h); 
			g2.setStroke(new BasicStroke(1.5F)); 
			g2.setColor(Color.BLUE); 
			g2.drawLine(x1, y1, x2, y2);
		}
	}
	
	//从字符串中随机返回一个字符
	private char randomChar () {
		int index = r.nextInt(codes.length());
		return codes.charAt(index);
	}
	
	private BufferedImage createImage () {
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB); 
		Graphics2D g2 = (Graphics2D)image.getGraphics(); 
		g2.setColor(this.bgColor);
		g2.fillRect(0, 0, w, h);
 		return image;
	}
	
	//直接调用此方法得到验证码
	public BufferedImage getImage () {

		BufferedImage image = createImage(); //创建图片缓冲区
		//得到绘制环境
		Graphics2D g2 = (Graphics2D)image.getGraphics();
		StringBuilder sb = new StringBuilder();//用来装载生成的验证码文本
		// 向图片中添加4个字符
		for(int i = 0; i < 4; i++)  {
			//随机生成一个字符并且将其变为字符串
			String s = randomChar() + ""; 
			sb.append(s); 
			/*
			 *  x为横坐标，w为验证码宽度，每循环一次x的坐标向右移动四分之一的宽度
			 *  通过randomFont和randomColor方法设置字体和颜色
			 */
			float x = i * 1.0F * w / 4; 
			g2.setFont(randomFont()); 
			g2.setColor(randomColor()); 
			g2.drawString(s, x, h);
		}
		/*
		 * 将真正的验证码内容保存在名为text的字符串中
		 * 调用drawLine方法在图片缓冲区image中画线
		 * 返回image 
		 */
		this.text = sb.toString(); 
		//drawLine(image);
		return image;		
	}
	
	//返回验证码的真实内容
	public String getText () {
		return text;
	}
	
	//保存图片到指定的输出流
	public static void output (BufferedImage image, OutputStream out) 
				throws IOException {
		ImageIO.write(image, "PNG", out);
	}
}
