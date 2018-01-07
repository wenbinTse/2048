package game.a2048;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Random;

import game.a2048.Constant.Direction;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {
    private int rows = Constant.ROWS, columns = Constant.COLUMNS;
    private Cell cells[][] = new Cell[rows][columns];
    private TextView currentScoreElement, maxScoreElement;
    private int maxScore, currentScore;
    private GestureDetector detector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addCells();
        init();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return detector.onTouchEvent(event);
    }

    private void addCells() {
        TableLayout table = (TableLayout) this.findViewById(R.id.table);
        for (int row = 0; row < rows; row++) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            table.addView(tableRow);
            addRow(tableRow, row);
        }
    }

    private void addRow(TableRow tableRow, int row) {
        for (int col = 0; col < columns; col++) {
            Cell textView = new Cell(this);
            cells[row][col] = textView;
            tableRow.addView(textView);
        }
    }

    private void addNum(boolean delay) {
        ArrayList<Integer> nullCells = new ArrayList<>(); // 获取为空的方块
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++) {
                if (cells[i][j].getNum() == 0) {
                    nullCells.add(i * columns + j);
                }
            }
        if (nullCells.size() == 0) {
            return;
        }
        Random random = new Random();
        int index = random.nextInt(nullCells.size());
        cells[nullCells.get(index) / rows][nullCells.get(index) % columns].setNum(random.nextInt(2) + 1, delay);
    }

    private void init() {
        detector = new GestureDetector(this,this);
        maxScoreElement = (TextView) findViewById(R.id.maxScore);
        currentScoreElement = (TextView) findViewById(R.id.currentScore);
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        maxScore = preferences.getInt(Constant.MAX_SCORE, 0);
        maxScoreElement.setText(maxScore + ""); // 获取历史最高分
        currentScore = 0;
        currentScoreElement.setText(currentScore + "");
        for (int i = 0; i < columns; i++) {
           addNum(false);
        }
    }

    /**
     * 将cells转换成字符串形式，以便比较移动前后，方块有没变化
     * @return cells的字符串形式
     */
    private String getCellsStatus() {
        String s = "";
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < columns; col++){
                s += (cells[row][col].getNum() + "/");
            }
        return s;
    }

    /**
     * 移动所有方块
     * @param direction 移动方向
     */
    private void move(Direction direction) {
        String status = getCellsStatus();
        if (direction == Direction.left)
            moveLeft();
        else if (direction == Direction.right)
            moveRight();
        else if (direction == Direction.up)
            moveUp();
        else if (direction == Direction.down)
            moveDown();
        checkGameOver(); // 每次移动完检查游戏是否结束。
        // 如果移动前后方块的状态发生了变化，直接返回；否则，随机添加设置一个空方块为1或者2，并再次检查游戏是否结束。
        if (status.equals(getCellsStatus())) {
            return;
        }
        addNum(true);
        checkGameOver();
    }

    private void moveUp() {
        for (int col = 0; col < columns; col++) {
            boolean merged = false;
            for (int row = 0; row < rows; row++) {
                merged = moveCell(Direction.up, row, col, merged);
            }
        }
    }

    private void moveDown () {
        for (int col = 0; col < columns; col++) {
            boolean merged = false;
            for (int row = rows - 1; row >= 0; row--) {
                merged = moveCell(Direction.down, row, col, merged);
            }
        }
    }

    private void moveRight () {
        for (int row = 0; row < rows; row++) {
            boolean merged = false;
            for (int col = columns - 1; col >= 0; col--) {
                merged = moveCell(Direction.right, row, col, merged);
            }
        }
    }

    private void moveLeft () {
        for (int row = 0; row < rows; row++) {
            boolean merged = false;
            for (int col = 0; col < columns; col++) {
                merged = moveCell(Direction.left, row, col, merged);
            }
        }
    }

    /**
     * 移动特定方块（再次之前，本行或本列之前的方块已经移动完毕）
     * @param direction 方向
     * @param row 第几行
     * @param col 第几列
     * @param merged 至此，本次滑屏是否导致了移动
     * @return
     */
    private boolean moveCell(Direction direction, int row, int col, boolean merged) {
        boolean mergedHere = false; // 用于标记本次函数调用中是否发生了合并
        Cell cellCurrent = cells[row][col];
        if (cellCurrent.getNum() == 0)
            return mergedHere;
        int rowNext = row + Constant.Directions[direction.ordinal()][0];
        int colNext = col + Constant.Directions[direction.ordinal()][1];
        if (rowNext >= rows || colNext >= columns || rowNext < 0 || colNext < 0)
            return mergedHere;
        Cell cellNext = cells[rowNext][colNext];
        if (!merged && cellNext.getNum() == cellCurrent.getNum()) {
            mergedHere = true;
            cellNext.setNum(cellNext.getNum() * 2);
            updateScore(cellNext.getNum() * 2);
            cellCurrent.setNum(0);
        } else if (cellNext.getNum() == 0) {
            cellNext.setNum(cellCurrent.getNum());
            cellCurrent.setNum(0);
            mergedHere = moveCell(direction, rowNext, colNext, mergedHere);
        }
        return mergedHere;
    }

    /**
     * 检查游戏是否结束
     */
    private void checkGameOver() {
        // 如果有空方块，游戏尚未结束
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < columns; col++) {
                if (cells[row][col].getNum() == 0) {
                    return;
                }
            }
        // 如果某列存在相邻的数字相等的方块，游戏尚未结束
        for (int col = 0; col < columns; col++)
            for (int row = 0; row < rows - 1; row++) {
            if (cells[row][col].getNum() == cells[row + 1][col].getNum()) {
                return;
            }
        }
        // 如果某行存在相邻的数字相等的方块，游戏尚未结束
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns - 1; col++) {
                if (cells[row][col].getNum() == cells[row][col + 1].getNum()) {
                    return;
                }
            }
        }
        // 游戏结束
        gameOver();
    }

    private void gameOver() {
        new AlertDialog.Builder(this).
                setTitle(R.string.app_name).
                setMessage(R.string.game_over).
                setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }
                ).show();
    }

    /**
     * 更新分数
     * @param add
     */
    private void updateScore(int add) {
        currentScore += add;
        currentScoreElement.setText(currentScore + "");
        // 如果当前分数大于历史最高分，更新历史最高分。
        if (currentScore > maxScore) {
            maxScore = currentScore;
            maxScoreElement.setText(maxScore + "");
            SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences.edit();
            editor.putInt(Constant.MAX_SCORE, maxScore);
            editor.commit();
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // DO NOTHING
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // DO NOTHING
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float minMove = Constant.MIN_MOVE;         //最小滑动距离
        float minVelocity = Constant.MIN_VELOCITY;      //最小滑动速度
        float beginX = e1.getX();
        float endX = e2.getX();
        float beginY = e1.getY();
        float endY = e2.getY();
        float x = Math.abs(endX - beginX);
        float y = Math.abs(endY - beginY);
        if (Math.abs(velocityX) <= minVelocity) {
            return false;
        }
        Direction direction = null;
        if(beginX - endX > minMove && x - y > minMove){   //左滑
            direction = Direction.left;
        }else if(endX - beginX > minMove && x - y > minMove){   //右滑
            direction = Direction.right;
        }else if(beginY - endY > minMove && y - x > minMove){   //上滑
           direction = Direction.up;
        }else if(endY - beginY > minMove && y - x > minMove){   //下滑
           direction = Direction.down;
        }
        if (direction != null) {
            move(direction);
        }
        return false;
    }
}
