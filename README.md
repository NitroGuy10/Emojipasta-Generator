# Emojipasta-Generator
Surround text with relevant Emoji.

Sort of like what [u/EmojifierBot](https://www.reddit.com/user/EmojifierBot) does.

Oh yeah and this is meant for non-serious meme-related usage in case you couldn't tell.

> "red sus" ----> "red :no_entry: sus :flushed: :speak_no_evil:"

---

## How to Use

1) Use someone else's Emojipasta generator.
2) If step 1 is not good enough for you, scrape r/emojipasta with [URS](https://github.com/JosephLai241/URS).
3) Load the generated JSON file(s) into a directory and specify the directory name in the code.
4) Compile and run the program. It will read through the JSON files and create a HashMap of words and their relevant Emoji according to the data set. When prompted, enter some text and the program will surround it with Emoji.

---

## Dependencies

- [emoji-java](https://github.com/vdurmont/emoji-java)
  - [JSON-java](https://github.com/stleary/JSON-java)

---

## Things that could be added in the future

- Read through the data ONCE and generate a JSON file representing the HashMap. Then have a separate executable read that JSON file and deal with user input and Emoji surrounding.
- Factor in the number of occurances of each Emoji (already recorded in the HashMap but not used) to the random selection process for Emoji surrounding.
- Specifially add all Emoji aliases to the HashMap. (i.e., <"dog", <:dog:, 1>>)
- Remake the Emoji surrounding tool in JavaScript OR have it run on a server to turn this program into a web app.