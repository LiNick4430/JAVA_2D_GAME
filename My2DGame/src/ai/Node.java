package ai;

public class Node {

	Node parent;	// 我們會記錄下是從哪個前一個節點到達當前節點的。這個 parent 變數就是用來儲存這個前一個節點的引用。在找到目標節點後，我們可以通過回溯 parent 引用來重建找到的最短路徑。
	public int col, row;
	int gCost, hCost, fCost;
	boolean solid;
	boolean open;
	boolean checked;
	
	public Node(int col, int row) {
		this.col = col;
		this.row = row;
		
	}
}
