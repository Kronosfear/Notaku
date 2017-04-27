package com.heavyobj.notaku;

import android.app.LauncherActivity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by admin on 4/26/2017.
 */

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>
{
    private List<Card> listitems;

    public Adapter(List<Card> listitems, Context context)
    {
        this.listitems = listitems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        Card listItem = listitems.get(position);
        holder.name.setText(listItem.getName());
        holder.watched.setText(listItem.getWatched());
    }

    @Override
    public int getItemCount()
    {
        return listitems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView name;
        public TextView watched;

        public ViewHolder(View itemView)
        {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            watched = (TextView) itemView.findViewById(R.id.watched);
        }
    }
}
