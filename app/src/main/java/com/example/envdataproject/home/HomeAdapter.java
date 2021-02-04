package com.example.envdataproject.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.envdataproject.R;

import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    private ArrayList<HomeItem> list;

    public HomeAdapter(ArrayList<HomeItem> list) {
        this.list = list;
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView time, tag, author;
        ViewHolder(View view) {
            super(view);
            time = view.findViewById(R.id.home_item_time_tv);
            tag = view.findViewById(R.id.home_item_tag_tv);
            author = view.findViewById(R.id.home_item_author_tv);
        }
    }

    @NonNull
    @Override
    public HomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.home_item,parent,false);

        return new HomeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final HomeAdapter.ViewHolder holder, int position) {
        final HomeItem item = list.get(position);
        holder.time.setText(item.getTime());
        holder.tag.setText(item.getTag().toString());
        holder.author.setText(item.getAuthor());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity activity = (Activity) view.getContext();
                Intent intent = new Intent(view.getContext(), HomeDetailActivity.class);
                intent.putExtra("idx",item.getIdx());
                view.getContext().startActivity(intent);
                activity.overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
