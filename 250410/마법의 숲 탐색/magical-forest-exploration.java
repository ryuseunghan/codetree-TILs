import java.util.*;
import java.io.*;

public class Main {
    static int[][] graph;
    static boolean[][] connection; //연결정보 때문에 넣은거
    static int r, c, k;
    static int[] move_r = new int[]{-1, 0, 1, 0};
    static int[] move_c = new int[]{0, 1, 0, -1};
    static int ans = 0;
    static int golemNum = 1;
    public static void main(String[] args) throws IOException{
        // 0. 초기화
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        r = Integer.parseInt(st.nextToken()) + 3; // 1, 2, 3은 그래프 밖 4 ~ r
        c = Integer.parseInt(st.nextToken());
        k = Integer.parseInt(st.nextToken());
        List<Golem> GolemList = new ArrayList<>();
        graph = new int[r+1][c+1];

        for(int i = 0; i < k; i++){
            st = new StringTokenizer(br.readLine());
            int col = Integer.parseInt(st.nextToken());
            int exit = Integer.parseInt(st.nextToken());
            GolemList.add(new Golem(col, exit));
        }

        connection = new boolean[GolemList.size()+1][GolemList.size()+1];
        for(int i = 1; i <= GolemList.size(); i++){
            connection[i][i] = true;
        }
        // 탐색
        for(Golem golem : GolemList){
            while(true){
                int num = checkAndMove(golem);
                if(num == -1){
                    graph = new int[r+1][c+1];
                    break;
                }
                if(num == 4){
                    break;
                }
            }
        }
        System.out.println(ans);
    }
    static int fairyMove(Golem golem){
        // 두가지 경우수
        // 골렘을 타고 이동할 수 있는 경우
        // 아니면 자신의 위치에서 row + 1하는 경우

        // 골렘 타고 이동하는 경우, 시작점은 exit
        int[] start = new int[]{golem.row + move_r[golem.exit], golem.col + move_c[golem.exit]};

        // exit 근처의 골렘들은 connection을 통해 연결
        for(int i = 0; i < 4; i++){
            int[] adjNode = new int[]{start[0] + move_r[i], start[1]+ move_c[i]};
            if(!canMove(adjNode)) continue;
            int startGolemNum = graph[golem.row][golem.col];
            int adjGolemNum = graph[adjNode[0]][adjNode[1]];
            if(adjGolemNum != 0){
                connection[startGolemNum][adjGolemNum] = true;
                connection[adjGolemNum][startGolemNum] = true;
            }
        }
        boolean[][] visited = new boolean[r+1][c+1];
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(start);
        int loc = 4;
        while(!queue.isEmpty()){
            int[] node = queue.poll();
            for(int i = 0; i < 4; i++){
                int[] newNode = new int[]{node[0] + move_r[i], node[1]+ move_c[i]};
                // 그래프 내부이고, 방문하지 않고, 이동 가능한 골렘(connection)일 때 이동
                if(canMove(newNode) && !visited[newNode[0]][newNode[1]] && connection[graph[newNode[0]][newNode[1]]][graph[node[0]][node[1]]]){
                    visited[newNode[0]][newNode[1]] = true;
                    queue.offer(newNode);
                    loc = Math.max(loc, newNode[0]);
                    if(loc == r) break;
                }
            }
            if(loc == r) break;
        }

        //디버깅용
//        System.out.println(ans);
//
//        for(int i = 4; i <= r; i++){
//            for(int j = 1; j <= c; j++){
//                if(graph[i][j] > 0) System.out.print("T ");
//                else System.out.print("F ");
//            }
//            System.out.println();
//        }

        // 마지막 본인 골렘위치 +1과 타 골렘 이동 거리간에 비교
        // -3은 조정 ( 4부터 시작하므로)
        return Math.max(loc, golem.row + 1) - 3;
    }
    static boolean canMove(int[] node){
        int row = node[0];
        int col = node[1];
        if(row <= r && row >= 4 && col >=1 && col <= c) return true;
        return false;
    }

    static int checkAndMove(Golem golem){
        int[] point_low = new int[]{golem.row + 1, golem.col}; // 하
        int[] point_right = new int[]{golem.row, golem.col + 1}; // 우
        int[] point_left = new int[]{golem.row, golem.col - 1}; // 좌
        int[] point_high = new int[]{golem.row - 1, golem.col}; // 상
        // 남쪽 가능 - 오 아래 왼 비교
        if(point_low[0] + 1 <= r && graph[point_low[0] + 1][point_low[1]] == 0&& graph[point_right[0] + 1][point_right[1]] == 0&& graph[point_left[0] + 1][point_left[1]] == 0){
            golem.move(1, 0, 0);
            return 1;
        }
        // 서쪽 회전 - 상 좌 하 비교
        if(point_left[1] - 1 > 0 && graph[point_high[0]][point_high[1] - 1] == 0&& graph[point_left[0]][point_left[1] - 1] == 0&& graph[point_low[0]][point_low[1] - 1 ]== 0){
            // 좌 하 비교
            if(point_low[0] + 1 <= r && graph[point_left[0]+1][point_left[1] - 1] == 0&& graph[point_low[0]+1][point_low[1] - 1] == 0){
                golem.move(1, -1, -1);
                return 2;
            }
        }
        // 동쪽 회전 - 상 우 하 비교
        if(point_right[1] + 1 <= c && graph[point_high[0]][point_high[1] + 1] == 0&& graph[point_right[0]][point_right[1] + 1] == 0 && graph[point_low[0]][point_low[1] + 1]== 0){
            // 우 하 비교
            if(point_low[0] + 1 <= r && graph[point_right[0]+1][point_right[1] + 1] == 0&& graph[point_low[0] + 1][point_low[1] + 1]== 0){
                golem.move(1, 1, 1);
                return 2;
            }
        }
        if(point_high[0] <= 3) return -1;

        // 그래프에 골렘 표시
        graph[golem.row][golem.col] = golemNum;
        graph[golem.row+ 1][golem.col] = golemNum;
        graph[golem.row][golem.col + 1] = golemNum;
        graph[golem.row - 1][golem.col] = golemNum;
        graph[golem.row][golem.col - 1] = golemNum;
        golemNum++;

        // 페어리 무브
        ans += fairyMove(golem);

        return 4;
    }


    static class Golem{
        int row, col, exit;
        public void move(int row, int col, int exit){
            this.row += row;
            this.col += col;
            this.exit = (this.exit + exit + 4) % 4;
        }
        Golem(int col, int exit){
            this.row = 0;
            this.col = col;
            this.exit = exit;
        }
    }
}