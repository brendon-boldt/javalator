package javalator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {
	
	public static void main(String[] args) throws IOException {
		ArrayList<String> sourceDirs = new ArrayList<>(Arrays.asList(
//				"/home/brendon/ht/jdk/src/share/classes/java/util/concurrent"
//				"files/"
				"/home/brendon/ht/elasticsearch/",
				"/home/brendon/ht/spring-framework/",
				"/home/brendon/ht/jdk/",
				"/home/brendon/ht/guava/"
				));
		Tokenizer t = new Tokenizer(sourceDirs, "tokens.txt");
		t.nameFlag = false;
		t.typeFlag = false;
		t.literalFlag = false;
		t.operatorFlag = false;
		t.tokenizeSources();
//		Sourcifier s = new Sourcifier("tokens.txt");
		//s.sourcifyTokens();
	}

}
