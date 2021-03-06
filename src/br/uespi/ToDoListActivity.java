package br.uespi;

import java.util.ArrayList;

import br.uespi.daos.ListDAO;
import br.uespi.models.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ToDoListActivity extends Activity {
	private ListDAO listDataSource;

	private TextView todoHello;

	private ListView myListView;

	private EditText todoEditText;

	private ArrayList<String> todoItems;
	private ArrayAdapter<String> aa;

	private ArrayList<List> allLists = null;

	private AdapterContextMenuInfo acmi = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		listDataSource = new ListDAO(this);
		listDataSource.open();

		setContentView(R.layout.to_do_list);

		String name = "";

		if (getIntent().hasExtra("name")) {
			name = getIntent().getStringExtra("name");
		} else {
			name = "Nada";
		}

		todoHello = (TextView) findViewById(R.id.to_do_hello);

		todoHello.setText(name);

		myListView = (ListView) findViewById(R.id.list_view);

		todoEditText = (EditText) findViewById(R.id.to_do_edit_text);

		todoItems = new ArrayList<String>();

		allLists = listDataSource.getAllLists();

		for (List list : allLists) {
			todoItems.add(list.toString());
		}

		aa = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, todoItems);

		myListView.setAdapter(aa);

		todoEditText.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN)
					if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
						todoItems.add(0, todoEditText.getText().toString());
						aa.notifyDataSetChanged();
						listDataSource.createList(todoEditText.getText()
								.toString());
						todoEditText.setText("");
						Log.w("MyCalc", "List adicionando com sucesso!");
						Toast toast = Toast
								.makeText(getApplicationContext(),
										R.string.todo_list_toast_add,
										Toast.LENGTH_LONG);
						toast.show();
						return true;
					}
				return false;
			}
		});

		myListView
				.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
					@Override
					public void onCreateContextMenu(ContextMenu menu, View v,
							ContextMenuInfo menuInfo) {
						acmi = (AdapterContextMenuInfo) menuInfo;
						menu.setHeaderTitle(R.string.to_todolist_menu_title);
						menu.add(0, 0, 0, R.string.to_todolist_edit);
						menu.add(0, 1, 1, R.string.to_todolist_destroy);
					}
				});
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		List list;
		if (item.getItemId() == 0) {
			String desc = todoItems.get(acmi.position);

			todoItems.remove(acmi.position);
			aa.notifyDataSetChanged();

			list = listDataSource.getListByDescription(desc);

			todoEditText.setText(list.getDescription());

			listDataSource.deleteList(list);
		} else if (item.getItemId() == 1) {
			String desc = todoItems.get(acmi.position);

			todoItems.remove(acmi.position);
			aa.notifyDataSetChanged();

			list = listDataSource.getListByDescription(desc);

			listDataSource.deleteList(list);

			Toast toast = Toast.makeText(getApplicationContext(),
					R.string.todo_list_toast_rmv, Toast.LENGTH_LONG);
			toast.show();
		} else {
			return false;
		}
		return true;
	}

	@Override
	protected void onResume() {
		listDataSource.open();
		super.onResume();
	}

	@Override
	protected void onPause() {
		listDataSource.close();
		super.onPause();
	}
}
