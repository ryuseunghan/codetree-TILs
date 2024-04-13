from collections import deque


# n 격자,m 플레이어 수,k 라운드 수
n,m,k = map(int,input().split())
board = [[0]*(n+1) for _ in range(n+1)]
px = [0]*(m+1)
py = [0]*(m+1)
p_dir = [0]*(m+1)
ps = [0]*(m+1)
pg = [0]*(m+1)
point = [0]*(m+1)
#상우하좌 시계방향
dx = [-1,0,1,0]
dy = [0,1,0,-1]
for i in range(1,n+1):
    board[i] = [0]+list(map(int,input().split()))
guns = [[0,0,0,True] for _ in range(n**2+2)]

gun_num = 0
for i in range(1,n+1):
    for j in range(1,n+1):
        if board[i][j] >0:
            gun_num+=1
            guns[gun_num] = [i,j,board[i][j],False]

for i in range(1,m+1):
    px[i],py[i],p_dir[i],ps[i] = map(int,input().split())


def move(player):
    # global px, py, p_dir, pg, guns
    # 사람과 총 이동
    nx = px[player] + dx[p_dir[player]]
    ny = py[player] + dy[p_dir[player]]
    # 격자 밖일 경우 반대로
    if nx < 1 or nx > n:
        nx = px[player] - dx[p_dir[player]]
        p_dir[player] = (p_dir[player]+2)%4
    if ny < 1 or ny > n:
        ny = py[player] - dy[p_dir[player]]
        p_dir[player] = (p_dir[player]+2)%4
    px[player], py[player] = nx, ny
    if pg[player]:
        guns[pg[player]][0] = nx
        guns[pg[player]][1] = ny
    #사람이 있을 경우
    for j in range(1, m+1):
        if j == player:
            continue
        if (nx,ny) == (px[j],py[j]):
            fight(player,j)
    #총 교체
    gun_change(player)

def gun_change(player):
    nx, ny =px[player], py[player]
    for gun_num in range(1, len(guns)):
        if guns[gun_num] == 0:
            break
        if nx == guns[gun_num][0] and ny == guns[gun_num][1] and not guns[gun_num][3]:
            # 총 없는 경우
            if pg[player] == 0 :
                pg[player] = gun_num
                guns[gun_num][3] = True
            # 총 있는 경우
            else:
                hold = pg[player]
                if guns[hold][2] <guns[gun_num][2]:
                    guns[hold][3] = False
                    pg[player] = gun_num
                    guns[gun_num][3] = True

def fight(i, j):
    if ps[i] + guns[pg[i]][2] > ps[j] + guns[pg[j]][2]:
        win = i
        lose = j
    elif ps[i] + guns[pg[i]][2] < ps[j] + guns[pg[j]][2]:
        win = j
        lose = i
    else:
        if ps[i] > ps[j]:
            win = i
            lose = j
        else:
            win = j
            lose = i
    # 승리자 포인트 획득
    point[win] += ((ps[win] + guns[pg[win]][2]) - (ps[lose] + guns[pg[lose]][2]))
    # 패배자 총 내려놓기
    guns[pg[lose]][3] = False
    pg[lose] = 0
    # 승리자 총 바꾸기
    gun_change(win)
    # 패배자 이동
    for _ in range(4):
        flag = False
        nx = px[lose] + dx[p_dir[lose]]
        ny = py[lose] + dy[p_dir[lose]]
        # 벽일 경우
        if nx < 1 or nx > n or ny < 1 or ny > n:
            p_dir[lose] = (p_dir[lose]+1)%4
            continue
        # 사람이 있을 경우
        for person in range(1, m+1):
            if person != lose and (nx,ny) == (px[person],py[person]):
                p_dir[lose] = (p_dir[lose] + 1) % 4
                flag = True
        if flag:
            continue
        px[lose], py[lose] = nx, ny
        break
    # 패배자 총 바꾸기
    gun_change(lose)

for _ in range(k):
    for a in range(1, m+1):
        move(a)
for i in range(1,m+1):
    print(point[i], end = " ")