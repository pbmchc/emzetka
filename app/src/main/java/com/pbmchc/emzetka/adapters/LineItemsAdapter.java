package com.pbmchc.emzetka.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pbmchc.emzetka.mzkandroid.R;
import com.pbmchc.emzetka.models.Line;
import com.pbmchc.emzetka.utils.StringUtils;

import java.util.List;

/**
 * Created by Piotrek on 2016-12-16.
 */
public class LineItemsAdapter extends RecyclerView.Adapter<LineItemsAdapter.ViewHolder> {


    private List<Line> lineList;
    private Context mContext;
    private OnItemClickListener listener;

    public interface OnItemClickListener{
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView busLineName;
        public TextView busLineDirection;

        public ViewHolder(final View itemView) {

            super(itemView);

            busLineName = (TextView) itemView.findViewById(R.id.busLineName);
            busLineDirection = (TextView) itemView.findViewById(R.id.busLineDirection);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION)
                            listener.onItemClick(itemView, position);
                    }
                }
            });
        }
    }

    public LineItemsAdapter(Context context, List<Line> lines) {
        lineList = lines;
        mContext = context;
    }

    private Context getContext() {
        return mContext;
    }

    @Override
    public LineItemsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View busLinesView = inflater.inflate(R.layout.item_line, parent, false);
        return new ViewHolder(busLinesView);
    }

    @Override
    public void onBindViewHolder(LineItemsAdapter.ViewHolder viewHolder, int position) {

        Line line = lineList.get(position);
        String direction = line.getStartStation() + " - " + line.getEndStation();

        TextView lineNameTxt = viewHolder.busLineName;
        lineNameTxt.setText(StringUtils.fixLineNameForDisplaying(line.getLineName()));

        TextView directionTxt = viewHolder.busLineDirection;
        directionTxt.setText(direction);
    }

    @Override
    public int getItemCount() {
        return lineList.size();
    }
}
