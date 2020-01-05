package co.avinash.targetgitrepos.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import co.avinash.targetgitrepos.R;
import co.avinash.targetgitrepos.model.TrendingRepoModel;
import co.avinash.targetgitrepos.utils.CircleTransform;

public class TrendingRepoDataAdapter extends RecyclerView.Adapter<TrendingRepoDataAdapter.TrendingRepoDataViewHolder>
        implements Filterable {

    private static final String TAG = TrendingRepoDataAdapter.class.getSimpleName();

    private List<TrendingRepoModel> mFilteredNameList;
    private List<TrendingRepoModel> mTrendingRepoModels;
    private Context mContext;
    private NameFilter mNameFilter;
    private TrendingRepoNameFilterListener mTrendingRepoNameFilterListener;
    private TrendingRepoItemClickListener mTrendingRepoItemClickListener;

    @Inject
    public TrendingRepoDataAdapter() {
    }

    public void setNameFilterListener(TrendingRepoNameFilterListener trendingRepoNameFilterListener) {
        this.mTrendingRepoNameFilterListener = trendingRepoNameFilterListener;
    }

    public void setTrendingRepoItemClickListener(TrendingRepoItemClickListener trendingRepoItemClickListener) {
        this.mTrendingRepoItemClickListener = trendingRepoItemClickListener;
    }

    @Override
    public TrendingRepoDataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TrendingRepoDataViewHolder(LayoutInflater.from(mContext).inflate(
                R.layout.trending_repo_row_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(final TrendingRepoDataViewHolder holder, final int position) {
        final TrendingRepoModel model = mFilteredNameList.get(position);
        holder.mAuthorName.setText(model.getAuthor());
        holder.mRepoName.setIncludeFontPadding(false);
        holder.mRepoName.setText(model.getName());
        holder.mRepoName.setIncludeFontPadding(false);
        holder.mDescription.setText(model.getDescription());
        try {
            Picasso.get()
                    .load(model.getAvatar())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .transform(new CircleTransform())
                    .into(holder.mAuthorImage);
        } catch (Exception exeception) {
            Log.e(TAG, "Received exception while loading image from Picasso");
        }

        holder.mLanuage.setText(model.getLanguage());
        holder.mStars.setText(String.valueOf(model.getStars()));
        holder.mForks.setText(String.valueOf(model.getForks()));

        holder.mParentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTrendingRepoItemClickListener != null) {
                    mTrendingRepoItemClickListener.handleItemClick(model);
                }
            }
        });
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public void setTrendingRepoModels(List<TrendingRepoModel> repoModels) {
        if(repoModels != null && repoModels.size() > 0) {
            mTrendingRepoModels = new ArrayList<>();
            mTrendingRepoModels.addAll(repoModels);
            mFilteredNameList = new ArrayList<>();
            mFilteredNameList.addAll(repoModels);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mFilteredNameList != null ? mFilteredNameList.size() : 0;
    }

    @Override
    public Filter getFilter() {
        if(mNameFilter == null) {
            mNameFilter = new NameFilter();
        }

        return mNameFilter;
    }

    public class TrendingRepoDataViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout mParentLayout, mDescRelLyt;
        public TextView mAuthorName, mRepoName, mDescription, mLanuage, mStars, mForks;
        public ImageView mAuthorImage;

        public TrendingRepoDataViewHolder(View itemView) {
            super(itemView);
            mParentLayout = itemView.findViewById(R.id.parent_layout);
            mAuthorName = itemView.findViewById(R.id.rowUserName);
            mRepoName = itemView.findViewById(R.id.rowRepoName);
            mDescription = itemView.findViewById(R.id.rowDescription);
            mLanuage = itemView.findViewById(R.id.rowLanguage);
            mStars = itemView.findViewById(R.id.rowStars);
            mForks = itemView.findViewById(R.id.rowForks);
            mAuthorImage = itemView.findViewById(R.id.rowImageVw);
            mDescRelLyt = itemView.findViewById(R.id.descRelLyt);
        }
    }


    private class NameFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            mFilteredNameList.clear();
            FilterResults results = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                // No filter implemented we return all the list
                mFilteredNameList.clear();
                mFilteredNameList.addAll(mTrendingRepoModels);
                results.values = mFilteredNameList;
                results.count = mFilteredNameList.size();
            } else {
                for (TrendingRepoModel item : mTrendingRepoModels) {
                    if (item != null && item.getName() != null) {
                        //if image title name starts with constraint, add it to filtered list
                        if (item.getName().toLowerCase().trim().contains(
                                constraint.toString().toLowerCase().trim())) {
                            mFilteredNameList.add(item);
                        }
                    }
                }
                results.values = mFilteredNameList;
                results.count = mFilteredNameList.size();
            }
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            if(mTrendingRepoNameFilterListener != null) {
                if (results.count > 0) {
                    mTrendingRepoNameFilterListener.showNoReposLayout(false);
                } else {
                    mTrendingRepoNameFilterListener.showNoReposLayout(true);
                }
            }
            notifyDataSetChanged();
        }
    }

    public interface TrendingRepoNameFilterListener {
        void showNoReposLayout(boolean show);
    }

    public interface TrendingRepoItemClickListener {
        void handleItemClick(TrendingRepoModel trendingRepoModel);
    }
}
