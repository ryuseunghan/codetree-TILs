import java.io.*;
import java.util.*;

public class Main {
	static int n, m;
	static int[] start, end;
	static Sol[] Sols;
	static int[][] graph;
	static int[] medusa;
	static int[] moveR = new int[] {-1, 1, 0, 0};
	static int[] moveC = new int[] {0, 0, -1, 1};
	static int solMoved;
	static int maxRockedSol;
	static int attack;
    public static void main(String[] args) throws IOException{
    	// 초기화
    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    	StringTokenizer st = new StringTokenizer(br.readLine());
    	n = Integer.parseInt(st.nextToken());
    	m = Integer.parseInt(st.nextToken());
    	st = new StringTokenizer(br.readLine());
    	start = new int[] {Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken())};
    	end = new int[] {Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken())};
    	Sols = new Sol[m];
    	st = new StringTokenizer(br.readLine());
    	for(int i = 0; i < m; i++) {
    		int r = Integer.parseInt(st.nextToken());
    		int c = Integer.parseInt(st.nextToken());
    		Sols[i] = new Sol(r, c, false, true);
    	}
    	graph = new int[n][n];
    	for(int i = 0 ; i < n ; i++) {
    		st = new StringTokenizer(br.readLine());
    		for(int j = 0 ; j < n ; j++) {
    			graph[i][j] = Integer.parseInt(st.nextToken());
    		}
    	}
    	
    	
    	medusa = new int[] {start[0], start[1]};
    	while(!(end[0] == medusa[0] && end[1] == medusa[1])) {
    		solMoved = 0;
    		maxRockedSol = 0;
    		attack = 0;

    		//메두사 이동, 공원까지 도달 못할 시 -1 출력 후 종료 
    		mMove();
    		if(medusa[0] == -1) {
    			System.out.println(-1);
    			break;
    		}
    		// 메두사 공원 도달 시 0 출력 후 종료
    		if(medusa[0] == end[0] && medusa[1] == end[1]) {
    			System.out.println(0);
    			break;
    		}
    		// 메두사 이동한 칸에 전사 있을 시 삭제
    		solDel();
    		
    		// 메두사 시선
    		boolean[][] rocked = glance();
    		
    		// 시야 내부 전사 석화
    		for(int i = 0; i < m; i++) {
    			Sol sol = Sols[i];
    			if(sol.alive && rocked[sol.r][sol.c]) {
    				sol.isRock = true;
    			}
    		}
    		
    		// 전사 이동
    		for(int i = 0; i < m; i++) {
    			Sol sol = Sols[i];
    			if(sol.alive && !sol.isRock) {
    				sMove(sol, rocked);
    				sMove(sol, rocked);
    			}
    		}
    		// 전사 비석화
    		for(int i = 0; i < m; i++) {
    			Sol sol = Sols[i];
    			if(sol.alive && sol.isRock) {
    				sol.isRock = false;
    			}
    		}
    		System.out.println(solMoved + " " + maxRockedSol + " " + attack);
    	}
    }
    // ------------------------------------------------
    // 전사 이동 - 최단 거리 계산 후 이 중 상하좌우
    static void sMove(Sol sol, boolean[][] rocked) {
    	int resultR = sol.r;
    	int resultC = sol.c;
    	int origin = sDistance(new int[] {resultR, resultC});
    	int distance = origin;
    	if(!sol.alive) return;
    	for(int i = 0; i < 4 ; i++) {
    		int newR = sol.r + moveR[i];
    		int newC = sol.c + moveC[i];
    		// 그래프 내부이며 시야 밖일 때
    		if(inGraph(newR, newC) && !rocked[newR][newC]) {
    			// 거리 구하기
    			int newDistacne = sDistance(new int[] {newR, newC});
    			// 최단거리 갱신 시 갱신
    			if(distance > newDistacne) { 
    				resultR = newR;
    				resultC = newC;
    				distance = newDistacne;
    			}
    		}
    		// 메두사 위치와 겹칠 시 공격 후 소멸
    		if(medusa[0] == newR && medusa[1] == newC) {
    			attack++;
    			sol.alive = false;
    			solMoved++;
    		}
    	}
    	// rocked로 인해 못지나가지 않았으면 거리 갱신
    	if(distance != origin) {
        	sol.r = resultR;
        	sol.c = resultC;
        	solMoved++;
    	}
    }
    static int sDistance(int[] sol) {
    	boolean[][] visited = new boolean[n][n];
    	Queue<int[]> mq = new LinkedList<>();
    	mq.offer(new int[] {sol[0], sol[1], 0});
    	while(!mq.isEmpty()) {
    		int[] loc = mq.poll();
    		for(int i = 0; i < 4 ; i++) {
    			int newR = loc[0] + moveR[i];
        		int newC = loc[1] + moveC[i];
        		if(inGraph(newR, newC) && !visited[newR][newC]) {
        			if(newR == medusa[0] && newC == medusa[1]) {
        				return loc[2]+1;
        			}
        			mq.offer(new int[] {newR, newC, loc[2]+1});
        			visited[newR][newC] = true;
        		}
    		}
    	}
    	return -1;
    }
    // ------------------------------------
    // 메두사 시야 돌로 바꾸기
    static boolean[][] glance() {
    	boolean[][] rocked;
    	int findDir = 0;
    	maxRockedSol = 0;
    	for(int dir = 0; dir < 4; dir++) {
    		rocked = firstGlance(new boolean[n][n], dir);
    		rocked = secondGlance(rocked, dir);
    		int rockedSol = 0;
    		// 살아있으며, rocked 위치에 있는 전사 수 구한 후 maxSol, findDir 갱신
    		for(int i = 0; i < m; i++) {
    			Sol sol = Sols[i];
    			if(sol.alive && rocked[sol.r][sol.c]) {
    				rockedSol++;
    			}
    		}
    		if(maxRockedSol < rockedSol) {
    			maxRockedSol = rockedSol;
    			findDir = dir;
    		}
    	}
    	
    	rocked = firstGlance(new boolean[n][n], findDir);
		rocked = secondGlance(rocked, findDir);
    	return rocked;
    }
    // 1. 메두사 바로보는 방향 전부 rocked 처리
    static boolean[][] firstGlance(boolean[][] rocked, int dir){
		// 상
		if(dir == 0) {
			int width = 0;
			for(int i = medusa[0]; i >= 0; i--) {
				for(int j = medusa[1] - width; j <= medusa[1] + width; j++) {
					if(inGraph(i, j)) {
						rocked[i][j] = true;
					}
				}
				width++;
			}
		}
		// 하
		if(dir == 1) {
			int width = 0;
			for(int i = medusa[0]; i < n; i++) {
				for(int j = medusa[1] - width; j <= medusa[1] + width; j++) {
					if(inGraph(i, j)) {
						rocked[i][j] = true;
					}
				}
				width++;
			}
		}
		// 좌
		if(dir == 2) {
			int width = 0;
			for(int j = medusa[1]; j >= 0; j--) {
				for(int i = medusa[0] - width; i <= medusa[0] + width; i++) {
					if(inGraph(i, j)) {
						rocked[i][j] = true;
					}
				}
				width++;
			}
		}
		// 우
		if(dir == 3) {
			int width = 0;
			for(int j = medusa[1]; j < n; j++) {
				for(int i = medusa[0] - width; i <= medusa[0] + width; i++) {
					if(inGraph(i, j)) {
						rocked[i][j] = true;
					}
				}
				width++;
			}
		}
		rocked[medusa[0]][medusa[1]] = false;
		return rocked;
    }
    
    // 2. 전사 위치에 맞추어 메두사 시야 처리
    static boolean[][] secondGlance(boolean[][] rocked, int dir){
		// 전사 후방 돌 막아주기 - 메두사 위치 기반해서 좌 중 우 판별
		if(dir == 0) { // 상
			for(int s = 0; s < m; s++) {
				Sol sol = Sols[s];
				if(sol.alive && rocked[sol.r][sol.c]) { // 살아있고 돌이 되는 위치일 때
					if(sol.c < medusa[1]) {// 메두사보다 c이 작을 때
						int width = 0;
		    			for(int i = sol.r; i >= 0; i--) {
		    				for(int j = sol.c - width; j <= sol.c; j++) {
		    					if(inGraph(i, j)) {
		    						rocked[i][j] = false; // 돌 해제
		    					}
		    				}
		    				width++;
		    			}
		    			rocked[sol.r][sol.c] = true; // 본인 위치는 돌로
					}
					if(sol.c == medusa[1]) {
		    			for(int i = sol.r; i >= 0; i--) {
		    				rocked[i][sol.c] = false; // 돌 해제
		    			}
		    			rocked[sol.r][sol.c] = true; // 본인 위치는 돌로

					}
					if(sol.c > medusa[1]) {
						int width = 0;
						for(int i = sol.r; i >= 0; i--) {
		    				for(int j = sol.c; j <= sol.c + width; j++) {
		    					if(inGraph(i, j)) {
		    						rocked[i][j] = false; // 돌 해제
		    					}
		    				}
		    				width++;
		    			}
		    			rocked[sol.r][sol.c] = true; // 본인 위치는 돌로
					}
				}
			}
		}
		if(dir == 1) { //하
			for(int s = 0; s < m; s++) {
				Sol sol = Sols[s];
				if(sol.alive && rocked[sol.r][sol.c]) { // 살아있고 돌이 되는 위치일 때
					if(sol.c < medusa[1]) {// 메두사보다 r이 작을 때
						int width = 0;
		    			for(int i = sol.r; i < n; i++) {
		    				for(int j = sol.c - width; j <= sol.c; j++) {
		    					if(inGraph(i, j)) {
		    						rocked[i][j] = false; // 돌 해제
		    					}
		    				}
		    				width++;
		    			}
		    			rocked[sol.r][sol.c] = true; // 본인 위치는 돌로
					}
					if(sol.c == medusa[1]) {
						for(int i = sol.r; i < n; i++) {
		    				rocked[i][sol.c] = false; // 돌 해제
		    			}
		    			rocked[sol.r][sol.c] = true; // 본인 위치는 돌로

					}
					if(sol.c > medusa[1]) {
						int width = 0;
						for(int i = sol.r; i < n; i++) {
		    				for(int j = sol.c; j <= sol.c + width; j++) {
		    					if(inGraph(i, j)) {
		    						rocked[i][j] = false; // 돌 해제
		    					}
		    				}
		    				width++;
		    			}
		    			rocked[sol.r][sol.c] = true; // 본인 위치는 돌로
					}
				}
			}

		}
		if(dir == 2) { // 좌
			for(int s = 0; s < m; s++) {
				Sol sol = Sols[s];
				if(sol.alive && rocked[sol.r][sol.c]) { // 살아있고 돌이 되는 위치일 때
					if(sol.r < medusa[0]) {// 메두사보다 r이 작을 때
						int width = 0;
		    			for(int j = sol.c; j >= 0; j--){
		    				for(int i = sol.r; i >= sol.r - width; i--) {
		    					if(inGraph(i, j)) {
		    						rocked[i][j] = false; // 돌 해제
		    					}
		    				}
		    				width++;
		    			}
		    			rocked[sol.r][sol.c] = true; // 본인 위치는 돌로
					}
					if(sol.r == medusa[0]) {
						for(int j = sol.c; j >= 0; j--) {
		    				rocked[sol.r][j] = false; // 돌 해제
		    			}
		    			rocked[sol.r][sol.c] = true; // 본인 위치는 돌로

					}
					if(sol.r > medusa[0]) {
						int width = 0;
						for(int j = sol.c; j >= 0; j--) {
							for(int i = sol.r; i <= sol.r + width; i++) {
		    					if(inGraph(i, j)) {
		    						rocked[i][j] = false; // 돌 해제
		    					}
		    				}
		    				width++;
		    			}
		    			rocked[sol.r][sol.c] = true; // 본인 위치는 돌로
					}
				}
			}

		}
		if(dir == 3) { // 우
			for(int s = 0; s < m; s++) {
				Sol sol = Sols[s];
				if(sol.alive && rocked[sol.r][sol.c]) { // 살아있고 돌이 되는 위치일 때
					if(sol.r < medusa[0]) {// 메두사보다 r이 작을 때
						int width = 0;
		    			for(int j = sol.c; j < n; j++) {
		    				for(int i = sol.r; i >= sol.r - width; i--) {
		    					if(inGraph(i, j)) {
		    						rocked[i][j] = false; // 돌 해제
		    					}
		    				}
		    				width++;
		    			}
		    			rocked[sol.r][sol.c] = true; // 본인 위치는 돌로
					}
					if(sol.r == medusa[0]) {
						for(int j = sol.c; j < n; j++) {
		    				rocked[sol.r][j] = false; // 돌 해제
		    			}
		    			rocked[sol.r][sol.c] = true; // 본인 위치는 돌로

					}
					if(sol.r > medusa[0]) {
						int width = 0;
						for(int j = sol.c; j < n; j++) {
							for(int i = sol.r; i <= sol.r + width; i++) {
		    					if(inGraph(i, j)) {
		    						rocked[i][j] = false; // 돌 해제
		    					}
		    				}
		    				width++;
		    			}
		    			rocked[sol.r][sol.c] = true; // 본인 위치는 돌로
					}
				}
			}

		}
		return rocked;
    }
    // 메두사 이동한 칸에 전사 있을 시 삭제
    static void solDel(){
    	for(int i = 0; i < m; i++) {
    		if(medusa[0] == Sols[i].r && medusa[1] == Sols[i].c) {
    			Sols[i].alive = false;
    		}
    	}
    }
    //메두사 이동 - 최단 거리 계산 후 이 중 상하좌우
    static void mMove() {
    	int resultR = medusa[0];
    	int resultC = medusa[1];
    	int distance = 2500; // n이 50이므로
    	for(int i = 0; i < 4 ; i++) {
    		int newR = medusa[0] + moveR[i];
    		int newC = medusa[1] + moveC[i];
    		// 그래프 내부이며 도로 위일 때
    		if(inGraph(newR, newC) && graph[newR][newC] == 0) {
    			int newDistacne = mDistance(new int[] {newR, newC});
    			if(newDistacne == -1) {
    				medusa[0] = -1;
    				medusa[1] = -1;
    				return; // 공원에 도달할 수 없을 때 처리
    			}
    			if(distance > newDistacne) { // 최단거리 갱신 시 갱신
    				resultR = newR;
    				resultC = newC;
    				distance = newDistacne;
    			}
    		}
    	}
    	medusa[0] = resultR;
    	medusa[1] = resultC;
    }
    static int mDistance(int[] medusa) {
    	if(medusa[0] == end[0] && medusa[1] == end[1]) {
			return 0;
		}
    	boolean[][] visited = new boolean[n][n];
    	Queue<int[]> mq = new LinkedList<>();
    	mq.offer(new int[] {medusa[0], medusa[1], 0});
    	while(!mq.isEmpty()) {
    		int[] loc = mq.poll();
    		
    		for(int i = 0; i < 4 ; i++) {
    			int newR = loc[0] + moveR[i];
        		int newC = loc[1] + moveC[i];
        		if(inGraph(newR, newC) && graph[newR][newC] == 0 && !visited[newR][newC]) {
        			if(newR == end[0] && newC == end[1]) {
        				return loc[2]+1;
        			}
        			mq.offer(new int[] {newR, newC, loc[2]+1});
        			visited[newR][newC] = true;
        		}
    		}
    	}
    	return -1;
    }
    static boolean inGraph(int r, int c) {
    	if(r < n && r >= 0 && c < n && c >=0) return true;
    	return false;
    }
	static class Sol{
		int r, c;
		boolean isRock, alive;
		Sol(int r, int c, boolean isRock, boolean alive){
			this.r = r;
			this.c = c;
			this.isRock = isRock;
			this.alive = alive;
		}
	}

}
