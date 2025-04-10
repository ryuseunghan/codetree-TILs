import java.nio.Buffer;
import java.util.*;
import java.io.*;

public class Main {
    static int[][] graph;
    static Queue<Integer> sub;
    static int[] move_r = new int[]{-1, 0 ,1, 0};
    static int[] move_c = new int[]{0, 1, 0, -1};
    static boolean[][] visited;
    static int score;
    static int ans;
    static Queue<int[]> q;
    public static void main(String[] args) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        int k = Integer.parseInt(st.nextToken());
        int m = Integer.parseInt(st.nextToken());
        graph = new int[6][6];
        for(int i = 1; i <= 5; i++){
            st = new StringTokenizer(br.readLine());
            for(int j = 1; j <= 5; j++){
                graph[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        st = new StringTokenizer(br.readLine());
        sub = new LinkedList<>();
        for(int i = 0; i < m; i++){
            sub.offer(Integer.parseInt(st.nextToken()));
        }
        for(int t = 0; t < k; t++){
            ans = 0;
            // 1. 회전 찾기
            PriorityQueue<Case> pq = new PriorityQueue<>();
            for(int i = 1; i <= 3; i++){
                for(int j = 1; j<=3; j++){
                    int[][] graphCopy = new int[graph.length][];
                    for(int line = 0; line < graph.length; line++){
                        graphCopy[line] = graph[line].clone();
                    }
                    pq.offer(new Case(1, i, j, rotate_90(i, j , graphCopy)));

                    graphCopy = new int[graph.length][];
                    for(int line = 0; line < graph.length; line++){
                        graphCopy[line] = graph[line].clone();
                    }
                    pq.offer(new Case(2, i, j, rotate_180(i, j , graphCopy)));

                    graphCopy = new int[graph.length][];
                    for(int line = 0; line < graph.length; line++){
                        graphCopy[line] = graph[line].clone();
                    }
                    pq.offer(new Case(3, i, j, rotate_270(i, j , graphCopy)));
                }
            }
            Case c = pq.poll();
            if(c.score == 0) break; // 탐사 종료
            // 2. 회전
            if(c.rotate == 1) ans += rotate_90(c.r, c.c, graph);
            if(c.rotate == 2) ans += rotate_180(c.r, c.c, graph);
            if(c.rotate == 3) ans += rotate_270(c.r, c.c, graph);

            // 3. 유물 채우기
            while(!q.isEmpty()){
                int[] node = q.poll();
                graph[node[0]][node[1]] = 0;
            }
            for(int j = 1; j <= 5; j++){
                for(int i = 5; i>=1; i--){
                    if(graph[i][j] == 0) graph[i][j] = sub.poll();
                }
            }

            // 4. 유물 연쇄 획득
            while(true){
                int gain = getScore(graph);
                if(gain == 0) break;
                ans += gain;
                // 유물 채우기
                while(!q.isEmpty()){
                    int[] node = q.poll();
                    graph[node[0]][node[1]] = 0;
                }
                for(int j = 1; j <= 5; j++){
                    for(int i = 5; i>=1; i--){
                        if(graph[i][j] == 0) graph[i][j] = sub.poll();
                    }
                }
            }
            System.out.print(ans + " ");

        }
        
    }
    static class Case implements Comparable<Case>{
        int rotate, r, c, score;
        Case(int rotate, int r, int c, int score){
            this.rotate = rotate;
            this.r = r;
            this.c = c;
            this.score = score;
        }
        @Override
        public int compareTo(Case o){
            if(this.score != o.score) return o.score - this.score;
            if(this.rotate != o.rotate) return this.rotate - o.rotate;
            if(this.c != o.c) return this.c - o.c;
            return this.r - o.r;
        }
    }
    static int rotate_90(int r, int c, int[][] graphCopy){
        int[][] rotated = new int[3][3];
        for(int i = 0; i < 3 ; i++){
            for(int j = 0; j < 3 ; j++){
                rotated[j][2 - i] = graphCopy[r+i][c+j];
            }
        }

        for(int i = 0; i < 3 ; i++){
            for(int j = 0; j < 3 ; j++){
                graphCopy[r+ i][c + j] = rotated[i][j];
            }
        }
        return getScore(graphCopy);
    }
    static int rotate_180(int r, int c, int[][] graphCopy){
        int[][] rotated = new int[3][3];
        for(int i = 0; i < 3 ; i++){
            for(int j = 0; j < 3 ; j++){
                rotated[i][j] = graphCopy[r + 2 - i][c + 2 - j];
            }
        }

        for(int i = 0; i < 3 ; i++){
            for(int j = 0; j < 3 ; j++){
                graphCopy[r+ i][c + j] = rotated[i][j];
            }
        }
        return getScore(graphCopy);
    }
    static int rotate_270(int r, int c, int[][] graphCopy){
        int[][] rotated = new int[3][3];
        for(int i = 0; i < 3 ; i++){
            for(int j = 0; j < 3 ; j++){
                rotated[i][j] = graphCopy[r + j][c + 2- i];
            }
        }

        for(int i = 0; i < 3 ; i++){
            for(int j = 0; j < 3 ; j++){
                graphCopy[r + i][c + j] = rotated[i][j];
            }
        }
        return getScore(graphCopy);
    }

    // 연속된 숫자 0으로 처리하고 전체 유물 점수 총합 계산
    static int getScore(int[][] graphCopy){
        int total = 0;
        visited = new boolean[6][6];
        q = new LinkedList<>();
        for(int i = 1; i <= 5; i++){
            for(int j = 1; j <= 5; j++){
                if(!visited[i][j]){
                    score = 0;
                    int num = graphCopy[i][j];
                    Queue<int[]> subq = new LinkedList<>();
                    dfs(i, j, num, graphCopy, subq);
                    if(score >= 3) {
                        total += score;
                        while(!subq.isEmpty()){
                            q.offer(subq.poll());
                        }
                    }
                }
            }
        }
        return total;
    }
    static void dfs(int r, int c, int num, int[][] graphCopy, Queue<int[]> subq){
        visited[r][c] = true;
        subq.offer(new int[]{r, c});
        score ++;
        for(int i = 0; i < 4; i++){
            int newR = r + move_r[i];
            int newC = c + move_c[i];
            if(canMove(newR, newC) && graphCopy[newR][newC] == num){
                dfs(newR, newC, num, graphCopy, subq);
            }
        }
    }
    static boolean canMove(int r, int c){
        if(r >= 1 && r <= 5 && c >= 1 && c <= 5 && !visited[r][c]){
            return true;
        }
        return false;
    }
}