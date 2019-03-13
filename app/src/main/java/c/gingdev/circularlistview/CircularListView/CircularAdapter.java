package c.gingdev.circularlistview.CircularListView;

import android.view.View;

import java.util.ArrayList;

public abstract class CircularAdapter {

  /**
   * 아이템 Count를 가져옮
   * @return Numbers of item
   * */
  public abstract int getCount();

  /**
   * 모든 아이템의 뷰들을 가져옮.
   * @return a list of view
   * */
  public abstract ArrayList<View> getAllView();

  /**
   * i번째 아이템을 가져옮.
   * @param i index of item
   * @return view at position i
   * */
  public abstract View getItemAt( int i );

  /**
   * i번째 아이템을 제거함.
   * @param i index of item
   * */
  public abstract void removeItemAt( int i );

  /**
   * 리스트에 아이템추가.
   * */
  public abstract void addItem( View view );

  private CircularItemChangeListener circularItemChangeListener;


  /**
   * 부모뷰에게 아이템이 변경됨을 알려야할경우.
   * */
  public void notifyItemChange() {
    circularItemChangeListener.onCircularItemChanged();
  }

  public void setOnItemChangeListener(CircularItemChangeListener listener) {
    this.circularItemChangeListener = listener;
  }

  interface CircularItemChangeListener {
    void onCircularItemChanged();
  }
}
