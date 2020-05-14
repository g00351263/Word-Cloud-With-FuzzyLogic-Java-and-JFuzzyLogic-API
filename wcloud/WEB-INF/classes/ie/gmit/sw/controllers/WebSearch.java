package ie.gmit.sw.controllers;

import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.encog.bot.browse.Browser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.gargoylesoftware.htmlunit.BrowserVersion;

import org.openqa.selenium.*;

import ie.gmit.sw.ServiceHandler;

public class WebSearch {

//	public Queue queue = new PriorityQueue(Comparator.comparing(classname:method));
	public List list = new ArrayList();

	// Multithreading with 10 threads.
	public ExecutorService executor = Executors.newFixedThreadPool(10);
	public static Set<String> set = new HashSet<String>();
	public static String searchString = "";

	public void start(String inputString, String option) {
		searchString = inputString;

		Document document;
		try {
			String url = "";
			if (option.equals("Duck Duck Go")) {
				url = "https://duckduckgo.com/?q=" + inputString;
				WebDriver driver = new HtmlUnitDriver();
				driver.get(url);
				List<WebElement> list_ = driver.findElements(By.xpath("//*[@href or @src]"));

				for (WebElement e : list_) {
					try {
						String link = e.getAttribute("href");
						if (null == link)
							link = e.getAttribute("src");

//						System.out.println(e.getTagName() + "=" + link);
						list.add(link);
					} catch (Exception e_) {

					}
				}
			}

			else {
				if (option.equals("Bing"))
					url = "https://www.bing.com/search?q=test" + inputString;
				else

					url = "\r\n" + "https://www.google.com/search?sxsrf"
							+ "=ALeKk03A5u1ku0P9CnwfdAUM2Isexk9MVQ%3A1585815441748&source=hp&ei=kZ-FXqfJK5Hf9QP8xqmoBw&q="
							+ inputString + "&oq=" + inputString
							+ "&gs_lcp=CgZwc3ktYWIQAzIECCMQJzIECCMQJzIFCAAQkQIyBQgAEJECMgQIABBDMgIIADIFCAAQgwEyAggAMgIIADICCAA6CAgAEIM"
							+ "BEJECUIYIWKgLYKUPaAFwAHgAgAGuAYgB-QSSAQMwLjSYAQCgAQGqAQdnd3Mtd2l6&sclient=psy-ab&ved=0ahUKEwjn1dTwpsnoAhWRb30KHXxjCnUQ4dUDCAY&uact=5";
				document = Jsoup.connect(url).get();
//			String link = document.getElementsByClass("LC20lb DKV0Md").get(0).toString();
				readDoc(document);
				String title = document.title();
			}
			test();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void readDoc(Document doc) {

		Elements elements = doc.getElementsByAttribute("href");
		for (Element element : elements) {
			try {
				String a = element.absUrl("href");
//			System.out.println(a);
				list.add(a);
			} catch (Exception e) {

				System.err.println("error");
			}

		}

	}

	public void test() {
		try {

			for (Object object : list) {
				String link = object.toString();
				executor.execute(new DocumentReader(link));
			}
			executor.shutdown();
			try {
				executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Map<String, Integer> m = DocumentReader.map;

//			FileWriter writer = new FileWriter("fcl\\read.csv");
//			
//			m.forEach((k, v) -> {
//				try {
//					writer.write(v + " , " + k +"\r\n");
////					writer.write();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			});
//			writer.close();

//			f.load(m)
//		executor.execute(new DocumentReader());
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	public void readChildDoc(Document doc) {

	}

}
