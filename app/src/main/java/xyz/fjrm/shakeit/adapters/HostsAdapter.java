package xyz.fjrm.shakeit.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.fjrm.shakeit.R;
import xyz.fjrm.shakeit.interfaces.OnClickHostListener;
import xyz.fjrm.shakeit.model.Player;

/**
 * Clase para relacionar los datos (binding) con la lista
 * de jugadores o hosts mostrada.
 *
 * @author Francisco Javier Reyes Mangas
 */
public class HostsAdapter extends RecyclerView.Adapter<HostsAdapter.ViewHolder>{
    private List<Player> mHostsList;
    private OnClickHostListener onClickHostListener;

    public HostsAdapter() {
        mHostsList = new ArrayList<>();
    }

    public void setOnClickHostListener(OnClickHostListener listener){
        onClickHostListener = listener;
    }

    @Override
    public HostsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_hosts, parent, false);
        final ViewHolder vh = new ViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickHostListener.onClickHost(v, mHostsList.get(vh.getAdapterPosition()));
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Player aPlayer = mHostsList.get(position);
        holder.mGameNameTextView.setText(aPlayer.getName());
        holder.mGameMacTextView.setText(aPlayer.getId());
    }

    @Override
    public int getItemCount() {
        return mHostsList.size();
    }

    public void changeHosts(List<Player> hosts) {
        mHostsList = hosts;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.text_game_mac)
        TextView mGameMacTextView;
        @Bind(R.id.text_game_name)
        TextView mGameNameTextView;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}
