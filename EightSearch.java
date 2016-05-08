import java.util.*;
import java.lang.Math;

class Child implements Comparable<Child>{
 
    public String seq;
    public int depth;
    public int heuristic;
    public String action;
    public Child parent;


    Child(String seq, String action, Child parent){
	this.seq = seq;
	this.action = action;
	this.parent = parent;
	this.depth = -1;
	this.heuristic = 0;
    }
   
    public void setHeuristic(int sumDist){
	this.heuristic = sumDist + depth;
    }
    public void setGreedyHeuristic(int sumDist){
	this.heuristic = sumDist;
    }
 
    @Override
    public int compareTo(Child node){
	if(this.heuristic > node.heuristic){
	    return 1;
	}
	return -1;
    }  
}
 
public class EightSearch{
 
 
    public static int[][]       _initial;  //matriz inicial
    public static int[][]         _final;  //matriz final
    public static String           start;  //sequência inicial
    public static String             end;  //sequência final
    public static int                  a;  //nr de inversoes no inicio
    public static int                  b;  //nr de inversoes no fim
    public static int              level;  //nível de iteração actual para o I-DFS
 
    public static long timeStart;
    public static long timeEnd;
    public static long totalTime;
   
    public static LinkedList<Child>     frontier;    
    public static ArrayList<Child>           desc;    
    public static HashMap<String, Integer> visited;
    public static PriorityQueue<Child>      pqueue;

    public static int nrBT;
    
    public static boolean running;
   
    public static String makeSequence(int[][] matriz){
	int seq = 0;
	for(int i=0; i<3; i++)
	    for(int j=0; j<3; j++)
		seq = seq*10 + matriz[i][j];
 
	return (String.valueOf(seq).length() == 9? String.valueOf(seq):"0"+String.valueOf(seq));  
    }
 
 
    public static int countInversion(int[][] matriz){
     
	int counter = 0;
	int[] temp = new int[9];
	int index = 0;
   
	for(int i=0; i<3; i++){
	    for(int j=0; j<3; j++){
		temp[index] = matriz[i][j];
		index++;
	    }
	}
   
	first:for(int i=0; i<9; i++){
	    second:for(int j=i+1; j<9; j++){
       
		if(temp[i] == 0){
		    continue first;
		}
		if(temp[j] == 0){
		    continue second;
		}
		if(temp[i] > temp[j]){
		    counter++;
		}
	    }
	}
       
	return counter;
    }
   
    public static boolean isVisited(String seq){
 
	if(visited.containsKey(seq))
	    return true;
   
	return false;
 
    }
 
   
    public static boolean isSolution(String seq){
   
	if(seq.equals(end))
	    return true;
 
	return false;
   
    }

    public static int calcDist(String seq){
	//retornar distancias verticais + horizontais de todas as posiçoes de 1 a 8 somadas
	int sum   = 0;
	int index = 0;
 
	//posições (j, i) de cada número na sequencia seq, e na sequencia end
	int[] _i  = new int[9];
	int[] _j  = new int[9];
 
	int[] __i = new int[9];
	int[] __j = new int[9];
   
   
	for(int i=0; i<3; i++){
	    for(int j=0; j<3; j++){
       
		int parsed   = Character.getNumericValue(seq.charAt(index));
		int _parsed  = Character.getNumericValue(end.charAt(index));
       
		_i[parsed]   = i;
		_j[parsed]   = j;
		__i[_parsed] = i;
		__j[_parsed] = j;
       
		index++;      
	    }      
	}
 
	for(int i=1; i<9; i++){
	    int diff = 0;
	    diff    += Math.abs(_i[i] - __i[i]);
	    diff    += Math.abs(_j[i] - __j[i]);
	    sum     += diff;
	}
   
	return sum;
    }
   
    public static void h_search(int func){
 
	pqueue = new PriorityQueue<Child>();
   
	Child current = new Child(start, "", null);
	current.depth = 1;

	int sumDist = calcDist(current.seq);
	if(func == 5)
	    current.setHeuristic(sumDist);
	else
	    current.setGreedyHeuristic(sumDist);
   
	pqueue.add(current);
	visited.put(current.seq, current.depth);
 
	while(pqueue.size() > 0){
	    current = pqueue.poll();
	    MakeDescendants(current, func);
	    insert(func);
	    if(running == false){
		desc.clear();
		visited.clear();
		pqueue.clear();
	    }
	}
 
    }
   
    public static void generalSearchAlgorithm(int func){
 
	if(func == 5 || func == 4){
	    h_search(func);
	    return;
	}
	nrBT = 0;
	Child current = null;
	Child top = new Child(start, "", null);
	top.depth = 0;
	frontier.add(top);
	visited.put(start, 0);
	try{
	    while(frontier.size() > 0){    
		current = frontier.remove(0);
		MakeDescendants(current, func);
		insert(func);			
		if(running == false){
		    desc.clear();
		    visited.clear();
		    frontier.clear();
		    return;
		}
	    }
       
	}catch(OutOfMemoryError e){
	    desc.clear();
	    visited.clear();
	    frontier.clear();
	    System.out.println("Error: Out of memory.");
	    timeEnd = System.currentTimeMillis();
	    totalTime = timeEnd - timeStart;
	    System.out.println("Execution time: "+(double)(totalTime*0.001)+" seconds");
	    System.exit(42);
	}
 
    }

    public static String getPath(Child node){

	String path = "";
	
	while(node.parent != null){
	    path = node.action+path;
	    node = node.parent;
	}
	
	return path;
    }
    
    public static void printSolution(Child node, int func){

	String path = getPath(node);
	timeEnd = System.currentTimeMillis();
	totalTime = timeEnd - timeStart;
   
	System.out.println("Solution Found");
	System.out.println("Steps: "+path);
	System.out.println("Number of steps: "+path.length());
	System.out.println("Execution time: "+(double)(totalTime*0.001)+" seconds");
	System.out.println("nr of backtracks = "+nrBT);
   
	if(func == 5 || func == 4){
	    System.out.println("Number of nodes in memory: HashMap = "+visited.size()+ " PriorityQueue = "+pqueue.size());
	}else{
	    System.out.println("Number of nodes in memory: HashMap = "+visited.size()+ " LinkedList = "+frontier.size());
	}
   
	running = false;   
    }
 
   
 
    public static String move(String dir, String seq, int z){
 
	StringBuilder _seq = new StringBuilder(seq);
   
	if(dir.equals("U")){
	    //3 casas para a esquerda
	    _seq.setCharAt(z, seq.charAt(z-3));
	    _seq.setCharAt(z-3, seq.charAt(z));
       
	}else if(dir.equals("D")){
	    //3 casas para a direita
	    _seq.setCharAt(z, seq.charAt(z+3));
	    _seq.setCharAt(z+3, seq.charAt(z));
	}else if(dir.equals("L")){
	    //1 casa para a esquerda
	    _seq.setCharAt(z, seq.charAt(z-1));
	    _seq.setCharAt(z-1, seq.charAt(z));
	}else if(dir.equals("R")){
	    //1 casa para a direita
	    _seq.setCharAt(z, seq.charAt(z+1));
	    _seq.setCharAt(z+1, seq.charAt(z));
	}
	return _seq.toString();
    }
    
    public static void MakeDescendants(Child node, int func){
 
	int z            = -1; //zero index
	String temp      = "";
	String direction = "";
   
        for(int i=0; i<9; i++){
	    if(node.seq.charAt(i) == '0'){
		z = i;
	    }
	}
	if(func == 3 && node.depth > level){
	    backtrack(node, z);    
	    return;
	}
   
	if(z > 2){
	    //pode ir para cima
	    temp = node.seq;
	    direction = "U";
	    temp = move("U", temp, z);
	    //verificar aqui se ja foi visitado
	    //verificar profundidade
	    if(!isVisited(temp) || (func == 3 && visited.get(temp) > visited.get(node.seq)+1)){
		desc.add(new Child(temp, direction, node));
		if(func == 2 || func == 3){
		    return;
		}
	    }
	}
   
   
	if(z < 6){
	    //pode ir para baixo
	    temp = node.seq;
	    direction = "D";
	    temp = move("D", temp, z);
	    //verificar aqui se ja foi visitado    
	    if(!isVisited(temp) || (func == 3 && visited.get(temp) > visited.get(node.seq)+1)){
		desc.add(new Child(temp, direction, node));
		if(func == 2 || func == 3){
		    return;
		}
	    }  
	}
   
   
	if(z != 0 && z != 3 && z != 6){
	    //pode ir para esquerda
	    temp = node.seq;
	    direction = "L";
	    temp = move("L", temp, z);
	    //verificar aqui se ja foi visitado    
	    if(!isVisited(temp) || (func == 3 && visited.get(temp) > visited.get(node.seq)+1)){
		desc.add(new Child(temp, direction, node));
		if(func == 2 || func == 3){
		    return;
		}
	    }
       
	}
   
	if(z != 2 && z != 5 && z != 8){
	    //pode ir para direita
	    temp = node.seq;
	    direction = "R";
	    temp = move("R", temp, z);
	    //verificar aqui se ja foi visitado    
	    if(!isVisited(temp) || (func == 3 && visited.get(temp) > visited.get(node.seq)+1)){
		desc.add(new Child(temp, direction, node));
		if(func == 2 || func == 3){
		    return;
		}
	    }
 
	}
	if(func == 2 || func == 3){
	    conditionCheck(node, z, func);
	}
       
    }

    public static void conditionCheck(Child node, int z, int func){
	
	if(desc.size() == 0 && !(node.depth == 0)){
	    backtrack(node, z);
	}
	
	if(desc.size() == 0 && func == 3 && node.depth == 0){
	    level++;
	    desc.clear();
	    visited.clear();
	    frontier.clear();
	    return;
	}
	if(desc.size() == 0 && func == 2 && node.depth == 0){
	    running = false;
	    desc.clear();
	    visited.clear();
	    pqueue.clear();
	    return;
	}
 
    }
 
    public static void backtrack(Child node, int z){
	
	nrBT++;
	frontier.add(node.parent);
	
    }
      
    public static void insert(int func){
 
	//BFS
	if(func == 1){
	    while(desc.size() > 0){
		Child child = desc.remove(0);
		child.depth = child.parent.depth+1;
		visited.put(child.seq, child.depth);      
		if(isSolution(child.seq)){
		    printSolution(child, func);
		    return;
		}
		frontier.addLast(child);      
	    }
	}
	//DFS
	else if(func == 2){
	    if(desc.size() > 0){
		Child child = desc.remove(0);
		child.depth = child.parent.depth+1;
		visited.put(child.seq, child.depth);
		if(isSolution(child.seq)){
		    printSolution(child, func);
		    return;
		}
		frontier.addFirst(child);
	    }
	}
	//Iterative Deepening DFS
	else if(func == 3){    
	    if(desc.size() > 0){
		Child child = desc.remove(0);
		child.depth = child.parent.depth + 1;
		visited.put(child.seq, child.depth);      
		if(isSolution(child.seq)){
		    printSolution(child, func);
		    return;
		}
		frontier.addFirst(child);          
	    }
	}
	//Greedy Search
	else if(func == 4){
	    while(desc.size() > 0){
		Child child = desc.remove(0);
		child.depth = child.parent.depth + 1;
		child.setGreedyHeuristic(calcDist(child.seq));
		visited.put(child.seq, child.depth);      
		if(isSolution(child.seq)){
		    printSolution(child, func);
		    return;
		}
		pqueue.add(child);    
	    }
	}
	//A* Search
	else if(func == 5){
	    while(desc.size() > 0){
		Child child = desc.remove(0);
		child.depth = child.parent.depth + 1;
		child.setHeuristic(calcDist(child.seq));
		visited.put(child.seq, child.depth);      
		if(isSolution(child.seq)){
		    printSolution(child, func);
		    return;
		}
		pqueue.add(child);    
	    }
	}
   
    }
 
    public static void main(String args[]){
   
	frontier    = new LinkedList<Child>();
	desc        = new ArrayList<Child>();
	visited     = new HashMap<String,Integer>();
 
	running     = true;
	level       = 0;
       
	Scanner inp = new Scanner(System.in);
        _initial    = new int[3][3];
        _final      = new int[3][3];
	String line = "?";
   
	System.out.println();
	System.out.println("[Eight Puzzle Solver]");
	System.out.println();
	System.out.println(" 1 - BFS");
	System.out.println(" 2 - DFS");
	System.out.println(" 3 - Iterative Deepening DFS");
	System.out.println(" 4 - Greedy Search");
	System.out.println(" 5 - A* Search");
	System.out.println();
   	System.out.println("Insert initial and final state.");

	for(int i=0; i<3; i++){
	    line = inp.nextLine();
	    Scanner scan = new Scanner(line);
	    for(int j=0; j<3; j++){
		_initial[i][j] = scan.nextInt();
	    }
	}
   
	for(int i=0; i<3; i++){
	    line = inp.nextLine();
	    Scanner scan = new Scanner(line);
	    for(int j=0; j<3; j++){
		_final[i][j] = scan.nextInt();
	    }
	}
	System.out.println("Insert search function code.");
	int flag  = inp.nextInt();
   
	timeStart = System.currentTimeMillis();    
        start     = makeSequence(_initial);
	end       = makeSequence(_final);  
	a         = countInversion(_initial);
	b         = countInversion(_final);
   
	if((a % 2) != (b % 2)){
	    System.out.println("Error: Not possible to go from initial state to final state.");
	    System.exit(42);
	}
   
	while(running == true){
	    generalSearchAlgorithm(flag);
	}
   
    }  
}
