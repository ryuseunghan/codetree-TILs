import heapq
from collections import deque
# n 격자 m 사람 수

n, m = map(int, input().split())
# 각 사람의 x 축 y축과 가고자하는 편의점의 x축 y축
px = [0] *(m+1)
py = [0] *(m+1)
cx = [0] *(m+1)
cy = [0] *(m+1)
back_x = [[0] *(n+1)for _ in range(n+1)]
back_y = [[0] *(n+1)for _ in range(n+1)]
available_people = [False] * (m+1)
board = [[0]*(n+1)
         for _ in range(n+1)
        ]
available = [[True]*(n+1)
            for _ in range(n+1)
            ]

# 상좌우하
dx = [-1,0,0,1]
dy = [0,-1,1,0]

for num in range(1,n+1):
    board[num] = [0]+list(map(int,input().split()))
for num in range(1, m+1):
    cx[num], cy[num] = map(int, input().split())
basecamp_list = []
for i in range(1, n+1):
    for j in range(1, n+1):
        if board[i][j] == 1:
            basecamp_list.append((i,j))
time = 0

# 1번 2번
def move():
    global available,available_people
    false_list = []
    minimum = min(m+1, time)
    #유효한 사람
    for p_num in range(1,minimum):
        if available_people[p_num]:
            notVisited = [row[:] for row in available]
            distance_board = [[0] * (n + 1) for _ in range(n + 1)]
            q = deque()
            ex, ey = cx[p_num], cy[p_num]
            sx, sy = px[p_num], py[p_num]
            # 입구를 탐색하기 위해서
            notVisited[sx][sy] = True
            q.append((ex, ey))
            while q:
                x, y = q.popleft()
                if (x,y) == (sx,sy):
                    break
                for direction in range(4):
                    nx, ny = x, y
                    nx += dx[direction]
                    ny += dy[direction]
                    if 0 < nx <= n and 0 < ny <= n and notVisited[nx][ny]:
                        notVisited[nx][ny] = False
                        distance_board[nx][ny] = distance_board[x][y] + 1
                        back_x[nx][ny] = x
                        back_y[nx][ny] = y
                        q.append((nx,ny))
            px[p_num],py[p_num] = back_x[x][y],back_y[x][y]
            if (px[p_num],py[p_num]) == (ex, ey):
                false_list.append((px[p_num],py[p_num]))
                #available[px[p_num]][py[p_num]] = False
                available_people[p_num] = False
    for false in false_list:
        fx, fy = false
        available[fx][fy] = False

# 3번 available 가능할 시 cx와 cy 에서 가장 가까운 베이스캠프로 px py 갱신
def to_base():
    global time, available
    #지나갈 수 없는 곳이 False, 지나간 곳도 False로 추가
    notVisited = [row[:] for row in available]
    distance_board = [[0]*(n+1) for _ in range(n+1)]
    q = deque()
    ex, ey = cx[time], cy[time]
    q.append((ex, ey))
    while q:
        x, y = q.popleft()
        for direction in range(4):
            nx, ny = x, y
            nx += dx[direction]
            ny += dy[direction]
            if  0 < nx <= n and 0 < ny <= n and notVisited[nx][ny]:
                notVisited[nx][ny] = False
                distance_board[nx][ny] = distance_board[x][y] + 1
                q.append((nx, ny))
    close_basecamps = []
    for [x,y] in basecamp_list:
        if available[x][y] and distance_board[x][y]:
            heapq.heappush(close_basecamps,(distance_board[x][y],x,y))
    distance, px[time], py[time] =  heapq.heappop(close_basecamps)
    #베이스캠프 못지나가게끔
    available[px[time]][py[time]] = False
    available_people[time] = True



while True:
    time += 1
    move()
    if time <= m:
        to_base()
    if any(available_people):
        continue
    else:
        break
print(time)