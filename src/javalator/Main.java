package javalator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {
	
	public static void main(String[] args) throws IOException {
		ArrayList<String> sourceDirs = new ArrayList<>(Arrays.asList(
//				"/home/brendon/ht/jdk/src/share/classes/java/util/concurrent",
				"files/"
//				,"/home/brendon/ht/jdk/"
				));
		Tokenizer t = new Tokenizer(sourceDirs);
		t.tokenizeSources();
		Sourcifier s = new Sourcifier("out.txt");
		s.sourcifyTokens();
	}

}
