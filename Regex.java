import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class Regex {
	
	private String regex;
	private Container root;
	private ArrayList<Incidence> results;
	private ArrayList<Container> containing;
	
	
	public void enterRegex() {
		this.regex = "";
		File path;
		this.results = new ArrayList<>();
		this.containing = new ArrayList<>();
		
		this.regex = JOptionPane.showInputDialog("Enter regular expression: ");
		if(this.regex == null) {
			System.exit(0);
		}
		this.root = new Container();
		boolean accepted = false;
		while(!accepted) {
			try {
				if(this.regex.equals("") || this.regex.equals("()")) {
					throw new IOException();
				}
				this.containRegex(root, 0, this.regex.length());
				accepted = true;
			} catch (IOException e) {
				System.out.println("Regular expression: " + this.regex + " is not valid");
				this.regex = JOptionPane.showInputDialog("Enter a valid regular expression: ");
				if(this.regex == null) {
					System.exit(0);
				}
			}		
		}
		System.out.println("Regex has been accepted");
		System.out.println();
		
		String possiblePath = "";
		possiblePath = JOptionPane.showInputDialog("Enter directory path");
		if(possiblePath == null) {
			System.exit(0);
		}
		path = new File(possiblePath);
		
		path.isDirectory();
		if(!path.isDirectory() || possiblePath.equals("")) {
			while (!path.isDirectory() && !possiblePath.equals("")); {
				System.out.println("Directory path: " + path.getPath() + " is not valid");
				possiblePath = JOptionPane.showInputDialog("Enter directory path");
				if(possiblePath == null) {
					System.exit(0);
				}
				path = new File(possiblePath);
			}
		}
		System.out.println("Directory path has been accepted");
		System.out.println();
		
		System.out.println("Starting search...");
		System.out.println("...................................................");
		this.searchDirectories(path);
		System.out.println();
		System.out.println("Stoping search");
		System.out.println();
		System.out.println();
		System.out.println("----------------------------------------------------");
		this.printResults();
	}

	private void searchDirectories(File currentDirectory) {
		System.out.println("Searching in " + currentDirectory.getPath());
		File[] filesInDirectory = currentDirectory.listFiles();
		if(filesInDirectory != null) {
			for(File currentFile : filesInDirectory) {
				if(currentFile.isDirectory()) {
					this.searchDirectories(currentFile);
				} else {
					System.out.println("Searching in " + currentFile.getName());
					this.searchFiles(currentFile);
				}
			}
		} else {
			System.out.println("The current directory '" + currentDirectory.getPath() + "' is empty");
		}
	}
	
	private void searchFiles(File file) {

		
		//Tries to find incidences in the title of the file
		this.evaluateRegex(file, file.getName(), "Title");
		
		//Tries to find incidences in the contents of the file
		try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
		    String line = null;
		    while ((line = reader.readLine()) != null) {
		        this.evaluateRegex(file, line, "Text");
		    }
		} catch (IOException x) {
			System.out.println("The file " + file.getPath() + " could not be read");
		}
	}
	
	private void containRegex(Container current, int first, int last) throws IOException{
		int openedParenthesis = 0;
		int closedParenthesis = 0;
		
		for(int i = 0; i < last - first; i++) {
			if(this.regex.charAt(first + i) == '(') {
				openedParenthesis++;
			} else if(this.regex.charAt(first + i) == ')') {
				closedParenthesis++;
			}
		}
		if(openedParenthesis == 0 && closedParenthesis == 0) {
			current.setRegex(this.regex.substring(first, last));
			this.containing.add(new Container(current));
		} else if(openedParenthesis > 0 || closedParenthesis > 0) {
			if(openedParenthesis != closedParenthesis) {
				throw new IOException();
			} else {
				current.initializeArray(openedParenthesis);
				openedParenthesis = closedParenthesis = 0;
				boolean isParenthesis = false;
				for(int i = 0; i < last - first; i++) {
					if(isParenthesis) {
						openedParenthesis++;
					}
					if(this.regex.charAt(first + i) == '(') {
						isParenthesis = true;
					} else if (this.regex.charAt(first + i) == ')') {
						isParenthesis = false;
						Container newContainer = new Container();
						try {
							switch (this.regex.charAt(first + i + 1)) {
							case '+':
								newContainer.setOperation("+");
								break;
								
							case '*':
								newContainer.setOperation("*");
								break;

							case 'o':
								newContainer.setOperation("o");
								break;
								
							default:
								break;
							}
						} catch(StringIndexOutOfBoundsException e) {	
						}
						this.containRegex(current.insertContainer(newContainer), i + 1 - openedParenthesis, i);
						openedParenthesis = closedParenthesis = 0;
					}
				}
			}
		}
	}
	
	private void evaluateRegex(File file, String candidate, String section){
		Incidence newIncidence = new Incidence("", file.getPath(), section);
		String match = "";
		String possibleMatch;
		int currentCandidateIndex = 0;
		int charSkipper = 0;
		OUTER:
		for(int i = 0; i < candidate.length(); i++) { 
			i += charSkipper;
			currentCandidateIndex = i;
			int containerSkipper = 0;
			charSkipper = 0;
			INNER2:
			for(int j = 0; j < this.containing.size(); j++) {
				if(containerSkipper > 0) {
					containerSkipper--;
					continue INNER2;
				}
				
				possibleMatch = "";
				Container currentContainer = this.containing.get(j);
				
				if(currentContainer.getOperation().equals("*")) {
					int possibleIndex = 0;
					INNER:
					do {
						match += possibleMatch;
						currentCandidateIndex += possibleIndex;
						if(currentContainer.getRegex().length() > candidate.length() - currentCandidateIndex) {
							break INNER2;
						} else {
							possibleMatch = "";
							possibleIndex = 0;
							for(int k = 0; k < currentContainer.getRegex().length(); k++) {
								if(candidate.charAt(currentCandidateIndex + possibleIndex) == currentContainer.getRegex().charAt(k)) {
									possibleMatch += currentContainer.getRegex().charAt(k);
									possibleIndex++;
								} else {
									break INNER;
								}
							}
						}
					} while(possibleMatch.equals(currentContainer.getRegex()));
					continue INNER2;
					
				} else if(currentContainer.getOperation().equals("+")) {
					int howManyIncidences = 0;
					int possibleIndex = 0;
					INNER:
					do {
						match += possibleMatch;
						currentCandidateIndex += possibleIndex;
						if(currentContainer.getRegex().length() > candidate.length() - currentCandidateIndex) {
							if(howManyIncidences == 0) {
								return;
							} else {
								break INNER2;
							}
						} else {
							possibleMatch = "";
							possibleIndex = 0;
							for(int k = 0; k < currentContainer.getRegex().length(); k++) {
								if(candidate.charAt(currentCandidateIndex + possibleIndex) == currentContainer.getRegex().charAt(k)) {
									possibleMatch += currentContainer.getRegex().charAt(k);
									possibleIndex++;
								} else {
									if(howManyIncidences == 0) {
										match = "";
										continue OUTER;
									}
									break INNER;
								}
							}
							howManyIncidences++;
						}
					} while(possibleMatch.equals(currentContainer.getRegex()));
					continue INNER2;
					
				} else if(currentContainer.getOperation().equals("o")) {
					boolean canEvaluate = false;
					for(int h = j; h < this.containing.size(); h++) {
						canEvaluate = this.containing.get(h).getRegex().length() < candidate.length() - currentCandidateIndex ? true : false ; 
					}
					if(!canEvaluate) {
						return;
					}
					ArrayList<Container> unionContainers = new ArrayList<>();
					for(int h = j; h < this.containing.size(); h++) {
						unionContainers.add(this.containing.get(h));
						if(!this.containing.get(h).getOperation().equals("o")) {
							break;
						}
					}
					containerSkipper = unionContainers.size() - 1;
					int currentChar = 0;
					int currentCont = 0;
					boolean matched = false;
					while(!matched && unionContainers.size() > 0) {
						while(currentCont < unionContainers.size()) {
							if(currentChar == unionContainers.get(currentCont).getRegex().length()) {
								possibleMatch = unionContainers.get(currentCont).getRegex();
								matched = true;
								break;
							}
							if(candidate.charAt(currentCandidateIndex) != unionContainers.get(currentCont).getRegex().charAt(currentChar)) {
								unionContainers.remove(currentCont);
								currentCont--;
							}
							
							currentCont++;
						}	
						currentCandidateIndex++;
						currentChar++;
						currentCont = 0;
					}
					currentCandidateIndex--;
					if(!matched) {
						match = "";
						continue OUTER;
					}
					match += possibleMatch;
					
				} else if(currentContainer.getOperation().equals("")) {
					if(currentContainer.getRegex().length() > candidate.length() - currentCandidateIndex) {
						return;
					} else {
						for(int k = 0; k < currentContainer.getRegex().length(); k++) {
							if(candidate.charAt(currentCandidateIndex) == currentContainer.getRegex().charAt(k)) {
								possibleMatch += currentContainer.getRegex().charAt(k);
								currentCandidateIndex++;
								if(possibleMatch.equals(currentContainer.getRegex())) {
									match += possibleMatch;
									break;
								}
							} else {
								match = "";
								continue OUTER;
							}
						}
					}
				}
			}
			if(!match.equals("")) {
				newIncidence.setWord(match);
				this.results.add(new Incidence(newIncidence));
				match = "";
				newIncidence.setWord("");
				charSkipper = currentCandidateIndex - i-1;
			}
		}
	}
	
	private void printResults() {
		if(this.results.isEmpty()) {
			System.out.println("The search designated with " + this.regex + " got no results");
		} else {
			System.out.println("The search designated with " + this.regex + " found the next results:");
			System.out.println();
			for(int  i = 0; i < this.results.size();i++) {
				System.out.println(this.results.get(i).toString());
			}
		}
	}
	
	
	
	public static void main(String[] args) {
		Regex regex = new Regex();
		regex.enterRegex();
	}
	
}

class Container {
	
	private String operation,
				   regex;
	private Container[] containersWithin;
	
	public Container() {
		this.operation = "";
		this.regex = "";
	}
	
	public Container(Container container) {
		this.operation = container.getOperation();
		this.regex = container.getRegex();
	}
	
	public void initializeArray(int length) {
		this.containersWithin = new Container[length];
	}
	
	public Container insertContainer() throws NullPointerException {
		for(int i = 0; i < this.containersWithin.length; i++) {
			if(this.containersWithin[i] == null) {
				this.containersWithin[i] = new Container();
				return this.containersWithin[i];
			}
		}
		throw new NullPointerException("The array of containers is full");
	}
	
	public Container insertContainer(Container newContainer) throws NullPointerException {
		for(int i = 0; i < this.containersWithin.length; i++) {
			if(this.containersWithin[i] == null) {
				this.containersWithin[i] = new Container(newContainer);
				return this.containersWithin[i];
			}
		}
		throw new NullPointerException("The array of containers is full");
	}
	
	public String getOperation() {
		return this.operation;
	}
	
	public String getRegex() {
		return this.regex;
	}
	
	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	public void setRegex(String regex) {
		this.regex = regex;
	}
}


class Incidence {
	private String word,
				   path,
				   section;
	public Incidence() {
		this.word = this.path = this.section = "";
	}
	
	public Incidence(String word, String path, String section) {
		this.word = word;
		this.path = path;
		this.section = section;
	}
	
	public Incidence(Incidence incidence) {
		this.word = incidence.getWord();
		this.path = incidence.getPath();
		this.section = incidence.getSection();
	}
	
	public String getWord() {
		return this.word;
	}
	
	public String getPath() {
		return this.path;
	}
	
	public String getSection() {
		return this.section;
	}
	
	public void setWord(String word) {
		this.word = word;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public void setSection(String section) {
		this.section = section;
	}
	
	public String toString() {
		return this.word + ", found in " + this.path + " in the " + this.section + " section.";
	}
}
