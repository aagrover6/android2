package edu.calpoly.android.lab3;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class JokeView extends LinearLayout implements OnCheckedChangeListener{

	/** Radio buttons for liking or disliking a joke. */
	private RadioButton m_vwLikeButton;
	private RadioButton m_vwDislikeButton;
	
	/** The container for the radio buttons. */
	private RadioGroup m_vwLikeGroup;

	/** Displays the joke text. */
	private TextView m_vwJokeText;
	
	/** The data version of this View, containing the joke's information. */
	private Joke m_joke;

	/**
	 * Basic Constructor that takes only an application Context.
	 * 
	 * @param context
	 *            The application Context in which this view is being added. 
	 *            
	 * @param joke
	 * 			  The Joke this view is responsible for displaying.
	 */
	public JokeView(Context context, Joke joke) {
		super(context);

		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.joke_view, this, true);

		m_vwLikeButton = (RadioButton) findViewById(R.id.likeButton);
		m_vwDislikeButton = (RadioButton) findViewById(R.id.dislikeButton);
		m_vwLikeGroup = (RadioGroup) findViewById(R.id.ratingRadioGroup);
		m_vwJokeText = (TextView) findViewById(R.id.jokeTextView);

		m_vwLikeGroup.setOnCheckedChangeListener(this);

		setJoke(joke);
	}

	/**
	 * Mutator method for changing the Joke object this View displays. This View
	 * will be updated to display the correct contents of the new Joke.
	 * 
	 * @param joke
	 *            The Joke object which this View will display.
	 */
	public void setJoke(Joke joke) {
		this.m_joke = joke;
		this.m_vwJokeText.setText(joke.getJoke());

		// Set the checked state.
		if (this.m_joke.getRating() == Joke.UNRATED) {
			this.m_vwLikeGroup.clearCheck();
		}
		if (this.m_joke.getRating() == Joke.LIKE) {
			this.m_vwLikeButton.setChecked(true);
			this.m_vwDislikeButton.setChecked(false);
		}
		else if (this.m_joke.getRating() == Joke.DISLIKE) {
			this.m_vwDislikeButton.setChecked(true);
			this.m_vwLikeButton.setChecked(false);
		}

		this.requestLayout();
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {

		Log.d("chk", "id" + checkedId + " " + R.id.likeButton );

		if (group.equals(m_vwLikeGroup)) {
			if (checkedId == -1) {
				this.m_joke.setRating(Joke.UNRATED);
				this.m_vwLikeButton.setBackgroundResource(R.drawable.ic_action_emo_laugh_deselected);
				this.m_vwDislikeButton.setBackgroundResource(R.drawable.ic_action_emo_err_deselected);
			} else if (checkedId == R.id.likeButton) {
				this.m_joke.setRating(Joke.LIKE);
				this.m_vwLikeButton.setBackgroundResource(R.drawable.ic_action_emo_laugh);
				this.m_vwDislikeButton.setBackgroundResource(R.drawable.ic_action_emo_err_deselected);
			} else if (checkedId == R.id.dislikeButton) {
				this.m_joke.setRating(Joke.DISLIKE);
				this.m_vwDislikeButton.setBackgroundResource(R.drawable.ic_action_emo_err);
				this.m_vwLikeButton.setBackgroundResource(R.drawable.ic_action_emo_laugh_deselected);
			}
		}
	}
}
