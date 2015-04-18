package com.cloud.poly.twitter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

	public static int getFrequency(String data, String test) {
		Pattern pattern = Pattern.compile(test);
		Matcher matcher = pattern.matcher(data);
		int count = 0;
		while (matcher.find()) {
			count++;
		}
		return count;
	}

	public static Map<String, Integer> split(String input, int nGramSize,
			int windowSize) {
		Ngram ngram = new Ngram(nGramSize, windowSize);
		ngram.addWords(input.split(" "));
		// ngram.getNGramMap();
		HashMap<String, Integer> map = (HashMap<String, Integer>) ngram
				.getNGramMap();
		ValueComparator bvc = new ValueComparator(map);
		TreeMap<String, Integer> sorted_map = new TreeMap<String, Integer>(bvc);
		sorted_map.putAll(map);

		return sorted_map;
	}

	public static void main(String[] args) {
		String data = "Steve McCormack is the former head coach of Super League club Widnes. He is currently head coach of Scotland RL and Swinton His first coaching appointment had been at Salford, who made him the youngest coach in Super League at the age of just 28 in 2001 but was sacked just 10 months later. He won NL1 Coach of the Year having twice taken Whitehaven to the Grand Final but losing to Leigh (2004) and Castleford (2005) to miss out on promotion to Super League. He was appointed coach of Widnes after the club's relegation from Super League in 2005 and the subsequent departure of Frank Endacott. In his first season with Widnes he led the club to the National League One grand final where they subsequently lost to Hull Kingston Rovers. During the 2007 season McCormack won the Northern Rail Cup beating his old side Whitehaven 56-6 in the final at Blackpool. McCormack was soon after rewarded with a new 2 year contract keeping him with Widnes until the end of the 2009 season. Widnes failed to gain promotion to Super League losing out to Castleford in the 2007 NL1 Grand Final. In October 2007 Steve McCormack left Widnes and joined Hull Kingston Rovers as assistant coach to Justin Morgan. However, nine days later after leaving Widnes, Steve returned as head coach with a new set up after Steve O'Connor took over the club. In February 2009 Steve parted company with Widnes and this is thought to have happened after a poor performance against Oldham. He was taken over by assistant John Stankevitch in a caretaker role. He has named his Scotland training squad for the 2008 Rugby League World Cup.[1] On 22 October 2009 he was named as the new head coach of Barrow. McCormack resigned from Barrow at the end of the 2010 season to take up a teaching job in Wigan but wished to continue to be involved in Rugby League on a part-time basis. Therefore on 26 September 2010 it was confirmed that he had accepted the offer from Swinton to become their new head coach for the 2011 season. Steve McCormack is the son of the former rugby league footballer for Oldham, Jim McCormack.";
		
		// System.out.println(map);
		 System.out.println(split(data, 4, 4));

	}
}

class ValueComparator implements Comparator<String> {

	Map<String, Integer> base;

	public ValueComparator(Map<String, Integer> base) {
		this.base = base;
	}

	// Note: this comparator imposes orderings that are inconsistent with
	// equals.
	public int compare(String a, String b) {
		if (base.get(a) >= base.get(b)) {
			return -1;
		} else {
			return 1;
		} // returning 0 would merge keys
	}
}
