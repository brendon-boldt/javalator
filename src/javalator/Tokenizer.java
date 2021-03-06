package javalator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.regex.Pattern;

public class Tokenizer {

	private List<String> sourceDirs;
	public final String outfilePostfix;

	public boolean nameFlag 		= false;
	public boolean typeFlag 		= false;
	public boolean operatorFlag 	= false;
	public boolean literalFlag 	 	= false;
	
	static BiPredicate<Path, BasicFileAttributes> javaMatcher = new BiPredicate<Path, BasicFileAttributes>() {
		Pattern pattern = Pattern.compile(".+\\.java$");
		public boolean test(Path p, BasicFileAttributes bfa) {
			return pattern.matcher(p.toString()).matches();
		}
	};
	
	Tokenizer(List<String> sourceDirs, String outfilePostfix) {
		this.sourceDirs = sourceDirs;
		this.outfilePostfix = outfilePostfix;
	}
	
	Tokenizer(List<String> sourceDirs) {
		this(sourceDirs, "out.txt");
	}
	
	
	void tokenizeSources() throws IOException {
		for (String dir : sourceDirs) {
			String[] pathArray = dir.split("/");
			tokenize(dir, pathArray[pathArray.length-1] + "." + this.outfilePostfix);
		}
	}
		
	private void tokenize(String sourceDir, String outfile) throws IOException {
		
		ArrayList<Object[]> pathArrays = new ArrayList<>();
//		for (String dir : sourceDirs) {
//			pathArrays.add(Files.find(Paths.get(dir),
//					Integer.MAX_VALUE, javaMatcher).toArray());
//		}
		pathArrays.add(Files.find(Paths.get(sourceDir),
				Integer.MAX_VALUE, javaMatcher).toArray());

		ArrayList<String> tokens = new ArrayList<>();
		for (Object[] paths : pathArrays) {
			for (int i = 0; i < paths.length; i++) {
				String filename = paths[i].toString();
					System.out.println("Parsing: " + filename);
				SourceAST sa = new SourceAST(filename);
				sa.operatorFlag = this.operatorFlag;
				sa.nameFlag = this.nameFlag;
				sa.typeFlag = this.typeFlag;
				sa.literalFlag = this.literalFlag;
				tokens.addAll(sa.getMethodTokens());
			}
		}
		

		System.out.println("Writing tokens to file...");
		Path path = Paths.get(outfile);
		BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("UTF-8"));
		writer.write(" ");
		for (String token : tokens) {
			if (!token.isEmpty()) {
				writer.write(token);
				writer.write(" ");
			} else {
//				writer.write("<eps>");
//				writer.write(" ");
			}
		}
		writer.close();
		System.out.println("Done");
		
	}

}
