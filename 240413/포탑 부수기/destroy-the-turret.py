from collections import deque
# row : n, col : m, k: order
n, m, k = map(int, input().split())
board = [ [0] *(m+1)
          for _ in range(n+1)
         ]
k_board = [[0] *(m+1) for _ in range(n+1)]
visited_board = [[False] *(m+1) for _ in range(n+1)]
participate_board = [[False] *(m+1) for _ in range(n+1)]
back_x = [[0] *(m+1) for _ in range(n+1)]
back_y = [[0] *(m+1) for _ in range(n+1)]
for num in range(1, n+1):
    board[num] = [0] + list(map(int,input().split()))
minimum = 5001
maximum = 0
# 우 하 좌 상
dx=[0,1,0,-1]
dy=[1,0,-1,0]
# cannon 용
cdx=[-1,-1,-1,0,1,1,1,0]
cdy=[-1,0,1,1,1,0,-1,-1]

potab = 0
# min max 구하기
def min_max():
    global minimum, maximum, potab, visited_board
    potab = 0
    minimum = 5001
    maximum = 0
    for i in range(1, n+1):
        for j in range(1, m+1):
            # 부서진 포탑 제외
            if board[i][j] >0:
                minimum = min(board[i][j], minimum)
                maximum = max(board[i][j], maximum)
                potab += 1
            else:
                visited_board[i][j] = True
# 공격자 선정
def offender_choice(time):
    global board, k_board, participate_board
    min_max()
    if potab == 1:
        return
    min_list =[]
    for i in range(1, n+1):
        for j in range(1, m+1):
            if board[i][j] == minimum:
                min_list.append((i,j))
    if len(min_list) > 1:
        rec_list = []
        rec = 0
        for min_elem in min_list:
            x, y = min_elem[0], min_elem[1]
            if k_board[x][y] > rec:
                rec = k_board[x][y]
                rec_list = [min_elem]
            elif k_board[x][y] == rec:
                rec_list.append(min_elem)

        if len(rec_list) > 1:
            row_col_max_list = []
            row_col_max = 0
            for rec_elem in rec_list:
                x, y = rec_elem[0], rec_elem[1]
                if x+y > row_col_max:
                    row_col_max = (x+y)
                    row_col_max_list=[rec_elem]
                elif x+y == row_col_max:
                    row_col_max_list.append(rec_elem)

            if len(row_col_max_list) > 1:
                col_max = 0
                for row_col_max_elem in row_col_max_list:
                    y = row_col_max_elem[1]
                    if y > col_max:
                        col_max = y
                        col_max_elem = row_col_max_elem
                offender = col_max_elem
            else:
                offender = row_col_max_list.pop()
        else:
            offender =  rec_list.pop()
    else:
        offender =  min_list.pop()
    board[offender[0]][offender[1]] += (m + n)
    k_board[offender[0]][offender[1]] = time
    participate_board[offender[0]][offender[1]] = True
    return offender
# 타겟 선정
def target_choice(damage, offender):
    global board, k_board, participate_board
    max_list = []
    for i in range(1, n+1):
        for j in range(1, m+1):
            if board[i][j] == maximum and (i,j) != offender:
                max_list.append((i,j))
    if len(max_list) > 1:
        old_list = []
        old = 1001
        for max_elem in max_list:
            x, y = max_elem[0], max_elem[1]
            if k_board[x][y] < old:
                old = k_board[x][y]
                old_list = [max_elem]
            elif k_board[x][y] == old:
                old_list.append(max_elem)

        if len(old_list) > 1:
            row_col_min_list = []
            row_col_min = 101
            for old_elem in old_list:
                x, y = old_elem[0], old_elem[1]
                if x+y < row_col_min:
                    row_col_min = (x+y)
                    row_col_min_list=[old_elem]
                elif x+y == row_col_min:
                    row_col_min_list.append(old_elem)

            if len(row_col_min_list) > 1:
                col_min = 11
                for row_col_min_elem in row_col_min_list:
                    y = row_col_min_elem[1]
                    if y < col_min:
                        col_min = y
                        col_min_elem = row_col_min_elem
                target = col_min_elem
            else:
                target = row_col_min_list.pop()
        else:
            target =  old_list.pop()
    else:
        target =  max_list.pop()
    participate_board[target[0]][target[1]] = True
    board[target[0]][target[1]] =max(board[target[0]][target[1]] - damage, 0)
    return target



# 공격 타입 설정 후 공격
def offend(offender, target):
    global visited_board, participate_board, board
    q = deque()
    q.append(offender)
    ox, oy = offender[0], offender[1]
    dmg = board[ox][oy]
    tx, ty = target[0], target[1]
    can_attack = False
    while q and not can_attack:
        cur = q.popleft()
        for i in range(4):
            x, y = cur[0], cur[1]
            nx, ny = x+ dx[i], y+dy[i]
            if nx == n+1: nx = 1
            elif nx == 0 : nx = n
            if ny == m+1: ny = 1
            elif ny == 0: ny = m
            if board[nx][ny] > 0 and not visited_board[nx][ny] and (ox, oy) != (nx, ny):
                visited_board[nx][ny] = True
                back_x[nx][ny] = x
                back_y[nx][ny] = y
                q.append((nx,ny))
            if (nx, ny) == target:
                back_x[nx][ny] = x
                back_y[nx][ny] = y

                can_attack = True
                break
    # razor
    if can_attack:
        cx = back_x[tx][ty]
        cy = back_y[tx][ty]

        while not (cx == ox and cy == oy):
            board[cx][cy] -= dmg // 2
            if board[cx][cy] < 0:
                board[cx][cy] = 0
            participate_board[cx][cy] = True

            next_cx = back_x[cx][cy]
            next_cy = back_y[cx][cy]

            cx = next_cx
            cy = next_cy
    else:
        #canon
        for direction in range(8):
            cx, cy  = tx, ty
            cx += cdx[direction]
            cy += cdy[direction]
            if cx == n+1: cx = 1
            elif cx == 0 : cx = n
            if cy == m+1: cy = 1
            elif cy == 0: cy = m
            if board[cx][cy] and (cx,cy) != offender:
                participate_board[cx][cy] = True
                board[cx][cy] = max(board[cx][cy] - dmg // 2, 0)

def restore():
    global board
    for row in range(1,n+1):
        for col in range(1,m+1):
            if (not participate_board[row][col]) and (board[row][col] > 0):
                board[row][col] += 1


for time in range(1,k+1):
    offender_1 = offender_choice(time)
    if potab == 1:
        break
    target_1 = target_choice(board[offender_1[0]][offender_1[1]], offender_1)
    offend(offender_1, target_1)
    restore()
    # praticpant board, offend board 초기화 필요
    visited_board = [[False] *(m+1) for _ in range(n+1)]
    participate_board = [[False] *(m+1) for _ in range(n+1)]

min_max()
print(maximum)