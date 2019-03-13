package c.gingdev.circularlistview.CircularListView;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class CircularListView extends RelativeLayout implements CircularAdapter.CircularItemChangeListener {
  public CircularListView ( Context context ) {
    super( context );
    init();
  }
  public CircularListView ( Context context, AttributeSet attrs ) {
    super( context, attrs );
    init();
  }
  public CircularListView ( Context context, AttributeSet attrs, int defStyleAttr ) {
    super( context, attrs, defStyleAttr );
    init();
  }
  public CircularListView ( Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes ) {
    super( context, attrs, defStyleAttr, defStyleRes );
    init();
  }

  private CircularTouchListener circularTouchListener;

  public float itemWidth = 0;
  public float itemHeight = 0;
  public float layoutWidth;
  public float layoutHeight;
  public float layoutCenter_X;
  public float layoutCenter_Y;
  public float radius;

  private double intervalAngle = Math.PI / 4;
  private double preIntervalAngle = Math.PI / 4;

  public ArrayList<View> itemViewList;
  private CircularAdapter circularAdapter;

  public static float moveAccumulator = 0;

  private void init () {
    post(new Runnable() {
      @Override
      public void run () {
        layoutWidth = getWidth();
        layoutHeight = getHeight();
        layoutCenter_X = layoutWidth / 2;
        layoutCenter_Y = layoutHeight / 2;
        radius = layoutWidth / 3;
      }
    });

    itemViewList = new ArrayList<>();
    circularTouchListener = new CircularTouchListener();
    setOnTouchListener( circularTouchListener );
  }

  /**
   * 원형의 반지름을 설정함.
   * @param r radius of circle. 기본값 : layoutWidth / 3
   * */
  public void setRadius(float r) {
    r = ( r < 0 ) ? 0 : r;
    radius = r;

    if ( circularAdapter != null )
      circularAdapter.notifyItemChange();
  }

  /**
   * click Listener 지정
   * */
  public void setOnItemClickListener( CircularTouchListener.CircularItemClickListener listener ) {
    circularTouchListener.setCircularItemClickListener( listener );
  }

  /**
   * 각 뷰마다의 간격을 구함
   *
   * @return degree
   * */
  public double getIntervalAngle() {
    return intervalAngle;
  }

  @Override
  public void onCircularItemChanged () {
    setItemPosition();
  }

  /**
   * 뷰에다가 커스텀뷰를 넣음.
   * */
  public void setAdpater( CircularAdapter adapter ) {
    this.circularAdapter = adapter;
    circularAdapter.setOnItemChangeListener( this );
    setItemPosition();
  }

  private void setItemPosition() {
    int itemCnt = circularAdapter.getCount();
    int existChildCnt = getChildCount();
    boolean isLayoutEmpty = existChildCnt == 0;

    preIntervalAngle = isLayoutEmpty ? 0 : 2.0f * Math.PI / (double) existChildCnt;
    intervalAngle = 2.0f * Math.PI / (double) itemCnt;

    for ( int i = 0; i < circularAdapter.getCount(); i++ ) {
      final int idx = i;
      final View item = circularAdapter.getItemAt( i );

      if ( item.getParent() == null ) {
        item.setVisibility( View.INVISIBLE );
        addView( item );
      }

      item.post(new Runnable() {
        @Override
        public void run () {
          itemWidth = item.getWidth();
          itemHeight = item.getHeight();

          /*
          * 원의 공식에따 아이템의 위치를 지정.
          * margin left -> x = h + r * cos(theta)
          * margin top -> y = k + r * sin(theta)
          */
          ValueAnimator valueAnimator = new ValueAnimator();
          valueAnimator.setFloatValues( (float) preIntervalAngle, (float) intervalAngle );
          valueAnimator.setDuration( 500 );
          valueAnimator.setInterpolator( new OvershootInterpolator() );
          valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate ( ValueAnimator animation ) {
              float value = (Float) (animation.getAnimatedValue());
              RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) item.getLayoutParams();
              params.setMargins(
                      (int) (layoutCenter_X - (itemWidth / 2)
                        + (radius * Math.cos( idx * value + CircularListView.moveAccumulator * Math.PI * 2 ))),
                      (int) (layoutCenter_Y - (itemHeight / 2)
                              + (radius * Math.sin( idx * value + CircularListView.moveAccumulator * Math.PI * 2 ))),
                      0,
                      0);
              item.setLayoutParams( params );
            }
          });
          valueAnimator.start();
          item.setVisibility( View.VISIBLE );
        }
      });
    }

//    리스트에서 없는 아이템을 Parent 에서 제거.
    for ( int i = 0; i < itemViewList.size(); i++ ) {
      View itemAfterChanged = itemViewList.get( i );
      if ( circularAdapter.getAllView().indexOf( itemAfterChanged ) == -1) {
        removeView( itemAfterChanged );
      }
    }
    itemViewList = (ArrayList<View>) circularAdapter.getAllView().clone();
  }
}
