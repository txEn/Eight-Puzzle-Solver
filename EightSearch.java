import java.util.*;
import java.lang.Math;
import java.nio.charset.StandardCharsets;
 
class Child implements Comparable<Child>{
 
    public String seq;
    public String path;
    public int heuristic;
 
    Child(String seq, String path){
	this.seq = seq;
	this.path = path;
	this.heuristic = 0;
    }
   
    public void setHeuristic(int sumDist){
	this.heuristic = sumDist + path.length();
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
   
    public static LinkedList<String>     frontier;    
    public static ArrayList<Child>           desc;    
    public static HashMap<String, byte[]> visited;
    public static PriorityQueue<Child>      pqueue;
   
    public static boolean running;
   
 
    public static String byte2str(byte[] bytes){
	String string = new String(bytes, StandardCharsets.UTF_8);
	return string;
    }
    public static byte[] str2byte(String string){
	byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
	return bytes;
    }
   
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
    public static void printMat(String seq){
 
	int k=0;
   
	for(int i=1; i<=9; i++){      
	    System.out.print(seq.charAt(i-1)+" ");
	    if(i == 6){
		System.out.print("Path Length = "+byte2str(visited.get(seq)).length());
	    }
	    if(i % 3 == 0){
		System.out.println();
	    }
	}
	System.out.println();
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
   
	Child current = new Child(start, "");
   
	int sumDist = calcDist(current.seq);
	if(func == 5)
	    current.setHeuristic(sumDist);
	else
	    current.setGreedyHeuristic(sumDist);
   
	pqueue.add(current);
	visited.put(current.seq, str2byte(current.path));
 
	while(pqueue.size() > 0){
	    current = pqueue.poll();
	    MakeDescendants(current.seq, func);
	    insert(func);
	    desc.clear();
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
 
	String current = null;
	frontier.add(start);
	visited.put(start, str2byte(new String("")));
	try{
	    while(frontier.size() > 0){    
		current = frontier.remove(0);
		MakeDescendants(current, func);    
		insert(func);
		desc.clear();
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
 
    public static void printSolution(String seq, int func){
   
	timeEnd = System.currentTimeMillis();
	totalTime = timeEnd - timeStart;
   
	System.out.println("Solution Found");
	System.out.println("Steps: "+byte2str(visited.get(seq)));
	System.out.println("Number of steps: "+byte2str(visited.get(seq)).length());
	System.out.println("Execution time: "+(double)(totalTime*0.001)+" seconds");
   
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
   
    public static void MakeDescendants(String sequence, int func){
 
	int z            = -1; //zero index
	String temp      = "";
	String direction = "";
   
        for(int i=0; i<9; i++){
	    if(sequence.charAt(i) == '0'){
		z = i;
	    }
	}
   
	if(byte2str(visited.get(sequence)).length() > level && func == 3){
	    backtrack(sequence, z);    
	    return;
	}
   
	if(z > 2){
	    //pode ir para cima
	    temp = sequence;
	    direction = byte2str(visited.get(sequence))+"U";
	    temp = move("U", temp, z);
	    //verificar aqui se ja foi visitado
	    //verificar profundidade
	    if(!isVisited(temp) || (func == 3 && byte2str(visited.get(temp)).length() > direction.length())){
		desc.add(new Child(temp, direction));      
	    }
	}
   
   
	if(z < 6){
	    //pode ir para baixo
	    temp = sequence;
	    direction = byte2str(visited.get(sequence))+"D";
	    temp = move("D", temp, z);
	    //verificar aqui se ja foi visitado    
	    if(!isVisited(temp) || (func == 3 && byte2str(visited.get(temp)).length() > direction.length())){
		desc.add(new Child(temp, direction));
	    }  
	}
   
   
	if(z != 0 && z != 3 && z != 6){
	    //pode ir para esquerda
	    temp = sequence;
	    direction = byte2str(visited.get(sequence))+"L";
	    temp = move("L", temp, z);
	    //verificar aqui se ja foi visitado    
	    if(!isVisited(temp) || (func == 3 && byte2str(visited.get(temp)).length() > direction.length())){
		desc.add(new Child(temp, direction));
	    }
       
	}
   
	if(z != 2 && z != 5 && z != 8){
	    //pode ir para direita
	    temp = sequence;
	    direction = byte2str(visited.get(sequence))+"R";
	    temp = move("R", temp, z);
	    //verificar aqui se ja foi visitado    
	    if(!isVisited(temp) || (func == 3 && byte2str(visited.get(temp)).length() > direction.length())){
		desc.add(new Child(temp, direction));
	    }
 
	}
   
	//BACKTRACKING DFS / DFS-I
	if(desc.size() == 0 && (func == 2 || func == 3) && !(byte2str(visited.get(sequence)).equals(""))){
       
	    //adicionar ao stack frontier um nó igual ao nó na posição anterior
	    //sequence = sequencia do actual deadend
	    backtrack(sequence, z);
	    desc.clear();
	}
   
	if(desc.size() == 0 && func == 3 && byte2str(visited.get(sequence)).equals("")){
	    level++;
	    desc.clear();
	    visited.clear();
	    frontier.clear();
	    return;
	}
 
	if(desc.size() == 0 && func == 2 && byte2str(visited.get(sequence)).equals("")){
	    running = false;
	    desc.clear();
	    visited.clear();
	    pqueue.clear();
	    return;
	}
       
    }
 
    public static void backtrack(String sequence, int z){
        String path = byte2str(visited.get(sequence));      //caminho atual
        char back   = path.charAt(path.length()-1);         //ação anterior
        String sub  = path.substring(0, path.length()-1);   //caminho anterior
        String temp = "";
       
        if(back == 'U'){
	    temp = sequence;
	    temp = move("D", temp, z);
	    frontier.addFirst(temp);
        }else if(back == 'D'){
	    temp = sequence;
	    temp = move("U", temp, z);
	    frontier.addFirst(temp);
        }else if(back == 'L'){
	    temp = sequence;
	    temp = move("R", temp, z);
	    frontier.addFirst(temp);
        }else if(back == 'R'){
	    temp = sequence;
	    temp = move("L", temp, z);
	    frontier.addFirst(temp);
        }
       
    }
   
    public static void insert(int func){
 
	//BFS
	if(func == 1){
	    while(desc.size() > 0){
		Child child = desc.remove(0);
		visited.put(child.seq, str2byte(child.path));      
		if(isSolution(child.seq)){
		    printSolution(child.seq, func);
		    return;
		}
		frontier.addLast(child.seq);      
	    }
	}
	//DFS
	else if(func == 2){
	    if(desc.size() > 0){
		Child child = desc.remove(desc.size()-1);
		visited.put(child.seq, str2byte(child.path));
		if(isSolution(child.seq)){
		    printSolution(child.seq, func);
		    return;
		}
		frontier.addFirst(child.seq);
	    }      
	}
	//Iterative Deepening DFS
	else if(func == 3){    
	    if(desc.size() > 0){
		Child child = desc.remove(desc.size()-1);
		visited.put(child.seq, str2byte(child.path));      
		if(isSolution(child.seq)){
		    printSolution(child.seq, func);
		    return;
		}
		frontier.addFirst(child.seq);          
	    }
	}
	//Greedy Search
	else if(func == 4){
	    while(desc.size() > 0){
		Child child = desc.remove(0);
		child.setGreedyHeuristic(calcDist(child.seq));
		visited.put(child.seq, str2byte(child.path));      
		if(isSolution(child.seq)){
		    printSolution(child.seq, func);
		    return;
		}
		pqueue.add(child);    
	    }
	}
	//A* Search
	else if(func == 5){
	    while(desc.size() > 0){
		Child child = desc.remove(0);
		child.setHeuristic(calcDist(child.seq));
		visited.put(child.seq, str2byte(child.path));      
		if(isSolution(child.seq)){
		    printSolution(child.seq, func);
		    return;
		}
		pqueue.add(child);    
	    }
	}
   
    }
 
    public static void main(String args[]){
   
	frontier    = new LinkedList<String>();
	desc        = new ArrayList<Child>();
	visited     = new HashMap<String,byte[]>();
 
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
