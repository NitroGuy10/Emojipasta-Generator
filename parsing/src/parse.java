import org.json.simple.JSONObject;

import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;
import com.vdurmont.emoji.EmojiParser.FitzpatrickAction;

import java.util.HashMap;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.regex.Pattern;

public class parse
{


	public static void main (String[] args) throws FileNotFoundException
	{
		System.out.println("Hello, Emojipasta-Generator!\n");

		final File inputDirectory = new File("../reddit_data/scrapes/05-31-2021/subreddits");

		// HashMap<word, HashMap<related_emoji, occurrences>>
		HashMap<String, HashMap<String, Integer>> emojiMap = new HashMap<>();

		final long startTime = System.currentTimeMillis();
		int numDataStrings = 0;

		// parseData("Martha\\ud83d\\ude01was\\ud83e\\udd70an\\ud83d\\ude43average\\ud83d\\udc15dog.", emojiMap);
		// parseData("Martha \\ud83d\\ude01", emojiMap);
		
		for (File inputFile : inputDirectory.listFiles())
		{
			if (inputFile.getName().endsWith(".json"))
			{
				System.out.println("Parsing: " + inputFile.getName());
				Scanner reader = new Scanner(inputFile);
				String nextData = getNextData(reader);
				while (nextData != null) 
				{
					System.out.println(nextData);
					parseData(nextData, emojiMap);

					numDataStrings++;
					nextData = getNextData(reader);
				}
				reader.close();
			}
		}

		System.out.println("Parsed " + numDataStrings + " data strings in " + ((System.currentTimeMillis() - startTime) / 1000.0) + " seconds.");
		// System.out.println(emojiMap);
		
		
		
		
		
		// System.out.println(Pattern.matches("\\\\u[0-9a-fA-F]{4}", "\\uA3f7"));

		// System.out.println(EmojiParser.parseToAliases("Here is a boy: \uD83D\uDC66\uD83C\uDFFF!", FitzpatrickAction.REMOVE));

	}

	public static void parseData (String dataString, HashMap<String, HashMap<String, Integer>> map)
	{
		final Pattern UNICODE_SEQUENCE = Pattern.compile("\\\\u[0-9a-fA-F]{4}");
		Scanner reader = new Scanner(dataString);
		reader.useDelimiter("");

		String lastToken = "";
		while (reader.hasNext())
		{
			String potentialEmoji = reader.findWithinHorizon(UNICODE_SEQUENCE, 6);
			if (potentialEmoji == null)
			{
				String nextCharacter = reader.next();
				System.out.println(nextCharacter);
				if (Character.isLetterOrDigit(nextCharacter.charAt(0)) || nextCharacter.charAt(0) == '\'')
				{
					if (endsWithWhitespace(lastToken) || lastToken.endsWith("\\n"))
					{
						lastToken = nextCharacter;
					}
					else
					{
						lastToken += nextCharacter;
					}
				}
			}
			else // if (EmojiManager.isEmoji(nextCharacter))
			{
				if (!endsWithWhitespace(lastToken))
				{
					// To signify that a new token should be created after emojis are added to the HashMap
					lastToken += " ";
				}
				System.out.println("Emoji Unicode piece found: " + potentialEmoji);

				String nextCharacter = getCharFromUnicodeSequence(potentialEmoji) + "";
				String nextPotentialCharacter = reader.findWithinHorizon(UNICODE_SEQUENCE, 6);
				while (nextPotentialCharacter != null)
				{
					System.out.println("Emoji Unicode piece found: " + nextPotentialCharacter);
					nextCharacter += getCharFromUnicodeSequence(nextPotentialCharacter);
					nextPotentialCharacter = reader.findWithinHorizon(UNICODE_SEQUENCE, 6);
				}
				nextCharacter = EmojiParser.parseToUnicode(nextCharacter);
				if (EmojiManager.isEmoji(nextCharacter))
				{
					System.out.println("Emoji found: " + nextCharacter);

					String mapToken = lastToken.substring(0, lastToken.length() - 1).toLowerCase();

					if (map.containsKey(mapToken))
					{
						HashMap<String, Integer> occurrenceMap = map.get(mapToken);
						if (occurrenceMap.containsKey(nextCharacter))
						{
							occurrenceMap.put(nextCharacter, occurrenceMap.get(nextCharacter) + 1);
						}
						else
						{
							occurrenceMap.put(nextCharacter, 1);
						}
					}
					else
					{
						HashMap<String, Integer> occurrenceMap = new HashMap<>();
						occurrenceMap.put(nextCharacter, 1);
						map.put(mapToken, occurrenceMap);
					}
				}
				else
				{
					lastToken += nextCharacter;
				}
			}
		}

		reader.close();
	}

	// Get the next string of usable data from the scraper's JSON output file
	public static String getNextData (Scanner reader)
	{
		if (reader.findWithinHorizon("\"selftext\": \"", 0) == null)
		{
			return null;
		}


		String rawData = reader.nextLine();
		rawData = rawData.substring(0, rawData.length() - 2) + '\u00A7';

		reader.findWithinHorizon("\"title\": \"", 0);
		rawData += reader.nextLine();
		rawData = rawData.substring(0, rawData.length() - 2);

		// rawData will contain sequences like "\ud83d" in PLAIN TEXT characters,
		// not the actual unicode characters
		
		/*
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
		*/
		return rawData;
	}

	public static char getCharFromUnicodeSequence (String sequence)
	{
		return (char) Integer.parseInt(sequence.substring(2, 6), 16);
	}

	public static boolean endsWithWhitespace(String str)
	{
		if (str.isEmpty())
		{
			return false;
		}
		return Character.isWhitespace(str.charAt(str.length() - 1));
	}

}