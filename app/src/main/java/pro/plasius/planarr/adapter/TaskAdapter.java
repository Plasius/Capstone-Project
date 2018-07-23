package pro.plasius.planarr.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import pro.plasius.planarr.R;
import pro.plasius.planarr.TaskActivity;
import pro.plasius.planarr.data.Task;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private ArrayList<Task> mDataset;

    public interface OnItemClickListener {
        void onItemClick(Task task);
    }

    private final OnItemClickListener listener;

    public TaskAdapter(ArrayList<Task> dataset, OnItemClickListener listener) {
        mDataset = dataset;
        this.listener = listener;
    }




    @Override
    public TaskAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mDataset.get(position).getTimestamp());

        holder.mDateView.setText(Integer.toString(calendar.get(Calendar.YEAR))+"-"
                +calendar.get(Calendar.MONTH)+"-"
                +calendar.get(Calendar.DAY_OF_MONTH));

        holder.mTitleView.setText(mDataset.get(position).getTitle());
        holder.itemView.setTag(mDataset.get(position).getTaskId());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                listener.onItemClick(mDataset.get(position));
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder{
        // each data item is just a string in this case
        TextView mTitleView;
        TextView mDateView;

        ViewHolder(View v) {
            super(v);
            mTitleView = v.findViewById(R.id.item_tv_title);
            mDateView = v.findViewById(R.id.item_tv_date);
        }

    }
}