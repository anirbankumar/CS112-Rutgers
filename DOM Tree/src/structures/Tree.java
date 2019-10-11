package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 * 
 */

// Sorry I removed most of the comments... it started getting very messy and confusing
public class Tree {

	TagNode root=null;

	Scanner sc;

	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}

	private boolean isATag(String s, String t){
		if(t.equals("open")){
			if(s.startsWith("<") && s.endsWith(">") && !s.contains("/"))
				return true;
			return false;
		}
		if(t.equals("close")){
			if(s.startsWith("</") && s.endsWith(">"))
				return true;
			return false;
		}
		return true; 
	}

	public void build() {
		String current;
		TagNode pointer = null;
		Stack<TagNode> stack = new Stack<TagNode>();

		while(sc.hasNextLine() == true){
			current = sc.nextLine();
			if(isATag(current, "open") == true){
				current = current.replace("<" , "");
				current = current.replace(">" , "");
				if(root == null){
					root = new TagNode(current, null, null);
					stack.push(root);
					pointer = root;
					continue;
				}
				if(pointer.firstChild == null){
					pointer.firstChild = new TagNode(current, null, null);
					stack.push(pointer.firstChild);
					pointer = stack.peek();
					continue;
				}
				pointer = pointer.firstChild;
				while(pointer.sibling!=null){
					pointer = pointer.sibling;
				}
				pointer.sibling = new TagNode(current, null, null);
				stack.push(pointer.sibling);
				pointer = stack.peek();
				continue;
			}
			if(isATag(current, "close")){
				pointer = stack.pop();
				if(stack.isEmpty())
					continue;
				pointer = stack.peek();
				continue;
			}
			if(isATag(current, "open") == false && isATag(current, "close") == false){ //if not a tag
				if(pointer.firstChild == null){
					pointer.firstChild = new TagNode(current, null, null);
					continue;
				}
				else{
					pointer = pointer.firstChild;
					while(pointer.sibling!=null){
						pointer = pointer.sibling;
					}
					pointer.sibling = new TagNode(current, null, null);
					pointer = stack.peek();
					continue;
				}
			}
		}
	}

	public void replaceTag(String oldTag, String newTag) {
		replaceHelper(root, oldTag, newTag);
	}
	
	private void replaceHelper(TagNode temp, String oldTag2, String newTag2) {
		TagNode curr = temp;
		
		if (curr == null) {
			return;
		}
		if (curr.tag.equals(oldTag2)) {
			curr.tag = newTag2;
		}
		replaceHelper(temp.firstChild, oldTag2, newTag2);
		replaceHelper(temp.sibling, oldTag2, newTag2);
	}


	public void boldRow(int row) { 
		TagNode curr = new TagNode(null, null, null);		
		TagNode temp;
		
		curr = boldHelper(root);
		if (curr == null) {
			System.out.println("No table!!!");
			return;
		}
		
		curr = curr.firstChild;
	
		for(int i = 1; i < row; i++) {
			curr = curr.sibling;
		} 
		
		for (temp = curr.firstChild; temp != null; temp = temp.sibling) {
			temp.firstChild = new TagNode("b", temp.firstChild, null);
		}

	} 
	
	private TagNode boldHelper(TagNode curr) { 
		if (curr == null) {
			return null; 
		}
		
		TagNode nodeTemp = null;
		String strTemp = curr.tag;
		
		if(strTemp.equals("table")) { 
			nodeTemp = curr; 
			return nodeTemp;
		} 
		
		if(nodeTemp == null) {
			nodeTemp = boldHelper(curr.firstChild);
		}
		
		if(nodeTemp == null) { 
			nodeTemp = boldHelper(curr.sibling);
		} 
		
		return nodeTemp;
	}

	public void removeTag(String tag) {
		removerHelper(null, root, tag);
	}

	private void removerHelper(TagNode prev, TagNode curr, String tag2) {
		if (curr == null) {
			return;
		}
		if(curr.tag.equals("b") || curr.tag.equals("em") || curr.tag.equals("p")) {
			if (curr.tag.equals(tag2)) {
				if (prev.firstChild != null && prev.firstChild.tag.equals(curr.tag)) {
					// 
					if(curr.sibling != null) {

						if (curr.firstChild.sibling != null) {		
							TagNode temp = curr.firstChild;
							prev.sibling = temp;
							while (temp.sibling != null) {
								temp = temp.sibling;
							}
							temp.sibling = curr.sibling;
							curr.firstChild = null;
							curr.sibling = null;
						}
						else {																
							curr.firstChild.sibling = curr.sibling;
							prev.firstChild = curr.firstChild;
						}
					}
					else {
						prev.firstChild = curr.firstChild; 
					}
				}
				else if (prev.sibling != null) {
					if(curr.sibling != null) {
						if (curr.firstChild.sibling != null) {
							TagNode temp = curr.firstChild;
							prev.sibling = temp;
							while (temp.sibling != null) {
								temp = temp.sibling;
							}
							temp.sibling = curr.sibling;
							curr.firstChild = null;
							curr.sibling = null;
						}

						else {
							curr.firstChild.sibling = curr.sibling;				
							prev.sibling = curr.firstChild;
						}
					}
					else {
						prev.sibling = curr.firstChild;
					}
				}
			}
		}

		else if(curr.tag.equals("ol") || curr.tag.equals("ul")) {
			if (curr.tag.equals(tag2)) {
				if (prev.firstChild != null && prev.firstChild.tag.equals(curr.tag)) {
					if(curr.sibling != null) {
						if (curr.firstChild.sibling != null) {
							TagNode temp = curr.firstChild;
							while (temp.sibling != null) {
								if (temp.tag.equals("li"))
									temp.tag = "p";
								temp = temp.sibling;
							}

							if (temp.tag.equals("li"))
								temp.tag = "p";
							temp.sibling = curr.sibling;
							prev.firstChild = curr.firstChild;
						}
						else {
							if (curr.firstChild.tag.equals("li")) 
								curr.firstChild.tag = "p";
							curr.firstChild.sibling = curr.sibling;
							prev.firstChild = curr.firstChild;
						}
					}
					else {
						if (curr.firstChild.sibling != null) {
							TagNode temp = curr.firstChild;
							while(temp.sibling != null) {
								if (temp.tag.equals("li"))
									temp.tag = "p";
								temp = temp.sibling;
							}
							if (temp.tag.equals("li"))
								temp.tag = "p";
							prev.firstChild = curr.firstChild;
						}
						else {
							if (curr.firstChild.tag.equals("li")) 
								curr.firstChild.tag = "p";	
							prev.firstChild = curr.firstChild;		
						}
					}
				}
				else if (prev.sibling != null) {
					if(curr.sibling != null) {
						if (curr.firstChild.tag.equals("li"))
							curr.firstChild.tag = "p";

						if (curr.firstChild.sibling != null) {
							TagNode temp = curr.firstChild;
							prev.sibling = temp;
							while (temp.sibling != null) {
								if (temp.tag.equals("li"))
									temp.tag = "p";
								temp = temp.sibling;
							}
							if (temp.tag.equals("li"))
								temp.tag = "p";
							temp.sibling = curr.sibling;
							curr.firstChild = null;
							curr.sibling = null;
						}
						else {
							curr.firstChild.sibling = curr.sibling;
							prev.sibling = curr.firstChild;
						}
					}

					else {
						if (curr.firstChild.sibling != null) {
							TagNode temp = curr.firstChild;
							while(temp.sibling != null) {
								if (temp.tag.equals("li"))
									temp.tag = "p";
								temp = temp.sibling;
							}
							if (temp.tag.equals("li"))
								temp.tag = "p";
							prev.sibling = curr.firstChild;
						}
						else {
							if (curr.firstChild.tag.equals("li"))
								curr.firstChild.tag = "p";
							prev.sibling = curr.firstChild;
						}
					}
				}
			}
		}

		removerHelper(curr, curr.firstChild, tag2);
		removerHelper(curr, curr.sibling, tag2);
	}

	public void addTag(String word, String tag) {
		adderHelper(null, root, word, tag);
	}

	private void adderHelper(TagNode prev, TagNode curr, String word2, String tag2) {

		if (curr == null) {
			return;
		}
		if(prev != null && prev.tag.equals(tag2)) {
			return;
		}

		if (tag2.equals("html") || tag2.equals("body") || tag2.equals("p") || tag2.equals("em") || tag2.equals("b") ||
				tag2.equals("table") || tag2.equals("tr") || tag2.equals("td") || tag2.equals("ol") || tag2.equals("ul") || tag2.equals("li")) {

			if(curr.tag.equals("html") || curr.tag.equals("body") || curr.tag.equals("p") || curr.tag.equals("em") || curr.tag.equals("b") ||
					curr.tag.equals("table") || curr.tag.equals("tr") || curr.tag.equals("td") || curr.tag.equals("ol") || curr.tag.equals("ul") || curr.tag.equals("li")) {
			}
			else {
				String[] array = curr.tag.split(" ");
				int len = array.length;
				String before = "";
				String target = "";
				String after = "";
				TagNode temp = new TagNode(tag2, null, null);
				if (len == 1) {
					for (int i = 0; i < len; i++) {
						if (specialEqualsMethod(array[i], word2)) {
							if (prev.firstChild == curr) {
								if (curr.sibling != null) {
									prev.firstChild = temp;
									temp.firstChild = curr;
									temp.sibling = curr.sibling;
									curr.sibling = null;
								}
								else {
									prev.firstChild = temp;
									temp.firstChild = curr;
								}
							}
							if (prev.sibling == curr) {
								if (curr.sibling != null) {
									prev.sibling = temp;
									temp.firstChild = curr;
									temp.sibling = curr.sibling;
									curr.sibling = null;
								}
								else {
									prev.sibling = temp;
									temp.firstChild = curr;
								}
							}
						}
					}	
				}
				else {
					TagNode head = null;
					TagNode tail = null;
					boolean beforeCheck = true;
					boolean targetCheck = true;
					boolean afterCheck = true;
					while (afterCheck == true) {
						TagNode beforeTN = new TagNode(null, null, null);
						TagNode targetTN = new TagNode(null, null, null);
						TagNode afterTN = new TagNode(null, null, null);
						before = "";
						target = "";
						after = "";
						for (int n = 0; n < len && (targetCheck == true); n++) {
							if (specialEqualsMethod(array[n], word2)) {
								beforeCheck = false;
								targetCheck = false;
								target = array[n];
								targetTN.tag = target;
								if (n != len - 1) {
									for (int m = n + 1; m < len; m++) {
										after = after + array[m] + " ";
									}
									afterTN.tag = after;
								}
							}
							else if (beforeCheck == true) {
								before = before + array[n] + " ";
								beforeTN.tag = before;
							}
						}

						if (targetCheck == true){
							if (prev.firstChild == curr)
								prev.firstChild = beforeTN;
							if (prev.sibling == curr)
								prev.sibling = beforeTN;
							break;
						}
						if (beforeTN.tag != null && targetTN.tag != null && afterTN.tag != null) {
							beforeTN.sibling = temp;
							temp.firstChild = targetTN;
							temp.sibling = afterTN;
						}
						else if (beforeTN.tag != null && targetTN.tag != null) {
							beforeTN.sibling = temp;
							temp.firstChild = targetTN;

						}
						else if (afterTN.tag != null) {
							temp.firstChild = targetTN;
							temp.sibling = afterTN;
						}

						if (head == null && beforeTN.tag != null)
							head = beforeTN;
						else if (head == null && beforeTN.tag == null) {
							temp.firstChild = targetTN;
							head = temp;
						}
						else
							tail = targetTN;

						if (afterTN.tag != null) {
							afterCheck = true;
							array = afterTN.tag.split(" ");
							len = array.length;
							if (head == null && beforeTN.tag != null)
								head = beforeTN;
							else if (head == null && beforeTN.tag == null) {
								temp.firstChild = targetTN;
								head = temp;
							}
							else
								tail = afterTN;
						}
						else
							afterCheck = false;
					}

					if (prev.firstChild == curr)
						prev.firstChild = head;
					else if (prev.sibling == curr)
						prev.sibling = head;
					if(curr.sibling == null) {

					}
					else {
						tail.sibling = curr.sibling;
						curr.sibling = null;
					}
				}
			}

			adderHelper(curr, curr.firstChild, word2, tag2);
			adderHelper(curr, curr.sibling,  word2, tag2);
		}
	}

	private boolean specialEqualsMethod (String currword, String targetword) {
		String currword2 = currword.toLowerCase();
		String targetword2 = targetword.toLowerCase();

		if(currword2.equals(targetword2)) {
			return true;
		}
		
		char last = currword.charAt(currword.length() - 1);

		if (Character.isLetter(last)) {
			return false;
		}
		else if (targetword2.equals(currword2.substring(0, currword.length() - 1))) {
			return true;
		}
		else {
			return false;
		}
	}

	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}

	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");	
			}
		}
	}

	public void print() {
		print(root, 1);
	}

	private void print(TagNode root, int level) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			for (int i=0; i < level-1; i++) {
				System.out.print("      ");
			};
			if (root != this.root) {
				System.out.print("|----");
			} else {
				System.out.print("     ");
			}
			System.out.println(ptr.tag);
			if (ptr.firstChild != null) {
				print(ptr.firstChild, level+1);
			}
		}
	}
}
