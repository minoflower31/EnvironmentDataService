package com.example.envdataproject.board;

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

public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.ViewHolder> {
    private ArrayList<BoardItem> list;

    public BoardAdapter(ArrayList<BoardItem> list) {
        this.list = list;
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, person, date;
        ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.board_free_item_title_tv);
            person = view.findViewById(R.id.board_free_item_person_tv);
            date = view.findViewById(R.id.board_free_item_date_tv);
        }
    }

    @NonNull
    @Override
    public BoardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.board_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BoardAdapter.ViewHolder holder, int position) {
        final BoardItem item = list.get(position);
        holder.title.setText(item.getTitle());
        holder.person.setText(item.getPerson());
        holder.date.setText(item.getDate());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity activity = (Activity) view.getContext();
                Intent intent = new Intent(view.getContext(), BoardDetailActivity.class);
                intent.putExtra("idx",item.getIdx());
                intent.putExtra("boardName", item.getBoardName());
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
