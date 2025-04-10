import java.util.*;
import java.io.*;

public class Main {
    static int n, m, k;
    static int[][] maze;
    static Person[] people;
    static int[] exit;
    static int[] move_r = new int[]{-1, 1, 0, 0}; // 상하 좌우
    static int[] move_c = new int[]{0, 0, -1, 1};
    static int moveDistance = 0;
    static int leftPeople;
    static boolean[] arrive;
    public static void main(String[] args) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        n = Integer.parseInt(st.nextToken()); // 미로 크기
        m = Integer.parseInt(st.nextToken()); // 참가자 수
        k = Integer.parseInt(st.nextToken()); // 게임 시간
        maze = new int[n+1][n+1];
        people = new Person[m];
        arrive = new boolean[m];
        leftPeople = m;
        for(int i = 1; i <= n; i++){
            st = new StringTokenizer(br.readLine());
            for(int j = 1; j <= n ; j++){
                maze[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        for(int i = 0; i < m; i++){
            st = new StringTokenizer(br.readLine());
            people[i] = new Person(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
        }
        st = new StringTokenizer(br.readLine());
        exit = new int[]{Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken())};

        while(k > 0 && leftPeople > 0){
            k--;
            // 참가자 움직임
            for(int i = 0; i < m; i++){
                if(!arrive[i]){ //도착하지 않은 사용자인 경우
                    Person p = people[i];
                    // 움직이기
                    personMove(p, i);
                }
            }
            // 돌리기
            rotateProcess();

        }

        System.out.println(moveDistance);
        System.out.println(exit[0] + " "+exit[1]);
    }
    static void rotateProcess(){
        // 정사각형 구하기
        for(int len = 1; len <= n-1; len++){
            for(int i = 1; i <= n - len; i++){
                for(int j = 1; j <= n - len; j++){
                    if(inRectangle(i, j, i+ len, j+ len) && inRectangleExit(i, j, i+ len, j+ len)){
                        rotate(i, j, i+ len, j+ len);
                        return;
                    }
                }
            }
        }
    }
    static void rotate(int r1, int c1, int r2, int c2){
        int len = r2 - r1 + 1;
        int[][] rotated = new int[len][len];
        int[] newExit = new int[2];
        Person[] newPeople = new Person[m];

        // 회전 전에 미리 rotated 채우고, maze 값 감소
        for(int i = 0 ; i < len; i++){
            for(int j = 0 ; j < len; j++){
                int origR = r1 + len - 1 - j;
                int origC = c1 + i;

                // 회전된 위치 계산
                rotated[i][j] = Math.max(maze[origR][origC] - 1, 0); // 장애물 감소 (최소 0)

                // 참가자 위치 회전
                for(int p = 0; p < people.length ; p++){
                    if(people[p].r == origR && people[p].c == origC){
                        newPeople[p] = new Person(r1 + i, c1 + j); // 위치 갱신
                    }
                }

                // 출구 위치 회전
                if(exit[0] == origR && exit[1] == origC){
                    newExit[0] = r1 + i;
                    newExit[1] = c1 + j;
                }
            }
        }
        for(int p = 0; p < people.length ; p++){
            if(newPeople[p]!=null) people[p] = newPeople[p];
        }

        // 회전 결과를 maze에 반영
        for(int i = 0 ; i < len; i++){
            for(int j = 0 ; j < len; j++){
                maze[r1 + i][c1 + j] = rotated[i][j];
            }
        }

        // 출구 좌표 갱신
        exit[0] = newExit[0];
        exit[1] = newExit[1];
    }
    static boolean inRectangle(int r1, int c1, int r2, int c2){
        for(int i = 0; i < m; i++){
            Person p = people[i];
            if(!arrive[i] && p.r >=  r1 && p.r <= r2 && p.c >= c1 && p.c <= c2) return true;
        }
        return false;
    }
    static boolean inRectangleExit(int r1, int c1, int r2, int c2){
        if(exit[0] >=  r1 && exit[0] <= r2 && exit[1] >= c1 && exit[1] <= c2) return true;
        return false;
    }


    static void personMove(Person p, int num){
        int distance = calculateDistance(p.r, p.c);
        for(int i = 0; i < 4; i++){
            int newR = p.r + move_r[i];
            int newC = p.c + move_c[i];
            if(inMaze(newR, newC) && maze[newR][newC] <= 0 && distance > calculateDistance(newR, newC)){
                p.set(newR, newC);
                moveDistance++;
                if(newR == exit[0] && newC == exit[1]){
                    leftPeople--;
                    arrive[num] = true;
                }
                break;
            }
        }
    }
    static boolean inMaze(int r, int c){
        if(r > 0 && r <=n && c > 0 && c <= n) return true;
        else return false;
    }
    static int calculateDistance(int r, int c){
        return Math.abs(exit[0]- r) + Math.abs(exit[1] - c);
    }
    static class Person{
        int r, c;
        Person(int r, int c){
            this.r = r;
            this.c =c;
        }
        void set(int r, int c){
            this.r = r;
            this.c =c;
        }
    }

}