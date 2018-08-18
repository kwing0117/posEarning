package com.kwing.tools.posEarning;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.csvreader.CsvReader;
import com.kwing.tools.exception.TimeException;

/**
 *
 */
public class App {
	public static void main(String[] args) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		//1.参数获取
		double rate = 0d;//年化利率
		String csvPath = null;//csv路径
		Date date = new Date();//日期
		String isHead = "1";
		if(args.length == 0){
			System.out.println("输入参数格式不对");
			System.out.println("请输入：rate csvPath date isHead");
			return;
		}
		if(args.length == 1 && "help".equals(args[0])){
			System.out.println("需要输入两个参数：rate、csvPath、date、isHead");
			System.out.println("           rate:年化收益率 如输入12代表年化收益率为12%");
			System.out.println("           csvPath:为csv文件路径，csv中第一列为存入日期，第二列为存入金额");
			System.out.println("           date:计算利息截止当前时间，如果不输入默认为当前系统日期");
			System.out.println("           isHead:是否有表头，默认有,1表示有，其他标识无");
			return;
		}else if(args.length >= 2){
			try {
				rate = Double.parseDouble(args[0]);
			} catch (NumberFormatException e) {
				System.out.println("输入参数格式不对");
				return;
			}
			csvPath = args[1];
			if(args.length>=3){
				try {
					date = sdf.parse(args[2]);
				} catch (ParseException e) {
					System.out.println("第三个参数无法转化为日期类型");
					return ;
				}
			}else{
				date = new Date();
			}
			if(args.length>=4){
				isHead = args[3];
			}
			System.out.println("年化收益率为："+rate+"%");
			System.out.println("存入数据文件路径为："+csvPath);
			System.out.println("利息收益截止当前日期："+sdf.format(date));
		}
		//2.解析csv文件
		try {
			CsvReader csvReader = new CsvReader(csvPath,',',Charset.forName("UTF-8"));
			//2.1读取表头
			if("1".equals(isHead)){
				if(csvReader.readRecord()){
					//2.1.1读取表头内容
					String[] headValue = csvReader.getValues();
					System.out.println(headValue.toString());
				}
			}
			double sumIntr = 0d;
			double sumCap = 0d;
			//2.2读取内容
			while(csvReader.readRecord()){
				String[] contentValue = csvReader.getValues();
				//2.2.1计算收益
				sumCap += Double.parseDouble(contentValue[1]);
				sumIntr += getInterest(sdf.parse(contentValue[0]), Double.parseDouble(contentValue[1]), date, rate);
			}
			System.out.println("总存入本金总和："+sumCap);
			System.out.println("总收益总和："+sumIntr);
			System.out.println("本金+利息："+sumCap+sumIntr);
		} catch (FileNotFoundException e) {
			System.out.println("文件不存在："+csvPath);
		}catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (TimeException e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * 年化基数按365天计算
	 * @param startDate 开始日期
	 * @param amt 存入金额
	 * @param enddate 结束日期
	 * @param rate 年化利率
	 * @return
	 * @throws Exception 
	 */
	public static double getInterest(Date startDate,double amt,Date enddate,double rate) throws TimeException{
		long endTime = enddate.getTime();
		long startTime = startDate.getTime();
		if(endTime<startTime){
			throw new TimeException("结束时间不能小于开始时间");
		}
		//24*60*60*1000=86400000
		int days = (int)((endTime-startTime)/86400000);
		
		double intr = amt*days*rate/100/365; 
		System.out.println("存入日期："+startDate+"，存入金额："+amt+"，到期日期："+enddate+"，到期收益："+intr);
		return intr;
	}
}
