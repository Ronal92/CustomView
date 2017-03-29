package com.jinwoo.android.customview;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    CustomView view;

    FrameLayout ground;
    Button btnUp, btnDown, btnLeft, btnRight;

    private static final int GROUND_SIZE = 10;

    // 이동단위
    int unit = 0;

    // 플레이어 정보
    int player_x = 0;
    int player_y = 0;
    int player_radius = unit / 2;

    // 게임 완료 변수(목적지 개수)
    int numOfPush;

    Stage stage;
    // 스테이지 레벨
    int stage_level = 1;
    // 맵 정보 < Stage 에서 맵을 꺼내서 담아주는 변수
    int map[][];



    // 목적지들만 표시되는 맵으로 사용하게 된다.
    final int pushmap[][] = {
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stage = new Stage();
        init();

        ground = (FrameLayout)findViewById(R.id.ground);
        btnUp = (Button)findViewById(R.id.btnUp);
        btnDown = (Button)findViewById(R.id.btnDown);
        btnLeft = (Button)findViewById(R.id.btnLeft);
        btnRight = (Button)findViewById(R.id.btnRight);

        btnUp.setOnClickListener(this);
        btnDown.setOnClickListener(this);
        btnLeft.setOnClickListener(this);
        btnRight.setOnClickListener(this);

        view = new CustomView(this);
        ground.addView(view);


    }

    private void init(){
        // 자신의 화면 사이즈를 구한다.
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        unit = metrics.widthPixels / GROUND_SIZE; // 화면의 가로사이즈
        player_radius = unit / 2;

        // player 시작 지점
        player_x = 5;
        player_y = 5;

        map = stage.getStage(stage_level);

        /*
         map에 있는 push 자리를
          pushmap에 저장합니다.
         */
        for(int i = 0 ; i < map.length ; i++) {
            for (int j = 0; j < map[0].length; j++) {
                    if(map[i][j] == 3){
                        pushmap[i][j] = 3;
                        pushmap[i][j] = 0;
                    }

            }
        }

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnUp:
                if(map[player_y-1][player_x] != 2 && !boxBoundaryCheck("up")) {
                    player_y = player_y - 1; // 1 단위당 unit 만큼 그려준다.
                    if (collisionBox("up")) {
                        map[player_y][player_x] = 0;
                        map[player_y - 1][player_x] = 1;
                    }
                }
                break;
            case R.id.btnDown:
                if(map[player_y+1][player_x] != 2 && !boxBoundaryCheck("down")) {
                    player_y = player_y + 1;
                    if (collisionBox("down") ) {
                        map[player_y][player_x ] = 0;
                        map[player_y + 1][player_x] = 1;
                    }
                }
                break;
            case R.id.btnLeft:
                if(map[player_y][player_x-1] != 2 && !boxBoundaryCheck("left")) {
                    player_x = player_x - 1;
                    if (collisionBox("left")) {
                        map[player_y][player_x ] = 0;
                        map[player_y][player_x - 1] = 1;
                    }
                }
                break;
            case R.id.btnRight:
                if(map[player_y][player_x+1] !=2 && !boxBoundaryCheck("right")) {
                    player_x = player_x + 1;
                    if (collisionBox("right")) { // 오른쪽에 박스가 있다면!
                        map[player_y][player_x ] = 0;
                        map[player_y][player_x + 1] = 1;
                    }
                }
                break;
        }

        /*
         게임을 끝내기 위한 조건 맵에서 push가 있는지 확인하고 개수를 구한다.
         */
        numOfPush = 0;
        for( int i = 0; i < map.length ; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if(pushmap[i][j] == 3 && map[i][j] !=1 ){
                    map[i][j] = 3;
                }
                if( map[i][j] == 3){
                    numOfPush++;
                }
            }
        }

        /*
         게임을 끝냈으면 다음 스테이지로.....
         */
        if(numOfPush == 0){
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Finish");
            dialog.setMessage("Stage Clear!!!");
            dialog.setPositiveButton("Next", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                    stage_level++;
//                    init(stage_level);
//                    view.invalidate();
                    Toast.makeText(MainActivity.this,"Congraturation",Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            dialog.show();

        }

        // 화면을 지운후에 onDraw()를 호출해준다.
        view.invalidate();

    }



    private boolean boxBoundaryCheck(String direction){

        if(direction.equals("up")) {
                // 근처에 박스가 있는지 확인!        // 박스를 움직였을 때 바운더리랑 부딪히는지 확인!
            if (map[player_y - 1][player_x] == 1 && map[player_y - 2][player_x] == 2) {
                return true;
            }
        }else if(direction.equals("down")) {
            if (map[player_y + 1][player_x] == 1 && map[player_y +2][player_x] == 2 ) {
                return true;
            }
        }else if(direction.equals("left")){
            if (map[player_y][player_x - 1] == 1 && map[player_y][player_x-2] == 2) {
                return true;
            }
        }else if(direction.equals("right")){
            if( map[player_y][player_x+1] == 1 && map[player_y][player_x + 2] == 2) {
                return true;
            }
        }
        return false;
    }

    private boolean collisionBox(String direction){

        if(direction.equals("up")){
            if( map[player_y][player_x] == 1){
                return true;
            }
        }else if(direction.equals("down")){
            if( map[player_y][player_x] == 1){
                return true;
            }
        }else if(direction.equals("left")){
            if( map[player_y][player_x] == 1){
                return true;
            }
        }else if(direction.equals("right")){
            if( map[player_y][player_x] == 1){
                return true;
            }
        }
        return false;
    }

    class CustomView extends View {
        // # onDraw 함수에서 그림 그리기
        // 1. 색상을 정의
        Paint magenta = new Paint();
        Paint black = new Paint();
        Paint gray = new Paint();
        Paint red = new Paint();

        public CustomView(Context context) {
            super(context);
            magenta.setColor(Color.MAGENTA);
            black.setColor(Color.BLACK);
            gray.setColor(Color.LTGRAY);
            red.setColor(Color.RED);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            // 플레이어를 화면에 그린다.
            canvas.drawCircle(player_x*unit + player_radius, player_y*unit + player_radius, player_radius, magenta);

            for(int i = 0 ; i < map.length ; i++){
                for(int j = 0; j < map[0].length ; j++){
                    switch(map[i][j]){
                        case 1 :
                            // 움직일 돌
                            canvas.drawRect(unit*j, unit*i, unit*j+unit, unit*i+unit, black);
                            break;
                        case 2 :
                            // boundary
                            canvas.drawRect(unit*j, unit*i, unit*j+unit, unit*i+unit, gray);
                            break;
                        case 3 :
                            // 목적지
                            canvas.drawRect(unit*j, unit*i, unit*j+unit, unit*i+unit, red);
                            break;

                    }

                }
            }
        }

    }
}


