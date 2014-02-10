import java.util.ArrayList;
import java.lang.*;


public class WordSearch {
	
	private static int boardSize = 0;
	private static Boolean[][] visited;
	public static TST<Integer> gameTrie;
	private static TST<Integer> dictTrie;
	public static int gameTrieSize = 0;
	private static int foundWords = 0;
	private static int numberWordsFound = 0;
	public static String boardFile;
	public static String dictFile;
	private static int numCols = 6;
	public static Boolean correctArgs = false;
	private static TST<Integer> userWords;
	
	public static void main(String[] args) {
		String boardFile;
		String dictFile;
		int numCols;
		Boolean correctArgs = false;
		
		while (correctArgs == false) {
			correctArgs = readCommandLineInput(args);
		}
		StdOut.println("Welcome to the Word Search! Type -help for instructions.");
		
		//Read in dictionary
		Bag<String> dictBag = readDictionary();
		
		//Add dictionary to TST
		dictTrie = addDictToTST(dictBag);
		
		//read in gameboard
		String[][] gameBoard = readBoard();
		
		//find words in gameboard
		gameTrie = new TST<Integer>();
		visited = new Boolean[boardSize][boardSize];
		searchBoard(gameBoard);
		
		//allow user to play game
		userWords = new TST<Integer>();
		String answer = "Y";
		while (answer.equals("Y") || answer.equals("y")) {
			foundWords = 0;
			getUserInput();
		
			listWords();
		
			StdOut.println("\nPlay again? [Y/N]");
			answer = StdIn.readLine();
		}
	}
	
	//Method to read in dictionary
	private static Bag<String> readDictionary() {
		Stopwatch dictWatch = new Stopwatch();    //stopwatch for reading in dictionary
		In dictIn = new In(dictFile);				// read in dictionary
		Bag<String> dictBag = new Bag<String>();
		while (!dictIn.isEmpty()) {
			dictBag.add(dictIn.readString());
		}
		StdOut.println("Reading dictionary file (" + dictBag.size() + " words): " + dictWatch.elapsedTime() + " seconds");
		return dictBag;
	}
	
	//Method to add dictionary to TST
	private static TST<Integer> addDictToTST(Bag<String> dictBag) {
		Stopwatch trieWatch = new Stopwatch(); 			//stopwatch for adding dictionary to trie
		TST<Integer> dictTrie = new TST<Integer>();   //create trie for dictionary
		
		//Add dictionary to trie
		int i = 0;
		for (String s : dictBag) {
			dictTrie.put(s, i);
			i++;
		}
		StdOut.println("Putting dictionary into TST: " + trieWatch.elapsedTime() + " seconds");
		
		/*for (String x : dictTrie.keys()) {
			StdOut.println(x);
		} */
		return dictTrie; 
	}
	
	//Method to read in Gameboard
	private static String[][] readBoard() {
		In boardIn = new In(boardFile);
		
		boardSize = boardIn.readInt();
		
		StdOut.println("\n~~Gameboard~~");
		
		String inChar;
		String[][] boardArray = new String[boardSize][boardSize];
		for(int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				inChar = boardIn.readString();
				boardArray[i][j] = inChar;
				StdOut.print(boardArray[i][j].toString() + " ");
			}
			StdOut.println();           //prints board
		}
		
		return boardArray;
	}
	
	//Method to search board
	private static void searchBoard(String[][] board) {
		for(int i = 0; i < boardSize; i++){
			for(int j = 0; j < boardSize; j++){
				visited[i][j] = false;
			}
		}
		
		Stopwatch findWordsWatch = new Stopwatch();
		for(int i = 0; i < boardSize; i++){
			for(int j = 0; j < boardSize; j++){
				Stack<String> stack1 =  new Stack<String>();
				stack1.push(board[i][j]);
				findRightWords(board, i, j, stack1);
				
				Stack<String> stack2 =  new Stack<String>();
				stack2.push(board[i][j]);
				findLeftWords(board, i, j, stack2);
				
				Stack<String> stack3 =  new Stack<String>();
				stack3.push(board[i][j]);
				findUpWords(board, i, j, stack3);
				
				Stack<String> stack4 =  new Stack<String>();
				stack4.push(board[i][j]);
				findDownWords(board, i, j, stack4);
				
				Stack<String> stack5 =  new Stack<String>();
				stack5.push(board[i][j]);
				findUpRightWords(board, i, j, stack5);
				
				Stack<String> stack6 =  new Stack<String>();
				stack6.push(board[i][j]);
				findUpLeftWords(board, i, j, stack6);
				
				Stack<String> stack7 =  new Stack<String>();
				stack7.push(board[i][j]);
				findDownRightWords(board, i, j, stack7);
				
				Stack<String> stack8 =  new Stack<String>();
				stack8.push(board[i][j]);
				findDownLeftWords(board, i, j, stack8);
			}
		}
		StdOut.println("\nTime to find all words in board: " + findWordsWatch.elapsedTime() + " seconds.");
		/*for (String s : gameTrie.keys()) {
			StdOut.println(s);
		}*/
	}
	
	//Method to find all right words in board
	private static void findRightWords(String[][] board, int i, int j, Stack<String> stack) {
		
		visited[i][j] = true;             //mark index (letter) as visited
		String s = stack.toString().replaceAll("\\s+", "").toLowerCase(); //Set to string to compare to Dictionaries
		s = reverseString(s);
		//StdOut.println(s);
		if(dictTrie.prefixMatch(s).toString().length() != 0) {
			if(s.length() >= 4){ //Check if prefix exists
				if(dictTrie.contains(s)) { 
					if(!gameTrie.contains(s)) { //Add word to game Trie
						gameTrie.put(s, numberWordsFound); 
						numberWordsFound++; 					
					}
				}
			}
		} else if (s.contains("*")){
			String wildWordToCheck = s.replace("*", ".");
			//StdOut.println("wildWordToCheck is" + wildWordToCheck);
			for (String string : dictTrie.keys()) {
				if (string.length() >= 4 && string.matches(wildWordToCheck.toLowerCase())) {
					//StdOut.println(string);
					gameTrie.put(string, gameTrieSize);
					gameTrieSize++;
				}
			}
		}else{
			stack.pop(); //Pop if not prefix or wildcard
			visited[i][j] = false; 
			return; 
		}
		
		//Check next letter
		if((j+1) < board.length) { //Right
			if(!visited[i][j+1]) {
				stack.push(board[i][j+1]); 
				findRightWords(board, i, j+1 , stack);
		}}	
		 
		stack.pop(); 
		visited[i][j] = false; 
		
	}
	
	//Method to find left words on board
	private static void findLeftWords(String[][] board, int i, int j, Stack<String> stack) {
		
		visited[i][j] = true;             //mark index (letter) as visited
		String s = stack.toString().replaceAll("\\s+", "").toLowerCase(); //Set to string to compare to Dictionaries
		s = reverseString(s);
		//StdOut.println(s);
		if(dictTrie.prefixMatch(s).toString().length() != 0) {
			if(s.length() >= 4){ //Check if prefix exists
				if(dictTrie.contains(s)) { 
					if(!gameTrie.contains(s)) { //Add word to game Trie
						gameTrie.put(s, numberWordsFound); 
						numberWordsFound++; 					
					}
				}
			}
		}else if (s.contains("*")){
			String wildWordToCheck = s.replace("*", ".");
			//StdOut.println("wildWordToCheck is" + wildWordToCheck);
			for (String string : dictTrie.keys()) {
				if (string.length() >= 4 && string.matches(wildWordToCheck.toLowerCase())) {
					//StdOut.println(string);
					gameTrie.put(string, gameTrieSize);
					gameTrieSize++;
				}
			}
		} else{
			stack.pop(); //Pop if not prefix or wildcard
			visited[i][j] = false; 
			return; 
		}
		
		if((j-1) > (-1)) { //Left
			if(!visited[i][j-1]) {
				stack.push(board[i][j-1]);
				findLeftWords(board, i, j-1, stack);
		}}
		
		stack.pop(); 
		visited[i][j] = false; 
	}
	
	//Method to find up words on board
	private static void findUpWords(String[][] board, int i, int j, Stack<String> stack) {
		
		visited[i][j] = true;             //mark index (letter) as visited
		String s = stack.toString().replaceAll("\\s+", "").toLowerCase(); //Set to string to compare to Dictionaries
		s = reverseString(s);
		//StdOut.println(s);
		if(dictTrie.prefixMatch(s).toString().length() != 0) {
			if(s.length() >= 4){ //Check if prefix exists
				if(dictTrie.contains(s)) { 
					if(!gameTrie.contains(s)) { //Add word to game Trie
						gameTrie.put(s, numberWordsFound); 
						numberWordsFound++; 					
					}
				}
			}
		}else if (s.contains("*")){
			String wildWordToCheck = s.replace("*", ".");
			//StdOut.println("wildWordToCheck is" + wildWordToCheck);
			for (String string : dictTrie.keys()) {
				if (string.length() >= 4 && string.matches(wildWordToCheck.toLowerCase())) {
					//StdOut.println(string);
					gameTrie.put(string, gameTrieSize);
					gameTrieSize++;
				}
			}
		}else{
			stack.pop(); //Pop if not prefix or wildcard
			visited[i][j] = false; 
			return; 
		}
		
		if((i-1)>(-1)) { //Above
			if(!visited[i-1][j]) {
				stack.push(board[i-1][j]); //Push and Recurse
				findUpWords(board, i-1, j, stack);
		}}	
		
		stack.pop(); 
		visited[i][j] = false; 
	}
	
	//Method to find down words on board
	private static void findDownWords(String[][] board, int i, int j, Stack<String> stack) {
		
		visited[i][j] = true;             //mark index (letter) as visited
		String s = stack.toString().replaceAll("\\s+", "").toLowerCase(); //Set to string to compare to Dictionaries
		s = reverseString(s);
		//StdOut.println(s);
		if(dictTrie.prefixMatch(s).toString().length() != 0) {
			if(s.length() >= 4){ //Check if prefix exists
				if(dictTrie.contains(s)) { 
					if(!gameTrie.contains(s)) { //Add word to game Trie
						gameTrie.put(s, numberWordsFound); 
						numberWordsFound++; 					
					}
				}
			}
		}else if (s.contains("*")){
			String wildWordToCheck = s.replace("*", ".");
			//StdOut.println("wildWordToCheck is" + wildWordToCheck);
			for (String string : dictTrie.keys()) {
				if (string.length() >= 4 && string.matches(wildWordToCheck.toLowerCase())) {
					//StdOut.println(string);
					gameTrie.put(string, gameTrieSize);
					gameTrieSize++;
				}
			}
		}else{
			stack.pop(); //Pop if not prefix or wildcard
			visited[i][j] = false; 
			return; 
		}
		
		if((i+1) < board.length) { //Below
			if(!visited[i+1][j]) {
				stack.push(board[i+1][j]);
				findDownWords(board, i+1, j, stack);
		}}
		
		stack.pop(); 
		visited[i][j] = false; 
	}
	
	//Method to find down right words on board
	private static void findDownRightWords(String[][] board, int i, int j, Stack<String> stack) {
		
		visited[i][j] = true;             //mark index (letter) as visited
		String s = stack.toString().replaceAll("\\s+", "").toLowerCase(); //Set to string to compare to Dictionaries
		s = reverseString(s);
		//StdOut.println(s);
		if(dictTrie.prefixMatch(s).toString().length() != 0) {
			if(s.length() >= 4){ //Check if prefix exists
				if(dictTrie.contains(s)) { 
					if(!gameTrie.contains(s)) { //Add word to game Trie
						gameTrie.put(s, numberWordsFound); 
						numberWordsFound++; 					
					}
				}
			}
		}else if (s.contains("*")){
			String wildWordToCheck = s.replace("*", ".");
			//StdOut.println("wildWordToCheck is" + wildWordToCheck);
			for (String string : dictTrie.keys()) {
				if (string.length() >= 4 && string.matches(wildWordToCheck.toLowerCase())) {
					//StdOut.println(string);
					gameTrie.put(string, gameTrieSize);
					gameTrieSize++;
				}
			}
		}else{
			stack.pop(); //Pop if not prefix or wildcard
			visited[i][j] = false; 
			return; 
		}
		
		if((j+1) < board.length && (i+1) < board.length) { //Lower Right
			if(!visited[i+1][j+1]) {
				stack.push(board[i+1][j+1]); 
				findDownRightWords(board, i+1, j+1, stack);
		}}	
		
		stack.pop(); 
		visited[i][j] = false; 
	}
	
	//Method to find down left words on board
	private static void findDownLeftWords(String[][] board, int i, int j, Stack<String> stack) {
		
		visited[i][j] = true;             //mark index (letter) as visited
		String s = stack.toString().replaceAll("\\s+", "").toLowerCase(); //Set to string to compare to Dictionaries
		s = reverseString(s);
		//StdOut.println(s);
		if(dictTrie.prefixMatch(s).toString().length() != 0) {
			if(s.length() >= 4){ //Check if prefix exists
				if(dictTrie.contains(s)) { 
					if(!gameTrie.contains(s)) { //Add word to game Trie
						gameTrie.put(s, numberWordsFound); 
						numberWordsFound++; 					
					}
				}
			}
		}else if (s.contains("*")){
			String wildWordToCheck = s.replace("*", ".");
			//StdOut.println("wildWordToCheck is" + wildWordToCheck);
			for (String string : dictTrie.keys()) {
				if (string.length() >= 4 && string.matches(wildWordToCheck.toLowerCase())) {
					//StdOut.println(string);
					gameTrie.put(string, gameTrieSize);
					gameTrieSize++;
				}
			}
		}else{
			stack.pop(); //Pop if not prefix or wildcard
			visited[i][j] = false; 
			return; 
		}
		
		if((i+1)<board.length && (j-1) > (-1)) { //Lower Left
			if(!visited[i+1][j-1]) {
				stack.push(board[i+1][j-1]);
				findDownLeftWords(board, i+1, j-1, stack);
		}}
		
		stack.pop(); 
		visited[i][j] = false; 
	}
	
	//Method to find up left words on board
	private static void findUpLeftWords(String[][] board, int i, int j, Stack<String> stack) {
		
		visited[i][j] = true;             //mark index (letter) as visited
		String s = stack.toString().replaceAll("\\s+", "").toLowerCase(); //Set to string to compare to Dictionaries
		s = reverseString(s);
		//StdOut.println(s);
		if(dictTrie.prefixMatch(s).toString().length() != 0) {
			if(s.length() >= 4){ //Check if prefix exists
				if(dictTrie.contains(s)) { 
					if(!gameTrie.contains(s)) { //Add word to game Trie
						gameTrie.put(s, numberWordsFound); 
						numberWordsFound++; 					
					}
				}
			}
		}else if (s.contains("*")){
			String wildWordToCheck = s.replace("*", ".");
			//StdOut.println("wildWordToCheck is" + wildWordToCheck);
			for (String string : dictTrie.keys()) {
				if (string.length() >= 4 && string.matches(wildWordToCheck.toLowerCase())) {
					//StdOut.println(string);
					gameTrie.put(string, gameTrieSize);
					gameTrieSize++;
				}
			}
		}else{
			stack.pop(); //Pop if not prefix or wildcard
			visited[i][j] = false; 
			return; 
		}
		
		if((i-1)>(-1) && (j-1) > (-1)) { //Upper Left
			if(!visited[i-1][j-1]) {
				stack.push(board[i-1][j-1]);
				findUpLeftWords(board, i-1, j-1, stack);
		}}
		
		stack.pop(); 
		visited[i][j] = false; 
	}
	
	//Method to find up right words on board
	private static void findUpRightWords(String[][] board, int i, int j, Stack<String> stack) {
		
		visited[i][j] = true;             //mark index (letter) as visited
		String s = stack.toString().replaceAll("\\s+", "").toLowerCase(); //Set to string to compare to Dictionaries
		s = reverseString(s);
		//StdOut.println(s);
		if(dictTrie.prefixMatch(s).toString().length() != 0) {
			if(s.length() >= 4){ //Check if prefix exists
				if(dictTrie.contains(s)) { 
					if(!gameTrie.contains(s)) { //Add word to game Trie
						gameTrie.put(s, numberWordsFound); 
						numberWordsFound++; 					
					}
				}
			}
		}else if (s.contains("*")){
			String wildWordToCheck = s.replace("*", ".");
			//StdOut.println("wildWordToCheck is" + wildWordToCheck);
			for (String string : dictTrie.keys()) {
				if (string.length() >= 4 && string.matches(wildWordToCheck.toLowerCase())) {
					//StdOut.println(string);
					gameTrie.put(string, gameTrieSize);
					gameTrieSize++;
				}
			}
		}else{
			stack.pop(); //Pop if not prefix or wildcard
			visited[i][j] = false; 
			return; 
		}
		
		if((i-1)>(-1) && (j+1) < board.length) { //Upper-Right
			if(!visited[i-1][j+1]) {
				stack.push(board[i-1][j+1]); 
				findUpRightWords(board, i-1, j+1, stack);
		}}
		
		stack.pop(); 
		visited[i][j] = false; 
	}
	
	public static String reverseString(String orig) {
		int i;
		int length = orig.length();
		StringBuffer newString = new StringBuffer(length);
		for(i = (length - 1); i >= 0; i--) {
			newString.append(orig.charAt(i));
		}
		return newString.toString();
	}
	
	//Method to list all words on the board and the words the user found
	private static void listWords() {
		StdOut.println("\n Good work, here are all the words on the board: ");
		int i = 0;
		for (String x : gameTrie.keys()) {
			if (i == numCols) {
				StdOut.println();
				i = 0;
			}
			StdOut.print("\t" + x);
			i++;
		} 
		
		double percent = (double)userWords.size()/(double)gameTrie.size() * 100;
		StdOut.println("\nYou found " + foundWords + " out of " + gameTrie.size() + ", or " + percent + "%.");
		StdOut.println("You found: ");
		int counter = 0;
		for (String x : userWords.keys()) {
			if (counter == numCols) {
				StdOut.println();
				counter = 0;
			}
			StdOut.print("\t" + x);
			counter++;
		} 
	}
	
	//Method to allow user to guess words
	private static void getUserInput() {
		StdOut.println("\nYou can now type in words.  Type QUIT to quit playing and list all the words on the game board");
		String x = "go";
		while (!x.equals("QUIT") && !x.equals("Quit") && !x.equals("quit")) {
			String userInput = StdIn.readLine();
			x = userInput;
			if (gameTrie.contains(userInput.toLowerCase())) {
				if (userWords.contains(userInput.toLowerCase())) {
					StdOut.println("You already found this word! Try again!");
				} else {
					StdOut.println("That is correct! Found word: " + userInput);
					userWords.put(userInput, foundWords);
					foundWords++;
				}
			} else {
				StdOut.println("Oops! That word is either not a word or not on the board. Try again!");
			}
		}
	}
	
	//Read command line arguments
	public static Boolean readCommandLineInput(String[] args) {
		Boolean correctArgs = false;
		if (args.length > 0) {
	
			for(int i = 0; i < args.length; i++) {
				//define help
				if (args[i].equals("-help")) {
					StdOut.println("Welcome to WORD SEARCH!  Run with '-help' for cmd-line options.");
					StdOut.println("Options: ");
					StdOut.println( "'-board FILENAME': Specifies game board file.");
					StdOut.println("'-dict FILENAME':  Specifies dictionary file.");
					StdOut.println("'-cols NUMCOLS': Specifies the number of columns for printing words.");
					System.exit(0);
					correctArgs = true;
				}
				if (args[i].equals("-board")) {
					boardFile = args[i+1];
					correctArgs = true;
					i++;
				}
				if (args[i].equals("-dict")) {
					dictFile = args[i+1];
					correctArgs = true;
					i++;
				}
				if (args[i].equals("-cols")) {
					numCols = Integer.parseInt(args[i+1]);
					StdOut.println("Number of columns= " + numCols);
					correctArgs = true;
					i++;
				} 
				
			} 
		}else {
			boardFile = "data2.txt";
			dictFile = "dict10.txt";
			numCols = 6;
			correctArgs = true;
		}
		
		if (dictFile == null) {
			dictFile = "dict10.txt";
		}
		
		if (boardFile == null) {
			boardFile = "data2.txt";
		}
		return correctArgs;
	}
}