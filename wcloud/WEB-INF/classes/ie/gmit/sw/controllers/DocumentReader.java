package ie.gmit.sw.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ie.gmit.sw.ServiceHandler;
import info.debatty.java.stringsimilarity.Jaccard;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.rule.Variable;

public class DocumentReader implements Runnable {
	static int c = 0;

	private String link;
	static int count = 0;

	private static final int TitleWeight = 50;
	private static final int headingWeight = 20;
	private static final int paraWeight = 8;
	public static Map<String, Integer> map = new ConcurrentHashMap<String, Integer>();
	private static int i = 0;
//	public static Map<String, Integer> dummymap = new ConcurrentHashMap<String, Integer>();

	public DocumentReader(String link) {
//		super();
		this.link = link;
	}

	public void run() {

//		System.out.println(link + "--------------------" + count++);
		try {
			Map<String, Integer> dummyMap = new ConcurrentHashMap<String, Integer>();
			Document doc = Jsoup.connect(link).get();
			Elements PElements = null;
			Elements h1Elements = doc.select("h1,h2,h3,h4,h5,h6");
			Elements ParaElements = doc.select("body");
			if (ParaElements.size() > 0)
				PElements = ParaElements.select("p");

			Elements titleElements = doc.select("title");

			int HeadingTotalWeight = heuristicSearch(h1Elements, dummyMap) * headingWeight;
			int ParagrapghTotalWeight = heuristicSearch(PElements, dummyMap) * paraWeight;
			int TitleTotalWeight = heuristicSearch(titleElements, dummyMap) * TitleWeight;
			Elements[] elements_ = { h1Elements, PElements, titleElements };

			int fuzzyScore = fuzzyScore(HeadingTotalWeight, ParagrapghTotalWeight, TitleTotalWeight);
			if (fuzzyScore >= 14)
//				index(elements_);
				index(dummyMap);

//			System.out.println(FuzzySearch.ratio("Test", "Test"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void index(Map<String, Integer> dummyMap) {
//		headingTotal = headingTotal / headingWeight;
//		paragraphTotal = paragraphTotal / paraWeight;
//		titleTotal = titleTotal / TitleWeight;

		dummyMap.forEach((key, value) -> {
			int previousValue = map.containsKey(key) ? map.get(key) : 0;
			map.put(key, previousValue + value);
		});

	}

	public int fuzzyScore(int HeadingTotalWeight, int ParagraphTotalWeight, int TitleTotalWeight) {

		// Limiting the values to their MAX
		if (HeadingTotalWeight > 999)
			HeadingTotalWeight = 999;
		if (ParagraphTotalWeight > 999)
			ParagraphTotalWeight = 999;
		if (TitleTotalWeight > 999)
			TitleTotalWeight = 999;
		FIS fis = null;
		String fileName = ServiceHandler.tipperFile.getAbsolutePath();
		fis = FIS.load(fileName, true);
		// Error while loading?
		if (fis == null) {
			System.err.println("Can't load file: '" + fileName + "'");
			return 0;
		}
//		System.out.println(TitleTotalWeight + "   " + HeadingTotalWeight + "    " + ParagraphTotalWeight);
		fis.setVariable("title", TitleTotalWeight);
		fis.setVariable("heading", HeadingTotalWeight);
		fis.setVariable("body", ParagraphTotalWeight);

		// Evaluate
		fis.evaluate();
		FunctionBlock functionBlock = fis.getFunctionBlock(null);
		Variable score = functionBlock.getVariable("score");
//		System.out.println("Output value:" + fis.getVariable("score").getValue());

		return (int) fis.getVariable("score").getValue();
	}

	/*
	 * This method is used to calculate the matching elemnts from set and set them
	 * into a dummy Hash Map......
	 */

	public int heuristicSearch(Elements elements, Map<String, Integer> dummyMap) {
		double match = 0.0;
		int count = 0;
		String[] textList;
		if (!(elements.size() > 0))
			return 0;
		else
			for (Element element : elements) { // for Each loop for getting Element from Elements
				try {
					String text = element.text();
					textList = text.split(" "); // Splitting the text into array of Text Strings based on space.
					for (String string : textList) {
//						for (String setString : WebSearch.set) {
						/*
						 * for Each loop each word in the Set defined for the items to be searched.
						 */

						/*
						 * calculating the matching percent among the String from Set and the String
						 * from the textListArray
						 */
//							match = FuzzySearch.ratio(setString, string);
						match = matchingFactor(string, WebSearch.searchString);

//						System.out.println(match);
						if (match > 0.75) {
							count++;

						}
						if (dummyMap.containsKey(string)) {
//								int times = dummyMap.get(string);
							int occurrence = dummyMap.containsKey(string) ? dummyMap.get(string) : 0;
							dummyMap.put(string, occurrence + 1);
						} else {
							dummyMap.put(string, 1);
						}

//						}

					}
				} catch (Exception e) {
					System.err.println(e);
				}
			}

		System.out.println("count-------------------------" + count);
		return count;
	}

	private double matchingFactor(String setString, String string) {
		// TODO Auto-generated method stub

		Jaccard j = new Jaccard();
		try {
			return j.similarity(setString, string);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;

	}

//	public void index(Elements[] elements_) {
//		int match = 0;
//		int count = 0;
//		String[] textList;
//		for (Elements elements : elements_) {
//
//			if (!(elements.size() > 0))
//				continue;
//			else
//				for (Element element : elements) {
//					try {
//						String text = element.text();
//						textList = text.split(" ");
//						for (String string : textList) {
//
//							for (String setString : WebSearch.set) {
//
//								match = FuzzySearch.ratio(setString, string);
////							System.out.println(match);
//								if (match > 50) {
//									int m = map.containsKey(setString) ? map.get(setString) : 0;
//									map.put(setString, m + 1);
//								}
//
//							}
//						}
//					} catch (Exception e) {
//						System.err.println(e);
//					}
//				}
//		}
//
//	}

}
