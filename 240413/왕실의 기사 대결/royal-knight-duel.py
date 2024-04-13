from collections import deque
# l:체스판, n:기사수, q:명령수
l, n, q = map(int,input().split())

board= [[0]*(l+1) for _ in range(l+1)]
kr = [0]*(n+1)
sx = [0]*(n+1)
kc = [0]*(n+1)
sy = [0]*(n+1)
kh = [0]*(n+1)
kw = [0]*(n+1)
kk = [0]*(n+1) #available도 가능해보임
bef_k = [0]*(n+1)
dmg = [0]*(n+1)
is_moved = [False] * (n+1)
orders = []
for i in range(1, l+1):
    board[i] = [0]+list(map(int,input().split()))
for i in range(1, n+1):
    kr[i], kc[i], kh[i], kw[i], kk[i] = map(int,input().split())
    bef_k[i] = kk[i]
for i in range(q):
    orders.append(list(map(int,input().split())))
#상우하좌
dx= [-1,0,1,0]
dy =[0,1,0,-1]


def try_move(knight, direction):
    global sx, sy,dmg,kr,kc
    queue = deque([])
    queue.append(knight)
    for a in range(1, n + 1):
        dmg[a] = 0
        is_moved[a] = False
        sx[a] = kr[a]
        sy[a] = kc[a]

    is_moved[knight] = True
    while queue:
        k_num = queue.popleft()
        sx[k_num] +=dx[direction]
        sy[k_num] +=dy[direction]
        ex = sx[k_num]+ kh[k_num]  - 1
        ey = sy[k_num]+ kw[k_num]  - 1
        if sx[k_num] <= 0 or ex > l or sy[k_num] <= 0 or ey > l:
            return False
        for x in range(sx[k_num], ex+1):
            for y in range(sy[k_num], ey+1):
                if board[x][y] == 1:
                    dmg[k_num] += 1
                elif board[x][y] == 2:
                    return False
        # 기사 내부에 다른 기사 있는지 확인
        for other_knight in range(1,n+1):
            if is_moved[other_knight] or kk[other_knight] <= 0:
                continue
            if kr[other_knight]  > ex or kr[other_knight] +kh[other_knight]-1 < sx[k_num]:
                continue
            if kc[other_knight]  > ey or kc[other_knight] +kw[other_knight]-1 < sy[k_num]:
                continue
            is_moved[other_knight] = True
            queue.append(other_knight)
    dmg[knight] = 0
    return True
def move(knight, direction):
    global kk, kr, kc, kk
    if kk[knight] <= 0:
        return
    if try_move(knight, direction):
        for b in range(1, n+1):
            kr[b] = sx[b]
            kc[b] = sy[b]
            kk[b] -=dmg[b]


for order in orders:
    knight, direction = order[0], order[1]
    move(knight,direction)
total = 0
for num in range(1,n+1):
    if kk[num] >0:
        total +=(bef_k[num] - kk[num])
print(total)