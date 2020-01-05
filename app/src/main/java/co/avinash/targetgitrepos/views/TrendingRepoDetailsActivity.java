package co.avinash.targetgitrepos.views;

import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import co.avinash.targetgitrepos.R;
import co.avinash.targetgitrepos.model.TrendingRepoModel;

public class TrendingRepoDetailsActivity extends AppCompatActivity {

    private TrendingRepoModel mTrendingRepoModel;

    private ImageView mBackArrow;
    private TextView mRepoName;
    private ImageView mAuthorAvatar;
    private TextView mDescription;
    private TextView mAuhtorName;
    private TextView mGitUrl;
    private TextView mLanguage;
    private TextView mStars;
    private TextView mForks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trending_repo_details_activity);
        if (getIntent() != null && getIntent().getExtras() != null
                && getIntent().getExtras().containsKey("trending_repo_model")) {
            mTrendingRepoModel = (TrendingRepoModel) getIntent().getExtras().getSerializable("trending_repo_model");
        }
        initializeView();
        fillDataInViews();
    }

    private void fillDataInViews() {
        if (mTrendingRepoModel != null) {
            if (!TextUtils.isEmpty(mTrendingRepoModel.getName())) {
                mRepoName.setText(mTrendingRepoModel.getName());
            }

            if (!TextUtils.isEmpty(mTrendingRepoModel.getAvatar())) {
                Picasso.get()
                        .load(mTrendingRepoModel.getAvatar())
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .fit()
                        .into(mAuthorAvatar);
            }

            mLanguage.setText(mTrendingRepoModel.getLanguage());
            mStars.setText(String.valueOf(mTrendingRepoModel.getStars()));
            mForks.setText(String.valueOf(mTrendingRepoModel.getForks()));

            mDescription.setText(mTrendingRepoModel.getDescription());
            mAuhtorName.setText(mTrendingRepoModel.getAuthor());
            String link = "<a href='" + mTrendingRepoModel.getUrl() + "'>" + mTrendingRepoModel.getUrl() + " </a>";
            mGitUrl.setClickable(true);
            mGitUrl.setMovementMethod(LinkMovementMethod.getInstance());
            mGitUrl.setText(Html.fromHtml(link));
        }
    }

    private void initializeView() {
        mBackArrow = findViewById(R.id.back_arrow);
        mRepoName = findViewById(R.id.username);
        mAuthorAvatar = findViewById(R.id.image_src);
        mDescription = findViewById(R.id.description);
        mAuhtorName = findViewById(R.id.authorName);
        mGitUrl = findViewById(R.id.giturl);
        mLanguage = findViewById(R.id.rowLanguage);
        mStars = findViewById(R.id.rowStars);
        mForks = findViewById(R.id.rowForks);

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
