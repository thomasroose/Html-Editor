import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Html editor
 *
 */
public class Texed extends JFrame implements DocumentListener,KeyListener {
	private JTextArea textArea;
	private JFrame frame;
	
	StringBuffer bufferCurrent = new StringBuffer();
	StringBuffer bufferRedo = new StringBuffer();

	private static final long serialVersionUID = 5514566716849599754L;
	
	/**
	 * Constructs a new GUI: A TextArea on a ScrollPane
	 */
	public Texed() {
		super();
		
		setTitle("HTML Text editor");
		setBounds(800, 800, 600, 600);
		textArea = new JTextArea(30, 80);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		
		/**
		 * Registration of the callback
		 */
		textArea.getDocument().addDocumentListener(this);
		textArea.addKeyListener(this);
		JScrollPane pane = new JScrollPane(textArea);
		add(pane);
		
		JScrollPane scrollPane = new JScrollPane(textArea);
		add(scrollPane);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);		
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		
		/**
		 * Link the autoComplete() method to F1
		 */
		if(e.getKeyCode() == KeyEvent.VK_F1){
			autoComplete();
		}
		
		/**
		 * Link the undo() method to F2
		 */
		if(e.getKeyCode() == KeyEvent.VK_F2){
			try{
				undo();
			}catch(StringIndexOutOfBoundsException ex){
				JOptionPane.showMessageDialog(frame, "Please enter some text before undoing your actions");
			}
		}
		
		/**
		 * Link the redo() method to F3
		 */
		if(e.getKeyCode() == KeyEvent.VK_F3){
			try{
				redo();
			}catch(StringIndexOutOfBoundsException ex) {
			    JOptionPane.showMessageDialog(frame, "There are no actions to redo");
			}
		}
		
		/**
		 * Register every letter, number and backspace
		 */
		if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE){
			bufferCurrent.setLength(bufferCurrent.length()-1);
		}else{
			if(e.getKeyCode() != KeyEvent.VK_F1 && e.getKeyCode() != KeyEvent.VK_F2 && e.getKeyCode() != KeyEvent.VK_F3 && e.getKeyCode() != KeyEvent.VK_SHIFT){
				char ch = e.getKeyChar();
				bufferCurrent.append(ch);
			}
		}
	}
	
	/**
	 * Method to remove 1 event
	 */
	public void undo(){
		char lastChar = bufferCurrent.charAt(bufferCurrent.length()-1);
		bufferRedo.append(lastChar);
		bufferCurrent.setLength(bufferCurrent.length()-1);
		
		textArea.setText("");
		textArea.setText(bufferCurrent.toString());
	}
	
	/**
	 * Method to undo the undo method
	 */
	public void redo(){
		if(bufferRedo != null){
			char lastChar = bufferRedo.charAt(bufferRedo.length()-1);
			bufferCurrent.append(lastChar);
			bufferRedo.setLength(bufferRedo.length()-1);
			
			textArea.setText("");
			textArea.setText(bufferCurrent.toString());
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub	
	}
	
	@Override
	public void keyReleased(KeyEvent e){
		// TODO Auto-generated method stub
	}
	
	/**
	 * Method for autocompleting all the tags in the text editor 
	 */
	public void autoComplete(){
		String text = textArea.getText();
		fixTags(text);
		bufferCurrent.replace(0, bufferCurrent.length(), fixTags(text));
	}
	
	/**
	 * Method that adds closing tags to the text where they are needed 
	 * @param text	Text which needs to be checked
	 * @return tag 	All the text from the textArea with the correct opening- and closing tags at the correct position
	 */
	public String fixTags(String text){
		/**
		 * Original text
		 */
		String fullText = text;
		
		/**
		 * Corrected text
		 */
		String addedText ="";
		
		/**
		 * Text which Ok after checked
		 */
		String stringChecked = ""; 
		
		/**
		 * Text that needs checking
		 */
		String stringNotChecked = text;
		
		/**
		 * Closing tag
		 */
		String closingTagSign = ">";
		
		/**
		 * The tag that you are checking
		 */
		String currentTag;
		
		/**
		 * Next tag after the current tag
		 */
		String nextTag ="";
		
		/**
		 * Content of the tag with "<" and ">"
		 */
		String contentCurrentTag; 
		
		/**
		 * Content of the next tag without "<" and ">"
		 */ 
		String contentNextTag = "";
		
		/**
		 * String notChecked minus the current tag you are checking
		 */
		String leftTrim;
		
		String first2Chars ="";
		
		int currentTagLenght,nextTagLength = 0,posNextClosedTag,posNextOpenTag;
		Boolean endLoop = false, HtmlOkForTag = false;
		String[] htmlElements = {"html","body","header","footer","h1","h2","h3","h4","h5","h6","p","b","i","div","nav"};
		
		/**
		 * Check if there are any tags in the text
		 */
		posNextOpenTag = stringNotChecked.indexOf("<");	
		if ( posNextOpenTag < 0) {
			endLoop = true;
		}
		
		/**
		 * Looping over the full text
		 */
		while(!endLoop){
			
			/**
			 * Get the next tag for checking
			 */
			addedText = "";
			currentTagLenght  = stringNotChecked.indexOf(closingTagSign)+1;
			currentTag = stringNotChecked.substring(0, currentTagLenght);
			leftTrim = stringNotChecked.substring(currentTagLenght,stringNotChecked.length());
			contentCurrentTag = currentTag.substring(1,currentTagLenght-1);
			
			/**
			 * Check if tag is OK in stringNotChecked
			 */
			posNextOpenTag = leftTrim.indexOf(currentTag);
			posNextClosedTag =leftTrim.indexOf("</" + contentCurrentTag + ">");
			
			if ((posNextOpenTag > posNextClosedTag && posNextClosedTag >= 0)||(posNextClosedTag >= 0 && posNextOpenTag < 0) || (currentTag.indexOf("</") >= 0))  {
				HtmlOkForTag = true;
			}else{
				HtmlOkForTag = false;
			}
			
			if(leftTrim.length() > 0 && leftTrim.indexOf("<") >= 0){
				nextTagLength = leftTrim.indexOf(closingTagSign)+1;
				nextTag = leftTrim.substring(0,nextTagLength);
				contentNextTag = nextTag.substring(1,nextTagLength-1);
				if (!leftTrim.substring(0,1).equals("<")) {
					first2Chars = "xx";
				}
				else{
					if (leftTrim.substring(0,2).equals("</")) first2Chars = "</";
					else first2Chars = "<x";
				}
			}
			
			/**
			 * Add tag if closing tag is missing
			 */
			if(!HtmlOkForTag && IsHtmlTag(contentCurrentTag,htmlElements)){ 
				
				/**
				 * Case if there is text after the current tag
				 */
				if(leftTrim.length() > 0 && leftTrim.indexOf("<") >= 0){
					switch (first2Chars){
						case "<x": {
							addedText = currentTag + "</" + contentCurrentTag + ">" + leftTrim;	
							break;
					    }
						case "</": {
							addedText = currentTag + "</" + contentCurrentTag + ">" + leftTrim;
							endLoop = true;
							break;
						}
						case "xx": {
							posNextOpenTag = leftTrim.indexOf("<");
							if( posNextOpenTag > 0) { 
								addedText = currentTag + leftTrim.substring(0,posNextOpenTag) + "</" + contentCurrentTag + ">" + leftTrim.substring(posNextOpenTag,leftTrim.length());
							
							/**
							* Last tag in rest HTML
							 */
							}else{ 
								addedText = currentTag + leftTrim.substring(0,leftTrim.length()) + "</" + contentCurrentTag + ">";
							}
							break;
						}
					
					}
				}
				else{ 
					if (leftTrim.length() >= 0){
						addedText = currentTag + leftTrim + "</" + contentCurrentTag + ">";
					}else {
						addedText = currentTag + "</" + contentCurrentTag + ">";
					}
						
					
				}
				textArea.setText("");                           
				textArea.append(stringChecked + addedText);
				
				/**
				 * Recreate the Full text for checking
				 */
				stringNotChecked = stringChecked + addedText;	
				fullText = stringChecked + addedText;
				stringChecked = "";
					
			/**
			 * Current tag is OK and prepare string "notChecked" for next check of tags
			 */
			}else{
				
				/**
				 * Check if end of Html
				 */
				if(leftTrim.length() > 0 && leftTrim.indexOf("<") >= 0){
					switch (first2Chars){
						case "<x": {
							stringNotChecked = leftTrim;
							stringChecked = stringChecked + currentTag;
							break;
					    }
						case "</": {
							stringNotChecked = leftTrim.substring(nextTagLength,leftTrim.length());
							stringChecked = stringChecked + currentTag + nextTag;
							break;
						}
						case "xx": {
							posNextOpenTag = leftTrim.indexOf("<");
								stringNotChecked = leftTrim.substring(posNextOpenTag,leftTrim.length());
								stringChecked = stringChecked + currentTag + leftTrim.substring(0,posNextOpenTag);
							break;
						}
					}
					
					/**
					 * 	Check if end of file or no more tags	
					 */
					if(stringNotChecked.length() <= 0 || stringNotChecked.indexOf("<") < 0){ 
						endLoop = true;
						
					/**
					 * Handels case if "a<tag>" at the end
					 */
					}else { 
					 	if (!stringNotChecked.substring(0,1).equals("<")) {
					 		posNextOpenTag = stringNotChecked.indexOf("<");
					 		stringChecked = stringChecked + stringNotChecked.substring(0,posNextOpenTag);
					 		stringNotChecked = stringNotChecked.substring(posNextOpenTag,stringNotChecked.length());
						}
					}					
				}else{
					endLoop = true;
				} 	
			}
		}
		return fullText;
	}
	
	/**
	 * Method thats checks if the tag is an Html tag
	 * @param content		Tag which needs to be checked
	 * @param htmlTagArray	An array of some of the Html elements
	 * @return foundElement	True if tag is Html else false
	 */
	public boolean IsHtmlTag (String content, String[] htmlTagArray) {
		int lengthArray = htmlTagArray.length,i;
		boolean foundElement = false;
		i = 0;
		while (i < (lengthArray -1) && !foundElement) {
			if (content.equals(htmlTagArray[i])) {
				foundElement = true;
			}
			i++;
		}
		return foundElement;
	}
	
	/**
	 * Callback when changing an element
	 */
	public void changedUpdate(DocumentEvent ev) {
	}

	/**
	 * Callback when deleting an element
	 */
	public void removeUpdate(DocumentEvent ev) {
	}

	/**
	 * Callback when inserting an element
	 */
	public void insertUpdate(DocumentEvent ev) {
		/**
		 * Check if the change is only a single character, otherwise return so it does not go in an infinite loop
		 */
		if(ev.getLength() != 1) return;
	}
	
	/**
	 * Entry point of the application: starts a GUI
	 */
	public static void main(String[] args) {
		new Texed();
	}
}