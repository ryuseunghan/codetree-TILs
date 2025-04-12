import java.io.*;
import java.util.*;

public class Main {
	static int n, m, p, c, d;
	static int[] rudolf;
	static Santa[] santas;
	static int[] moveR = new int[] {-1, 0, 1, 0, -1, 1, 1, -1};
	static int[] moveC = new int[] {0, 1, 0, -1, 1, 1, -1, -1};
	
    public static void main(String[] args) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken()); // 판 길이
        m = Integer.parseInt(st.nextToken()); // 턴
        p = Integer.parseInt(st.nextToken()); // 산타수
        c = Integer.parseInt(st.nextToken()); //루돌프 충돌 시 획득 점수
        d = Integer.parseInt(st.nextToken()); //산타 충돌 시 획득 점수
        st = new StringTokenizer(br.readLine());
        rudolf = new int[] {Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken())};
        santas = new Santa[p];
        for(int i = 0; i < p; i++) {
        	st = new StringTokenizer(br.readLine());
        	int num = Integer.parseInt(st.nextToken()) - 1;
        	santas[num] = new Santa(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), 0, false, false);
        }
        for(int turn = 0; turn < m; turn++) {
        	for(Santa s : santas) {
            	if(!s.out) {
            		if(s.fainted) {
            			s.fainted = false;
            			s.preFainted = true;
            		}
            		else if(s.preFainted) {
            			s.preFainted = false;
            		}
            	}
        	}
            //  루돌프 : 가장 가까운 탈락 안한 산타에게 1칸 돌진
            PriorityQueue<Santa> pq = new PriorityQueue<>();
            for(Santa s : santas) {
            	if(!s.out) {
            		s.distance = calDistance(rudolf, new int[] {s.r, s.c});
            		pq.offer(s);
            	}
            }
            Santa closestSanta = pq.poll();
            rudolfMove(closestSanta);
            // 산타 : 루돌프에게 1칸 돌진
            boolean flag = true; // 산타 모두가 장외인 경우 조기 종료
            for(Santa s : santas) {
            	if(!s.out) {
            		if(!s.fainted && !s.preFainted) santaMove(s);
            		flag = false; 
            	}
        	}
            if(flag) break;
            
            // 탈락하지 않은 산타 + 1
            for(Santa s : santas) {
            	if(!s.out) s.point++;
            }
            
            
            //디버깅
//            System.out.println(turn + 1);
//            System.out.println(rudolf[0]+" " +rudolf[1]);
//            for(Santa s : santas) {
//            	System.out.println(" r"+s.r+" c" + s.c +" point"+s.point);
//            }
//            for(int i = 1; i <= n; i++) {
//            	for(int j = 1; j <= n; j++) {
//            		boolean bool = true;
//            		for(int k = 0; k<p; k++) {
//            			Santa s = santas[k];
//            			if(s.r == i && s.c == j) {
//            				System.out.print(k+1 +" ");
//            				bool = false;
//            				break;
//            			}
//            			
//            		}
//            		if(rudolf[0] == i && rudolf[1] == j) {
//        				System.out.print("-1");
//        				bool = false;
//        			}
//            		if(bool) System.out.print("0 ");
//            		
//            	}
//            	System.out.println();
//            }
        }
        
        for(Santa s : santas) {
        	System.out.print(s.point +" ");
        }
    }
    // 산타 움직임
    static void santaMove(Santa s) {
    	int orgDistance = calDistance(rudolf, new int[] {s.r, s.c});
    	int distance = orgDistance;
    	int dir = 0;
    	for(int i = 0; i < 4; i++) {
    		int newR = s.r + moveR[i];
    		int newC = s.c + moveC[i];
    		//보드 내부이며 산타가 없을 시
    		if(inBoard(newR, newC) && santaNotExist(newR, newC)) {
    			int newDistance = calDistance(rudolf, new int[] {newR, newC});
    			// 또, 거리 갱신 가능할 시 이동
    			if(newDistance < distance) {
    				distance = newDistance;
    				dir = i;
    			}
    		}
    	}
    	if(orgDistance != distance) {
    		s.r += moveR[dir];
    		s.c += moveC[dir];
    		// 충돌 시
    		if(s.r == rudolf[0] && s.c == rudolf[1]) {
    			santaCrash(s, dir);
    		} 
    	}
    }
    // 산타 충돌
    static void santaCrash(Santa s, int dir) {
    	dir = (dir + 2) % 4;
    	s.point += d;
    	s.r += moveR[dir] * d;
    	s.c += moveC[dir] * d;
    	s.fainted = true;
    	if(!inBoard(s.r, s.c)) {
    		s.out = true;
    		return;
    	}
    	chainCrash(s, dir);
    }
    // 루돌프 충돌
    static void rudolphCrash(Santa s, int dir) {
    	s.point += c;
    	s.r += moveR[dir] * c;
    	s.c += moveC[dir] * c;
    	s.fainted = true;
    	if(!inBoard(s.r, s.c)) {
    		s.out = true;
    		return;
    	}
    	chainCrash(s, dir);
    }
    // 연쇄 충돌
    static void chainCrash(Santa s, int dir) {
    	// 연쇄 충돌 여부 확인
    	for(Santa os : santas) {
    		if(!os.equals(s) && os.r == s.r && os.c == s.c) {
    			os.r += moveR[dir];
    	    	os.c += moveC[dir];
    	    	if(!inBoard(os.r, os.c)) {
    	    		os.out = true;
    	    		return;
    	    	}
    	    	chainCrash(os, dir);
    		}
    	}
    }
    // 루돌프 움직임
    static int rudolfMove(Santa s) {
    	int minDir = -1;
    	int minDistance = 2500;
    	for(int i = 0; i < 8; i++) {
    		int newR = rudolf[0] + moveR[i];
    		int newC = rudolf[1] + moveC[i];
    		int newDistance = calDistance(new int[] {newR, newC}, new int[] {s.r, s.c});
    		if(minDistance > newDistance) {
    			minDistance = newDistance;
    			minDir = i;
    		}
    	}
    	rudolf[0] += moveR[minDir];
    	rudolf[1] += moveC[minDir];
    	
    	// 산타 충돌 시
    	if(s.r == rudolf[0] && s.c == rudolf[1]) rudolphCrash(s, minDir);
    	
    	return minDir;
    }
    // 거리
    static int calDistance(int[] rudolf, int[] s) {
    	return (int) (Math.pow(rudolf[0] - s[0], 2) + Math.pow(rudolf[1] - s[1], 2)); 
    }
    static boolean santaNotExist(int r, int c) {
    	for(Santa s : santas) {
    		if(s.r == r && s.c == c) return false;
    	}
    	return true;
    }
    static boolean inBoard(int r, int c) {
    	if(r >= 1 && r<= n && c >= 1 && c<= n) return true;
    	return false;
    }
    static class Santa implements Comparable<Santa>{
    	int r, c, point, distance;
    	boolean fainted; // 현재 턴 스턴
    	boolean preFainted; // 다음 턴 스턴
    	boolean out;
    	Santa(int r, int c, int point, boolean fainted, boolean out){
    		this.r = r;
    		this.c = c;
    		this.point = point;
    		this.point = point;
    		this.out = out;
    		this.distance = 2500;
    		this.preFainted = false;
    	}
    	@Override
    	public int compareTo(Santa o) {
    		if(this.distance != o.distance)return this.distance - o.distance;
    		if(this.r != o.r) return o.r - this.r;
    		return o.c - this.c;
    	}
    }
}