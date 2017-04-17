package com.shizhefei.test.view.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shizhefei.task.Code;
import com.shizhefei.task.TaskHandle;
import com.shizhefei.task.TaskHelper;
import com.shizhefei.task.imp.SimpleCallback;
import com.shizhefei.test.models.enties.Movie;
import com.shizhefei.test.models.enties.MovieAmount;
import com.shizhefei.test.models.task.MovieAmountTask;
import com.shizhefei.view.mvc.demo.R;

/**
 * Created by LuckyJayce on 2016/7/20.
 */
public class MoviesAdapter extends ListDataAdapter<Movie> {
    private final TaskHelper<MovieAmount> taskHelper;

    public MoviesAdapter(TaskHelper<MovieAmount> taskHelper) {
        this.taskHelper = taskHelper;
    }

    @Override
    public AbsItemViewHolder onCreateViewHolderHF(ViewGroup viewGroup, int type) {
        return new ItemViewHolder(inflate(R.layout.item_movie, viewGroup));
    }

    private class ItemViewHolder extends AbsItemViewHolder {

        private final TextView commentCountTextView;
        private final TextView playCountTextView;
        private final TextView nameTextView;
        private final TextView timeTextView;
        private final TextView updateTimeTextView;
        public TaskHandle handle;

        public ItemViewHolder(View itemView) {
            super(itemView);
            commentCountTextView = (TextView) itemView.findViewById(R.id.item_movie_commentCount_textView);
            playCountTextView = (TextView) itemView.findViewById(R.id.item_movie_playCount_textView);
            nameTextView = (TextView) itemView.findViewById(R.id.item_movie_name_textView);
            timeTextView = (TextView) itemView.findViewById(R.id.item_movie_time_textView);
            updateTimeTextView = (TextView) itemView.findViewById(R.id.item_movie_updateTime_textView);

        }

        @Override
        public void setData(Movie movie, int position) {
            nameTextView.setText(movie.getName());
            timeTextView.setText(movie.getTime());
            if (handle != null) {
                handle.cancle();
            }
            handle = taskHelper.executeCache(new MovieAmountTask(movie.getName()), new SimpleCallback<MovieAmount>() {
                @Override
                public void onPostExecute(Object task, Code code, Exception exception, MovieAmount movieAmount) {
                    if (code == Code.SUCCESS) {
                        commentCountTextView.setText("评论数:" + movieAmount.commentCount);
                        playCountTextView.setText("播放量:" + movieAmount.playCount);
                        updateTimeTextView.setText(movieAmount.updateTime);
                    }
                }
            }, new MovieAmountTask.CacheConfig(10000) {
                @Override
                public boolean isUsefulCacheData(Object taskOrDataSource, long requestTime, long saveTime, MovieAmount movieAmount) {
                    //这里设置显示缓存数据,不设置的话就会显示被回收的ViewHolder的数据了
                    commentCountTextView.setText("评论数:" + movieAmount.commentCount);
                    playCountTextView.setText("播放量:" + movieAmount.playCount);
                    updateTimeTextView.setText(movieAmount.updateTime + " isUsefulCacheData");
                    return super.isUsefulCacheData(taskOrDataSource, requestTime, saveTime, movieAmount);
                }
            });
        }
    }
}
