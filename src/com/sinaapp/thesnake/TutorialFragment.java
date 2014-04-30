package com.sinaapp.thesnake;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.sinaapp.thesnake.R;

public class TutorialFragment extends Fragment {
	public static final String ARG_PAGE = "page";

    private int mPageNumber;

    public static TutorialFragment create(int pageNumber) {
    	TutorialFragment fragment = new TutorialFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.turorial_fragment_layout, container, false);

        // Set the title view to show the page number.
        ((ImageView) rootView.findViewById(R.id.imageView1)).setImageResource(R.drawable.apple);

        return rootView;
    }

    public int getPageNumber() {
        return mPageNumber;
    }

}
