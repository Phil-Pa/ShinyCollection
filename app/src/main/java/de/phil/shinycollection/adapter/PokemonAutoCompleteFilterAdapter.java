package de.phil.shinycollection.adapter;

import android.content.*;
import android.widget.*;

import androidx.annotation.*;

import org.jetbrains.annotations.*;

import java.util.*;

public class PokemonAutoCompleteFilterAdapter extends ArrayAdapter<String> {

    private List<String> tempItems;
    private List<String> suggestions;

    public PokemonAutoCompleteFilterAdapter(Context context, @LayoutRes int resource, List<String> items) {
        super(context, resource, items);
        tempItems = new ArrayList<>(items); // this makes the difference.
        suggestions = new ArrayList<>();
    }

    @NotNull
    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((String) resultValue);
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (String names : tempItems) {
                    if (names.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(names);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();

                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //noinspection unchecked
            List<String> filterList = (ArrayList<String>) results.values;
            if (results.count > 0) {
                clear();
                for (String names : filterList) {
                    add(names);
                    notifyDataSetChanged();
                }
            }
        }
    };
}