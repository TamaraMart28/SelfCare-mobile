package com.example.prototype.mentalArticle;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prototype.ActivityMainMenu;
import com.example.prototype.Api.Dto.mentalArticle.MentalArticleDto;
import com.example.prototype.Api.VkrService;
import com.example.prototype.R;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

public class MentalArticleAdapter extends RecyclerView.Adapter<MentalArticleAdapter.MentalArticleViewHolder>{
    private Context context;
    private List<MentalArticleDto> mentalArticleList;
    private CompositeDisposable disposable = new CompositeDisposable();
    private final VkrService vkrService = new VkrService();

    public MentalArticleAdapter(Context context, List<MentalArticleDto> mentalArticleList) {
        this.context = context;
        this.mentalArticleList = mentalArticleList;
    }

    public void addMentalArticles(List<MentalArticleDto> newArticles) {
        int startPosition = mentalArticleList.size();
        mentalArticleList.addAll(newArticles);
        notifyItemRangeInserted(startPosition, newArticles.size());
    }

    public void clear() {
        int size = mentalArticleList.size();
        mentalArticleList.clear();
        notifyItemRangeRemoved(0, size);
    }

    @NonNull
    @Override
    public MentalArticleAdapter.MentalArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.mental_article_item_layout, parent, false);
        return new MentalArticleAdapter.MentalArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MentalArticleAdapter.MentalArticleViewHolder holder, int position) {
        MentalArticleDto mentalArticle = mentalArticleList.get(position);

        holder.textViewName.setText(mentalArticle.getName());
        holder.textViewAuthor.setText(mentalArticle.getAuthor());
        holder.imageView.setImageResource(R.drawable.meditation);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment selectedFragment = new ScrollingFragmentMentalArticle();
                Bundle bundle = new Bundle();
                bundle.putLong("mentalArticleId", mentalArticle.getId());
                selectedFragment.setArguments(bundle);
                ((ActivityMainMenu)context).pushFragmentToStack(R.id.navigation_collections, selectedFragment);
                ((ActivityMainMenu)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).addToBackStack(null).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mentalArticleList.size();
    }

    public static class MentalArticleViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName;
        public TextView textViewAuthor;
        public ImageView imageView;

        public MentalArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.mentalArticleName);
            textViewAuthor = itemView.findViewById(R.id.mentalArticleInfoTest);
            imageView = itemView.findViewById(R.id.mentalArticleImageTest);
        }
    }
}
