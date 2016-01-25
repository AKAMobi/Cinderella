package com.qq;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.json.JSONStringer;

import com.jspsmart.upload.SmartUpload;
import com.jspsmart.upload.SmartUploadException;

import java.util.ArrayList;
import java.util.Map;


/**
 * Servlet implementation class GetInfo
 */
public class GetInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetInfo() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		String str="please use post method";
		response.getOutputStream().write(str.toString().getBytes("UTF-8"));  		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8");
		//PrintWriter writer = response.getWriter();
		String sender=URLDecoder.decode(request.getHeader("sender"), "utf-8") ;
		String reciever=URLDecoder.decode(request.getHeader("reciever"), "utf-8"); 
        String sendtime=URLDecoder.decode(request.getHeader("sendtime"), "utf-8");
        String subject=URLDecoder.decode(request.getHeader("subject"), "utf-8");       
        String body=URLDecoder.decode(request.getHeader("body"), "utf-8");
        
        ArrayList<String> attachTemp = new ArrayList<String> ();
        
        String realPath = getServletContext().getRealPath("/") + "RecFile";
		System.out.println(realPath);
		File fileupload = new File(realPath);
		if(!fileupload.exists()){
			fileupload.mkdir();
		}
		SmartUpload su=new SmartUpload();
		su.initialize(getServletConfig(), request, response);
		try 
		{
			su.upload();
			int count=su.save(realPath);
			for(int i=0;i<su.getFiles().getCount();i++)
			{
				com.jspsmart.upload.File tempFile=su.getFiles().getFile(i);
				System.out.println(realPath+"\\"+tempFile.getFileName());
				attachTemp.add(realPath+"\\"+tempFile.getFileName());
			}
			System.out.println("count:"+count);
		} 
		catch (SmartUploadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
		EmailInput email=new EmailInput();
		BotResult botResult=new BotResult();
		String Raw_filename="D:\\iBotest\\bizplan1.txt";
		String Abstract_filename="D:\\iBotest\\abstract.txt";
		
		email.setSender(sender);
		email.setReciever(reciever);
		email.setSendTime(sendtime);
		email.setEmailSubject(subject);
		email.setEmailBody(body);
		email.setEmailAttach(attachTemp);
		
		MailTxtInput(Raw_filename,email);		
		
		Txt2Abstract(Raw_filename,Abstract_filename);		

		botResult=Abstract2Meta(Abstract_filename);

		botResult=Meta2Subness(botResult);
		
		iBotOutput(botResult,response);
	}
	
	public void MailTxtInput(String fileName,EmailInput email)
	{
		String sendInfo=email.getSender();
		String recieveInfo=email.getReciever();
		String timeInfo=email.getSendTime();
		String subjectInfo=email.getEmailSubject();
		String bodyInfo=email.getEmailBody();
		ArrayList<String> appendInfo = email.getEmailAttach();
		Tika tika = new Tika();
		String text = "";
		if(appendInfo.get(0).equals("null"))
		{
			text="您没有指定任何附件";
			System.out.println(text);
		}
		else
		{
			try 
			{	
				for(int i=0;i<appendInfo.size();i++)
				{
					File file=new File(appendInfo.get(i));
					if(!file.exists())  
					{
						text = text+"您指定的第"+(i+1)+"个附件不存在!\n";
						System.out.println(text);
					}
					else
					{
						text = text+"\r\n"+"\r\n"+"附件"+(i+1)+"内容:"+"\r\n"+tika.parseToString(file);
					}					
				}
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			} 
			catch (TikaException e) 
			{
				e.printStackTrace();
			}			
		}
		try
		{
			String filename=fileName;	
			File file = new File(filename);
		    if(!file.exists()) 
		    {
			    file.createNewFile();
		    }
		    FileOutputStream fw = new FileOutputStream(file.getAbsoluteFile());
		    OutputStreamWriter bw = new OutputStreamWriter(fw, "UTF-8");
		    bw.write("发件人："+sendInfo+"\r\n");
		    bw.write("收件人："+recieveInfo+"\r\n"); 
		    bw.write("发件时间："+timeInfo+"\r\n");
		    bw.write("邮件主题："+subjectInfo+"\r\n");
		    bw.write("邮件正文："+bodyInfo+"\r\n");
		    bw.write("附件内容："+"\r\n"+text);
		    bw.close();
	
		    System.out.println("Done");  
		} 
		catch(IOException e) 
		{
			e.printStackTrace();
		}			
		
	}
	public void Txt2Abstract(String infileName,String outfilename) 
	{
		//摘要获取模块
		//输入为txt语料地址和摘要存储地址，无返回值
	}
	public BotResult Abstract2Meta(String filename)
	{
		//关键属性获取模块
		//输入为摘要存放地址，返回值为BotResult对象
		
		String location="北京";
		int financeLimit=30000;
		String tranStock="20%";
		String projectName="农夫之家";
		String companyName="北京市农业科技有限公司";
		String founderName="张三";
		String bizArea="农业";
		
		BotResult bot=new BotResult();
		bot.setLocation(location);
		bot.setFinanceLimit(financeLimit);
		bot.setTranStock(tranStock);
		bot.setProjectName(projectName);
		bot.setCompanyName(companyName);
		bot.setFounderName(founderName);
		bot.setBizArea(bizArea);
		bot.setSubness(0);
		
		return bot;
	}
	public BotResult Meta2Subness(BotResult bot)
	{
		//置信度获取模块
		//输入为BotResult对象，输出为置信度subness
		double subness=0.5;
		bot.setSubness(subness);
		return bot;
	}
	public void iBotOutput(BotResult bot,HttpServletResponse response)
	{
		response.reset();
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/json; charset=UTF-8"); 
		JSONStringer stringer = new JSONStringer();  

		try {
			 stringer.object().key("地点").value(bot.getLocation()).  
		        key("项目名称").value(bot.getProjectName()).  
		        key("公司名称").value(bot.getCompanyName()).  
		        key("成立者").value(bot.getFounderName()).
		        key("融资额度").value(bot.getFinanceLimit()).
		        key("出让股权").value(bot.getTranStock()).
		        key("行业").value(bot.getBizArea()).
		        key("置信程度").value(bot.getSubness()).endObject(); 
				System.out.println("---------------输出ing---------------------");
			response.getOutputStream().write(stringer.toString().getBytes("UTF-8"));  			
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
