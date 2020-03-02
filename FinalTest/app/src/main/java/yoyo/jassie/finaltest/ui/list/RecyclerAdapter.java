package yoyo.jassie.finaltest.ui.list;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import yoyo.jassie.finaltest.R;
import yoyo.jassie.finaltest.ui.maps.MapsFragment;
import yoyo.jassie.labtest2.DetailsActivity;
import yoyo.jassie.labtest2.model.Bookmark;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.TasksViewHolder> implements Filterable {

    private Context mCtx;
    private List<Bookmark> taskList;
    private List<Bookmark> filteredtaskList;

    private boolean noResults = true;

    public RecyclerAdapter(Context mCtx, List<Bookmark> taskList) {
        this.mCtx = mCtx;
        this.taskList = taskList;
        this.filteredtaskList = taskList;

    }

    @Override
    public TasksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view ;
        if (viewType == 0) {
            view = LayoutInflater.from(mCtx).inflate(R.layout.recycler_view_empty, parent, false);
        } else {
            view = LayoutInflater.from(mCtx).inflate(R.layout.recycler_view, parent, false);
        }

        return new TasksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TasksViewHolder holder, int position) {

        if(noResults){
            return;
        }

        Bookmark t = filteredtaskList.get(position);
        holder.textViewNameRV.setText(t.getName());
        holder.textViewCountryRV.setText(t.getCountry());
        holder.textViewGenderRV.setText(t.getGender());
        holder.textViewLatitudeRV.setText(""+t.getLatitude());
        holder.textViewLongitudeRV.setText(""+t.getLongitude());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy");
        long tt = t.getBirthday();
        Date dd = new Date(tt);
        String dateTime = dateFormat.format(dd);

        holder.textViewBirthdayRV.setText(""+dateTime);

        holder.imageView.setImageBitmap(t.getImage(mCtx));

    }


    @Override
    public int getItemViewType(int position) {
        if(noResults){
            return 0;
        }else{
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        int i = filteredtaskList.size();
        if(i <=0 ){
            noResults = true;
            i = 1;
        }else{
            noResults = false;
        }
        return i;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredtaskList = (List<Bookmark>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Bookmark> filteredResults = null;
                if (constraint.length() == 0) {
                    filteredResults = taskList;
                } else {
                    filteredResults = getFilteredResults(constraint.toString().toLowerCase());
                }

                FilterResults results = new FilterResults();
                results.values = filteredResults;

                return results;
            }
        };
    }

    protected List<Bookmark> getFilteredResults(String constraint) {
        List<Bookmark> results = new ArrayList<>();

        for (Bookmark item : taskList) {
            if (item.getName().toLowerCase().contains(constraint)) {
                results.add(item);
            }
        }
        return results;
    }

    class TasksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewNameRV, textViewCountryRV, textViewGenderRV, textViewBirthdayRV,textViewLatitudeRV,textViewLongitudeRV;
        ImageView imageView;


        public TasksViewHolder(View itemView) {
            super(itemView);

            if(noResults){
                return;
            }

            textViewNameRV = itemView.findViewById(R.id.textViewNameRecycler);
            textViewCountryRV = itemView.findViewById(R.id.textViewCountryRecycler);
            textViewGenderRV = itemView.findViewById(R.id.textViewGenderRecycler);
            textViewBirthdayRV = itemView.findViewById(R.id.textViewBirthdayRecycler);
            textViewLatitudeRV = itemView.findViewById(R.id.textViewLatitudeRecycler);
            textViewLongitudeRV = itemView.findViewById(R.id.textViewLongitudeRecycler);
            imageView = itemView.findViewById(R.id.imageViewRecycler);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(noResults){
                return;
            }

            Bookmark task = filteredtaskList.get(getAdapterPosition());

            Intent intent = new Intent(mCtx, DetailsActivity.class);
            intent.putExtra("isnew", false);
            intent.putExtra(MapsFragment.EXTRA_BOOKMARK_ID, task.getId());
            mCtx.startActivity(intent);
        }
    }
}
