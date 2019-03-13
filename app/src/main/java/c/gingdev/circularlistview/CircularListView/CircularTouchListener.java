package c.gingdev.circularlistview.CircularListView;

import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

public class CircularTouchListener implements View.OnTouchListener {
  private CircularItemClickListener circularItemClickListener;
  private float initX = 0;
  private float initY = 0;
  private float preX = 0;
  private float preY = 0;
  private float curX = 0;
  private float curY = 0;
  private float moveX = 0;
  private float moveY = 0;
  private float minClickDistance = 30.0f;
  private float minMoveDistance = 30.0f;
  private float movingSpeed = 2000.0f; // 기본속도 2000.0f, 값이 커질수록 속도는빨라짐
  private boolean isCircularMoving = false; //아이템을 클릭한건지 이동한건지 판단하는 변수.

  public void setCircularItemClickListener( CircularItemClickListener listener ) {
    this.circularItemClickListener = listener;
  }

  @Override
  public boolean onTouch ( View v, MotionEvent event ) {

    final CircularListView circularView = (CircularListView) v;

    switch ( event.getAction() ) {
      case MotionEvent.ACTION_DOWN:
        curX = event.getX();
        curY = event.getY();
        initX = event.getX();
        initY = event.getY();
//        Break 안하고 그냥계속이어감.

      case MotionEvent.ACTION_MOVE:
        preX = curX;
        preY = curY;
        curX = event.getX();
        curY = event.getY();

        float diffX = curX - preX;
        float diffY = curY - preY;

        moveX = initX - preX;
        moveY = initY - preY;

        float moveDistance = (float) Math.sqrt(Math.pow( moveX, 2 ) + Math.pow( moveY, 2 ));

        if ( curY >= circularView.layoutCenter_Y ) diffX = -diffX;
        if ( curX <= circularView.layoutCenter_X ) diffY = -diffY;

        if ( moveDistance > minMoveDistance ) {
          isCircularMoving = true;
          CircularListView.moveAccumulator += (diffX + diffY) / movingSpeed;

          for ( int i = 0; i < circularView.itemViewList.size(); i++ ) {
            final int idx = i;
            final View itemView = circularView.itemViewList.get( i );
            itemView.post( new Runnable() {
              @Override
              public void run () {
                RelativeLayout.LayoutParams params
                        = (RelativeLayout.LayoutParams) itemView.getLayoutParams();
                params.setMargins(
                        (int) (circularView.layoutCenter_X - (circularView.itemWidth / 2) +
                                (circularView.radius * Math.cos( idx * circularView.getIntervalAngle() + CircularListView.moveAccumulator * Math.PI * 2))),
                        (int) (circularView.layoutCenter_Y - (circularView.itemHeight / 2) +
                                (circularView.radius * Math.sin( idx * circularView.getIntervalAngle() + CircularListView.moveAccumulator * Math.PI * 2))),
                        0,
                        0 );
                itemView.setLayoutParams( params );
                itemView.requestLayout();
              }
            });
          }
        }
        return true;

        case MotionEvent.ACTION_UP:
          moveDistance = (float) Math.sqrt(Math.pow( moveX, 2 ) + Math.pow( moveY, 2 ));
          if ( moveDistance < minClickDistance && !isCircularMoving ) {
            for ( int i = 0; i < circularView.itemViewList.size(); i++ ) {
              View view = circularView.itemViewList.get( i );
              if ( isTouchInside( curX, curY, view ) ) {
                circularItemClickListener.onItemClick( view, i );

//                임시 애니메이션
                ScaleAnimation animation = new ScaleAnimation( 0.5f, 1.0f, 0.5f, 1.0f,
                        ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                        ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration( 300 );
                animation.setInterpolator( new OvershootInterpolator() );
                view.startAnimation( animation );
              }
            }
          }
          isCircularMoving = false; // ACTION_UP 때 moving을알리는 변수 초기화.
          return true;
    }
    return false;
  }

  private boolean isTouchInside(float x, float y, View view) {
    float left = view.getX();
    float top = view.getY();
    float width = view.getWidth();
    float height = view.getHeight();
    return ( x > left && x < left + width && y > top && y < top + height );
  }

  public interface CircularItemClickListener {
    void onItemClick(View v, int index);
  }
}
