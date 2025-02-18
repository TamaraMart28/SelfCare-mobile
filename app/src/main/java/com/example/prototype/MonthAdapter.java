package com.example.prototype;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prototype.Api.Dto.AnalyticsDto;

import java.util.List;

public class MonthAdapter extends RecyclerView.Adapter<MonthAdapter.ViewHolder> {
    private Context context;
    private List<AnalyticsDto> analyticsList;
    private String[] monthNames = new String[]{"ЯНВАРЬ", "ФЕВРАЛЬ", "МАРТ", "АПРЕЛЬ", "МАЙ", "ИЮНЬ", "ИЮЛЬ", "АВГУСТ", "СЕНТЯБРЬ", "ОКТЯБРЬ", "НОЯБРЬ", "ДЕКАБРЬ"};

    public MonthAdapter(Context context,List<AnalyticsDto> analyticsList) {
        this.analyticsList = analyticsList;
        this.context = context;
    }

    public AnalyticsDto getItem(int position) {
        return analyticsList.get(position);
    }

    public void addCurrentMonthAndYearItem(AnalyticsDto analyticsDto) {
        analyticsList.add(analyticsDto);
    }

    public int getPositionByMonthAndYear(int month, int year) {
        for (int i = 0; i < analyticsList.size(); i++) {
            AnalyticsDto analyticsDto = analyticsList.get(i);
            if (analyticsDto.getMonth() == month && analyticsDto.getYear() == year) {
                return i;
            }
        }
        return -1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.month_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AnalyticsDto analyticsDto = analyticsList.get(position);
        holder.textViewName.setText(monthNames[analyticsDto.getMonth() - 1] + " " + analyticsDto.getYear());
    }

    @Override
    public int getItemCount() {
        return analyticsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewMonth);
        }
    }
}
