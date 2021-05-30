import org.json.simple.JSONObject;
import com.vdurmont.emoji.EmojiParser;
import com.vdurmont.emoji.EmojiParser.FitzpatrickAction;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.regex.Pattern;

public class parse
{

	public static void main (String[] args) throws FileNotFoundException
	{
		System.out.println("Hello, Emojipasta-Generator!\n");
		
		Scanner reader = new Scanner(new File("../reddit_data/scrapes/05-30-2021/subreddits/emojipasta-hot-3-results.json"));
		
		System.out.println(getNextData(reader));
		System.out.println(getNextData(reader));
		System.out.println(getNextData(reader));
		System.out.println(getNextData(reader));
		
		System.out.println(Pattern.matches("\\\\u[0-9a-fA-F]{4}", "\\uA3f7"));
		
		System.out.println(EmojiParser.parseToAliases("Here is a boy: \uD83D\uDC66\uD83C\uDFFF!", FitzpatrickAction.REMOVE));
		
		
		reader.close();
	}
	
	// Get the next string of usable data from the scraper's JSON output file
	public static String getNextData (Scanner reader)
	{
		if (reader.findWithinHorizon("\"selftext\": \"", 0) == null)
		{
			return null;
		}
		
		// rawData will contain sequences like "\ud83d" in PLAIN TEXT characters,
		// not the actual unicode characters
		String rawData = reader.nextLine();
		rawData = rawData.substring(0, rawData.length() - 2);
		
		
		String fixedData = "";
		Scanner unicodeReader = new Scanner(rawData);
		final Pattern UNICODE_SEQUENCE = Pattern.compile("\\\\u[0-9a-fA-F]{4}");
		unicodeReader.useDelimiter(UNICODE_SEQUENCE);
		
		while (unicodeReader.hasNext())
		{
			fixedData += unicodeReader.next();
			String nextUnicode = unicodeReader.findWithinHorizon(UNICODE_SEQUENCE, 6);
			while (nextUnicode != null)
			{
				fixedData += getCharFromUnicodeSequence(nextUnicode);
				nextUnicode = unicodeReader.findWithinHorizon(UNICODE_SEQUENCE, 6);
			}
		}
		
		String finalUnicode = unicodeReader.findWithinHorizon(UNICODE_SEQUENCE, 0);
		while (finalUnicode != null)
		{
			fixedData += getCharFromUnicodeSequence(finalUnicode);
			finalUnicode = unicodeReader.findWithinHorizon(UNICODE_SEQUENCE, 0);
		}
		
		unicodeReader.close();
		return EmojiParser.parseToUnicode(fixedData);
	}
	
	public static char getCharFromUnicodeSequence (String sequence)
	{
		return (char) Integer.parseInt(sequence.substring(2, 6), 16);
	}

}