# 1. CustomView

--> 1. CustomWidgdet에서 다루었던 내용들을 바탕으로 모바일 화면에 여러 그림들을 표시해보면서 pushpush 게임을 진행하기 위한 맵을 구현하였습니다.

##1.1 화면출력

![](http://i.imgur.com/gs5twcJ.png)

##1.2 맵 구성

화면 상단은 FrameLayout입니다. 이 FrameLayout 안에 가로, 세로 10칸씩의 맵을 만들겠습니다. 이 맵은 도형을 표시하기 위한 위치 x, y 좌표를 제공하게 됩니다.

![](http://i.imgur.com/RyEGWt1.png)

먼저 getDisplayMetrices()를 사용하여 자신의 모바일 화면 사이즈를 불러온 뒤 GROUND_SIZE( 여기서는 상수값 10으로 설정하였음. ) 나눕니다. 맵의 1칸의 단위는 unit에 저장됩니다.


        DisplayMetrics metrics = getResources().getDisplayMetrics();
        unit = metrics.widthPixels / GROUND_SIZE; // 화면의 가로사이즈

맵으로 사용할 2차원 배열입니다. 초기값으로 0을 넣었습니다. 숫자 1은 검은색 사각형이 표시됩니다. 이 2차원 배열은 x, y좌표를 위한 역할입니다. 전역변수로 선언하였습니다.

    int mapOne[][] = {
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,1,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},

    };

[MainActivity.java]

![](http://i.imgur.com/pA5UoN5.png)

(1) 화면에 그림을 그릴 객체로 Paint instance를 사용합니다.

(2) CustomView 생성자 안에서는 Paint instance에 색깔을 setColor()로 지정합니다.

[MainActivity.java]

![](http://i.imgur.com/IF1aW7P.png)

(3) onDraw()는 canvas 객체를 참조하여 화면에 그림을 그리게 해줍니다.
		
		- 메소드 (동그라미를 그릴 때)
		canvas.drawcicle( x 좌표, y 좌표, 반지름, Paint instance)
		
		- 메소드 (정사각형을 그릴 때)
		canvas.drawRectangle( 좌상단 x좌표, 좌상단 y좌표, 우하단 x좌표, 우하단 y 좌표, Paint instance)



![](http://i.imgur.com/zN2lcy9.png)

위 그림은  분홍색 동그라미를 화면에 그리는 과정을 소개합니다.

![](http://i.imgur.com/8iHoyyd.png)

(4) 2차원 배열 변수 map을 가지고 검은색 사각형을 그립니다. 먼저 onCreate()에서는 Stage 인스턴스를 생성한 다음, init()에서 Stage 안에 map을 가져옵니다. onDraw()에서는 map을 좌표계로 사용하여 검은색 상자를 필요한 곳에 그리면 됩니다.

>> Stage 클래스는 게임 유저에게 보여줄 맵을 array list로 저장하기 위한 클래스입니다. 


(5) 위에서 만든 커스텀뷰를 실제로 모바일 화면에 보여주기 위해서는 CustomView 인스턴스를 생성한 다음 "ground.addView()"에 인스턴스를 담으면 뷰와 연결됩니다.

      				    view = new CustomView(this);
    				    ground.addView(view);


-----------------------------------------

# 2. 간단한 PushPush 게임 만들기

--> PushPush 게임에 기본적인 기능들 위주로 간단한 게임을 구현, 동작시켰습니다.

##2.1 출력 화면

![](http://i.imgur.com/X7FT20h.png)

##2.2 필요한 기능

PushPush 게임을 구현하면서 중요한 기능 위주로 설명드리겠습니다.
(버튼 클릭시, 사용자의 유닛의 움직임 / 박스와 사용자 유닛이 경계선을 넘지 않도록 체크 / 근처에 있는 움직일 박스를 체크 )

### 2.2.1 사용자의 유닛의 움직임

[MainActivity.java]

![](http://i.imgur.com/UFj4RLm.png)
![](http://i.imgur.com/Qv17st6.png)

(1) 위, 아래, 오른쪽, 왼쪽 각 방향에 대한 리스너를 먼저 만듭니다.

(2) 사용자 유닛이 이동할 곳과 (만약 박스가 있다면) 박스가 이동할 곳이 경계선과 부딪히지 않는지 확인합니다.


			if(map[player_y-1][player_x] != 2 && !boxBoundaryCheck("up")) 


(3) (2)의 조건을 만족시키면 사용자 유닛의 좌표를 움직입니다.

			                    player_y = player_y - 1; 

(4) 박스를 움직이려는 경우에는 박스의 현재 위치를 '0'으로 지우고 움직여질 곳에 '1'로 표시합니다.

                    if (collisionBox("up")) {
                        map[player_y][player_x] = 0;
                        map[player_y - 1][player_x] = 1;
                    }


(5) 버튼 처리 이후에는 view.invalidate로 모바일에 올라와있는 뷰를 지우고 새로운 뷰를 만듭니다.