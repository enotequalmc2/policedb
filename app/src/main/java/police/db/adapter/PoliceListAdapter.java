package police.db.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import police.db.R;
import police.db.entity.Police;

public class PoliceListAdapter extends RecyclerView.Adapter<PoliceListAdapter.ViewHolder> {
    List<Police> polices = new ArrayList<>();
    Context context;

    public PoliceListAdapter(Context context, List<Police> polices) {
        this.context = context;
        this.polices = polices;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.police_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Police police = polices.get(position);

        holder.name.setText(police.name);
        holder.pos.setText(police.position);

        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(police.source));
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return polices.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.pc_name)
        TextView name;
        @BindView(R.id.pc_pos)
        TextView pos;

        View v;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            v = itemView;

            ButterKnife.bind(this, itemView);
        }
    }
}
