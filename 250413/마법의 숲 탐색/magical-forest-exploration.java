import java.io.*;
import java.util.*;

public class Main {
	static int r, c, k;
	static int[] moveR = new int[] {-1, 0, 1, 0};
	static int[] moveC = new int[] {0, 1, 0, -1};
	static int[][] graph;
	static int golemNum = 1;
	static List<Golem> golemList = new LinkedList<>();
	static boolean flag;
    public static void main(String[] args) throws IOException{
    	// 초기화
    	Scanner sc = new Scanner(System.in);
        r = sc.nextInt();// 가로길이
        c = sc.nextInt();// 세로길이
        k = sc.nextInt();// 골렘 수
        graph = new int[r+4][c+1];
        for(int i = 0; i < k; i++) {
        	golemList.add(new Golem(sc.nextInt(), sc.nextInt(), golemNum));
        	golemNum++;
        }
        int score = 0;
        // 골렘이동
        for(Golem golem : golemList) {
        	flag = false;
        	golemMove(golem);
        	if(flag) continue;
        	// 이동 가능 골렘 분류
        	checkCanExit(golem);
        	// 이동이 끝날 시 점수 계산
        	score += calScore(golem);
        	// 디버깅용
//            for(int i = 1; i <= r+3; i++) {
//            	for(int j = 1; j <= c; j++) {
//            		System.out.print(graph[i][j]+" ");
//            	}
//            	System.out.println();
//            }
        	
        }
        
        System.out.println(score);
    }
    static int calScore(Golem golem) {
    	int maxRow = golem.r + 1;
    	Queue<int[]> queue = new LinkedList<>();
    	queue.offer(new int[] {golem.r, golem.c});
    	boolean[][] visited = new boolean[r+4][c+1];
    	visited[golem.r][golem.c] = true;
    	while(!queue.isEmpty()) {
    		int[] node = queue.poll();
    		int row = node[0];
    		int col = node[1];
    		for(int i = 0 ; i < 4; i++) {
    			int newR = row + moveR[i];
    			int newC = col + moveC[i];
    			// 여기서부터 같은 넘버만 바꿔줌
    			if(inBoard(newR, newC) && !visited[newR][newC] && graph[newR][newC] == golem.golemNum) {
    				visited[newR][newC] = true;
    				queue.offer(new int[] {newR, newC});
    				if(maxRow < newR) maxRow = newR;
    			}
    		}
    	}
    	return maxRow - 3;
    }
    // 입장 가능한지 확인
    static void checkCanExit(Golem golem) {
    	Queue<int[]> queue = new LinkedList<>();
    	queue.offer(new int[] {golem.r + moveR[golem.dir], golem.c+moveC[golem.dir], golem.golemNum});
    	boolean[][] visited = new boolean[r+4][c+1];
    	int[] node = queue.poll();
    	int golemR = node[0];
		int golemC = node[1];
		int golemNum = node[2];
		int diffNum = -1;
    	for(int i = 0 ; i < 4; i++) {
			int newR = golemR + moveR[i];
			int newC = golemC + moveC[i];
			if(inBoard(newR, newC) && graph[newR][newC] != golemNum && graph[newR][newC] != 0) {
				diffNum =graph[newR][newC];
				graph[newR][newC] = golemNum;
				visited[newR][newC] = true;
				queue.offer(new int[] {newR, newC, golemNum});
			}
		}
    	while(!queue.isEmpty()) {
    		node = queue.poll();
    		golemR = node[0];
    		golemC = node[1];
    		golemNum = node[2];
    		for(int i = 0 ; i < 4; i++) {
    			int newR = golemR + moveR[i];
    			int newC = golemC + moveC[i];
    			// 여기서부터 같은 넘버만 바꿔줌
    			if(inBoard(newR, newC) && !visited[newR][newC] && graph[newR][newC] == diffNum) {
    				graph[newR][newC] = golemNum;
    				visited[newR][newC] = true;
    				queue.offer(new int[] {newR, newC, golemNum});
    			}
    		}
    	}
    }
    
    static void golemMove(Golem golem) {
    	// 남쪽 이동 가능 시 남쪽 이동
    	if(canSouth(golem)) {
    		golem.r += 1;
    		golemMove(golem);
    	}else if(canWest(golem)) {
    		golem.r += 1;
    		golem.c -= 1;
    		golem.dir = (golem.dir - 1 + 4) % 4;
    		golemMove(golem);
    	}else if(canEast(golem)) {
    		golem.r += 1;
    		golem.c += 1;
    		golem.dir = (golem.dir + 1) % 4;
    		golemMove(golem);
    	}else {
    		int row = golem.r;
    		int col = golem.c;
    		if(!inBoard(row -1, col)) {
    			graph = new int[r+4][c+1];
    			flag = true;
    			return;
    		}
    		graph[row][col] = golem.golemNum;
    		graph[row+1][col] = golem.golemNum;
    		graph[row-1][col] = golem.golemNum;
    		graph[row][col+1] = golem.golemNum;
    		graph[row][col-1] = golem.golemNum;
    	}
    }
    static boolean canEast(Golem golem) {
    	int row = golem.r;
    	int col = golem.c;
    	if(inBoardForMove(row, col+2) && graph[row-1][col+1] == 0 && graph[row][col+2] == 0 && graph[row+1][col+1] == 0) {
    		col = golem.c + 1;
        	if(inBoardForMove(row+2, col) && graph[row+1][col+1] == 0 && graph[row+2][col] == 0) {
    			return true;
    		}
    	}
    	return false;
    }    

    static boolean canWest(Golem golem) {
    	int row = golem.r;
    	int col = golem.c;
    	if(inBoardForMove(row, col-2) && graph[row-1][col-1] == 0 && graph[row][col-2] == 0 && graph[row+1][col-1] == 0) {
    		col = golem.c - 1;
        	if(inBoardForMove(row+2, col) && graph[row+1][col-1] == 0 && graph[row+2][col] == 0) {
    			return true;
    		}
    	}
    	return false;
    }    
    static boolean canSouth(Golem golem) {
    	int row = golem.r;
    	int col = golem.c;
    	if(inBoardForMove(row+2, col) && graph[row+1][col-1] == 0 && graph[row+2][col] == 0 && graph[row+1][col+ 1] == 0) {
    		return true;
    	}
    	return false;
    }
    static boolean inBoard(int row, int col) {
    	if(row >= 4 && row <= r+3 && col > 0 && col <= c) return true;
    	return false;
    }
    static boolean inBoardForMove(int row, int col) {
    	if(row >= 1 && row <= r+3 && col > 0 && col <= c) return true;
    	return false;
    }
    static class Golem {
    	int r, c, dir, golemNum;
    	Golem(int c, int dir, int golemNum){
    		this.c = c;
    		this.dir = dir;
    		this.r = 2;
    		this.golemNum = golemNum;
    	}
    }
}