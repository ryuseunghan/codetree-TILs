import heapq
from collections import deque
# N: 게임판의 크기, M: 게임 턴 수, P: 산타의 수, C: 루돌프의 힘, D: 산타의 힘
n, m, p, c, d = map(int, input().split())
rx, ry = map(int, input().split())
santaInfosCopy = []
for _ in range(p):
    santaInfosCopy.append(list(map(int, input().split())))
santaInfosCopy = sorted(santaInfosCopy)
graph = [["X"] * n for _ in range(n)]
rx, ry = rx -1, ry -1
graph[rx][ry] = "R"
santaInfos = []
for pn, px, py in santaInfosCopy:
    graph[px-1][py-1] = pn -1
    santaInfos.append([pn-1, px-1, py-1])

santaNum = len(santaInfos)
santaPoint = [0]*santaNum
santaAvailialbe = [True]*santaNum
santaFaint = deque([])
santaList = [x for x in range(santaNum)]

dx = [-1,-1,0,1,1,1,0,-1]
dy = [0,1,1,1,0,-1,-1,-1]

#루돌프의 움직임
def rudolphMove(x, y):
    global rx, ry, graph, santaAvailialbe
    heapRudolph = []
    graph[x][y] = "X"
    for pn, px, py in santaInfos: 
        if santaAvailialbe[pn]:
            distance = (x-px)**2 + (y-py)**2
            heapq.heappush(heapRudolph,(distance, -px, -py, pn))
    _, px, py, pn= heapq.heappop(heapRudolph)
    px, py = -px, -py
    heapRudolph = []
    for i in range(8):
        nx, ny = x, y
        nx += dx[i]
        ny += dy[i]
        if(0 <= nx < n and 0 <= ny < n):
            distance = (nx-px)**2 + (ny-py)**2
            heapq.heappush(heapRudolph,(distance, nx, ny))
    _, nx, ny= heapq.heappop(heapRudolph)
    if graph[nx][ny] != "X":
            rudophConflict(pn, nx -x , ny - y)
            graph[nx][ny] = "R"
            rx, ry = nx, ny
    else:
        graph[nx][ny] = "R"
        rx, ry = nx, ny
#산타의 움직임
def santaMove():
    global santaInfos, graph, santaAvailialbe, santaFaint, rx, ry
    for pn, px, py in santaInfos:
        if santaAvailialbe[pn]:
            if pn in santaFaint:
                santaFaint.remove(pn)
                continue
            heapSanta = []
            graph[px][py] = "X"
            for i in range(4):
                nx, ny = px, py
                nx += dx[2*i]
                ny += dy[2*i]
                if(0 <= nx < n and 0 <= ny < n) and graph[nx][ny] not in santaList:
                    distance = (nx-rx)**2 + (ny-ry)**2
                    heapq.heappush(heapSanta,(distance, i, nx, ny, pn))
            distance = (px-rx)**2 + (py-ry)**2
            heapq.heappush(heapSanta,(distance, 4, px, py, pn))
            if heapSanta:
                distance, _, nx, ny, pn = heapq.heappop(heapSanta)
                if graph[nx][ny] != "X":
                    santaConflict(pn, nx-px, ny-py)
                else:
                    graph[nx][ny] = pn
                    santaInfos[pn] = [pn,nx,ny]

def santaPush(pn, dx, dy):
    global santaInfos, graph, santaAvailialbe
    # 해당 좌표에 santa가 있을 시 santa를 밀기
    santaInfos[pn][1] += dx
    santaInfos[pn][2] += dy
    nx = santaInfos[pn][1]
    ny = santaInfos[pn][2]
    if nx < 0  or nx >= n or ny < 0 or ny >= n:
        santaAvailialbe[pn] = False
    elif graph[nx][ny] != "X":
        pastpn = graph[nx][ny]
        graph[nx][ny] = pn
        santaPush(pastpn, dx, dy) 
    else:
        graph[nx][ny] = pn

#루돌프가 움직여서 충돌
def rudophConflict(pn, dx, dy):
    global santaInfos, graph, santaAvailialbe, santaPoint, santaFaint
    santaPoint[pn] += c
    santaFaint.append(pn)
    santaFaint.append(pn)
    nx, ny = santaInfos[pn][1]+dx * c, santaInfos[pn][2]+dy * c
    if 0 <= nx <n and 0<= ny <n:
        santaInfos[pn] = [pn, nx, ny]
        if graph[nx][ny] != "X":
           pastpn = graph[nx][ny]
           santaPush(pastpn, dx, dy)
        graph[nx][ny] = pn
    else :
        santaAvailialbe[pn] = False

#산타가 움직여서 충돌
def santaConflict(pn, dx, dy):
    global santaInfos, graph, santaAvailialbe, santaPoint
    santaPoint[pn] += d
    santaFaint.append(pn)
    nx, ny = santaInfos[pn][1]- dx * (d-1), santaInfos[pn][2]- dy * (d-1)
    if 0 <= nx <n and 0<= ny <n:
        santaInfos[pn] = [pn, nx, ny]
        if graph[nx][ny] != "X":
           pastpn = graph[nx][ny]
           santaPush(pastpn, -dx, -dy)
        graph[nx][ny] = pn
        santaInfos[pn] = [pn,nx,ny]
    else:
        santaAvailialbe[pn] = False
turn = 0
while(any(santaAvailialbe) and turn < m):
    turn += 1
    rudolphMove(rx, ry)
    santaMove()
    for i in range(santaNum):
        if santaAvailialbe[i]:
            santaPoint[i] += 1
for i in range(len(santaPoint)):
    print(santaPoint[i], end=" ")