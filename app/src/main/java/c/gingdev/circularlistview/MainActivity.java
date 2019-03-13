package c.gingdev.circularlistview;

import androidx.appcompat.app.AppCompatActivity;
import c.gingdev.circularlistview.CircularListView.CircularAdapter;
import c.gingdev.circularlistview.CircularListView.CircularListView;
import c.gingdev.circularlistview.CircularListView.CircularTouchListener;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * original Code ->
 * https://github.com/JungHsuan/CircularUI
 * */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
  private CircularAdapter adapter;
  private ArrayList<String> items = new ArrayList<>();

  private CircularListView circularListView;

  private Button addBtn;
  private Button removeBtn;
  private Button enlargeBtn;
  private Button reduceBtn;

  @Override
  protected void onCreate ( Bundle savedInstanceState ) {
    super.onCreate( savedInstanceState );
    setContentView( R.layout.activity_main );

    addTempItem();

    CircularListInit();
    ViewInit();
  }

  private void ViewInit () {
    addBtn = (Button) findViewById( R.id.add_item );
    removeBtn = (Button) findViewById( R.id.remove_item );
    enlargeBtn = (Button) findViewById( R.id.enlarge_radius );
    reduceBtn = (Button) findViewById( R.id.reduce_radius );

    addBtn.setOnClickListener( this );
    removeBtn.setOnClickListener( this );
    enlargeBtn.setOnClickListener( this );
    reduceBtn.setOnClickListener( this );
  }

  private void CircularListInit () {
    circularListView = (CircularListView) findViewById( R.id.circularList );
    adapter = new CircularItemAdapter(getLayoutInflater(), items);
    circularListView.setAdpater( adapter );
    circularListView.setRadius( 100 );
    circularListView.setOnItemClickListener(new CircularTouchListener.CircularItemClickListener() {
      @Override
      public void onItemClick ( View v, int index ) {
        Toast.makeText(MainActivity.this, index +" 번째 아이템 클릭!", Toast.LENGTH_LONG).show();
      }
    });
  }

  private void addTempItem () {
    for ( int i = 0; i < 6; i++ )
      items.add( String.valueOf( i ) );
  }

  @Override
  public void onClick ( View v ) {
    switch ( v.getId() ) {
      case R.id.add_item:
        View view = getLayoutInflater().inflate( R.layout.item, null );
        TextView textView = (TextView) view.findViewById( R.id.item_Txt );
        textView.setText( String.valueOf( adapter.getCount() + 1 ) );
        adapter.addItem( view );
        break;
      case R.id.remove_item:
        adapter.removeItemAt( 0 );
        break;
      case R.id.enlarge_radius:
        circularListView.setRadius( circularListView.radius += 15 );
        break;
      case R.id.reduce_radius:
        circularListView.setRadius( circularListView.radius -= 15 );
        break;
    }
  }

  private class CircularItemAdapter extends CircularAdapter {
    private ArrayList<String> items;
    private LayoutInflater inflater;
    private ArrayList<View> itemViews;

    public CircularItemAdapter( LayoutInflater inflater, ArrayList<String> items ) {
      this.itemViews = new ArrayList<>();
      this.items = items;
      this.inflater = inflater;

      for ( final String s : items) {
        View v = inflater.inflate( R.layout.item, null );
        TextView itemView = (TextView) v.findViewById( R.id.item_Txt );
        itemView.setText( s );
        itemViews.add( v );
      }
    }

    @Override
    public ArrayList<View> getAllView () {
      return itemViews;
    }

    @Override
    public int getCount () {
      return itemViews.size();
    }

    @Override
    public View getItemAt ( int i ) {
      return itemViews.get( i );
    }

    @Override
    public void removeItemAt ( int i ) {
      if ( itemViews.size() > 0 ) {
        itemViews.remove( i );
        notifyItemChange();
      }
    }

    @Override
    public void addItem ( View view ) {
      itemViews.add( view );
      notifyItemChange();
    }
  }
}
