package edu.calpoly.android.lab3;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class AdvancedJokeList extends ActionBarActivity {

	/** Contains the name of the Author for the jokes. */
	protected String m_strAuthorName;

	/** Contains the list of Jokes the Activity will present to the user. */
	protected ArrayList<Joke> m_arrJokeList;

	/** Contains the list of filtered Jokes the Activity will present to the user. */
	protected ArrayList<Joke> m_arrFilteredJokeList;

	/** Adapter used to bind an AdapterView to List of Jokes. */
	protected JokeListAdapter m_jokeAdapter;

	/** ViewGroup used for maintaining a list of Views that each display Jokes. */
	protected ListView m_vwJokeLayout;

	/** EditText used for entering text for a new Joke to be added to m_arrJokeList. */
	protected EditText m_vwJokeEditText;

	/** Button used for creating and adding a new Joke to m_arrJokeList using the
	 *  text entered in m_vwJokeEditText. */
	protected Button m_vwJokeButton;

	/** Menu used for filtering Jokes. */
	protected Menu m_vwMenu;

	/** Background Color values used for alternating between light and dark rows
	 *  of Jokes. Add a third for text color if necessary. */
	protected int m_nDarkColor;
	protected int m_nLightColor;
	protected int m_nTextColor;

	/**
	 * Context-Menu MenuItem IDs.
	 * IMPORTANT: You must use these when creating your MenuItems or the tests
	 * used to grade your submission will fail. These are commented out for now.
	 */
	//protected static final int FILTER = Menu.FIRST;
	//protected static final int FILTER_LIKE = SubMenu.FIRST;
	//protected static final int FILTER_DISLIKE = SubMenu.FIRST + 1;
	//protected static final int FILTER_UNRATED = SubMenu.FIRST + 2;
	//protected static final int FILTER_SHOW_ALL = SubMenu.FIRST + 3;

	private android.support.v7.view.ActionMode m_actionMode;
	private android.support.v7.view.ActionMode.Callback m_actionModeCallback;
	private int positionToRemoveFrom;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		String jokes[];
		Joke joke;

		super.onCreate(savedInstanceState);

		// Get various colors needed for display.
		m_nDarkColor = this.getResources().getColor(R.color.dark);
		m_nLightColor = this.getResources().getColor(R.color.light);
		m_nTextColor = this.getResources().getColor(R.color.text);

		// Initialize jokelist and authorname.
		this.m_arrJokeList = new ArrayList<Joke>();
		this.m_arrFilteredJokeList = new ArrayList<Joke>();

		this.m_jokeAdapter = new JokeListAdapter(this, this.m_arrFilteredJokeList);
		this.m_strAuthorName = this.getResources().getString(R.string.author_name);

		jokes = this.getResources().getStringArray(R.array.jokeList);

		this.initLayout();
		this.initAddJokeListeners();

		for (int i = 0; i < jokes.length; i++) {
			Log.d("aagrover", "Adding new joke: " + jokes[i]);
			joke = new Joke(jokes[i], m_strAuthorName);
			addJoke(joke);
		}
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.mainmenu, menu);

		this.m_vwMenu = menu;
        return true;
    }

	/**
	 * Method is used to encapsulate the code that initializes and sets the
	 * Layout for this Activity.
	 */
	protected void initLayout() {
		setContentView(R.layout.advanced);
		m_vwJokeButton = (Button) this.findViewById(R.id.addJokeButton);
		m_vwJokeEditText = (EditText) this.findViewById(R.id.newJokeEditText);
		m_vwJokeLayout = (ListView) this.findViewById(R.id.jokeListViewGroup);
		assert m_vwJokeLayout != null;
		m_vwJokeLayout.setAdapter(this.m_jokeAdapter);

		m_actionModeCallback = new ActionMode.Callback() {
			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				MenuInflater inflater = mode.getMenuInflater();
				inflater.inflate(R.menu.actionmenu, menu);
				return true;
			}

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false; // Return false if nothing is done
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				switch (item.getItemId()) {
					case R.id.menu_remove:
						// Do something to remove joke...
						removeThisJoke();

						mode.finish(); // Action picked, so close the CAB
						return true;
					default:
						return false;
				}
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
				m_actionMode = null;
			}
		};
	}

	/**
	 * Removes the joke if requested by the user.
	 */
	private void removeThisJoke() {
		Joke jokeToRemove = m_arrFilteredJokeList.get(this.positionToRemoveFrom);

		m_arrJokeList.remove(jokeToRemove);
		m_arrFilteredJokeList.remove(this.positionToRemoveFrom);
		this.m_jokeAdapter.notifyDataSetChanged();
	}

	/**
	 * Method is used to encapsulate the code that initializes and sets the
	 * Event Listeners which will respond to requests to "Add" a new Joke to the
	 * list.
	 */
	protected void initAddJokeListeners() {

		m_vwJokeButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				String jokeToAdd = m_vwJokeEditText.getText().toString();
				Joke joke = new Joke(jokeToAdd, m_strAuthorName);

				m_vwJokeEditText.setText("");

				if (!jokeToAdd.isEmpty()) {
					addJoke(joke);
				}

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(m_vwJokeEditText.getWindowToken(), 0);
			}
		});

		m_vwJokeEditText.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {

				if (event.getAction() == KeyEvent.ACTION_DOWN &&
						(keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER)) {
					String jokeToAdd = m_vwJokeEditText.getText().toString();
					Joke joke = new Joke(jokeToAdd, m_strAuthorName);

					m_vwJokeEditText.setText("");

					if (!jokeToAdd.isEmpty()) {
						addJoke(joke);
					}

					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(m_vwJokeEditText.getWindowToken(), 0);

					return true;
				}
				return false;
			}
		});

		m_vwJokeLayout.requestFocus();
		this.m_vwJokeLayout.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				positionToRemoveFrom = position;
				m_actionMode = startSupportActionMode(m_actionModeCallback);
				return true;
			}
		});
	}

	/**
	 * Method used for encapsulating the logic necessary to properly add a new
	 * Joke to m_arrJokeList, and display it on screen.
	 *
	 * @param joke
	 *            The Joke to add to list of Jokes.
	 */
	protected void addJoke(Joke joke) {

		this.m_arrJokeList.add(joke);
		this.m_arrFilteredJokeList.add(joke);
		this.m_jokeAdapter.notifyDataSetChanged();
	}

	/**
	 * Updates the joke list set based on how the user filters the list.
	 *
	 * @param type the type of filter wanted - like, dislike, unused, show all.
	 * @param rating the rating we're searching for, based on the type of filter.
     */
	private void filterJokes(int type, int rating) {

		this.m_arrFilteredJokeList.clear();

		for (Joke filtered : this.m_arrFilteredJokeList) {
			for (Joke original : this.m_arrJokeList) {
				if (filtered.equals(original)) {
					original.setRating(filtered.getRating());
				}
			}
		}

		for (int i = 0; i < m_arrJokeList.size(); i++) {
			Joke joke = m_arrJokeList.get(i);

			if (joke.getRating() == rating) {
				m_arrFilteredJokeList.add(joke);
			}
		}

		if (rating == -1) {
			this.m_arrFilteredJokeList.addAll(this.m_arrJokeList);
		}

		this.m_jokeAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int rating = -1;

		switch(item.getItemId()) {
			case R.id.submenu_like:
				rating = Joke.LIKE;
				filterJokes(R.id.submenu_like, rating);
				return true;
			case R.id.submenu_dislike:
				rating = Joke.DISLIKE;
				filterJokes(R.id.submenu_dislike, rating);
				return true;
			case R.id.submenu_unrated:
				rating = Joke.UNRATED;
				filterJokes(R.id.submenu_unrated, rating);
				return true;
			case R.id.submenu_show_all:
				filterJokes(R.id.submenu_show_all, rating);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

}